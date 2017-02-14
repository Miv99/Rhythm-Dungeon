package components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
	private float health;
	private float maxHealth;
	private float defense;
	
	public HealthComponent(float maxHealth, float defense) {
		health = maxHealth;
		this.maxHealth = maxHealth;
		this.defense = defense;
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
	
	public float getDefense() {
		return defense;
	}
}
