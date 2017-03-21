package components;

import com.badlogic.ashley.core.Component;

import entity_ai.EntityAI;

public class FriendlyAIComponent implements Component {
	private EntityAI entityAI;
	
	public void getEnemyAI(EntityAI entityAI) {
		this.entityAI = entityAI;
	}
	
	public EntityAI getEntityAI() {
		return entityAI;
	}
}
