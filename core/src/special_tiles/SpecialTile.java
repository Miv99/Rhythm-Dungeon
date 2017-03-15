package special_tiles;

import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class SpecialTile {
	// Sprite that is overlayed on the rendered tile
	private Sprite tileOverlay;
	
	// If true, onPlayerTrigger/onEnemyTrigger no longer get called
	private boolean deactivated;
	
	/**
	 * Called when an entity with the PlayerComponent steps on the tile
	 */
	public abstract void onPlayerTrigger();
	
	/**
	 * Called when an entity with the EnemyComponent steps on the tile
	 */
	public abstract void onEnemyTrigger();
	
	public void setTileOverlay(Sprite tileOverlay) {
		this.tileOverlay = tileOverlay;
	}
	
	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}
	
	public Sprite getTileOverlay() {
		return tileOverlay;
	}
	
	public boolean isDeactivated() {
		return deactivated;
	}
}
