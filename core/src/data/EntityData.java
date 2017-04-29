package data;

public class EntityData {
	private String entityName;
	private String hitboxName;
	private String spriteName;
	private String hurtSoundName;
	private String deathSoundName;
	private String tileBreakAnimationName;
	private String deathAnimationName;
	
	private boolean isEnemy;
	
	private boolean isPlayer;
	private String playerAttackName;
	
	public EntityData(String entityName, String hitboxName, String spriteName, String hurtSoundName, String deathSoundName, String playerAttackName, String tileBreakAnimationName, String deathAnimationName) {
		this.entityName = entityName;
		this.hitboxName = hitboxName;
		this.spriteName = spriteName;
		this.hurtSoundName = hurtSoundName;
		this.deathSoundName = deathSoundName;
		this.tileBreakAnimationName = tileBreakAnimationName;
		this.deathAnimationName = deathAnimationName;
		
		isPlayer = true;
		this.playerAttackName = playerAttackName;
		
		isEnemy = false;
	}
	
	public EntityData(String entityName, String hitboxName, String spriteName, String hurtSoundName, String deathSoundName, boolean isEnemy, String tileBreakAnimationName, String deathAnimationName) {
		this.entityName = entityName;
		this.hitboxName = hitboxName;
		this.spriteName = spriteName;
		this.hurtSoundName = hurtSoundName;
		this.deathSoundName = deathSoundName;
		this.isEnemy = isEnemy;
		this.tileBreakAnimationName = tileBreakAnimationName;
		this.deathAnimationName = deathAnimationName;
		
		isPlayer = false;
	}
	
	public String getTileBreakAnimationName() {
		return tileBreakAnimationName;
	}
	
	public String getHurtSoundName() {
		return hurtSoundName;
	}
	
	public String getDeathSoundName() {
		return deathSoundName;
	}
	
	public boolean isEnemy() {
		return isEnemy;
	}
	
	public boolean isPlayer() {
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
	
	public String getDeathAnimationName() {
		return deathAnimationName;
	}
}
