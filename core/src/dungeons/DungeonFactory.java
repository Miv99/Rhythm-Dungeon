package dungeons;

import com.badlogic.ashley.core.Entity;
import com.miv.Options;

import audio.Audio;
import data.HitboxData.HitboxType;
import special_tiles.LadderTile;

public class DungeonFactory {
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(int maxFloors, Entity player, Options options, Audio audio) {
		Dungeon dungeon = new Dungeon(player, options, audio);
		
		Floor[] floors = new Floor[maxFloors];
		for(int i = 0; i < maxFloors; i++) {
			Floor f = new Floor(10, 10);
			for(Tile[] col : f.getTiles()) {
				for(Tile tile : col) {
					tile.setHitboxType(HitboxType.Intangible);
				}
			}
			f.getTiles()[2][2].setHitboxType(HitboxType.Intangible);
			f.getTiles()[5][5].setSpecialTile(new LadderTile(dungeon));
			floors[i] = f;
		}
		
		dungeon.setFloors(floors);
		
		return dungeon;
	}
}
