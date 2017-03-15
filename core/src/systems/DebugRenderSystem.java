package systems;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.miv.ComponentMappers;
import com.miv.Options;

import components.HitboxComponent;
import data.HitboxData.HitboxType;

public class DebugRenderSystem extends EntitySystem {
	private SpriteBatch batch;
	private ImmutableArray<Entity> entities;
	private Options options;
	private ShapeRenderer debugRenderer;
	
	public DebugRenderSystem(Options options) {
		this.options = options;
		batch = new SpriteBatch();
		debugRenderer = new ShapeRenderer();
		debugRenderer.setAutoShapeType(true);
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(HitboxComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}
	
	public ShapeRenderer getDebugRenderer() {
		return debugRenderer;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void update(float deltaTime) {
		if(options.isDebug()) {
			debugRenderer.begin();
			
			// Draw tangible hitboxes
			debugRenderer.setColor(Color.PINK);
			for(Entity e : entities) {
				HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(e);
				Point mapPosition = hitboxComponent.getMapPosition();
				HitboxType[][] hitbox = hitboxComponent.getHitbox();
				for(int x = 0; x < hitbox.length; x++) {
					for(int y = 0; y < hitbox[x].length; y++) {
						if(hitbox[x][y].isTangible()) {
							debugRenderer.rect((mapPosition.x + x) * Options.TILE_SIZE, (mapPosition.y + y) * Options.TILE_SIZE, Options.TILE_SIZE, Options.TILE_SIZE);
						}
						if(hitbox[x][y].isAttackOrigin()) {
							debugRenderer.circle((mapPosition.x + x)*Options.TILE_SIZE + (Options.TILE_SIZE/2), (mapPosition.y + y)*Options.TILE_SIZE + (Options.TILE_SIZE/2), Options.TILE_SIZE/2);
						}
					}
				}
			}
			
			debugRenderer.end();
		}
	}
}
