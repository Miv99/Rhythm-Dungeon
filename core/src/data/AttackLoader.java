package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.miv.EntityActions.Direction;
import com.miv.Options;

import components.EnemyComponent;
import components.HealthComponent;
import components.PlayerComponent;
import components.TileComponent;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import utils.FileUtils;
import utils.GeneralUtils;

public class AttackLoader {
	// HashMap of attackName:attackData
	private HashMap<String, AttackData> attacksData = new HashMap<String, AttackData>();
	
	public void loadAttacks() {
		// Load text file containing the attacks
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.attacksDataFilePath);
		
		String attackName = "";
		String animationName = "";
		AttackDirectionDeterminant directionDeterminant = null;
		Class<? extends Component> hittableRequirement = null;
		boolean autoRotate = false;
		int recordingAttackPartIndex = 0;
		boolean recordingRight = false;
		boolean recordingLeft = false;
		boolean recordingUp = false;
		boolean recordingDown = false;
		int attackDelay = 0;
		boolean warnTilesBeforeAttack = false;
		int disabledAttackTime = 0;
		int disabledMovementTime = 0;
		Array<String> recordingBuffer = new Array<String>();
		HashMap<Character, String> animationMap = new HashMap<Character, String>();
		HashMap<Integer, TileAttackData[][]> tileAttackDataRight = new HashMap<Integer, TileAttackData[][]>();
		HashMap<Integer, TileAttackData[][]> tileAttackDataLeft = new HashMap<Integer, TileAttackData[][]>();
		HashMap<Integer, TileAttackData[][]> tileAttackDataUp = new HashMap<Integer, TileAttackData[][]>();
		HashMap<Integer, TileAttackData[][]> tileAttackDataDown = new HashMap<Integer, TileAttackData[][]>();
		int lineCount = 1;
		for(int i = 0; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("name=")) {
					attackName = line.replace("name=", "");
				} else if(line.startsWith("direction=")) {
					directionDeterminant = AttackDirectionDeterminant.valueOf(line.replace("direction=", ""));
				} else if(line.startsWith("animation_name=")) {
					animationName = line.replace("animation_name=", "");
				} else if(line.startsWith("auto_rotate=")) {
					autoRotate = Boolean.valueOf(line.replace("auto_rotate=", ""));
				} else if(line.startsWith("warn_tiles=")) {
					warnTilesBeforeAttack = Boolean.valueOf(line.replace("warn_tiles=", ""));
				} else if(line.startsWith("can_hit=")) {
					hittableRequirement = parseHittableRequirementString(line.replace("can_hit=", ""));
				} else if(line.startsWith("right")) {
					recordingRight = true;
					recordingAttackPartIndex = Integer.valueOf(line.substring(5, line.lastIndexOf('='))) - 1;
				} else if(line.startsWith("left")) {
					recordingLeft = true;
					recordingAttackPartIndex = Integer.valueOf(line.substring(4, line.lastIndexOf('='))) - 1;
				} else if(line.startsWith("up")) {
					recordingUp = true;
					recordingAttackPartIndex = Integer.valueOf(line.substring(2, line.lastIndexOf('='))) - 1;
				} else if(line.startsWith("down")) {
					recordingDown = true;
					recordingAttackPartIndex = Integer.valueOf(line.substring(4, line.lastIndexOf('='))) - 1;
				} else if(line.startsWith("attack_delay=")) {
					attackDelay = Integer.valueOf(line.replace("attack_delay=", ""));
				} else if(line.startsWith("]")) {
					if(recordingRight) {
						tileAttackDataRight.put(recordingAttackPartIndex, parseTileAttackDataStrings(animationMap, recordingBuffer));
						recordingRight = false;
					} else if(recordingLeft) {
						tileAttackDataLeft.put(recordingAttackPartIndex, parseTileAttackDataStrings(animationMap, recordingBuffer));
						recordingLeft = false;
					} else if(recordingUp) {
						tileAttackDataUp.put(recordingAttackPartIndex, parseTileAttackDataStrings(animationMap, recordingBuffer));
						recordingUp = false;
					} else if(recordingDown) {
						tileAttackDataDown.put(recordingAttackPartIndex, parseTileAttackDataStrings(animationMap, recordingBuffer));
						recordingDown = false;
					}
				} else if(recordingRight
						|| recordingLeft
						|| recordingUp
						|| recordingDown) {
					recordingBuffer.add(line);
				} else if(line.startsWith("'")) {
					animationMap.put(line.charAt(1), line.substring(4, line.length()));
				} else if(line.startsWith("disable_movement=")) {
					disabledMovementTime = Integer.valueOf(line.replaceAll("disable_movement=", ""));
				} else if(line.startsWith("disable_attack=")) {
					disabledAttackTime = Integer.valueOf(line.replaceAll("disable_attack=", ""));
				} else if(line.equals("")) {
					if(!attackName.equals("")) {						
						if(autoRotate) {
							AttackData attackData = new AttackData(hittableRequirement, attackDelay, warnTilesBeforeAttack, directionDeterminant, 
									disabledAttackTime, disabledMovementTime, animationName, GeneralUtils.toOrderedArrayList(tileAttackDataRight));
							attacksData.put(attackName, attackData);
						} else {
							HashMap<Direction, ArrayList<TileAttackData[][]>> directionalTileAttackData = new HashMap<Direction, ArrayList<TileAttackData[][]>>();
							directionalTileAttackData.put(Direction.RIGHT, GeneralUtils.toOrderedArrayList(tileAttackDataRight));
							directionalTileAttackData.put(Direction.LEFT, GeneralUtils.toOrderedArrayList(tileAttackDataLeft));
							directionalTileAttackData.put(Direction.UP, GeneralUtils.toOrderedArrayList(tileAttackDataUp));
							directionalTileAttackData.put(Direction.DOWN, GeneralUtils.toOrderedArrayList(tileAttackDataDown));
							
							AttackData attackData = new AttackData(hittableRequirement, attackDelay, warnTilesBeforeAttack, directionDeterminant, 
									disabledAttackTime, disabledMovementTime, animationName, directionalTileAttackData);
							attacksData.put(attackName, attackData);
						}
						
						attackName = "";
						animationName = "";
						directionDeterminant = null;
						autoRotate = false;
						recordingRight = false;
						recordingLeft = false;
						recordingUp = false;
						recordingDown = false;
						recordingBuffer.clear();
						attackDelay = 0;
						warnTilesBeforeAttack = false;
						disabledMovementTime = 0;
						disabledAttackTime = 0;
						animationMap.clear();
						tileAttackDataRight.clear();
						tileAttackDataLeft.clear();
						tileAttackDataUp.clear();
						tileAttackDataDown.clear();
					}
				} else {
					System.out.println("Attacks data invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Attacks data invalid value at line " + lineCount + " (expecting number)");
				e.printStackTrace();
			} catch(IllegalArgumentException e) {
				System.out.println("Attacks data invalid value at line " + lineCount + " (no such AttackDirectionDeterminant)");
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!attackName.equals("")) {
			if(autoRotate) {
				AttackData attackData = new AttackData(hittableRequirement, attackDelay, warnTilesBeforeAttack, directionDeterminant, 
						disabledAttackTime, disabledMovementTime, animationName, GeneralUtils.toOrderedArrayList(tileAttackDataRight));
				attacksData.put(attackName, attackData);
			} else {
				HashMap<Direction, ArrayList<TileAttackData[][]>> directionalTileAttackData = new HashMap<Direction, ArrayList<TileAttackData[][]>>();
				directionalTileAttackData.put(Direction.RIGHT, GeneralUtils.toOrderedArrayList(tileAttackDataRight));
				directionalTileAttackData.put(Direction.LEFT, GeneralUtils.toOrderedArrayList(tileAttackDataLeft));
				directionalTileAttackData.put(Direction.UP, GeneralUtils.toOrderedArrayList(tileAttackDataUp));
				directionalTileAttackData.put(Direction.DOWN, GeneralUtils.toOrderedArrayList(tileAttackDataDown));
				
				AttackData attackData = new AttackData(hittableRequirement, attackDelay, warnTilesBeforeAttack, directionDeterminant, 
						disabledAttackTime, disabledMovementTime, animationName, directionalTileAttackData);
				attacksData.put(attackName, attackData);
			}
		}
	}
	
	private Class<? extends Component> parseHittableRequirementString(String string) {
		if(string.toLowerCase().equals("all")) {
			return HealthComponent.class;
		} else if(string.toLowerCase().equals("player")) {
			return PlayerComponent.class;
		} else if(string.toLowerCase().equals("enemy")) {
			return EnemyComponent.class;
		} else if(string.toLowerCase().equals("tile")) {
			return TileComponent.class;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private TileAttackData[][] parseTileAttackDataStrings(HashMap<Character, String> animationMap, Array<String> strings) {
		TileAttackData[][] tileAttackData = new TileAttackData[strings.first().length()][strings.size];
		for(int x = 0; x < tileAttackData.length; x++) {
			for(int y = 0; y < tileAttackData[x].length; y++) {
				char c = strings.get(y).charAt(x);
				if(c == '#') {
					if(animationMap.containsKey('#')) {
						tileAttackData[x][y] = new TileAttackData(true, true, animationMap.get('#'));
					} else {
						tileAttackData[x][y] = new TileAttackData(true, false, "none");
					}
				} else if(c != '-' && animationMap.containsKey(c)) {
					tileAttackData[x][y] = new TileAttackData(false, true, animationMap.get(c));
				} else if(c == '-') {
					tileAttackData[x][y] = new TileAttackData(false, false, "none");
				}
			}
		}
		return tileAttackData;
	}
	
	public HashMap<String, AttackData> getAttacksData() {
		return attacksData;
	}
}
