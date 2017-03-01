package components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
	private float health;
	private float maxHealth;
	
	public HealthComponent(float maxHealth) {
		health = maxHealth;
		this.maxHealth = maxHealth;
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
}
