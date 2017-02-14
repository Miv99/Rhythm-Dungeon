package systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miv.ComponentMappers;

import components.ImageComponent;
import utils.Point;

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

	@Override
	public void update(float deltaTime) {
		for(Entity e : entities) {
			ImageComponent image = ComponentMappers.im.get(e);
			Point position = image.getPosition();
			
			batch.draw(image.getImage(), position.getX(), position.getY());
		}
	}
}
