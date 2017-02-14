package data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationData {
	private String animationName;
	private Animation<TextureRegion> animation;
	private float animationDurationInBeats;
	
	public AnimationData(String animationName, Animation<TextureRegion> animation, float animationDurationInBeats) {
		this.animationName = animationName;
		this.animation = animation;
		this.animationDurationInBeats = animationDurationInBeats;
	}
	
	public String getAnimationName() {
		return animationName;
	}
	
	public Animation<TextureRegion> getAnimation() {
		return animation;
	}
	
	public float getAnimationDurationInBeats() {
		return animationDurationInBeats;
	}
}
