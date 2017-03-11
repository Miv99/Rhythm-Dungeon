package dungeons;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class Floor {
	// Disables all actions for all entities (eg movement, attacks)
	private boolean actionsDisabled;
	private Tile[][] tiles;
	// All non-player entities that are spawned on entering the floor
	private Array<Entity> entitiesToBeSpawned = new Array<Entity>();
	
	public Floor(int xSize, int ySize) {
		tiles = new Tile[xSize][ySize];
	}
	
	public void setActionsDisabled(boolean actionsDisabled) {
		this.actionsDisabled = actionsDisabled;
	}
	
	public Array<Entity> getEntitiesToBeSpawned() {
		return entitiesToBeSpawned;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public boolean getActionsDisabled() {
		return actionsDisabled;
	}
}
