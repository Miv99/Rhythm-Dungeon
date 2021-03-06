package entity_ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.miv.Options;

import audio.Audio;
import dungeons.Dungeon;
import factories.EntityFactory;

public abstract class EntityAI {
	public static class EntityAIParams {
		protected Engine engine;
		protected EntityFactory entityFactory;
		protected Entity target;
		protected Options options;
		protected Dungeon dungeon;
		protected Audio audio;
		
		public EntityAIParams(Engine engine, EntityFactory entityFactory, Options options, Audio audio, Dungeon dungeon, Entity target) {
			this.engine = engine;
			this.entityFactory = entityFactory;
			this.options = options;
			this.audio = audio;
			this.dungeon = dungeon;
			this.target = target;
		}
	}
	
	protected Options options;
	protected Engine engine;
	protected Dungeon dungeon;
	protected EntityFactory entityFactory;
	protected Audio audio;
	
	// The number of tiles any entity with PlayerComponent must
	// come within the enemy entity for the enemy entity
	// to activate its AI
	protected int activationRadiusInTiles;
	
	protected boolean activated;
	
	protected Entity target;
	protected Entity self;
		
	public EntityAI(EntityAIParams params, Entity self, int activationRadiusInTiles) {
		this.engine = params.engine;
		this.entityFactory = params.entityFactory;
		this.options = params.options;
		this.audio = params.audio;
		this.dungeon = params.dungeon;
		this.target = params.target;
		this.self = self;
		this.activationRadiusInTiles = activationRadiusInTiles;
	}
	
	// Triggered when player comes within activation range of the enemy entity
	protected abstract void onActivation();
	
	// Triggered whenever there is a new beat from the ActionBar
	public abstract void onNewBeat();
	
	public void setActivationRadius(int activationRadiusInTiles) {
		this.activationRadiusInTiles = activationRadiusInTiles;
	}
	
	public void setActivated(boolean activated) {
		if(activated && !this.activated) {
			onActivation();
		}
		this.activated = activated;
	}
	
	public int getActivationRadiusInTiles() {
		return activationRadiusInTiles;
	}
	
	public boolean isActivated() {
		return activated;
	}
}
