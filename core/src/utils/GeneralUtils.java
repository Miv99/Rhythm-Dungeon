package utils;

public class GeneralUtils {
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
	
	public static Object[][] horizontallyFlipArray(Object[][] array) {
		for(int i = 0; i < (array.length / 2); i++) {
			Object[] temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
		
		return array;
	}
	
	/**
	 * @param array - must be a square
	 */
	public static Object[][] verticallyFlipArray(Object[][] array) {
		//TODO - muy importante
		
		return array;
	}
	
	public static Object[][] rotateCounterClockwise(Object[][] array) {
		//TODO - muy importante
		
		return array;
	}
}
