package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.miv.EntityActions.Direction;

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
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.animationsDataFilePath);
		
		String animationName = "";
		float animationDurationInBeats = 1f;
		float finalFrameDuration = 0;
		int lineCount = 1;
		for(int i = 0; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("name=")) {
					animationName = line.replace("name=", "");
				} else if(line.startsWith("duration=")) {
					animationDurationInBeats = GeneralUtils.toFloat(line.replace("duration=", ""));
				} else if(line.startsWith("final_frame_duration=")) {
					finalFrameDuration = GeneralUtils.toFloat(line.replace("final_frame_duration=", ""));
				} else if(line.equals("")) {
					if(!animationName.equals("")) {
						Array<Sprite> rightSprites = new Array<Sprite>();
						rightSprites.addAll(images.loadGroupedSprites(animationName));
						
						if(rightSprites.size > 0) {
							AnimationData animationRightData = new AnimationData(new Animation<Sprite>(1f, rightSprites), rightSprites.size, animationDurationInBeats, finalFrameDuration);
							animationsData.put(animationName + "_" + Direction.RIGHT.getStringRepresentation(), animationRightData);
							
							Array<Sprite> leftSprites = new Array<Sprite>();
							leftSprites.addAll(images.forceLoadNewGroupedSprites(animationName));
							
							// Flip sprites to create left-facing animations
							for(Sprite sprite : leftSprites) {
								sprite.flip(true, false);
							}
							
							AnimationData animationLeftData = new AnimationData(new Animation<Sprite>(1f, leftSprites), leftSprites.size, animationDurationInBeats, finalFrameDuration);
							animationsData.put(animationName + "_" + Direction.LEFT.getStringRepresentation(), animationLeftData);
						}
						
						animationName = "";
						animationDurationInBeats = 1f;
						finalFrameDuration = 0;
					}
				} else {
					System.out.println("Animation data invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Animation data invalid value at line " + lineCount);
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!animationName.equals("")) {
			Array<Sprite> rightSprites = new Array<Sprite>();
			rightSprites.addAll(images.loadGroupedSprites(animationName));
			
			if(rightSprites.size > 0) {
				AnimationData animationRightData = new AnimationData(new Animation<Sprite>(1f, rightSprites), rightSprites.size, animationDurationInBeats, finalFrameDuration);
				animationsData.put(animationName + "_" + Direction.RIGHT.getStringRepresentation(), animationRightData);
				
				Array<Sprite> leftSprites = new Array<Sprite>();
				leftSprites.addAll(images.forceLoadNewGroupedSprites(animationName));
				
				// Flip sprites to create left-facing animations
				for(Sprite sprite : leftSprites) {
					sprite.flip(true, false);
				}
				
				AnimationData animationLeftData = new AnimationData(new Animation<Sprite>(1f, leftSprites), leftSprites.size, animationDurationInBeats, finalFrameDuration);
				animationsData.put(animationName + "_" + Direction.LEFT.getStringRepresentation(), animationLeftData);
			}
		}
	}
	
	/**
	 * Updates all animations' frame duration to match the new bpm
	 */
	public void updateAllAnimationFrameDuration(float newBpm) {
		for(AnimationData data : animationsData.values()) {
			if(data.getFinalFrameDuration() == 0) {
				data.getAnimation().setFrameDuration((60f * data.getAnimationDurationInBeats()/(newBpm * data.getFrameCount())));
			} else {
				data.getAnimation().setFrameDuration((60f * data.getAnimationDurationInBeats()/(newBpm * (data.getFrameCount() - 1))));
			}
		}
	}
	
	public HashMap<String, AnimationData> getAnimationsData() {
		return animationsData;
	}
}
