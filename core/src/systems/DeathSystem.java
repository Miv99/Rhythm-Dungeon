package systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.miv.ComponentMappers;
import com.miv.Death;

import components.HealthComponent;

public class DeathSystem extends EntitySystem {
	private Engine engine;
	private ImmutableArray<Entity> entities;
	
	public DeathSystem(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(HealthComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}

	@Override
	public void update(float deltaTime) {
		for(Entity e : entities) {
			HealthComponent healthComponent = ComponentMappers.healthMapper.get(e);
			
			if(healthComponent.getHealth() <= 0) {
				Death.killEntity(engine, e);
			}
		}
	}
}
