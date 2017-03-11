package factories;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.miv.Movement.Direction;

import components.AnimationComponent;
import components.AttackComponent;
import components.EnemyComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.PlayerComponent;
import data.AnimationData;
import data.AnimationLoader;
import data.AttackData;
import data.AttackLoader;
import data.HitboxData;
import data.HitboxData.HitboxType;
import graphics.Images;

public class EntityFactory {
	public static class EntityData {
		private String enemyName;
		private Point mapPosition;
		
		public EntityData(String enemyName, Point mapPosition) {
			this.enemyName = enemyName;
			this.mapPosition = mapPosition;
		}
	}
	
	private Images images;
	private HashMap<String, AnimationData> animationsData;
	private HashMap<String, AttackData> attacksData;
	private Engine engine;
	
	public EntityFactory(Images images, AnimationLoader animationLoader, AttackLoader attackLoader, Engine engine) {
		this.images = images;
		animationsData = animationLoader.getAnimationsData();
		attacksData = attackLoader.getAttacksData();
		this.engine = engine;
	}
	
	/**
	 * Spawns an entity whose sole purpose is to show an animation on a map tile
	 */
	public void spawnAnimationEntity(String animationName, Point mapPosition) {
		Entity e = new Entity();
		
		AnimationComponent animationComponent = new AnimationComponent(animationsData, "none");
		e.add(animationComponent);
		e.add(new ImageComponent(mapPosition));
		
		animationComponent.startAnimation(animationName, PlayMode.NORMAL);
		animationComponent.setRemoveEntityOnAnimationFinish(true);
		
		engine.addEntity(e);
	}
	
	/**
	 * TODO
	 */
	public Entity createEnemy(EntityData entityData, float bpm) {
		Entity e = new Entity();
		
		e.add(new EnemyComponent());
		
		e.add(new AnimationComponent(animationsData, entityData.enemyName + "_idle"));
		e.add(new ImageComponent(entityData.enemyName, createDirectionalSprites(entityData.enemyName), entityData.mapPosition));
		
		e.add(new AttackComponent(attacksData));
		
		return e;
	}
	
	public Entity createPlayer(Point startingMapPosition, float bpm) {
		Entity e = new Entity();
		
		HitboxType[][] hitbox = new HitboxType[1][1];
		hitbox[0][0] = HitboxType.TangibleAttackableAttackOrigin;
		HitboxData hitboxData = new HitboxData(hitbox);
		e.add(new HitboxComponent(hitboxData, startingMapPosition));
		
		e.add(new ImageComponent("player", createDirectionalSprites("player"), startingMapPosition));
		
		e.add(new AnimationComponent(animationsData, "player_idle"));
		e.add(new AttackComponent(attacksData));
		
		PlayerComponent playerComponent = new PlayerComponent();
		playerComponent.setWeaponEquipped("player_sword");
		e.add(playerComponent);
		
		return e;
	}
	
	private HashMap<Direction, Sprite> createDirectionalSprites(String spriteName) {
		HashMap<Direction, Sprite> directionalSprites = new HashMap<Direction, Sprite>();
		directionalSprites.put(Direction.Right, images.loadSprite(spriteName));
		
		Sprite leftSprite = new Sprite(images.loadSprite(spriteName));
		leftSprite.flip(true, false);
		directionalSprites.put(Direction.Left, leftSprite);
		return directionalSprites;
	}
}
