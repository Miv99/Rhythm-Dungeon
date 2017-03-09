package systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.Options;

import components.AttackComponent;
import special_tiles.WarningTile;

public class TileWarningSystem extends EntitySystem {
	private ShapeRenderer shapeRenderer;
	private ImmutableArray<Entity> entities;
	private Array<WarningTile> deletionQueue;
	
	public TileWarningSystem() {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		
		deletionQueue = new Array<WarningTile>();
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(AttackComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}

	@Override
	public void update(float deltaTime) {
		shapeRenderer.begin();
		shapeRenderer.set(ShapeType.Filled);
		
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		
		for(Entity e : entities) {
			AttackComponent attack = ComponentMappers.attackMapper.get(e);
			
			for(WarningTile tile : attack.getWarningTiles()) {
				// Change alpha of red color depending on how many beats remain until attack hits the warning tile
				shapeRenderer.setColor(0.72f, 0f, 0f, 
						((tile.getMaxTimeUntilAttackInBeats() - tile.getTimeUntilAttackInBeats())/tile.getMaxTimeUntilAttackInBeats() * 0.8f) + 0.2f);
				
				shapeRenderer.rect(tile.getX() * Options.TILE_SIZE, tile.getY() * Options.TILE_SIZE, Options.TILE_SIZE, Options.TILE_SIZE);
								
				if(tile.getTimeUntilAttackInBeats() <= 0) {
					deletionQueue.add(tile);
				}
			}
			
			attack.getWarningTiles().removeAll(deletionQueue, false);
			deletionQueue.clear();
		}
		
		shapeRenderer.end();
		Gdx.gl.glDisable(GL30.GL_BLEND);
	}
	
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
}
