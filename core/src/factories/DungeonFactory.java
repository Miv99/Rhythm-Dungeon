package factories;

import java.awt.Point;
import java.awt.Rectangle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.miv.Options;
import com.miv.EntityActions.Direction;

import audio.Audio;
import components.EntityAIComponent;
import components.MovementAIComponent;
import components.PlayerComponent;
import data.HitboxData.HitboxType;
import dungeons.Dungeon;
import dungeons.Dungeon.DungeonParams;
import entity_ai.Dragon;
import entity_ai.EntityAI.EntityAIParams;
import entity_ai.ExplodingTrap;
import entity_ai.PulsatingExpandingRingTrap;
import movement_ai.EfficientChaser;
import movement_ai.MovementAI.MovementAIParams;
import dungeons.Floor;
import dungeons.Tile;
import utils.GeneralUtils;
import utils.MapUtils;

public class DungeonFactory {
	private static class InsufficientRoomsException extends Exception {
		public InsufficientRoomsException() {
			super();
		}
	}
	private static class Room {
		private Rectangle rect;
		private RoomType roomType;
		private Tile[][] tiles;
	}
	private static enum RoomType {
		TREASURE,
		TRAPS_AND_TREASURE,
		EMPTY,
		EXIT,
		ENTRANCE
	}
	
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(DungeonParams dungeonParams) {
		Dungeon dungeon = new Dungeon(dungeonParams);
		
		Floor[] floors = new Floor[dungeonParams.getMaxFloors()];
		
		//floors[0] = generateFloor(dungeonParams, 0);
		floors[0] = new Floor(200, 200);
		for(int x = 0; x < floors[0].getTiles().length; x++) {
			for(int y = 0; y < floors[0].getTiles()[x].length; y++) {
				Tile t = new Tile(new Point(x, y));
				floors[0].getTiles()[x][y] = t;
				t.setSprite(dungeonParams.getImages().loadSprite("stone_tile"));
				t.setHitboxType(HitboxType.INTANGIBLE);
			}
		}
		for(int y = 0; y < 20; y++) {
			floors[0].getTiles()[25][y].setSprite(dungeonParams.getImages().loadSprite("stone_wall"));
			floors[0].getTiles()[25][y].setHitboxType(HitboxType.TANGIBLE);
		}
		for(int y = 44; y < 80; y++) {
			floors[0].getTiles()[25][y].setSprite(dungeonParams.getImages().loadSprite("stone_wall"));
			floors[0].getTiles()[25][y].setHitboxType(HitboxType.TANGIBLE);
		}
		
		//Engine engine, EntityFactory entityFactory, Options options, Audio audio, Dungeon dungeon, Entity self, Entity target, int activationRadiusInTiles
		EntityAIParams entityAIParams = new EntityAIParams(dungeonParams.getEngine(), dungeonParams.getEntityFactory(), dungeonParams.getOptions(), dungeonParams.getAudio(), dungeon, dungeonParams.getPlayer());
		MovementAIParams movementAIParams = new MovementAIParams(dungeonParams.getEngine(), dungeon, dungeonParams.getPlayer());
		
		Array<Entity> spawns = floors[0].getEntitiesToBeSpawned();
		Entity dragon1 = dungeonParams.getEntityFactory()
				.createEntity(dungeonParams.getEntityLoader().getEntitiesData().get("dragon"), new Point(20, 30), 5);
		dragon1.add(new EntityAIComponent(new Dragon(entityAIParams, dragon1, 10)));
		dragon1.add(new MovementAIComponent(new EfficientChaser(movementAIParams, dragon1, 10)));
		spawns.add(dragon1);
		/**
		 * EntityFactory entityFactory, Options options, Audio audio, Dungeon dungeon, Entity self, Entity target, int activationRadiusInTiles
		 * 
		 * EnemyAIParams params, Class<? extends Component> entityHittableRequirement, boolean warnTilesBeforeAttack,
			int ringMaxRadiusInTiles, int pulseExpansionFrequencyInBeats, String animationOnTile
		 */
		Entity trap1 = dungeonParams.getEntityFactory().createEntity(dungeonParams.getEntityLoader().getEntitiesData().get("default_trap"), new Point(30, 30), 1);
		int trap1Radius = 5;
		trap1.add(new EntityAIComponent(new PulsatingExpandingRingTrap(entityAIParams, trap1, 10, PlayerComponent.class, true, 3, trap1Radius, "none")));
		//spawns.add(trap1);
		
		dungeon.setFloors(floors);
		
		return dungeon;
	}
	
