package enemy_ai;

public abstract class EnemyAI {
	// The number of tiles any entity with PlayerComponent must
	// come within the enemy entity for the enemy entity
	// to activate its AI
	private int activationRadiusInTiles;
	
	private boolean activated;
	
	// Triggered when player comes within activation range of the enemy entity
	public abstract void onActivation();
	
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
