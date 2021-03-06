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
import components.MovementAIComponent;
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
import movement_ai.MovementAI;
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
		private Engine engine;
		private Options options;
		private Audio audio;
		private Floor floor;
		private Entity attacker;
		private EntityFactory entityFactory;
		private AttackData attackData;
		private Point focusAbsoluteMapPosition;
		private Point focusPositionRelativeToTargetttedTiles;
		private Point attackerAttackOrigin;
		private TileAttackData[][] targetedTiles;
		private Array<WarningTile> nextAttackPartWarningTiles;
		private float beatDelay;
		
		public EntityAttackParams(Engine engine, Options options, Audio audio, Floor floor, Entity attacker, EntityFactory entityFactory,
				AttackData attackData, Point attackerAttackOrigin, Point focusAbsoluteMapPosition, Point focusPositionRelativeToTargetttedTiles,
				TileAttackData[][] targetedTiles, int beatDelay, Array<WarningTile> nextAttackPartWarningTiles) {
			this.engine = engine;
			this.options = options;
			this.audio = audio;
			this.floor = floor;
			this.attacker = attacker;
			this.entityFactory = entityFactory;
			this.attackData = attackData;
			this.attackerAttackOrigin = attackerAttackOrigin;
			this.focusAbsoluteMapPosition = focusAbsoluteMapPosition;
			this.focusPositionRelativeToTargetttedTiles = focusPositionRelativeToTargetttedTiles;
			this.targetedTiles = targetedTiles;
			this.beatDelay = beatDelay;
			this.nextAttackPartWarningTiles = nextAttackPartWarningTiles;
		}
		
		public EntityAttackParams(Engine engine, Options options, Audio audio, Floor floor, Entity attacker, EntityFactory entityFactory,
			AttackData attackData, Point attackerAttackOrigin, Point focusAbsoluteMapPosition, Point focusPositionRelativeToTargetttedTiles,
			TileAttackData[][] targetedTiles, int beatDelay) {
			this.engine = engine;
			this.options = options;
			this.audio = audio;
			this.floor = floor;
			this.attacker = attacker;
			this.entityFactory = entityFactory;
			this.attackData = attackData;
			this.attackerAttackOrigin = attackerAttackOrigin;
			this.focusAbsoluteMapPosition = focusAbsoluteMapPosition;
			this.focusPositionRelativeToTargetttedTiles = focusPositionRelativeToTargetttedTiles;
			this.targetedTiles = targetedTiles;
			this.beatDelay = beatDelay;
		}
		
		public void setBeatDelay(float beatDelay) {
			this.beatDelay = beatDelay;
		}
		
		public float getBeatDelay() {
			return beatDelay;
		}
	}
	
	public static void moveEntity(Engine engine, Floor floor, Entity entity, Direction direction) {
		Tile[][] tiles = floor.getTiles();
		HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
		ImageComponent imageComponent = ComponentMappers.imageMapper.get(entity);
		AnimationComponent animationComponent = ComponentMappers.animationMapper.get(entity);

		if(!floor.isActionsDisabled() && !hitboxComponent.isMovementDisabled()) {
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			Point hitboxPosition = hitboxComponent.getMapPosition();

			if(isValidMovement(tiles, entity, direction)) {
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
				if(animationComponent.isPlayingIdleAnimation()) {
					animationComponent.transitionAnimation(imageComponent.getSpriteName() + "_idle_" + hitboxComponent.getHorizontalFacing().getStringRepresentation(), PlayMode.NORMAL);
				}
				
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
				
				// If the entity moving is a player, check if it goes in range of any AI
				if(ComponentMappers.playerMapper.has(entity)) {
					Point hitboxCenterPosition = new Point(hitboxPosition.x + hitbox.length/2, hitboxPosition.y + hitbox[0].length/2);
					
					// Entity AI
					for(Entity e : engine.getEntitiesFor(Family.all(EntityAIComponent.class).get())) {
						HitboxComponent eHitboxComponent = ComponentMappers.hitboxMapper.get(e);
						EntityAI ai = ComponentMappers.entityAIMapper.get(e).getEntityAI();
						if(Math.hypot(eHitboxComponent.getCenterMapPositionX() - hitboxCenterPosition.x, eHitboxComponent.getCenterMapPositionY() - hitboxCenterPosition.y) 
								<= ai.getActivationRadiusInTiles()) {
							ai.setActivated(true);
						}
					}
					
					// Movement AI
					for(Entity e : engine.getEntitiesFor(Family.all(MovementAIComponent.class).get())) {
						HitboxComponent eHitboxComponent = ComponentMappers.hitboxMapper.get(e);
						MovementAI ai = ComponentMappers.movementAIMapper.get(e).getMovementAI();
						if(Math.hypot(eHitboxComponent.getCenterMapPositionX() - hitboxCenterPosition.x, eHitboxComponent.getCenterMapPositionY() - hitboxCenterPosition.y) 
								<= ai.getActivationRadiusInTiles()) {
							ai.setActivated(true);
						}
					}
					
					ComponentMappers.playerMapper.get(entity).setMovedInLastBeat(true);
					ComponentMappers.playerMapper.get(entity).setLastPosition(xNew, yNew);
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
				// Remove the entity from the set of occupants on all tiles the entity is currently on
				for(int x = hitboxPosition.x; x < hitboxPosition.x + hitbox.length; x++) {
					for(int y = hitboxPosition.y; y < hitboxPosition.y + hitbox[x - hitboxPosition.x].length; y++) {
						tiles[x][y].getAttackableOccupants().remove(entity);
						tiles[x][y].getTangibleOccupants().remove(entity);
					}
				}
				
				// Entity turns but does not move
				hitboxComponent.faceDirection(direction);
				imageComponent.faceDirection(direction);
				if(animationComponent.isPlayingIdleAnimation()) {
					animationComponent.transitionAnimation(imageComponent.getSpriteName() + "_idle_" + hitboxComponent.getHorizontalFacing().getStringRepresentation(), PlayMode.NORMAL);
				}
				
				// Update occupants set on all tiles in the floor that the entity's hitboxes now preside in after moving
				for(int x = hitboxPosition.x; x < hitboxPosition.x + hitbox.length; x++) {
					for(int y = hitboxPosition.y; y < hitboxPosition.y + hitbox[x - hitboxPosition.x].length; y++) {
						if(hitbox[x - hitboxPosition.x][y - hitboxPosition.y].isTangible()) {
							tiles[x][y].getTangibleOccupants().add(entity);
						}
						if(hitbox[x - hitboxPosition.x][y - hitboxPosition.y].isAttackable()) {
							tiles[x][y].getAttackableOccupants().add(entity);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns true if attack was successful (if actions were not disabled)
	 */
	public static boolean entityStartAttack(Engine engine, Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, String attackName, EntityFactory entityFactory) {
		try {
			return entityStartAttack(engine, options, audio, dungeon, attacker, target, ComponentMappers.attackMapper.get(attacker).getAttacksData().get(attackName), entityFactory);
		} catch(NullPointerException e) {
			System.out.println("No attack exists named \"" + attackName + "\"");
			return false;
		}
	}
	
	/**
	 * Returns true if attack was successful (if actions were not disabled)
	 */
	public static boolean entityStartAttack(Engine engine, Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, AttackData attackData, EntityFactory entityFactory) {
		Floor floor = dungeon.getFloors()[dungeon.getCurrentFloor()];
		AttackComponent attackerAttackComponent = ComponentMappers.attackMapper.get(attacker);

		if(!floor.isActionsDisabled() && attackerAttackComponent.getAttackDisabledTimeInBeats() <= 0) {
			Tile[][] mapTiles = floor.getTiles();
			HitboxComponent attackerHitboxComponent = ComponentMappers.hitboxMapper.get(attacker);
			Point attackerPosition = attackerHitboxComponent.getMapPosition();
					
			AttackDirectionDeterminant directionDeterminant = attackData.getAttackDirectionDeterminant();
			Direction attackDirection = null;
			Point focusAbsoluteMapPosition = null;
			Point attackerAttackOrigin = new Point(attackerHitboxComponent.getAttackOrigin());
			if(directionDeterminant.equals(AttackDirectionDeterminant.SELF_FACING)) {
				attackDirection = attackerHitboxComponent.getFacing();
				focusAbsoluteMapPosition = new Point(attackerPosition);
			} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_FACING)) {
				attackDirection = ComponentMappers.hitboxMapper.get(target).getFacing();
				focusAbsoluteMapPosition = new Point(ComponentMappers.hitboxMapper.get(target).getMapPosition());
			} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_RELATIVE_TO_SELF)) {
				attackDirection = MapUtils.getRelativeDirection(ComponentMappers.hitboxMapper.get(target).getMapPosition(), new Point(attackerPosition.x + attackerAttackOrigin.x, attackerPosition.y + attackerAttackOrigin.y));
				if(!isValidTurn(mapTiles, attacker, attackDirection)) {
					return false;
				} else {
					// Remove the entity from the set of occupants on all tiles the entity is currently on
					HitboxType[][] hitbox = attackerHitboxComponent.getHitbox();
					for(int x = attackerPosition.x; x < attackerPosition.x + hitbox.length; x++) {
						for(int y = attackerPosition.y; y < attackerPosition.y + hitbox[x - attackerPosition.x].length; y++) {
							mapTiles[x][y].getAttackableOccupants().remove(attacker);
							mapTiles[x][y].getTangibleOccupants().remove(attacker);
						}
					}
					
					// Entity turns but does not move
					attackerHitboxComponent.faceDirection(attackDirection);
					ComponentMappers.imageMapper.get(attacker).faceDirection(attackDirection);
					attackerAttackOrigin = new Point(attackerHitboxComponent.getAttackOrigin());
					
					// Update occupants set on all tiles in the floor that the entity's hitboxes now preside in after moving
					for(int x = attackerPosition.x; x < attackerPosition.x + hitbox.length; x++) {
						for(int y = attackerPosition.y; y < attackerPosition.y + hitbox[x - attackerPosition.x].length; y++) {
							if(hitbox[x - attackerPosition.x][y - attackerPosition.y].isTangible()) {
								mapTiles[x][y].getTangibleOccupants().add(attacker);
							}
							if(hitbox[x - attackerPosition.x][y - attackerPosition.y].isAttackable()) {
								mapTiles[x][y].getAttackableOccupants().add(attacker);
							}
						}
					}
				}
				focusAbsoluteMapPosition = new Point(attackerPosition);
			} else if(directionDeterminant.equals(AttackDirectionDeterminant.ALWAYS_RIGHT_FROM_SELF)) {
				attackDirection = Direction.RIGHT;
				focusAbsoluteMapPosition = new Point(attackerPosition);
			} else {
				System.out.println("YOU FORGOT TO MAKE AN IF STATEMENT FOR " + directionDeterminant + " IN Attack.class");
				return false;
			}
			
			Array<TileAttackData[][]> targetedTilesArray = new Array<TileAttackData[][]>();
			Array<Array<WarningTile>> warningTilesArray = new Array<Array<WarningTile>>();
			for(int i = 0; i < attackData.getDirectionalTilesAttackData().get(attackDirection).size(); i++) {
				TileAttackData[][] targetedTiles = attackData.getDirectionalTilesAttackData().get(attackDirection).get(i);
				Point focusPositionRelativeToTargetttedTiles = null;
				for(int x = 0; x < targetedTiles.length; x++) {
					for(int y = 0; y < targetedTiles[x].length; y++) {
						if(targetedTiles[x][y] != null && targetedTiles[x][y].isFocus()) {
							focusPositionRelativeToTargetttedTiles = new Point(x, y);
							break;
						}
					}
				}
				
				// Create warning tiles of incoming attacks
				Array<WarningTile> warningTiles = new Array<WarningTile>();
				if(i == 0) {
					if(attackData.getWarnTilesBeforeAttack()[0]) {
						int absX = focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x + attackerAttackOrigin.x;
						int absY = focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y + attackerAttackOrigin.y;
						for(int x = absX; x < absX + targetedTiles.length; x++) {
							for(int y = absY; y < absY + targetedTiles[x - absX].length; y++) {
								if(x >= 0 && y >= 0 && x < mapTiles.length && y < mapTiles[x].length) {
									// Get x and y relative to targetedTiles
									TileAttackData tile = targetedTiles[x - absX][y - absY];
									if(tile != null && tile.isAttack()) {
										warningTiles.add(new WarningTile(attackData.getAttackDelayInBeats(), x, y));
									}
								}
							}
						}
					}
				} else {
					// Multi-step attacks display warning tiles for steps other than the first one only after the previous step has been completed
					if(attackData.getWarnTilesBeforeAttack()[i]) {
						int absX = focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x + attackerAttackOrigin.x;
						int absY = focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y + attackerAttackOrigin.y;
						for(int x = absX; x < absX + targetedTiles.length; x++) {
							for(int y = absY; y < absY + targetedTiles[x - absX].length; y++) {
								if(x >= 0 && y >= 0 && x < mapTiles.length && y < mapTiles[x].length) {
									// Get x and y relative to targetedTiles
									TileAttackData tile = targetedTiles[x - absX][y - absY];
									if(tile != null && tile.isAttack()) {
										warningTiles.add(new WarningTile(1, x, y));
									}
								}
							}
						}
					}
				}
				
				targetedTilesArray.add(targetedTiles);
				warningTilesArray.add(warningTiles);
			}
			
			for(int i = 0; i < attackData.getDirectionalTilesAttackData().get(attackDirection).size(); i++) {
				TileAttackData[][] targetedTiles = targetedTilesArray.get(i);
				Point focusPositionRelativeToTargetttedTiles = null;
				for(int x = 0; x < targetedTiles.length; x++) {
					for(int y = 0; y < targetedTiles[x].length; y++) {
						if(targetedTiles[x][y] != null && targetedTiles[x][y].isFocus()) {
							focusPositionRelativeToTargetttedTiles = new Point(x, y);
							break;
						}
					}
				}
				
				// Queue entity attack after beat delay
				if(i + 1 < warningTilesArray.size) {
					if(attackData.getAttackDelayInBeats() + i > 0) {
						dungeon.getActionBar().getActionBarSystem().queueEntityAttack(new EntityAttackParams(engine, options, audio, floor, attacker, entityFactory,
								attackData, attackerAttackOrigin, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
								targetedTiles, attackData.getAttackDelayInBeats() + i, warningTilesArray.get(i + 1)));
					} else {
						EntityAttackParams params = new EntityAttackParams(engine, options, audio, floor, attacker, entityFactory,
								attackData, attackerAttackOrigin, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
								targetedTiles, 0, warningTilesArray.get(i + 1));
						runEntityAttackAnimations(params);
						dungeon.getActionBar().getActionBarSystem().queueEntityAttackDamageCalculations(params);
					}
				} else {
					if(attackData.getAttackDelayInBeats() + i > 0) {
						dungeon.getActionBar().getActionBarSystem().queueEntityAttack(new EntityAttackParams(engine, options, audio, floor, attacker, entityFactory,
								attackData, attackerAttackOrigin, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
								targetedTiles, attackData.getAttackDelayInBeats() + i));
					} else {
						EntityAttackParams params = new EntityAttackParams(engine, options, audio, floor, attacker, entityFactory,
								attackData, attackerAttackOrigin, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
								targetedTiles, 0);
						runEntityAttackAnimations(params);
						dungeon.getActionBar().getActionBarSystem().queueEntityAttackDamageCalculations(params);
					}
				}
			}
			
			// Warn tiles
			if(warningTilesArray.size > 0) {
				attackerAttackComponent.getWarningTiles().addAll(warningTilesArray.first());
			}
			
			if(attackData.getDisabledAttackTimeInBeats() > 0) {
				attackerAttackComponent.setAttackDisabledTimeInBeats(attackData.getDisabledAttackTimeInBeats());
			}
			if(attackData.getDisabledMovementTimeInBeats() > 0) {
				attackerHitboxComponent.disableMovement(attackData.getDisabledMovementTimeInBeats());
			}
			
			// Attacker does attack animation
			if(ComponentMappers.animationMapper.has(attacker)) {
				AnimationComponent animationComponent = ComponentMappers.animationMapper.get(attacker);
				animationComponent.startAnimation(attackData.getAttackerAnimationName() + "_" + ComponentMappers.hitboxMapper.get(attacker).getHorizontalFacing().getStringRepresentation(), PlayMode.NORMAL);
				animationComponent.setQueuedIdleAnimation(true);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Do animations only. Does not do damage calculations.
	 * Called only from action bar queue or from Attack.entityStartAttack if there is no delay.
	 */
	public static void runEntityAttackAnimations(EntityAttackParams params) {		
		// Check if attacker or target is dead
		if(params.attacker == null 
				|| (ComponentMappers.healthMapper.has(params.attacker) && ComponentMappers.healthMapper.get(params.attacker).getHealth() <= 0)) {
			return;
		}
		
		Tile[][] mapTiles = params.floor.getTiles();
		
		// Warn next tiles if attack is an attack part
		AttackComponent attackerAttackComponent = ComponentMappers.attackMapper.get(params.attacker);
		if(params.nextAttackPartWarningTiles != null) {
			attackerAttackComponent.getWarningTiles().addAll(params.nextAttackPartWarningTiles);
		}
		
		// Do animations on tiles
		int absX = params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x + params.attackerAttackOrigin.x;
		int absY = params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y + params.attackerAttackOrigin.y;
		for(int x = absX; x < absX + params.targetedTiles.length; x++) {
			for(int y = absY; y < absY + params.targetedTiles[x - absX].length; y++) {
				if(x >= 0 && y >= 0 && x < mapTiles.length && y < mapTiles[x].length) {
					// Get x and y relative to targetedTiles
					TileAttackData tile = params.targetedTiles[x - absX][y - absY];
					if(tile != null && tile.isAttack()) {
						params.entityFactory.spawnAnimationEntity(tile.getAnimationOnTileName() + "_right", new Point(x, y));
					}
				}
			}
		}
	}
	
	/**
	 * Instantly do damage calculations only. Called only from action bar queue.
	 */
	public static void calculateEntityAttackDamage(EntityAttackParams params) {
		// Check if attacker or target is dead
		if(params.attacker == null 
				|| (ComponentMappers.healthMapper.has(params.attacker) && ComponentMappers.healthMapper.get(params.attacker).getHealth() <= 0)) {
			return;
		}
				
		Tile[][] mapTiles = params.floor.getTiles();

		// Get entities that are attacked
		Set<Entity> attackedEntities = new HashSet<Entity>();
		Class<? extends Component> entityHittableRequirement = params.attackData.getEntityHittableRequirement();
		int absX = params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x + params.attackerAttackOrigin.x;
		int absY = params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y + params.attackerAttackOrigin.y;
		for(int x = absX; x < absX + params.targetedTiles.length; x++) {
			for(int y = absY; y < absY + params.targetedTiles[x - absX].length; y++) {
				if(x >= 0 && y >= 0 && x < mapTiles.length && y < mapTiles[x].length) {
					// Get x and y relative to targetedTiles
					TileAttackData tile = params.targetedTiles[x - absX][y - absY];
					if(tile != null && tile.isAttack()) {
						// Get attackble entities that reside on the absolute tile
						for(Entity occupant : mapTiles[x][y].getAttackableOccupants()) {
							// Check if occupant has the entityHittableRequirement component
							// and check if occupant is player that just moved and was previously in a safe tile
							if(occupant.getComponent(entityHittableRequirement) != null) {
								if(ComponentMappers.playerMapper.has(occupant)) {
									PlayerComponent playerComponent = ComponentMappers.playerMapper.get(occupant);
									Point lastPosition = playerComponent.getLastPosition();
									if((playerComponent.isMovedInLastBeat() && !isSafeTileForPlayer(params.engine, mapTiles, lastPosition.x, lastPosition.y)) 
											|| !playerComponent.isMovedInLastBeat()) {
										attackedEntities.add(occupant);
									}
								} else {
									attackedEntities.add(occupant);
								}
							}
						}
					}
				}
			}
		}
				
		// Deal damage to attacked entities
		int damage = 4;
		if(ComponentMappers.playerMapper.has(params.attacker)) {
			damage *= params.options.getDifficulty().getPlayerDamageMultiplier();
		}
		for(Entity attacked : attackedEntities) {
			if(!attacked.equals(params.attacker) && ComponentMappers.healthMapper.has(attacked)) {
				hitEntity(params.audio, attacked, damage);
			}
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
	
	public static void killEntity(EntityFactory entityFactory, Audio audio, Engine engine, Floor floor, Entity entity) {
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
		
		// Spawn separate entity to show death animation
		if(ComponentMappers.animationMapper.has(entity)
				&& !ComponentMappers.animationMapper.get(entity).getDeathAnimationName().equals("none")) {
			entityFactory.spawnAnimationEntity(ComponentMappers.animationMapper.get(entity).getDeathAnimationName() + "_" + ComponentMappers.hitboxMapper.get(entity).getHorizontalFacing().getStringRepresentation(), 
					new Point(ComponentMappers.hitboxMapper.get(entity).getMapPosition()));
		}
		
		engine.removeEntity(entity);
	}
	
	/**
	 * Checks if the specified tile is going to be attacked by any enemies within the next beat
	 */
	private static boolean isSafeTileForPlayer(Engine engine, Tile[][] tiles, int x, int y) {
		for(Entity e : engine.getEntitiesFor(Family.all(AttackComponent.class).get())) {
			AttackComponent attack = ComponentMappers.attackMapper.get(e);
			for(WarningTile warningTile : attack.getWarningTiles()) {
				if(warningTile.getX() == x && warningTile.getY() == y) {
					return false;
				}
			}
		}
		return true;
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
	
	public static boolean isValidMovement(Tile[][] tiles, Entity entity, Direction direction) {
		Point pos = ComponentMappers.hitboxMapper.get(entity).getMapPosition();
		return isValidMovement(tiles, entity, pos.x, pos.y, direction);
	}
	
	public static boolean isValidMovement(Tile[][] tiles, Entity entity, int xEntity, int yEntity, Direction direction) {
		HitboxType[][] hitbox;
		HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
		if(direction.isHorizontal()) {
			hitbox = hitboxComponent.getHitboxesData().get(hitboxComponent.getHitboxName() + "_" + direction.stringRepresentation).getHitbox();
		} else {
			hitbox = ComponentMappers.hitboxMapper.get(entity).getHitbox();
		}
		
		xEntity += direction.getDeltaX();
		yEntity += direction.getDeltaY();
		
		return isValidPosition(tiles, entity, hitbox, xEntity, yEntity);
	}
	
	public static boolean isValidPosition(Tile[][] tiles, Entity entity, HitboxType[][] hitbox, int xEntity, int yEntity) {
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