	public static Floor generateFloor(DungeonParams dungeonParams, int floorNumber) {
		int floorSideLength = getFloorMaxSideLength(floorNumber);
		Floor floor = new Floor(floorSideLength, floorSideLength);
		
		// Create rooms
		Array<Rectangle> roomRectangles = generateRoomRectangles(10, 10, floorSideLength - 10, floorSideLength - 10);
		Array<Room> rooms = toRoomsArray(roomRectangles);
		
		
		try {
			randomizeRoomTypes(rooms, floorNumber);
		} catch(InsufficientRoomsException e) {
			System.out.println("Not enough rooms. Re-generating floor.");
			return generateFloor(dungeonParams, floorNumber);
		}
		
		// Randomize rooms' layout based on room types
		

		// Carve corridors
		Array<Rectangle> corridorsRectangles = generateCorridors(roomRectangles);
		Array<Room> corridors = toRoomsArray(corridorsRectangles);
		
		// Create tiles from rooms
		String defaultTileSpriteName = getDefaultTileSpriteNameFromFloor(floorNumber);
		Array<Tile> allRoomTiles = new Array<Tile>();
		Tile[][] floorTiles = floor.getTiles();
		// Set sprites for all tiles in rooms
		for(Room room : rooms) {
			for(int x = room.rect.x; x < room.rect.x + room.tiles.length; x++) {
				for(int y = room.rect.y; y < room.rect.y + room.tiles[x - room.rect.x].length; y++) {
					if(floorTiles[x][y] == null) {
						floorTiles[x][y] = new Tile(new Point(x, y));
					}
					floorTiles[x][y].setSprite(dungeonParams.getImages().loadSprite(defaultTileSpriteName));
					floorTiles[x][y].setHitboxType(HitboxType.INTANGIBLE);
					allRoomTiles.add(floorTiles[x][y]);
				}
			}
		}
		// Convert any tangible tiles that the corridors cut through to breakable tiles
		for(Room room : corridors) {
			for(int x = room.rect.x; x < room.rect.x + room.tiles.length; x++) {
				for(int y = room.rect.y; y < room.rect.y + room.tiles[x - room.rect.x].length; y++) {
					if(floorTiles[x][y] == null) {
						floorTiles[x][y] = new Tile(new Point(x, y));
						floorTiles[x][y].setSprite(dungeonParams.getImages().loadSprite(defaultTileSpriteName));
						floorTiles[x][y].setHitboxType(HitboxType.INTANGIBLE);
					} else if(floorTiles[x][y].getHitboxType().isTangible()) {
						floor.createBreakableTile(dungeonParams.getEntityFactory(), dungeonParams.getEntityLoader().getEntitiesData().get("invisible_breakable"), new Point(x, y), 1);
						floorTiles[x][y].addSpriteOverlay(dungeonParams.getImages().loadGroupedSprites("crack").random());
					}
					allRoomTiles.add(floorTiles[x][y]);
				}
			}
		}
		// Initialize all null tiles
		for(int x = 0; x < floorSideLength; x++) {
			for(int y = 0; y < floorSideLength; y++) {
				if(floorTiles[x][y] == null) {
					floorTiles[x][y] = new Tile(new Point(x, y));
					floorTiles[x][y].setHitboxType(HitboxType.TANGIBLE);
				}
			}
		}
		
		for(Tile tile : allRoomTiles) {
			if(!tile.isTangibleTile()) {
				// Randomly place visual overlays on tiles
				if(Math.random() < 0.005) {
					tile.addSpriteOverlay(dungeonParams.getImages().loadGroupedSprites("small_rocks").random());
				} else if(Math.random() < 0.005) {
					tile.addSpriteOverlay(dungeonParams.getImages().loadGroupedSprites("crack").random());
				}
				
				// Randomly place rocks
				if(Math.random() < 0.005) {
					floor.createBreakableTile(dungeonParams.getEntityFactory(), dungeonParams.getEntityLoader().getEntitiesData().get("rock_breakable"), tile.getMapPosition(), 1);
				}
			}
		}
		
		return floor;
	}
	
