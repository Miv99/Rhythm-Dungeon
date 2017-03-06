package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import components.EnemyAIComponent;
import components.FriendlyAIComponent;
import components.HitboxComponent;
import components.ImageComponent;
import data.HitboxData.HitboxType;
import dungeons.Floor;
import dungeons.Tile;
import entity_ai.EntityAI;
import special_tiles.SpecialTile;

public class Movement {
	public enum Direction {
		Up("up"),
		Down("down"),
		Left("left"),
		Right("right");
		
		private String stringRepresentation;
		
		Direction(String stringRepresentation) {
			this.stringRepresentation = stringRepresentation;
		}
		
		public String getStringRepresentation() {
			return stringRepresentation;
		}
	}
	
	public static void moveEntity(Engine engine, Floor floor, Entity entity, Direction direction) {		
		if(!floor.getActionsDisabled()
				&& isValidMovement(floor.getTiles(), entity, direction)) {
			// Update hitbox and image positions
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
			ImageComponent imageComponent = ComponentMappers.imageMapper.get(entity);
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			Point hitboxPosition = hitboxComponent.getMapPosition();
			Point imagePosition = imageComponent.getMapPosition();
			int xNew = hitboxPosition.x;
			int yNew = hitboxPosition.y;
			
			// Remove the entity from the set of occupants on all tiles the entity is currently on
			Tile[][] tiles = floor.getTiles();
			for(int x = hitboxPosition.x; x < hitbox.length; x++) {
				for(int y = hitboxPosition.y; y < hitbox[x - hitboxPosition.x].length; y++) {
					tiles[x][y].getAttackableOccupants().remove(entity);
					tiles[x][y].getTangibleOccupants().remove(entity);
				}
			}
			
			if(direction.equals(Direction.Up)) {
				yNew++;
			} else if(direction.equals(Direction.Down)) {
				yNew--;
			} else if(direction.equals(Direction.Left)) {
				xNew--;
			} else if(direction.equals(Direction.Right)) {
				xNew++;
			}
			hitboxPosition.setLocation(xNew, yNew);
			imagePosition.setLocation(xNew, yNew);
		
			// Update occupants set on all tiles in the floor that the entity's hitboxes now preside in after moving
			for(int x = xNew; x < xNew + hitbox.length; x++) {
				for(int y = yNew; y < yNew + hitbox[x - xNew].length; y++) {
					if(hitbox[x - xNew][y - yNew].getTangible()) {
						tiles[x][y].getTangibleOccupants().add(entity);
					}
					if(hitbox[x - xNew][y - yNew].getAttackable()) {
						tiles[x][y].getAttackableOccupants().add(entity);
					}
				}
			}
			
			hitboxComponent.faceDirection(direction);
			imageComponent.faceDirection(direction);
			
			// Trigger special tile events, if any
			SpecialTile specialTile = tiles[xNew][yNew].getSpecialTile();
			if(specialTile != null 
					&& !specialTile.getDeactivated()) {
				if(ComponentMappers.playerMapper.has(entity)) {
					specialTile.onPlayerTrigger();
				}
				if(ComponentMappers.enemyMapper.has(entity)) {
					specialTile.onEnemyTrigger();
				}
			}
			
			// If the entity moving is a player, check if it goes in range of any enemy AI
			if(ComponentMappers.playerMapper.has(entity)) {
				for(Entity enemy : engine.getEntitiesFor(Family.all(EnemyAIComponent.class).get())) {
					Point enemyPosition = ComponentMappers.hitboxMapper.get(enemy).getMapPosition();
					EntityAI ai = ComponentMappers.enemyAIMapper.get(enemy).getEnemyAI();
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
					EntityAI ai = ComponentMappers.enemyAIMapper.get(enemy).getEnemyAI();
					if(Math.hypot(enemyPosition.x - hitboxPosition.x, enemyPosition.y - hitboxPosition.y) 
							>= ai.getActivationRadiusInTiles()) {
						ai.setActivated(true);
					}
				}
			}
		}
	}
	
	private static boolean isValidMovement(Tile[][] tiles, Entity entity, Direction direction) {
		HitboxType[][] hitbox;
		if(direction.equals(Direction.Left)
				|| direction.equals(Direction.Right)) {
			hitbox = ComponentMappers.hitboxMapper.get(entity).getDirectionalHitboxes().get(direction);
		} else {
			hitbox = ComponentMappers.hitboxMapper.get(entity).getHitbox();
		}
		
		Point currentPosition = ComponentMappers.hitboxMapper.get(entity).getMapPosition();
		int xEntity = currentPosition.x;
		int yEntity = currentPosition.y;
		
		if(direction.equals(Direction.Up)) {
			yEntity++;
		} else if(direction.equals(Direction.Down)) {
			yEntity--;
		} else if(direction.equals(Direction.Left)) {
			xEntity--;
		} else if(direction.equals(Direction.Right)) {
			xEntity++;
		}
		
		// Check if the entity is moving out of bounds
		if(xEntity + hitbox.length > tiles.length
				|| yEntity + hitbox[0].length > tiles[0].length
				|| xEntity < 0
				|| yEntity < 0) {
			return false;
		}
				
		// Check if any of the entity's hitboxes collide with a tangible tile
		for(int x = 0; x < hitbox.length; x++) {
			for(int y = 0; y < hitbox[x].length; y++) {
				if(hitbox[x][y].getTangible()
						&& tiles[xEntity + x][yEntity + y].isTangibleTile()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
