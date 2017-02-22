package dungeons;

import com.badlogic.ashley.core.Entity;

import audio.Audio;
import data.HitboxData.HitboxType;
import special_tiles.LadderTile;
import systems.TileRenderSystem;

public class DungeonFactory {
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(int maxFloors, Entity player, Audio audio, TileRenderSystem tileRenderSystem) {
		Dungeon dungeon = new Dungeon(player, audio, tileRenderSystem);
		
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
