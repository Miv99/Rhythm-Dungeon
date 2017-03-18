package utils;

import java.awt.Rectangle;
import java.util.Random;

import com.badlogic.gdx.utils.Array;

import data.AttackData.TileAttackData;
import data.HitboxData.HitboxType;

/**
 * WARNING: All my 2D arrays are (cols x rows)
 */
public class GeneralUtils {
	/**
	 * Checks if the rectangle intersects with any of the rectangles in the array
	 */
	public static boolean intersects(Rectangle rect, Array<Rectangle> checkedAgainst) {
		for(Rectangle r : checkedAgainst) {
			if(r.intersects(rect)) {
				return true;
			}
		}
		return false;
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
	
	public static HitboxType[][] verticallyFlipArray(HitboxType[][] array) {
		HitboxType[][] newArray = new HitboxType[array.length][array[0].length];

		for(int x = 0; x < array.length; x++) {
			for(int y = 0; y < newArray[x].length; y++) {
				newArray[x][y] = array[x][newArray[x].length - y - 1];
			}
		}
		
		return newArray;
	}
	
	public static TileAttackData[][] verticallyFlipArray(TileAttackData[][] array) {
		TileAttackData[][] newArray = new TileAttackData[array.length][array[0].length];

		for(int x = 0; x < array.length; x++) {
			for(int y = 0; y < newArray[x].length; y++) {
				newArray[x][y] = array[x][newArray[x].length - y - 1];
			}
		}
		
		return newArray;
	}
	
	public static HitboxType[][] horizontallyFlipArray(HitboxType[][] array) {
		HitboxType[][] newArray = new HitboxType[array.length][array[0].length];

		for(int x = 0; x < newArray.length; x++) {
			for(int y = 0; y < newArray[x].length; y++) {
				newArray[x][y] = array[newArray.length - x - 1][y];
			}
		}
		
		return newArray;
	}
	
	public static TileAttackData[][] horizontallyFlipArray(TileAttackData[][] array) {
		TileAttackData[][] newArray = new TileAttackData[array.length][array[0].length];

		for(int x = 0; x < newArray.length; x++) {
			for(int y = 0; y < newArray[x].length; y++) {
				newArray[x][y] = array[newArray.length - x - 1][y];
			}
		}
		
		return newArray;
	}
	
	public static TileAttackData[][] rotateClockwise(TileAttackData[][] array) {
		TileAttackData[][] newArray = new TileAttackData[array[0].length][array.length];
		
		for(int x = 0; x < newArray.length; x++) {
			for(int y = 0; y < newArray[x].length; y++) {
				newArray[x][y] = array[newArray[x].length - y - 1][x];
			}
		}
		
		return newArray;
	}
}
