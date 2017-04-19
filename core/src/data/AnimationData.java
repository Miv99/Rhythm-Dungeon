package data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationData {
	private Animation<Sprite> animation;
	private int frameCount;
	private float animationDurationInBeats;
	// For animations with an abnormal final frame duration
	private float finalFrameDuration;
	
	public AnimationData(Animation<Sprite> animation, int frameCount, float animationDurationInBeats, float finalFrameDuration) {
		this.animation = animation;
		this.frameCount = frameCount;
		this.animationDurationInBeats = animationDurationInBeats;
		this.finalFrameDuration = finalFrameDuration;
	}
	
	public Animation<Sprite> getAnimation() {
		return animation;
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	
	public float getAnimationDurationInBeats() {
		return animationDurationInBeats;
	}
	
	public float getFinalFrameDuration() {
		return finalFrameDuration;
	}
}
