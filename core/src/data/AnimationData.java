package data;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationData {
	private Animation<Sprite> animation;
	private float animationDurationInBeats;
	
	public AnimationData(Animation<Sprite> animation, float animationDurationInBeats) {
		this.animation = animation;
		this.animationDurationInBeats = animationDurationInBeats;
	}
	
	public Animation<Sprite> getAnimation() {
		return animation;
	}
	
	public float getAnimationDurationInBeats() {
		return animationDurationInBeats;
	}
}
