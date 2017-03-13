package factories;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.miv.ComponentMappers;
import com.miv.Movement.Direction;

import components.AnimationComponent;
import components.AttackComponent;
import components.EnemyComponent;
import components.HealthComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.PlayerComponent;
import data.AnimationData;
import data.AnimationLoader;
import data.AttackData;
import data.AttackLoader;
import data.EntityData;
import data.HitboxData;
import data.HitboxData.HitboxType;
import data.HitboxLoader;
import dungeons.Tile;
import graphics.Images;

public class EntityFactory {
	private Images images;
	private HashMap<String, AnimationData> animationsData;
	private HashMap<String, AttackData> attacksData;
	private HashMap<String, HitboxData> hitboxesData;
	private Engine engine;
	
	public EntityFactory(Images images, HashMap<String, AnimationData> animationsData, 
			HashMap<String, AttackData> attacksData, HashMap<String, HitboxData> hitboxesData, Engine engine) {
		this.images = images;
		this.animationsData = animationsData;
		this.attacksData = attacksData;
		this.hitboxesData = hitboxesData;
		this.engine = engine;
	}
	
	public void spawnEntity(Tile[][] mapTiles, Entity e) {
		// Update occupants fields in tiles the entity resides in
		if(ComponentMappers.hitboxMapper.has(e)) {
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(e);
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			Point position = hitboxComponent.getMapPosition();
			
			for(int x = position.x; x < position.x + hitbox.length; x++) {
				for(int y = position.y; y < position.y + hitbox[x - position.x].length; y++) {
					if(hitbox[x - position.x][y - position.y].getTangible()) {
						mapTiles[x][y].getTangibleOccupants().add(e);
					}
					if(hitbox[x - position.x][y - position.y].getAttackable()) {
						mapTiles[x][y].getAttackableOccupants().add(e);
					}
				}
			}
		}
		
		engine.addEntity(e);
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
	
	public Entity createEntity(EntityData entityData, Point mapPosition, int healthPoints) {
		Entity e = new Entity();
		
		if(entityData.getIsPlayer()) {
			PlayerComponent playerComponent = new PlayerComponent();
			playerComponent.setWeaponEquipped(entityData.getPlayerAttackName());
			e.add(playerComponent);
		}
		if(entityData.getIsEnemy()) {
			e.add(new EnemyComponent());
		}
		
		e.add(new HitboxComponent(entityData.getHitboxName(), hitboxesData, mapPosition));
		e.add(new AttackComponent(attacksData));
		e.add(new HealthComponent(healthPoints * 4));
		
		e.add(new ImageComponent(entityData.getSpriteName(), createDirectionalSprites(entityData.getSpriteName()), mapPosition));
		if(animationsData.containsKey(entityData.getSpriteName() + "_idle_" + Direction.RIGHT.getStringRepresentation())) {
			e.add(new AnimationComponent(animationsData, entityData.getSpriteName() + "_idle"));
		}
				
		return e;
	}
	
	private HashMap<Direction, Sprite> createDirectionalSprites(String spriteName) {
		HashMap<Direction, Sprite> directionalSprites = new HashMap<Direction, Sprite>();
		directionalSprites.put(Direction.RIGHT, images.loadSprite(spriteName));
		
		Sprite leftSprite = new Sprite(images.loadSprite(spriteName));
		leftSprite.flip(true, false);
		directionalSprites.put(Direction.LEFT, leftSprite);
		return directionalSprites;
	}
}
