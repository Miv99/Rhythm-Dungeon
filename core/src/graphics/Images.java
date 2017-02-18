package graphics;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miv.Options;

public class Images {
	private TextureAtlas spritesAtlas;
	private HashMap<String, Sprite> loadedSprites = new HashMap<String, Sprite>();
	
	public Images() {
		spritesAtlas = new TextureAtlas(new FileHandle(Options.spritesPackFilePath), new FileHandle(Options.spritesImageFilePath));
	}
	
	/**
	 * Attempts to get the sprite from the hashmap of all already-created sprites. If it does not exist,
	 * a new sprite is created.
	 */
	public Sprite getSprite(String name) {
		if(loadedSprites.containsKey(name)) {
			return loadedSprites.get(name);
		} else {
			return spritesAtlas.createSprite(name);
		}
	}
}
