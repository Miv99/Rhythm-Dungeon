package factories;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import data.HitboxData.HitboxType;
import dungeons.Dungeon;
import dungeons.Floor;
import dungeons.Tile;
import dungeons.Dungeon.DungeonParams;

public class DungeonFactory {
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
	
	public static Floor generateFloor(DungeonParams dungeonParams, int floor) {
		Floor f = new Floor(30, 30);
		int x = 0;
		for(Tile[] col : f.getTiles()) {
			for(int y = 0; y < col.length; y++) {
				col[y] = new Tile(new Point(x, y));
				col[y].setSprite(dungeonParams.getImages().loadSprite("stone_tile"));
				col[y].setHitboxType(HitboxType.INTANGIBLE);
			}
			x++;
		}
		
		return f;
	}
}
