package components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
	private String hurtSoundName;
	private String deathSoundName;
	private float health;
	private float maxHealth;
	
	public HealthComponent(float maxHealth, String hurtSoundName, String deathSoundName) {
		health = maxHealth;
		this.maxHealth = maxHealth;
		this.hurtSoundName = hurtSoundName;
		this.deathSoundName = deathSoundName;
	}
	
	public void setHealth(float health) {
		this.health = health;
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}
	
	public String getHurtSoundName() {
		return hurtSoundName;
	}

	public String getDeathSoundName() {
		return deathSoundName;
	}
}
