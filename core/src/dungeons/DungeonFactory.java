package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.miv.Options;

import audio.Audio;
import data.HitboxData.HitboxType;
import graphics.Images;
import special_tiles.LadderTile;

public class DungeonFactory {
	//TODO: generate rooms, populate tiles with enemies
	
	public static Dungeon generateDungeon(int maxFloors, Entity player, Options options, Audio audio, Images images) {
		Dungeon dungeon = new Dungeon(player, options, audio);
		
		Floor[] floors = new Floor[maxFloors];
		for(int i = 0; i < maxFloors; i++) {
			Floor f = new Floor(10, 10);
			int x = 0;
			for(Tile[] col : f.getTiles()) {
				for(int y = 0; y < col.length; y++) {
					col[y] = new Tile(new Point(x, y));
					col[y].setSprite(images.getSprite("stone tile"));
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
