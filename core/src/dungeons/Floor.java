package dungeons;

public class Floor {
	// Disables all actions for all entities (eg movement, attacks)
	private boolean actionsDisabled;
	private Tile[][] tiles;
	
	public Floor(int xSize, int ySize) {
		tiles = new Tile[xSize][ySize];
	}
	
	public void setActionsDisabled(boolean actionsDisabled) {
		this.actionsDisabled = actionsDisabled;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public boolean getActionsDisabled() {
		return actionsDisabled;
	}
}
