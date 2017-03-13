package com.miv;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.miv.Movement.Direction;

import audio.Audio;
import components.AnimationComponent;
import components.AttackComponent;
import components.HealthComponent;
import components.HitboxComponent;
import components.ImageComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import dungeons.Dungeon;
import dungeons.Floor;
import dungeons.Tile;
import factories.EntityFactory;
import special_tiles.WarningTile;
import utils.MapUtils;

public class Attack {	
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
	
	public static void entityStartAttack(Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, String attackName, EntityFactory entityFactory) {
		try {
			entityStartAttack(options, audio, dungeon, attacker, target, ComponentMappers.attackMapper.get(attacker).getAttacksData().get(attackName), entityFactory);
		} catch(NullPointerException e) {
			System.out.println("No attack exists named \"" + attackName + "\"");
		}
	}
	
	public static void entityStartAttack(Options options, Audio audio, Dungeon dungeon, Entity attacker, Entity target, AttackData attackData, EntityFactory entityFactory) {
		Floor floor = dungeon.getFloors()[dungeon.getCurrentFloor()];

		if(!floor.getActionsDisabled()) {
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
					if(targetedTiles[x][y].getIsFocus()) {
						focusPositionRelativeToTargetttedTiles = new Point(x, y);
						break;
					}
				}
			}
			
			// Warn tiles of incoming attacks
			AttackComponent attackComponent = ComponentMappers.attackMapper.get(attacker);
			Array<WarningTile> warningTiles = attackComponent.getWarningTiles();
			if(attackData.getWarnTilesBeforeAttack()) {
				for(int x = 0; x < targetedTiles.length; x++) {
					for(int y = 0; y < targetedTiles[x].length; y++) {
						// x and y iterations so far
						if(targetedTiles[x + attackerAttackOrigin.x][y + attackerAttackOrigin.y].getIsAttack()) {
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
					if(tile.getIsAttack()) {
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
}
