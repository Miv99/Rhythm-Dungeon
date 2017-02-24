package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

import data.AnimationData;
import utils.GeneralUtils;

public class AnimationComponent implements Component {
	private float animationStateTime;
	// If true, AnimationSystem will attempt to return the entity's ImageComponent's sprite to its original sprite
	private boolean queuedReturnToOriginalSprite;
	private HashMap<String, AnimationData> animations;
	private Animation<Sprite> currentAnimation;
	
	public AnimationComponent(HashMap<String, AnimationData> animations) {
		this.animations = animations;
	}
	
	public void startAnimation(String animationName) {
		if(animations.get(animationName) == null) {
			System.out.println("Missing animation: " + animationName);
		} else {
			animationStateTime = 0f;
			currentAnimation = animations.get(animationName).getAnimation();
		}
	}
	
	public void cancelAnimation() {
		queuedReturnToOriginalSprite = true;
		animationStateTime = 0f;
		currentAnimation = null;
	}
	
	public Animation<Sprite> getCurrentAnimation() {
		return currentAnimation;
	}
	
	public Sprite getKeyFrame() {
		return currentAnimation.getKeyFrame(animationStateTime);
	}
	
	public void update(float deltaTime) {
		if(currentAnimation != null) {
			animationStateTime += deltaTime;
			if(currentAnimation.isAnimationFinished(animationStateTime)) {
				cancelAnimation();
			}
		}
	}
	
	public void setQueuedReturnToOriginalSprite(boolean queuedReturnToOriginalSprite) {
		this.queuedReturnToOriginalSprite = queuedReturnToOriginalSprite;
	}
	
	public float getAnimationStateTime() {
		return animationStateTime;
	}
	
	public boolean getQueuedReturnToOriginalSprite() {
		return queuedReturnToOriginalSprite;
	}
}
