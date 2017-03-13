package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.miv.Options;

import utils.FileUtils;

public class EntityLoader {
	// HashMap of entityName:entityData
	private HashMap<String, EntityData> entitiesData = new HashMap<String, EntityData>();
	
	public void loadEntities() {
		// Load text file containing the animations' metadata
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.entitiesDataFilePath);
		
		String entityName = "";
		String hitboxName = "";
		String spriteName = "";
		String hurtSoundName = "";
		String deathSoundName = "";
		String playerAttackName = "";
		boolean isPlayer = false;
		boolean isEnemy = false;
		int lineCount = 1;
		for(int i = 0; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("name=")) {
					entityName = line.replace("name=", "");
				} else if(line.startsWith("player=")) {
					isPlayer = Boolean.valueOf(line.replace("player=", ""));
				} else if(line.startsWith("enemy=")) {
					isEnemy = Boolean.valueOf(line.replace("enemy=", ""));
				} else if(line.startsWith("hitbox=")) {
					hitboxName = line.replace("hitbox=", "");
				} else if(line.startsWith("sprite=")) {
					spriteName = line.replace("sprite=", "");
				} else if(line.startsWith("hurt_sound=")) {
					hurtSoundName = line.replace("hurt_sound=", "");
				} else if(line.startsWith("death_sound=")) {
					deathSoundName = line.replace("death_sound=", "");
				} else if(line.startsWith("player_attack=")) {
					playerAttackName = line.replace("player_attack=", "");
				} else if(line.equals("")) {
					if(!entityName.equals("")) {
						if(isPlayer) {
							entitiesData.put(entityName, new EntityData(entityName, hitboxName, spriteName, hurtSoundName, deathSoundName, playerAttackName));
						} else {
							entitiesData.put(entityName, new EntityData(entityName, hitboxName, spriteName, hurtSoundName, deathSoundName, isEnemy));
						}
						
						entityName = "";
						hitboxName = "";
						spriteName = "";
						hurtSoundName = "";
						deathSoundName = "";
						playerAttackName = "";
						isPlayer = false;
						isEnemy = false;
					}
				} else {
					System.out.println("Animation data invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Animation data invalid value at line " + lineCount);
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!entityName.equals("")) {
			if(isPlayer) {
				entitiesData.put(entityName, new EntityData(entityName, hitboxName, spriteName, hurtSoundName, deathSoundName, playerAttackName));
			} else {
				entitiesData.put(entityName, new EntityData(entityName, hitboxName, spriteName, hurtSoundName, deathSoundName, isEnemy));
			}
		}
	}
	
	public HashMap<String, EntityData> getEntitiesData() {
		return entitiesData;
	}
}
