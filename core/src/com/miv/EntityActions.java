package com.miv;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;

import audio.Audio;
import components.AnimationComponent;
import components.AttackComponent;
import components.EntityAIComponent;
import components.FriendlyAIComponent;
import components.HealthComponent;
import components.HitboxComponent;
import components.ImageComponent;
import components.PlayerComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import data.HitboxData.HitboxType;
import dungeons.Dungeon;
import dungeons.Floor;
import dungeons.Tile;
import entity_ai.EntityAI;
import factories.EntityFactory;
import special_tiles.SpecialTile;
import special_tiles.WarningTile;
import utils.MapUtils;

public class EntityActions {
	public enum Direction {
		UP("up", 0, 1),
		DOWN("down", 0, -1),
		LEFT("left", -1, 0),
		RIGHT("right", 1, 0);
		
		private String stringRepresentation;
		private int deltaX;
		private int deltaY;
		private boolean isHorizontal;
		
		Direction(String stringRepresentation, int deltaX, int deltaY) {
			this.stringRepresentation = stringRepresentation;
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			
			if(deltaX != 0) {
				isHorizontal = true;
			}
		}
		
		public String getStringRepresentation() {
			return stringRepresentation;
		}
		
		public int getDeltaX() {
			return deltaX;
		}
		
		public int getDeltaY() {
			return deltaY;
		}
		
		public boolean isHorizontal() {
			return isHorizontal;
		}
	}
	public static class EntityAttackParams {
		private Options options;
		private Audio audio;
		private Floor floor;
		private Entity attacker;
		private EntityFactory entityFactory;
		private AttackData attackData;
		private Point focusAbsoluteMapPosition;
		private Point focusPositionRelativeToTargetttedTiles;
		private TileAttackData[][] targetedTiles;
		private int beatDelay;
		
		public EntityAttackParams(Options options, Audio audio, Floor floor, Entity attacker, EntityFactory entityFactory,
			AttackData attackData, Point focusAbsoluteMapPosition, Point focusPositionRelativeToTargetttedTiles,
			TileAttackData[][] targetedTiles, int beatDelay) {
			this.options = options;
			this.audio = audio;
			this.floor = floor;
			this.attacker = attacker;
			this.entityFactory = entityFactory;
			this.attackData = attackData;
			this.focusAbsoluteMapPosition = focusAbsoluteMapPosition;
			this.focusPositionRelativeToTargetttedTiles = focusPositionRelativeToTargetttedTiles;
			this.targetedTiles = targetedTiles;
			this.beatDelay = beatDelay;
		}
		
		public void setBeatDelay(int beatDelay) {
			this.beatDelay = beatDelay;
		}
		
		public int getBeatDelay() {
			return beatDelay;
		}
	}
	
