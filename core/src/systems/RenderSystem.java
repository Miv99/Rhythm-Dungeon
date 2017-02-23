package systems;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miv.ComponentMappers;
import com.miv.Options;

import components.ImageComponent;

public class RenderSystem extends EntitySystem {
	private SpriteBatch batch;
	private ImmutableArray<Entity> entities;
	
	public RenderSystem() {
		batch = new SpriteBatch();
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(ImageComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {

	}
	
	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void update(float deltaTime) {
		batch.begin();
		for(Entity e : entities) {
			ImageComponent image = ComponentMappers.im.get(e);
			Point mapPosition = image.getMapPosition();
			
			batch.draw(image.getSprite(), mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
		}
		batch.end();
	}
}
