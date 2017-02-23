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
		spritesAtlas = new TextureAtlas(new FileHandle(Options.spritesPackFilePath), new FileHandle(Options.spritesImageFileDir));
	}
	
	/**
	 * Attempts to get the sprite from the hashmap of all already-created sprites. If it does not exist,
	 * a new sprite is created.
	 */
	public Sprite getSprite(String name) {
		Sprite sprite = null;
		if(loadedSprites.containsKey(name)) {
			sprite = loadedSprites.get(name);
		} else {
			sprite = spritesAtlas.createSprite(name);
		}
		if(sprite == null) {
			System.out.println(Options.spritesImageFileDir + " does not contain \"" + name + "\"");
		}
		return sprite;
	}
}
