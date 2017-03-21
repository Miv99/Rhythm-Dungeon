package components;

import com.badlogic.ashley.core.Component;

import entity_ai.EntityAI;

public class EntityAIComponent implements Component {
	private EntityAI entityAI;
	
	public EntityAIComponent(EntityAI entityAI) {
		this.entityAI = entityAI;
	}
	
	public void setEnemyAI(EntityAI entityAI) {
		this.entityAI = entityAI;
	}
	
	public EntityAI getEntityAI() {
		return entityAI;
	}
}
