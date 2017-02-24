package graphics;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.miv.Options;

public class Images {
	private TextureAtlas spritesAtlas;
	private HashMap<String, Sprite> loadedSprites = new HashMap<String, Sprite>();
	private HashMap<String, Array<Sprite>> loadedAnimationSprites = new HashMap<String, Array<Sprite>>();
	
	public Images() {
		spritesAtlas = new TextureAtlas(new FileHandle(Options.spritesPackFilePath), new FileHandle(Options.spritesImageFileDir));
	}
	
	/**
	 * Attempts to get the sprite from the hashmap of all already-created sprites. If it does not exist,
	 * a new sprite is created.
	 */
	public Sprite loadSprite(String name) {
		Sprite sprite = null;
		if(loadedSprites.containsKey(name)) {
			sprite = loadedSprites.get(name);
		} else {
			sprite = spritesAtlas.createSprite(name);
			loadedSprites.put(name, sprite);
		}
		if(sprite == null) {
			System.out.println(Options.spritesImageFileDir + " does not contain image \"" + name + "\"");
		}
		return sprite;
	}
	
	/**
	 * Used for sprites that are part of animations. Attempts to get the sprite from the hashmap of all already-created sprites. 
	 * If it does not exist, a new sprite is created.
	 */
	public Array<Sprite> loadAnimationSprites(String animationName) {
		Array<Sprite> sprites = null;
		if(loadedAnimationSprites.containsKey(animationName)) {
			sprites = loadedAnimationSprites.get(animationName);
		} else {
			sprites = new Array<Sprite>();
			for(AtlasRegion region : spritesAtlas.findRegions(animationName)) {
				sprites.add(new Sprite(region));
			}
			loadedAnimationSprites.put(animationName, sprites);
		}
		if(sprites == null) {
			System.out.println(Options.spritesImageFileDir + " does not contain animation \"" + animationName + "\"");
		}
		return sprites;
	}
}
