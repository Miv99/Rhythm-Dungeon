package entity_ai;

import com.badlogic.ashley.core.Entity;
import com.miv.Options;

import dungeons.Dungeon;
import factories.EntityFactory;

public abstract class EntityAI {
	public static class EnemyAIParams {
		protected EntityFactory entityFactory;
		protected Entity self;
		protected Entity target;
		protected int activationRadiusInTiles;
		protected Options options;
		protected Dungeon dungeon;
		
		public EnemyAIParams(EntityFactory entityFactory, Options options, Dungeon dungeon, Entity self, Entity target, int activationRadiusInTiles) {
			this.entityFactory = entityFactory;
			this.options = options;
			this.dungeon = dungeon;
			this.self = self;
			this.target = target;
			this.activationRadiusInTiles = activationRadiusInTiles;
		}
	}
	
	protected Options options;
	protected Dungeon dungeon;
	protected EntityFactory entityFactory;
	
	// The number of tiles any entity with PlayerComponent must
	// come within the enemy entity for the enemy entity
	// to activate its AI
	protected int activationRadiusInTiles;
	
	protected boolean activated;
	
	protected Entity target;
	protected Entity self;
		
	public EntityAI(EnemyAIParams params) {
		this.entityFactory = params.entityFactory;
		this.options = params.options;
		this.dungeon = params.dungeon;
		this.target = params.target;
		this.self = params.self;
		this.activationRadiusInTiles = params.activationRadiusInTiles;
	}
	
	// Triggered when player comes within activation range of the enemy entity
	protected abstract void onActivation();
	
	// Triggered whenever there is a new beat from the ActionBar
	public abstract void onNewBeat();
	
	public void setActivationRadius(int activationRadiusInTiles) {
		this.activationRadiusInTiles = activationRadiusInTiles;
	}
	
	public void setActivated(boolean activated) {
		this.activated = activated;
		if(activated) {
			onActivation();
		}
	}
	
	public int getActivationRadiusInTiles() {
		return activationRadiusInTiles;
	}
	
	public boolean getActivated() {
		return activated;
	}
}
