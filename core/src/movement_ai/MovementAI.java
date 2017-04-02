package movement_ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.miv.Options;

import audio.Audio;
import dungeons.Dungeon;
import factories.EntityFactory;

public abstract class MovementAI {
	public static class MovementAIParams {
		protected Engine engine;
		protected Dungeon dungeon;
		protected Entity target;
		
		public MovementAIParams(Engine engine, Dungeon dungeon, Entity target) {
			this.engine = engine;
			this.dungeon = dungeon;
			this.target = target;
		}
	}
	
	protected Engine engine;
	protected Dungeon dungeon;
	
	// The number of tiles any entity with PlayerComponent must
	// come within the enemy entity for the enemy entity
	// to activate its AI
	protected int activationRadiusInTiles;
	
	protected boolean activated;
	
	protected Entity target;
	protected Entity self;
		
	public MovementAI(MovementAIParams params, Entity self, int activationRadiusInTiles) {
		this.engine = params.engine;
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
