package factories;

import java.awt.Point;
import java.awt.Rectangle;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import data.HitboxData.HitboxType;
import dungeons.Dungeon;
import dungeons.Dungeon.DungeonParams;
import dungeons.Floor;
import dungeons.Tile;
import utils.GeneralUtils;

public class DungeonFactory {
	private static class Room {
		private Rectangle rect;
		private RoomType roomType;
		private Tile[][] tiles;
		private Point mapPosition;
	}
	private static enum RoomType {
		TREASURE,
		TRAPS_AND_TREASURE,
		EMPTY,
		EXIT_ROOM,
		ENTRANCE_ROOM
	}
	
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(DungeonParams dungeonParams) {
		Dungeon dungeon = new Dungeon(dungeonParams);
		
		Floor[] floors = new Floor[dungeonParams.getMaxFloors()];
		
		floors[0] = generateFloor(dungeonParams, 0);
			
		Array<Entity> spawns = floors[0].getEntitiesToBeSpawned();
		spawns.add(dungeonParams.getEntityFactory()
				.createEntity(dungeonParams.getEntityLoader().getEntitiesData().get("dragon"), new Point(5, 5), 5));
		
		dungeon.setFloors(floors);
		
		return dungeon;
	}
	
	public static Floor generateFloor(DungeonParams dungeonParams, int floorNumber) {
		int floorSideLength = getFloorMaxSideLength(floorNumber);
		Floor floor = new Floor(floorSideLength, floorSideLength);
		
		// Create rooms
		Array<Rectangle> roomRectangles = generateRoomRectangles(floorNumber, floorSideLength, floorSideLength);
		Array<Room> rooms = new Array<Room>();
		for(Rectangle rect : roomRectangles) {
			Room room = new Room();
			room.rect = rect;
			room.mapPosition = new Point(rect.x, rect.y);
			room.tiles = new Tile[rect.width][rect.height];
			for(int x = 0; x < room.tiles.length; x++) {
				for(int y = 0; y < room.tiles[x].length; y++) {
					room.tiles[x][y] = new Tile(new Point(x + rect.x, y + rect.y));
				}
			}
			rooms.add(room);
		}
		
		// Randomize room types
		// start with ENTRNACE/EXIT
		
		// Carve corridors
		
		// Set tile images
		
		// Randomize rooms' layout based on room types
		
		// Randomly place visual overlays on tiles
		
		// Randomly place rocks
		
		int x = 0;
		for(Tile[] col : floor.getTiles()) {
			for(int y = 0; y < col.length; y++) {
				col[y] = new Tile(new Point(x, y));
				col[y].setSprite(dungeonParams.getImages().loadSprite("stone_tile"));
				col[y].setHitboxType(HitboxType.INTANGIBLE);
			}
			x++;
		}
		floor.createBreakableTile(dungeonParams.getEntityFactory(), dungeonParams.getEntityLoader().getEntitiesData().get("stone_wall_breakable"), new Point(4, 4), 1);
		
		return floor;
	}
	
	/**
	 * @param generationAttempts - directly affects number of rooms
	 */
	private static Array<Rectangle> generateRoomRectangles(int floorNumber, int floorMaxX, int floorMaxY) {
		Array<Rectangle> rooms = new Array<Rectangle>();
		for(int i  = 0; i < getRoomPlacementAttempts(floorMaxX * floorMaxY); i++) {
			Rectangle rect = new Rectangle(GeneralUtils.randomInt(1, floorMaxX), GeneralUtils.randomInt(1, floorMaxY), 
					GeneralUtils.randomInt(getRoomMinSideLength(floorMaxX), getRoomMaxSideLength(floorMaxX)),
					GeneralUtils.randomInt(getRoomMinSideLength(floorMaxY), getRoomMaxSideLength(floorMaxY)));
			if(!GeneralUtils.intersects(rect, rooms)) {
				rooms.add(rect);
			}
		}
		return rooms;
	}
	
	private static int getFloorMaxSideLength(int floorNumber) {
		return 50 + floorNumber*2;
	}
	
	private static int getRoomPlacementAttempts(int floorArea) {
		return floorArea/10;
	}
	
	private static int getRoomMinSideLength(int floorMaxLengthOfSameOrientation) {
		return floorMaxLengthOfSameOrientation/10;
	}
	
	private static int getRoomMaxSideLength(int floorMaxLengthOfSameOrientation) {
		return 3 * floorMaxLengthOfSameOrientation/10;
	}
}
