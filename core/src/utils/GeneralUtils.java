package utils;

public class GeneralUtils {
	/**
	 * Returns the number of seconds per frame given the number of key frames of an animation and desired duration of an animation
	 */
	public static float calulateAnimationFrameDuration(int keyFrameCount, float desiredAnimationDuration) {
		return (float)keyFrameCount/desiredAnimationDuration;
	}
}
