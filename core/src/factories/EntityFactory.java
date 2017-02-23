package factories;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;

import components.EnemyComponent;
import components.HitboxComponent;
import components.ImageComponent;
import data.HitboxData;
import data.HitboxData.HitboxType;
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
	
	public Entity createPlayer(Point startingMapPosition) {
		Entity e = new Entity();
		
		HitboxType[][] hitbox = new HitboxType[1][1];
		hitbox[0][0] = HitboxType.TangibleAttackable;
		HitboxData hitboxData = new HitboxData(hitbox);
		e.add(new HitboxComponent(hitboxData, startingMapPosition));
		
		e.add(new ImageComponent(images.getSprite("player"), startingMapPosition));
		
		return e;
	}
}