	/**
	 * TODO: add to this
	 */
	private static String getDefaultTileSpriteNameFromFloor(int floorNumber) {
		if(floorNumber < 10) {
			return "stone_tile";
		}
		return "stone_tile";
	}
	
	/**
	 * Generates an array of rectangles that connect every given rectangle with at least one other one
	 */
	private static Array<Rectangle> generateCorridors(Array<Rectangle> rects) {
		Array<Rectangle> corridors = new Array<Rectangle>();
		for(int i = 1; i < rects.size; i++) {
			corridors.addAll(generateCorridor(rects.get(i - 1), rects.get(i)));
		}
		return corridors;
	}
	
	/**
	 * Generates an array of rectangles that connect the two given rectangles
	 * Each corridor has a random number of turns
	 */
	private static Array<Rectangle> generateCorridor(Rectangle rect1, Rectangle rect2) {
		Point rect1Center = new Point(rect1.x + rect1.width/2, rect1.y + rect1.height/2);
		Point rect2Center = new Point(rect2.x + rect2.width/2, rect2.y + rect2.height/2);
		
		Point corridorStart = null;
		Point corridorEnd = null;
		Direction r2RelativeToR1 = MapUtils.getRelativeDirection(rect2Center, rect1Center);
		boolean singleCorridor = false;
		if(r2RelativeToR1.equals(Direction.RIGHT)) {
			// Start on right wall
			corridorStart = new Point(rect1.x + rect1.width, rect1.y + MathUtils.random(rect1.height));
			
			if(Math.abs(rect2.y - corridorStart.y) < rect2.height) {
				// End on left wall
				corridorEnd = new Point(rect2.x, corridorStart.y);
				singleCorridor = true;
			} else if(rect2.y > corridorStart.y) {
				// End on lower wall
				corridorEnd = new Point(rect2.x + MathUtils.random(rect2.width), rect2.y);
			} else if(rect2.y < corridorStart.y) {
				// End on upper wall
				corridorEnd = new Point(rect2.x + MathUtils.random(rect2.width), rect2.y + rect2.height);
			}
		} else if(r2RelativeToR1.equals(Direction.LEFT)) {
			// Start on left wall
			corridorStart = new Point(rect1.x, rect1.y + MathUtils.random(rect1.height));
			
			if(Math.abs(rect2.y - corridorStart.y) < rect2.height) {
				// End on right wall
				corridorEnd = new Point(rect2.x + rect2.width, corridorStart.y);
				singleCorridor = true;
			} else if(rect2.y > corridorStart.y) {
				// End on lower wall
				corridorEnd = new Point(rect2.x + MathUtils.random(rect2.width), rect2.y);
			} else if(rect2.y < corridorStart.y) {
				// End on upper wall
				corridorEnd = new Point(rect2.x + MathUtils.random(rect2.width), rect2.y + rect2.height);
			}
		} else if(r2RelativeToR1.equals(Direction.UP)) {
			// Start on upper wall
			corridorStart = new Point(rect1.x + MathUtils.random(rect1.width), rect1.y + rect1.height);
			
			if(Math.abs(rect2.x - corridorStart.x) < rect2.width) {
				// End on bottom wall
				corridorEnd = new Point(corridorStart.x, rect2.y);
				singleCorridor = true;
			} else if(rect2.x > corridorStart.x) {
				// End on left wall
				corridorEnd = new Point(rect2.x, rect2.y + MathUtils.random(rect2.height));
			} else if(rect2.x < corridorStart.x) {
				// End on right wall
				corridorEnd = new Point(rect2.x + rect2.width, rect2.y + MathUtils.random(rect2.height));
			}
		} else if(r2RelativeToR1.equals(Direction.DOWN)) {
			// Start on lower wall
			corridorStart = new Point(rect1.x + MathUtils.random(rect1.width), rect1.y);
			
			if(Math.abs(rect2.x - corridorStart.x) < rect2.width) {
				// End on upper wall
				corridorEnd = new Point(corridorStart.x, rect2.y + rect2.height);
				singleCorridor = true;
			} else if(rect2.x > corridorStart.x) {
				// End on left wall
				corridorEnd = new Point(rect2.x, rect2.y + MathUtils.random(rect2.height));
			} else if(rect2.x < corridorStart.x) {
				// End on right wall
				corridorEnd = new Point(rect2.x + rect2.width, rect2.y + MathUtils.random(rect2.height));
			}
		}
		
		final int corridorMinWidth = 2;
		final int corridorMaxWidth = 5;
		
		Array<Rectangle> rects = new Array<Rectangle>();
		if(singleCorridor) {
			int corridorWidth = MathUtils.random(corridorMinWidth, corridorMaxWidth);
			
			if(r2RelativeToR1.equals(Direction.RIGHT)) {
				rects.add(new Rectangle(corridorStart.x, corridorStart.y - corridorWidth/2, corridorEnd.x - corridorStart.x, corridorWidth));
			} else if(r2RelativeToR1.equals(Direction.LEFT)) {
				rects.add(new Rectangle(corridorStart.x + (corridorEnd.x - corridorStart.x), corridorStart.y - corridorWidth/2, -(corridorEnd.x - corridorStart.x), corridorWidth));
			} else if(r2RelativeToR1.equals(Direction.UP)) {
				rects.add(new Rectangle(corridorStart.x - corridorWidth/2, corridorStart.y, corridorWidth, corridorEnd.y - corridorStart.y));
			} else if(r2RelativeToR1.equals(Direction.DOWN)) {
				rects.add(new Rectangle(corridorStart.x - corridorWidth/2, corridorStart.y + (corridorEnd.y - corridorStart.y), corridorWidth, -(corridorEnd.y - corridorStart.y)));
			}
		} else {
			int corridor1Width = MathUtils.random(corridorMinWidth, corridorMaxWidth);
			int corridor2Width = MathUtils.random(corridorMinWidth, corridorMaxWidth);
			
			if(r2RelativeToR1.equals(Direction.RIGHT)) {
				rects.add(new Rectangle(corridorStart.x, corridorStart.y - corridor1Width/2, corridorEnd.x - corridorStart.x, corridor1Width));
				
				if(corridorEnd.y - corridorStart.y > 0) {
					rects.add(new Rectangle(corridorStart.x - corridor2Width/2, corridorStart.y, corridor2Width, corridorEnd.y - corridorStart.y));
				} else {
					rects.add(new Rectangle(corridorStart.x - corridor2Width/2, corridorStart.y + (corridorEnd.y - corridorStart.y), corridor2Width, -(corridorEnd.y - corridorStart.y)));
				}
			} else if(r2RelativeToR1.equals(Direction.LEFT)) {
				rects.add(new Rectangle(corridorStart.x + (corridorEnd.x - corridorStart.x), corridorStart.y - corridor1Width/2, -(corridorEnd.x - corridorStart.x), corridor1Width));
				
				if(corridorEnd.y - corridorStart.y > 0) {
					rects.add(new Rectangle(corridorStart.x - corridor2Width/2 + (corridorEnd.x - corridorStart.x), corridorStart.y, corridor2Width, corridorEnd.y - corridorStart.y));
				} else {
					rects.add(new Rectangle(corridorStart.x - corridor2Width/2 + (corridorEnd.x - corridorStart.x), corridorStart.y + (corridorEnd.y - corridorStart.y), corridor2Width, -(corridorEnd.y - corridorStart.y)));
				}
			} else if(r2RelativeToR1.equals(Direction.UP)) {
				rects.add(new Rectangle(corridorStart.x - corridor1Width/2, corridorStart.y, corridor1Width, corridorEnd.y - corridorStart.y));
				
				if(corridorEnd.x - corridorStart.x > 0) {
					rects.add(new Rectangle(corridorStart.x, corridorStart.y - corridor2Width/2, corridorEnd.x - corridorStart.x, corridor2Width));
				} else {
					rects.add(new Rectangle(corridorStart.x + (corridorEnd.x - corridorStart.x), corridorStart.y - corridor1Width/2, -(corridorEnd.x - corridorStart.x), corridor1Width));
				}
			} else if(r2RelativeToR1.equals(Direction.DOWN)) {
				rects.add(new Rectangle(corridorStart.x - corridor1Width/2, corridorStart.y + (corridorEnd.y - corridorStart.y), corridor1Width, -(corridorEnd.y - corridorStart.y)));
			
				if(corridorEnd.x - corridorStart.x > 0) {
					rects.add(new Rectangle(corridorStart.x, corridorStart.y - corridor2Width/2 + (corridorEnd.y - corridorStart.y), corridorEnd.x - corridorStart.x, corridor2Width));
				} else {
					rects.add(new Rectangle(corridorStart.x + (corridorEnd.x - corridorStart.x), corridorStart.y - corridor1Width/2 + (corridorEnd.y - corridorStart.y), -(corridorEnd.x - corridorStart.x), corridor1Width));
				}
			}
		}
		
		
		return rects;
	}
	
