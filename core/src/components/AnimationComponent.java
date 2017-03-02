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
	
	public AnimationComponent(HashMap<String, AnimationData> animations, String idleAnimationName) {
		this.animations = animations;
		this.idleAnimationName = idleAnimationName;
	}
	
	public void startAnimation(String animationName, PlayMode animationPlayMode) {
		if(!animationName.equals("none")) {
			if(animations.get(animationName) == null) {
				System.out.println("Missing animation: " + animationName);
			} else {
				animationStateTime = 0f;
				currentAnimation = animations.get(animationName).getAnimation();
				currentAnimation.setPlayMode(animationPlayMode);
			}
		}
	}
	
	public void cancelAnimation() {
		animationStateTime = 0f;
		currentAnimation = null;
	}
	
	public String getIdleAnimationName() {
		return idleAnimationName;
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
	
	public void setQueuedIdleAnimation(boolean queuedIdleAnimation) {
		this.queuedIdleAnimation = queuedIdleAnimation;
	}
	
	public float getAnimationStateTime() {
		return animationStateTime;
	}
	
	public boolean getQueuedIdleAnimation() {
		return queuedIdleAnimation;
	}
}
