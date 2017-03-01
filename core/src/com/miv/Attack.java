package com.miv;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.miv.Movement.Direction;

import components.AnimationComponent;
import components.AttackComponent;
import components.HealthComponent;
import components.HitboxComponent;
import data.AttackData;
import data.AttackData.AttackDirectionDeterminant;
import data.AttackData.TileAttackData;
import dungeons.Dungeon;
import dungeons.Floor;
import dungeons.Tile;
import utils.MapUtils;
import utils.MapUtils.TileDoesNotContainEntityException;

public class Attack {
	public static class EntityAttackParams {
		private Options options;
		private Floor floor;
		private Entity attacker;
		private Entity target;
		private String attackName;
		private AttackData attackData;
		private Point focusAbsoluteMapPosition;
		private Point focusPositionRelativeToTargetttedTiles;
		private TileAttackData[][] targettedTiles;
		private int beatDelay;
		
		public EntityAttackParams(Options options, Floor floor, Entity attacker, Entity target, String attackName,
			AttackData attackData, Point focusAbsoluteMapPosition, Point focusPositionRelativeToTargetttedTiles,
			TileAttackData[][] targettedTiles, int beatDelay) {
			this.options = options;
			this.floor = floor;
			this.attacker = attacker;
			this.target = target;
			this.attackName = attackName;
			this.attackData = attackData;
			this.focusAbsoluteMapPosition = focusAbsoluteMapPosition;
			this.focusPositionRelativeToTargetttedTiles = focusPositionRelativeToTargetttedTiles;
			this.targettedTiles = targettedTiles;
			this.beatDelay = beatDelay;
		}
		
		public void setBeatDelay(int beatDelay) {
			this.beatDelay = beatDelay;
		}
		
		public int getBeatDelay() {
			return beatDelay;
		}
	}
	
