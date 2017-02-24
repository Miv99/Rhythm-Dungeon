package data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationData {
	private Animation<Sprite> animation;
	private int frameCount;
	private float animationDurationInBeats;
	
	public AnimationData(Animation<Sprite> animation, int frameCount, float animationDurationInBeats) {
		this.animation = animation;
		this.frameCount = frameCount;
		this.animationDurationInBeats = animationDurationInBeats;
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
}
