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
		String imagesString = "";
		float animationDurationInBeats = 1f;
		int lineCount = 1;
		for(int i = 0; i < metadata.size() - 1; i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("animation_name=")) {
					animationName = line.replace("animation_name=", "");
				} else if(line.startsWith("duration=")) {
					animationDurationInBeats = GeneralUtils.toFloat(line.replace("duration=", ""));
				} else if(line.startsWith("images=")) {
					imagesString = line.replace("images=", "");
				} else if(line.equals("")) {
					Array<Sprite> sprites = new Array<Sprite>();
					// Fill array of sprites
					for(String imageName : imagesString.split(",")) {
						sprites.add(images.getSprite(imageName.replaceAll(" ", "")));
					}
					AnimationData animationData = new AnimationData(new Animation<Sprite>(1f, sprites), animationDurationInBeats);
					animationsData.put(animationName, animationData);
					
					animationName = "";
					imagesString = "";
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
			for(String imageName : imagesString.split(",")) {
				sprites.add(images.getSprite(imageName.replaceAll(" ", "")));
			}
			AnimationData animationData = new AnimationData(new Animation<Sprite>(1f, sprites), animationDurationInBeats);
			animationsData.put(animationName, animationData);
		}
	}
}
