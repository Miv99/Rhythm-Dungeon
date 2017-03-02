package factories;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Entity;
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
	private Images images;
	private HashMap<String, AnimationData> animationsData;
	private HashMap<String, AttackData> attacksData;
	
	public EntityFactory(Images images, AnimationLoader animationLoader, AttackLoader attackLoader) {
		this.images = images;
		animationsData = animationLoader.getAnimationsData();
		attacksData = attackLoader.getAttacksData();
	}
	
	/**
	 * TODO
	 */
	public Entity createEnemy(String enemyName, float bpm) {
		Entity e = new Entity();
		
		e.add(new EnemyComponent());
		
		e.add(new AnimationComponent(animationsData, enemyName + "_idle"));
		
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