	private static Array<Room> toRoomsArray(Array<Rectangle> roomRectangles) {
		Array<Room> rooms = new Array<Room>();
		for(Rectangle rect : roomRectangles) {
			Room room = new Room();
			room.rect = rect;
			room.tiles = new Tile[Math.abs(rect.width)][Math.abs(rect.height)];
			for(int x = 0; x < room.tiles.length; x++) {
				for(int y = 0; y < room.tiles[x].length; y++) {
					room.tiles[x][y] = new Tile(new Point(x, y));
				}
			}
			rooms.add(room);
		}
		return rooms;
	}
	
	private static Array<Rectangle> generateRoomRectangles(int floorMinX, int floorMinY, int floorMaxX, int floorMaxY) {
		Array<Rectangle> rooms = new Array<Rectangle>();
		for(int i  = 0; i < getRoomPlacementAttempts(floorMaxX * floorMaxY); i++) {
			Rectangle rect = new Rectangle(MathUtils.random(floorMinX, floorMaxX - getRoomMaxSideLength(floorMaxX)), MathUtils.random(floorMinY, floorMaxY - getRoomMaxSideLength(floorMaxY)), 
					MathUtils.random(getRoomMinSideLength(floorMaxX), getRoomMaxSideLength(floorMaxX)),
					MathUtils.random(getRoomMinSideLength(floorMaxY), getRoomMaxSideLength(floorMaxY)));
			if(!GeneralUtils.intersects(rect, rooms)) {
				rooms.add(rect);
			}
		}
		return rooms;
	}
	
