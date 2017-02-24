package factories;

import java.awt.Point;

import data.HitboxData.HitboxType;
import dungeons.Dungeon;
import dungeons.Floor;
import dungeons.Tile;
import dungeons.Dungeon.DungeonParams;
import special_tiles.LadderTile;

public class DungeonFactory {
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(DungeonParams dungeonParams) {
		Dungeon dungeon = new Dungeon(dungeonParams);
		
		Floor[] floors = new Floor[dungeonParams.getMaxFloors()];
		for(int i = 0; i < dungeonParams.getMaxFloors(); i++) {
			Floor f = new Floor(10, 10);
			int x = 0;
			for(Tile[] col : f.getTiles()) {
				for(int y = 0; y < col.length; y++) {
					col[y] = new Tile(new Point(x, y));
					col[y].setSprite(dungeonParams.getImages().loadSprite("stone_tile"));
					col[y].setHitboxType(HitboxType.Intangible);
				}
				x++;
			}
			f.getTiles()[2][2].setHitboxType(HitboxType.Intangible);
			f.getTiles()[5][5].setSpecialTile(new LadderTile(dungeon));
			floors[i] = f;
		}
		
		dungeon.setFloors(floors);
		
		return dungeon;
	}
}
