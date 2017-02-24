package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.miv.Options;

import graphics.Images;
import utils.FileUtils;
import utils.GeneralUtils;

public class AnimationLoader {
	private Images images;
	// HashMap of animationName:animationData
	private HashMap<String, AnimationData> animationsData = new HashMap<String, AnimationData>();
	
	public AnimationLoader(Images images) {
		this.images = images;
	}
	
	public void loadAnimations() {
		// Load text file containing the animations' metadata
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.animationsMetadataFilePath);
		
		String animationName = "";
		float animationDurationInBeats = 1f;
		int lineCount = 1;
		for(int i = 0; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("animation_name=")) {
					animationName = line.replace("animation_name=", "");
				} else if(line.startsWith("duration=")) {
					animationDurationInBeats = GeneralUtils.toFloat(line.replace("duration=", ""));
				} else if(line.equals("")) {
					Array<Sprite> sprites = new Array<Sprite>();
					
					// Fill array of sprites
					sprites.addAll(images.loadAnimationSprites(animationName));
					
					AnimationData animationData = new AnimationData(new Animation<Sprite>(1f, sprites), sprites.size, animationDurationInBeats);
					animationsData.put(animationName, animationData);
					
					animationName = "";
					animationDurationInBeats = 1f;
				} else {
					System.out.println("Animation metadata invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Animation metadata invalid value at line " + lineCount);
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!animationName.equals("")) {
			Array<Sprite> sprites = new Array<Sprite>();
			
			// Fill array of sprites
			sprites.addAll(images.loadAnimationSprites(animationName));
			
			AnimationData animationData = new AnimationData(new Animation<Sprite>(1f, sprites), sprites.size, animationDurationInBeats);
			animationsData.put(animationName, animationData);
		}
	}
	
	/**
	 * Updates all animations' frame duration to match the new bpm
	 */
	public void updateAllAnimationFrameDuration(float newBpm) {
		for(AnimationData data : animationsData.values()) {
			data.getAnimation().setFrameDuration(data.getFrameCount()/(newBpm/data.getAnimationDurationInBeats()) * 4);
		}
	}
	
	public HashMap<String, AnimationData> getAnimationsData() {
		return animationsData;
	}
}
