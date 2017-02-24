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
		for(int x = 0; x < array.length/2; x++) {
			Object[] temp = array[x];
			array[x] = array[array.length - x];
			array[array.length - x] = array[x];
		}
		
		return array;
	}
}
