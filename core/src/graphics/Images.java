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
	 * Used for sprites that are part of a group. A group is created when sprites of the same name end in "_[number]"
	 * Attempts to get the sprite from the hashmap of all already-created sprites. 
	 * If it does not exist, a new sprite is created.
	 */
	public Array<Sprite> loadGroupedSprites(String groupName) {
		Array<Sprite> sprites = null;
		if(loadedAnimationSprites.containsKey(groupName)) {
			sprites = loadedAnimationSprites.get(groupName);
		} else {
			sprites = new Array<Sprite>();
			for(AtlasRegion region : spritesAtlas.findRegions(groupName)) {
				sprites.add(new Sprite(region));
			}
			loadedAnimationSprites.put(groupName, sprites);
		}
		if(sprites == null) {
			System.out.println(Options.spritesImageFileDir + " does not contain group \"" + groupName + "\"");
		}
		return sprites;
	}
	
	/**
	 * Used for sprites that are part of animations. DOES NOT attempt to get the sprite from the hashmap of all already-created sprites. 
	 * A new sprite object is created each time.
	 */
	public Array<Sprite> forceLoadNewGroupedSprites(String groupName) {
		Array<Sprite> sprites = new Array<Sprite>();
		for(AtlasRegion region : spritesAtlas.findRegions(groupName)) {
			sprites.add(new Sprite(region));
		}
		loadedAnimationSprites.put(groupName, sprites);
		return sprites;
	}
}
