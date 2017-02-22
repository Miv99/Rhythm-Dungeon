package factories;

import com.badlogic.ashley.core.Entity;

import components.EnemyComponent;
import graphics.Images;

public class EntityFactory {
	private Images images;
	
	public EntityFactory(Images images) {
		this.images = images;
	}
	
	/**
	 * TODO
	 */
	public Entity createEnemy(String enemyName) {
		Entity e = new Entity();
		
		e.add(new EnemyComponent());
		
		return e;
	}
	
	public Entity createPlayer() {
		Entity e = new Entity();
		
		return e;
	}
}
