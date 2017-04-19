package components;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;

import data.AnimationData;

public class AnimationComponent implements Component {
	private float animationStateTime;
	// If true, AnimationSystem will attempt to play the idle animation
	private boolean queuedIdleAnimation;
	private HashMap<String, AnimationData> animations;
	private Animation<Sprite> currentAnimation;
	// Idle animation will be started on each new beat if currentAnimation is null
	private String idleAnimationName;
	private boolean playingIdleAnimation;
	private AnimationData currentAnimationData;
	// Time until final frame ends; only used for animations with an abnormal final frame length
	private float timeUntilEndOfFinalFrame;
	
	// If true, after the animation finishes, the entity the component belongs to is removed from the engine
	// Used for animation entities (entities used to show animations)
	private boolean removeEntityOnAnimationFinish;
	
	public AnimationComponent(HashMap<String, AnimationData> animations, String idleAnimationName) {
		this.animations = animations;
		this.idleAnimationName = idleAnimationName;
	}
	
	public void startAnimation(String animationName, PlayMode animationPlayMode) {
		if(!animationName.startsWith("none")) {
			if(animations.get(animationName) == null) {
				System.out.println("Missing animation: " + animationName);
			} else {
				playingIdleAnimation = animationName.contains(idleAnimationName);
				animationStateTime = 0f;
				currentAnimationData = animations.get(animationName);
				currentAnimation = currentAnimationData.getAnimation();
				currentAnimation.setPlayMode(animationPlayMode);
			}
		}
	}
	
	/**
	 * Transitions into a new animation without changing the animation state time.
	 * Used to ensure animations are timed correctly to the beat.
	 */
	public void transitionAnimation(String newAnimationName, PlayMode animationPlayMode) {
		if(!newAnimationName.startsWith("none")) {
			if(animations.get(newAnimationName) == null) {
				System.out.println("Missing animation: " + newAnimationName);
			} else {
				playingIdleAnimation = newAnimationName.contains(idleAnimationName);
				currentAnimationData = animations.get(newAnimationName);
				currentAnimation = currentAnimationData.getAnimation();
				currentAnimation.setPlayMode(animationPlayMode);
			}
		}
	}
	
	public void cancelAnimation() {
		animationStateTime = 0f;
		timeUntilEndOfFinalFrame = 0;
		currentAnimation = null;
		currentAnimationData = null;
		playingIdleAnimation = false;
	}
	
	/**
	 * Returns whether or not the current animation is one that is not the idle animation. Returns false if no animation in progress.
	 */
	public boolean isPlayingIdleAnimation() {
		if(currentAnimation == null) {
			return true;
		} else {
			return playingIdleAnimation;
		}
	}
	
	
	public void setRemoveEntityOnAnimationFinish(boolean removeEntityOnAnimationFinish) {
		this.removeEntityOnAnimationFinish = removeEntityOnAnimationFinish;
	}
	
	public void setPlayingIdleAnimation(boolean playingIdleAnimation) {
		this.playingIdleAnimation = playingIdleAnimation;
	}
	
	public String getIdleAnimationName() {
		return idleAnimationName;
	}
	
	public Animation<Sprite> getCurrentAnimation() {
		return currentAnimation;
	}
	
	public AnimationData getCurrentAnimationData() {
		return currentAnimationData;
	}
	
	public Sprite getKeyFrame() {
		return currentAnimation.getKeyFrame(animationStateTime);
	}
	
	public void update(float deltaTime) {
		if(currentAnimation != null) {
			animationStateTime += deltaTime;
		}
	}
	
	public void setQueuedIdleAnimation(boolean queuedIdleAnimation) {
		this.queuedIdleAnimation = queuedIdleAnimation;
	}
	
	public float getAnimationStateTime() {
		return animationStateTime;
	}
	
	public boolean isQueuedIdleAnimation() {
		return queuedIdleAnimation;
	}
	
	public boolean isRemoveEntityOnAnimationFinish() {
		return removeEntityOnAnimationFinish;
	}
	
	public float getTimeUntilEndOfFinalFrame() {
		return timeUntilEndOfFinalFrame;
	}
	
	public void setTimeUntilEndOfFinalFrame(float timeUntilEndOfFinalFrame) {
		this.timeUntilEndOfFinalFrame = timeUntilEndOfFinalFrame;
	}
}
