package components;

import com.badlogic.ashley.core.Component;

import entity_ai.EntityAI;

public class FriendlyAIComponent implements Component {
	private EntityAI entityAI;
	
	public void setEnemyAI(EntityAI entityAI) {
		this.entityAI = entityAI;
	}
	
	public EntityAI getEnemyAI() {
		return entityAI;
	}
}