	public static void moveEntity(Engine engine, Floor floor, Entity entity, Direction direction) {
		Tile[][] tiles = floor.getTiles();
		HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
		ImageComponent imageComponent = ComponentMappers.imageMapper.get(entity);
		AnimationComponent animationComponent = ComponentMappers.animationMapper.get(entity);

		if(!floor.isActionsDisabled() && !hitboxComponent.isMovementDisabled()) {
			if(isValidMovement(tiles, entity, direction)) {
				HitboxType[][] hitbox = hitboxComponent.getHitbox();
				Point hitboxPosition = hitboxComponent.getMapPosition();
				Point imagePosition = imageComponent.getMapPosition();
				int xNew = hitboxPosition.x + direction.getDeltaX();
				int yNew = hitboxPosition.y + direction.getDeltaY();
				
				// Remove the entity from the set of occupants on all tiles the entity is currently on
				for(int x = hitboxPosition.x; x < hitboxPosition.x + hitbox.length; x++) {
					for(int y = hitboxPosition.y; y < hitboxPosition.y + hitbox[x - hitboxPosition.x].length; y++) {
						tiles[x][y].getAttackableOccupants().remove(entity);
						tiles[x][y].getTangibleOccupants().remove(entity);
					}
				}
				
				// Update hitbox and image positions
				hitboxPosition.setLocation(xNew, yNew);
				imagePosition.setLocation(xNew, yNew);
			
				// Update occupants set on all tiles in the floor that the entity's hitboxes now preside in after moving
				for(int x = xNew; x < xNew + hitbox.length; x++) {
					for(int y = yNew; y < yNew + hitbox[x - xNew].length; y++) {
						if(hitbox[x - xNew][y - yNew].isTangible()) {
							tiles[x][y].getTangibleOccupants().add(entity);
						}
						if(hitbox[x - xNew][y - yNew].isAttackable()) {
							tiles[x][y].getAttackableOccupants().add(entity);
						}
					}
				}
				
				hitboxComponent.faceDirection(direction);
				imageComponent.faceDirection(direction);
				animationComponent.transitionAnimation(imageComponent.getSpriteName() + "_idle_" + hitboxComponent.getHorizontalFacing().getStringRepresentation(), PlayMode.LOOP);
				
				// Trigger special tile events, if any
				SpecialTile specialTile = tiles[xNew][yNew].getSpecialTile();
				if(specialTile != null 
						&& !specialTile.isDeactivated()) {
					if(ComponentMappers.playerMapper.has(entity)) {
						specialTile.onPlayerTrigger();
					}
					if(ComponentMappers.enemyMapper.has(entity)) {
						specialTile.onEnemyTrigger();
					}
				}
				
				// If the entity moving is a player, check if it goes in range of any entity AI
				if(ComponentMappers.playerMapper.has(entity)) {
					for(Entity enemy : engine.getEntitiesFor(Family.all(EntityAIComponent.class).get())) {
						Point enemyPosition = ComponentMappers.hitboxMapper.get(enemy).getMapPosition();
						EntityAI ai = ComponentMappers.entityAIMapper.get(enemy).getEntityAI();
						if(Math.hypot(enemyPosition.x - hitboxPosition.x, enemyPosition.y - hitboxPosition.y) 
								>= ai.getActivationRadiusInTiles()) {
							ai.setActivated(true);
						}
					}
				}
				
				// If the entity moving is an enemy, check if it goes in range of any friendly AI
				if(ComponentMappers.enemyMapper.has(entity)) {
					for(Entity enemy : engine.getEntitiesFor(Family.all(FriendlyAIComponent.class).get())) {
						Point enemyPosition = ComponentMappers.hitboxMapper.get(enemy).getMapPosition();
						EntityAI ai = ComponentMappers.friendlyAIMapper.get(enemy).getEntityAI();
						if(Math.hypot(enemyPosition.x - hitboxPosition.x, enemyPosition.y - hitboxPosition.y) 
								>= ai.getActivationRadiusInTiles()) {
							ai.setActivated(true);
						}
					}
				}
			} else if(isValidTurn(tiles, entity, direction)) {
				// Entity turns but does not move
				hitboxComponent.faceDirection(direction);
				imageComponent.faceDirection(direction);
				animationComponent.transitionAnimation(imageComponent.getSpriteName() + "_idle_" + hitboxComponent.getHorizontalFacing().getStringRepresentation(), PlayMode.LOOP);
			}
		}
	}

	public static void entityStartAttack(Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, String attackName, EntityFactory entityFactory) {
		try {
			entityStartAttack(options, audio, dungeon, attacker, target, ComponentMappers.attackMapper.get(attacker).getAttacksData().get(attackName), entityFactory);
		} catch(NullPointerException e) {
			System.out.println("No attack exists named \"" + attackName + "\"");
		}
	}
	
