package utils;

public class GeneralUtils {
	/**
	 * Returns the number of seconds per frame given the number of key frames of an animation and desired duration of an animation
	 */
	public static float calulateAnimationFrameDuration(int keyFrameCount, float desiredAnimationDuration) {
		return (float)keyFrameCount/desiredAnimationDuration;
	}
	
	public static float toFloat(String string) throws NumberFormatException {
		try {
			if(string.contains("/")) {
				String[] rat = string.split("/");
				return Float.parseFloat(rat[0]) / Float.parseFloat(rat[1]);
			} else {
				return Float.parseFloat(string);
			}
		} catch(NumberFormatException e) {
			throw e;
		}
	}
	
	public static String removeExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
}
