package data;

import java.awt.Point;

public class EntityData {
	private String entityName;
	private String hitboxName;
	private String spriteName;
	private String deathSoundName;
	
	private boolean isEnemy;
	
	private boolean isPlayer;
	private String playerAttackName;
	
	public EntityData(String entityName, String hitboxName, String spriteName, String deathSoundName, String playerAttackName) {
		this.entityName = entityName;
		this.hitboxName = hitboxName;
		this.spriteName = spriteName;
		this.deathSoundName = deathSoundName;
		
		isPlayer = true;
		this.playerAttackName = playerAttackName;
		
		isEnemy = false;
	}
	
	public EntityData(String entityName, String hitboxName, String spriteName, String deathSoundName, boolean isEnemy) {
		this.entityName = entityName;
		this.hitboxName = hitboxName;
		this.spriteName = spriteName;
		this.deathSoundName = deathSoundName;
		this.isEnemy = isEnemy;
		
		isPlayer = false;
	}
	
	public String getDeathSoundName() {
		return deathSoundName;
	}
	
	public boolean getIsEnemy() {
		return isEnemy;
	}
	
	public boolean getIsPlayer() {
		return isPlayer;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getHitboxName() {
		return hitboxName;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public String getPlayerAttackName() {
		return playerAttackName;
	}
}
