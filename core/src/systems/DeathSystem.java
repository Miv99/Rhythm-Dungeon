package systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.miv.ComponentMappers;
import com.miv.EntityActions;

import audio.Audio;
import components.HealthComponent;
import dungeons.Floor;

public class DeathSystem extends EntitySystem {
	private Engine engine;
	private Floor floor;
	private Audio audio;
	private ImmutableArray<Entity> entities;
	
	public DeathSystem(Engine engine, Audio audio) {
		this.engine = engine;
		this.audio = audio;
	}
	
	public void setFloor(Floor floor) {
		this.floor = floor;
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
				EntityActions.killEntity(audio, engine, floor, e);
			}
		}
	}
}
