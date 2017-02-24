package factories;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.miv.Movement.Direction;

import components.AnimationComponent;
import components.EnemyComponent;
import components.HitboxComponent;
import components.ImageComponent;
import data.AnimationData;
import data.AnimationLoader;
import data.HitboxData;
import data.HitboxData.HitboxType;
import graphics.Images;

public class EntityFactory {
	private Images images;
	private HashMap<String, AnimationData> animationsData;
	
	public EntityFactory(Images images, AnimationLoader animationLoader) {
		this.images = images;
		animationsData = animationLoader.getAnimationsData();
	}
	
	/**
	 * TODO
	 */
	public Entity createEnemy(String enemyName, float bpm) {
		Entity e = new Entity();
		
		e.add(new EnemyComponent());
		
		e.add(new AnimationComponent(animationsData));
		
		return e;
	}
	
	public Entity createPlayer(Point startingMapPosition, float bpm) {
		Entity e = new Entity();
		
		HitboxType[][] hitbox = new HitboxType[1][1];
		hitbox[0][0] = HitboxType.TangibleAttackable;
		HitboxData hitboxData = new HitboxData(hitbox);
		e.add(new HitboxComponent(hitboxData, startingMapPosition));
		
		e.add(new ImageComponent("player", createDirectionalSprites("player"), startingMapPosition));
		
		e.add(new AnimationComponent(animationsData));
		
		return e;
	}
	
	private HashMap<Direction, Sprite> createDirectionalSprites(String spriteName) {
		HashMap<Direction, Sprite> directionalSprites = new HashMap<Direction, Sprite>();
		directionalSprites.put(Direction.Left, images.loadSprite(spriteName + "_" + Direction.Left.getStringRepresentation()));
		directionalSprites.put(Direction.Right, images.loadSprite(spriteName + "_" + Direction.Right.getStringRepresentation()));
		return directionalSprites;
	}
}