	private static void randomizeRoomTypes(Array<Room> rooms, int floorNumber) throws InsufficientRoomsException {
		Array<Room> roomsCopy = new Array<Room>();
		roomsCopy.addAll(rooms);
		
		// Set entrance and exit rooms
		roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.ENTRANCE;
		roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.EXIT;
		
		// Set treasure/traps and treasure rooms
		if(floorNumber >= 0 && floorNumber < 9) {
			if(roomsCopy.size >= 1) {
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TREASURE;
			} else {
				throw new InsufficientRoomsException();
			}
		} else if(floorNumber >= 9 && floorNumber < 19) {
			if(roomsCopy.size >= 2) {
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
			} else {
				throw new InsufficientRoomsException();
			}
		} else if(floorNumber > 19 && floorNumber < 29) {
			if(roomsCopy.size >= 2) {
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
			} else {
				throw new InsufficientRoomsException();
			}
		} else if(floorNumber > 29 && floorNumber < 39) {
			if(roomsCopy.size >= 3) {
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
			} else {
				throw new InsufficientRoomsException();
			}
		} else if(floorNumber > 39 && floorNumber < 49) {
			if(roomsCopy.size >= 3) {
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
				roomsCopy.removeIndex(MathUtils.random(0, roomsCopy.size - 1)).roomType = RoomType.TRAPS_AND_TREASURE;
			} else {
				throw new InsufficientRoomsException();
			}
		}
	}
	
	private static int getFloorMaxSideLength(int floorNumber) {
		return 150 + floorNumber*4;
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
