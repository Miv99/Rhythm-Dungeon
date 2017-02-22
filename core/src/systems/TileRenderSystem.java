package systems;

import java.awt.Point;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miv.Options;

import dungeons.Tile;

public class TileRenderSystem {
	private Tile[][] tiles;
	private SpriteBatch batch;
	
	public TileRenderSystem() {
		batch = new SpriteBatch();
	}
	
	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	public void update(float deltaTime) {
		if(tiles != null) {
			for(Tile[] col : tiles) {
				for(Tile tile : col) {
					Point mapPosition = tile.getMapPosition();
					batch.draw(tile.getSprite(), mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
				}
			}
		}
	}
}
