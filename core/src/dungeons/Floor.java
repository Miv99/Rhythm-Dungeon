package dungeons;

public class Floor {
	private Tile[][] tiles;
	
	public Floor(int xSize, int ySize) {
		tiles = new Tile[xSize][ySize];
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
}