	public static void entityStartAttack(Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, AttackData attackData, EntityFactory entityFactory) {
		Floor floor = dungeon.getFloors()[dungeon.getCurrentFloor()];

		if(!floor.isActionsDisabled()) {
			HitboxComponent attackerHitboxComponent = ComponentMappers.hitboxMapper.get(attacker);
			Point attackerPosition = attackerHitboxComponent.getMapPosition();
					
			AttackDirectionDeterminant directionDeterminant = attackData.getAttackDirectionDeterminant();
			Direction attackDirection = null;
			Point focusAbsoluteMapPosition = null;
			Point attackerAttackOrigin = null;
			if(directionDeterminant.equals(AttackDirectionDeterminant.SELF_FACING)) {
				attackDirection = attackerHitboxComponent.getFacing();
				attackerAttackOrigin = attackerHitboxComponent.getAttackOrigin();
				focusAbsoluteMapPosition = attackerPosition;
			} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_FACING)) {
				attackDirection = ComponentMappers.hitboxMapper.get(target).getFacing();
				attackerAttackOrigin = new Point(0, 0);
				focusAbsoluteMapPosition = ComponentMappers.hitboxMapper.get(target).getMapPosition();
			} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_RELATIVE_TO_SELF)) {
				attackDirection = MapUtils.getRelativeDirection(ComponentMappers.hitboxMapper.get(target).getMapPosition(), attackerPosition);
				attackerAttackOrigin = new Point(0, 0);
				focusAbsoluteMapPosition = attackerPosition;
			} else {
				System.out.println("YOU FORGOT TO MAKE AN IF STATEMENT FOR " + directionDeterminant + " IN Attack.class");
				return;
			}
			
			TileAttackData[][] targetedTiles = attackData.getDirectionalTilesAttackData().get(attackDirection);
			Point focusPositionRelativeToTargetttedTiles = null;
			for(int x = 0; x < targetedTiles.length; x++) {
				for(int y = 0; y < targetedTiles[x].length; y++) {
					if(targetedTiles[x][y].isFocus()) {
						focusPositionRelativeToTargetttedTiles = new Point(x, y);
						break;
					}
				}
			}
			
			// Warn tiles of incoming attacks
			AttackComponent attackComponent = ComponentMappers.attackMapper.get(attacker);
			Array<WarningTile> warningTiles = attackComponent.getWarningTiles();
			if(attackData.isWarnTilesBeforeAttack()) {
				for(int x = 0; x < targetedTiles.length; x++) {
					for(int y = 0; y < targetedTiles[x].length; y++) {
						// x and y iterations so far
						if(targetedTiles[x + attackerAttackOrigin.x][y + attackerAttackOrigin.y].isAttack()) {
							warningTiles.add(new WarningTile(attackData.getAttackDelayInBeats(), 
									x + focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x, 
									y + focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y));
						}
					}
				}
			}
			
			// Queue entity attack after beat delay
			if(attackData.getAttackDelayInBeats() > 0) {
				dungeon.getActionBar().getActionBarSystem().queueEntityAttack(new EntityAttackParams(options, audio, floor, attacker, entityFactory,
						attackData, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
						targetedTiles, attackData.getAttackDelayInBeats()));
			} else {
				entityAttack(new EntityAttackParams(options, audio, floor, attacker, entityFactory,
						attackData, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
						targetedTiles, 0));
			}
			
			if(attackData.getDisabledMovementTimeInBeats() > 0) {
				attackerHitboxComponent.disableMovement(attackData.getDisabledMovementTimeInBeats());
			}
		}
	}
	
	/**
	 * Instantly do damage calculations. Called only from action bar queue or from Attack.entityStartAttack if there is no delay.
	 */
	public static void entityAttack(EntityAttackParams params) {
		Tile[][] mapTiles = params.floor.getTiles();
		
		// Check if attacker or target is dead
		if(params.attacker == null 
				|| (ComponentMappers.healthMapper.has(params.attacker) && ComponentMappers.healthMapper.get(params.attacker).getHealth() <= 0)) {
			return;
		}
		
		// Get entities that are attacked
		Set<Entity> attackedEntities = new HashSet<Entity>();
		Class<? extends Component> entityHittableRequirement = params.attackData.getEntityHittableRequirement();
		int absX = params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x;
		int absY = params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y;
		for(int x = absX; x < absX + params.targetedTiles.length; x++) {
			for(int y = absY; y < absY + params.targetedTiles[x - absX].length; y++) {
				if(x >= 0 && y >= 0 && x < mapTiles.length && y < mapTiles[x].length) {
					// Get x and y relative to targetedTiles
					TileAttackData tile = params.targetedTiles[x - absX][y - absY];
					if(tile.isAttack()) {
						// Get attackble entities that reside on the absolute tile
						for(Entity occupant : mapTiles[x][y].getAttackableOccupants()) {
							// Check if occupant has the entityHittableRequirement component
							if(occupant.getComponent(entityHittableRequirement) != null) {
								attackedEntities.add(occupant);
							}
						}
						
						// Do animation on tile
						params.entityFactory.spawnAnimationEntity(tile.getAnimationOnTileName() + "_right", new Point(x, y));
					}
				}
			}
		}
				
		// Deal damage to attacked entities
		int damage = 4;
		damage *= params.options.getDifficulty().getPlayerDamageMultiplier();
		for(Entity attacked : attackedEntities) {
			if(!attacked.equals(params.attacker) && ComponentMappers.healthMapper.has(attacked)) {
				hitEntity(params.audio, attacked, damage);
			}
		}
		
		// Attacker does attack animation
		if(ComponentMappers.animationMapper.has(params.attacker)) {
			AnimationComponent animationComponent = ComponentMappers.animationMapper.get(params.attacker);
			animationComponent.startAnimation(params.attackData.getAttackerAnimationName() + "_" + ComponentMappers.hitboxMapper.get(params.attacker).getHorizontalFacing().getStringRepresentation(), PlayMode.NORMAL);
		}
	}
	
	public static void hitEntity(Audio audio, Entity attacked, int damage) {
		HealthComponent healthComponent = ComponentMappers.healthMapper.get(attacked);
		healthComponent.setHealth(healthComponent.getHealth() - damage);
		
		// Play hurt sound effect
		if(healthComponent.getHealth() > 0
				&& !healthComponent.getHurtSoundName().equals("none")) {
			audio.playSoundEffect(healthComponent.getHurtSoundName());
		}
	}
	
	public static void killEntity(Audio audio, Engine engine, Floor floor, Entity entity) {
		// Remove warning tiles
		if(ComponentMappers.attackMapper.has(entity)) {
			AttackComponent attackComponent = ComponentMappers.attackMapper.get(entity);
			attackComponent.getWarningTiles().clear();
		}
		
		// Remove entity from occupants sets in previously residing tiles
		if(ComponentMappers.hitboxMapper.has(entity)) {
			Tile[][] mapTiles = floor.getTiles();
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			Point mapPosition = hitboxComponent.getMapPosition();
			for(int x = mapPosition.x; x < mapPosition.x + hitbox.length; x++) {
				for(int y = mapPosition.y; y < mapPosition.y + hitbox[x - mapPosition.x].length; y++) {
					mapTiles[x][y].getTangibleOccupants().remove(entity);
					mapTiles[x][y].getAttackableOccupants().remove(entity);
				}
			}
		}
		
		// Play death sound
		if(ComponentMappers.healthMapper.has(entity)) {
			HealthComponent healthComponent = ComponentMappers.healthMapper.get(entity);
			if(!healthComponent.getDeathSoundName().equals("none")) {
				audio.playSoundEffect(healthComponent.getDeathSoundName());
			}
		}
		
		//TODO: do some death animation thing (use same animation for every entity death) and remove entity from engine after animation finishes
		
		//TODO: remove this
		engine.removeEntity(entity);
	}
	
	/**
	 * Checks if the entity can turn
	 */
	private static boolean isValidTurn(Tile[][] tiles, Entity entity, Direction direction) {
		if(direction.isHorizontal()) {
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
			HitboxType[][] hitbox = hitboxComponent.getHitboxesData().get(hitboxComponent.getHitboxName() + "_" + direction.stringRepresentation).getHitbox();
			Point mapPosition = hitboxComponent.getMapPosition();
			
			return isValidPosition(tiles, entity, hitbox, mapPosition.x, mapPosition.y);
		} else {
			return true;
		}
	}
	
	private static boolean isValidMovement(Tile[][] tiles, Entity entity, Direction direction) {
		HitboxType[][] hitbox;
		HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
		if(direction.isHorizontal()) {
			hitbox = hitboxComponent.getHitboxesData().get(hitboxComponent.getHitboxName() + "_" + direction.stringRepresentation).getHitbox();
		} else {
			hitbox = ComponentMappers.hitboxMapper.get(entity).getHitbox();
		}
		
		Point currentPosition = ComponentMappers.hitboxMapper.get(entity).getMapPosition();
		int xEntity = currentPosition.x + direction.getDeltaX();
		int yEntity = currentPosition.y + direction.getDeltaY();
		
		return isValidPosition(tiles, entity, hitbox, xEntity, yEntity);
	}
	
	private static boolean isValidPosition(Tile[][] tiles, Entity entity, HitboxType[][] hitbox, int xEntity, int yEntity) {
		// Check if the entity out of bounds
		if(xEntity + hitbox.length > tiles.length
				|| yEntity + hitbox[0].length > tiles[0].length
				|| xEntity < 0
				|| yEntity < 0) {
			return false;
		}
		
		// Check if any of the entity's hitboxes collide with a tangible tile
		for(int x = 0; x < hitbox.length; x++) {
			for(int y = 0; y < hitbox[x].length; y++) {
				if(hitbox[x][y].isTangible()
						&& tiles[xEntity + x][yEntity + y].isTangibleTile(entity)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