	public static void entityStartAttack(Options options, Dungeon dungeon, Entity attacker, Entity target, String attackName) {		
		Floor floor = dungeon.getFloors()[dungeon.getCurrentFloor()];
		
		HitboxComponent attackerHitboxComponent = ComponentMappers.hitboxMapper.get(attacker);
		Point attackerPosition = attackerHitboxComponent.getMapPosition();
		
		AttackComponent attackComponent = ComponentMappers.attackMapper.get(attacker);
		AttackData attackData = null;
		try {
			attackData = attackComponent.getAttacksData().get(attackName);
		} catch(NullPointerException e) {
			System.out.println("No attack exists named \"" + attackName + "\"");
			return;
		}
		
		AttackDirectionDeterminant directionDeterminant = attackData.getAttackDirectionDeterminant();
		Direction attackDirection = null;
		Point focusAbsoluteMapPosition = null;
		if(directionDeterminant.equals(AttackDirectionDeterminant.SELF_FACING)) {
			attackDirection = attackerHitboxComponent.getFacing();
			focusAbsoluteMapPosition = attackerPosition;
		} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_FACING)) {
			attackDirection = ComponentMappers.hitboxMapper.get(target).getFacing();
			focusAbsoluteMapPosition = ComponentMappers.hitboxMapper.get(target).getMapPosition();
		} else if(directionDeterminant.equals(AttackDirectionDeterminant.TARGET_RELATIVE_TO_SELF)) {
			attackDirection = MapUtils.getRelativeDirection(ComponentMappers.hitboxMapper.get(target).getMapPosition(), attackerPosition);
			focusAbsoluteMapPosition = attackerPosition;
		} else {
			System.out.println("YOU FORGOT TO MAKE AN IF STATEMENT FOR " + directionDeterminant + " IN Attack.class");
			return;
		}
		
		TileAttackData[][] targettedTiles = attackData.getDirectionalTilesAttackData().get(attackDirection);
		Point focusPositionRelativeToTargetttedTiles = null;
		for(int x = 0; x < targettedTiles.length; x++) {
			for(int y = 0; y < targettedTiles[x].length; y++) {
				if(targettedTiles[x][y].getIsFocus()) {
					focusPositionRelativeToTargetttedTiles = new Point(x, y);
				}
			}
		}
		if(focusPositionRelativeToTargetttedTiles == null) {
			System.out.println("\"" + attackName + "\" attack contains no focus.");
			return;
		}
		
		// Warn tiles
		if(attackData.getWarnTilesBeforeAttack()) {
			Tile[][] mapTiles = floor.getTiles();
			Point targettedTilesAbsoluteMapPosition = new Point(focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x, focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y);
			for(int x = focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x; x < targettedTiles.length; x++) {
				for(int y = focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y; y < targettedTiles[x].length; y++) {
					// x and y relative to targettedTiles
					if(targettedTiles[targettedTilesAbsoluteMapPosition.x - (focusAbsoluteMapPosition.x - focusPositionRelativeToTargetttedTiles.x)][targettedTilesAbsoluteMapPosition.y - (focusAbsoluteMapPosition.y - focusPositionRelativeToTargetttedTiles.y)]
							.getIsAttack()) {
						// TODO: Do animation on tiles by spawning entities with only animation+image components on them
					}
				}
			}
		}
		
		// Queue entity attack after beat delay
		if(attackData.getAttackDelayInBeats() > 0) {
			dungeon.getActionBar().getActionBarSystem().queueEntityAttack(new EntityAttackParams(options, floor, attacker, target, attackName,
					attackData, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
					targettedTiles, attackData.getAttackDelayInBeats()));
		} else {
			entityAttack(new EntityAttackParams(options, floor, attacker, target, attackName,
					attackData, focusAbsoluteMapPosition, focusPositionRelativeToTargetttedTiles,
					targettedTiles, 0));
		}
		
		if(attackData.getDisabledMovementTimeInBeats() > 0) {
			//TODO: allow attacker to move after ^ beats
		}
		
		
	}
	
	public static void entityAttack(EntityAttackParams params) {
		Tile[][] mapTiles = params.floor.getTiles();
		Point targettedTilesAbsoluteMapPosition = new Point(params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x
					, params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y);
		
		// Check if attacker or target is dead
		if(params.attacker == null 
				|| params.target == null 
				|| ComponentMappers.healthMapper.get(params.attacker).getHealth() <= 0) {
			//TODO: unwarn all affected 
		}
		
		// Get entities that are attacked
		Set<Entity> attackedEntities = new HashSet<Entity>();
		Class<? extends Component> entityHittableRequirement = params.attackData.getEntityHittableRequirement();
		for(int x = params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x; x < params.targettedTiles.length; x++) {
			for(int y = params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y; y < params.targettedTiles[x].length; y++) {
				// x and y relative to targettedTiles
				if(params.targettedTiles[targettedTilesAbsoluteMapPosition.x - (params.focusAbsoluteMapPosition.x - params.focusPositionRelativeToTargetttedTiles.x)]
						[targettedTilesAbsoluteMapPosition.y - (params.focusAbsoluteMapPosition.y - params.focusPositionRelativeToTargetttedTiles.y)]
						.getIsAttack()) {
					// Get entity that resides on the absolute tile
					try {
						if(mapTiles[x][y].containsAttackableEntity()) {
							Entity occupant = params.floor.getTiles()[x][y].getTangibleOccupant();
							
							// Check if occupant has all entityHittableRequirements
							if(occupant.getComponent(entityHittableRequirement) != null) {
								attackedEntities.add(occupant);
							}
						}
					} catch(TileDoesNotContainEntityException e) {
						mapTiles[x][y].setTangibleOccupant(null);
					}
					
					// TODO: Do animation on tiles by spawning entities with only animation+image components on them
				}
			}
		}
				
		// Deal damage to attacked entities
		int damage = 4;
		damage *= params.options.getDifficulty().getPlayerDamageMultiplier();
		for(Entity attacked : attackedEntities) {
			HealthComponent healthComponent = ComponentMappers.healthMapper.get(attacked);
			healthComponent.setHealth(healthComponent.getHealth() - damage);
			if(healthComponent.getHealth() <= 0) {
				Death.killEntity(attacked);
			}
		}
		
		// Do attack animation on attacker
		if(ComponentMappers.animationMapper.has(params.attacker)) {
			AnimationComponent animationComponent = ComponentMappers.animationMapper.get(params.attacker);
			animationComponent.startAnimation(params.attackData.getAttackerAnimationName() + "_" + ComponentMappers.hitboxMapper.get(params.attacker).getHorizontalFacing().getStringRepresentation());
		}
	}
}
