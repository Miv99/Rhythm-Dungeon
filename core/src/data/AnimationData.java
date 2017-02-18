package data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationData {
	private String animationName;
	private Animation<Sprite> animation;
	private float animationDurationInBeats;
	
	public AnimationData(String animationName, Animation<Sprite> animation, float animationDurationInBeats) {
		this.animationName = animationName;
		this.animation = animation;
		this.animationDurationInBeats = animationDurationInBeats;
	}
	
	public String getAnimationName() {
		return animationName;
	}
	
	public Animation<Sprite> getAnimation() {
		return animation;
	}
	
	public float getAnimationDurationInBeats() {
		return animationDurationInBeats;
	}
}
