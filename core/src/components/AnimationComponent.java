package components;

import java.util.Collection;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import data.AnimationData;
import utils.GeneralUtils;

public class AnimationComponent implements Component {
	private float animationStateTime;
	private HashMap<String, AnimationData> animations;
	private Animation<TextureRegion> currentAnimation;
	
	public AnimationComponent(HashMap<String, AnimationData> animations, float dungeonBpm) {
		this.animations = animations;
		calculateAndSetAnimationFrameDurations(dungeonBpm);
	}
	
	public void calculateAndSetAnimationFrameDurations(float bpm) {
		Collection<AnimationData> animationDatas = animations.values();
		for(AnimationData data : animationDatas) {
			data.getAnimation().setFrameDuration(GeneralUtils.calulateAnimationFrameDuration(data.getAnimation().getKeyFrames().length, bpm/data.getAnimationDurationInBeats()));
		}
	}
	
	public void startAnimation(String animationName) {
		animationStateTime = 0f;
		currentAnimation = animations.get(animationName).getAnimation();
	}
	
	public void cancelAnimation() {
		animationStateTime = 0f;
	}
	
	public TextureRegion getKeyFrame() {
		return currentAnimation.getKeyFrame(animationStateTime);
	}
	
	public void update(float deltaTime) {
		animationStateTime += deltaTime;
	}
	
	public float getAnimationStateTime() {
		return animationStateTime;
	}
}
