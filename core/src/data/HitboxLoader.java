package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.miv.Options;
import com.miv.Movement.Direction;

import data.HitboxData.HitboxType;

import utils.FileUtils;
import utils.GeneralUtils;

public class HitboxLoader {
	// HashMap of entityName:hitboxData
	private HashMap<String, HitboxData> hitboxesData = new HashMap<String, HitboxData>();
	
	public void loadHitboxes() {
		// Load text file containing the animations' metadata
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.hitboxesDataFilePath);
		
		HashMap<Character, HitboxType> hitboxTypeMap = new HashMap<Character, HitboxType>();
		int lineCount = 1;
		for(int i = 0; i < HitboxType.values().length; i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("'")) {
					hitboxTypeMap.put(line.charAt(1), HitboxType.valueOf(line.substring(4)));
				} else {
					System.out.println("Hitboxes data invalid format at line " + lineCount);
				}
			} catch(IllegalArgumentException e) {
				System.out.println("Hitboxes data invalid hitbox type at line " + lineCount);
				System.out.println("Valid types: ");
				for(HitboxType type : HitboxType.values()) {
					System.out.println(type.toString());
				}
				e.printStackTrace();
			}
			lineCount++;
		}
		
		String hitboxName = "";
		boolean recording = false;
		Array<String> recordingBuffer = new Array<String>();
		for(int i = lineCount - 1; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("name=")) {
					hitboxName = line.replace("name=", "");
				} else if(line.equals("[")) {
					recording = true;
				} else if(line.equals("]")) {
					recording = false;
				} else if(recording) {
					recordingBuffer.add(line);
				} else if(line.equals("")) {
					if(!hitboxName.equals("")) {
						HitboxType[][] rightFacingHitbox = parseHitboxStrings(hitboxTypeMap, recordingBuffer);
						HitboxData rightFacingHitboxData = new HitboxData(rightFacingHitbox);
						hitboxesData.put(hitboxName + "_" + Direction.RIGHT.getStringRepresentation(), rightFacingHitboxData);
						
						HitboxType[][] leftFacingHitbox = GeneralUtils.horizontallyFlipArray(rightFacingHitbox);
						HitboxData leftFacingHitboxData = new HitboxData(leftFacingHitbox);
						hitboxesData.put(hitboxName + "_" + Direction.LEFT.getStringRepresentation(), leftFacingHitboxData);
						
						hitboxName = "";
						recording = false;
						recordingBuffer.clear();
					}
				} else {
					System.out.println("Hitboxes data invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Hitboxes data invalid value at line " + lineCount);
				e.printStackTrace();
			} catch(IllegalArgumentException e) {
				System.out.println("Hitboxes data unmapped character at line " + lineCount);
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!hitboxName.equals("")) {
			HitboxData hitboxData = new HitboxData(parseHitboxStrings(hitboxTypeMap, recordingBuffer));
			hitboxesData.put(hitboxName, hitboxData);
		}
	}
	
	public HitboxType[][] parseHitboxStrings(HashMap<Character, HitboxType> hitboxTypeMap, Array<String> strings) throws IllegalArgumentException {
		HitboxType[][] hitbox = new HitboxType[strings.first().length()][strings.size];
		for(int x = 0; x < hitbox.length; x++) {
			for(int y = 0; y < hitbox[x].length; y++) {
				char c = strings.get(y).charAt(x);
				if(hitboxTypeMap.containsKey(c)) {
					hitbox[x][y] = hitboxTypeMap.get(c);
				} else {
					throw new IllegalArgumentException();
				}
			}
		}
		return hitbox;
	}
	
	public HashMap<String, HitboxData> getHitboxesData() {
		return hitboxesData;
	}
}
