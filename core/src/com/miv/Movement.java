package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;

import audio.Song;
import components.HitboxComponent;
import components.ImageComponent;
import data.HitboxData.HitboxType;
import dungeons.Floor;
import dungeons.Tile;
import special_tiles.SpecialTile;
import utils.MapUtils.TileDoesNotContainEntityException;

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
	
	public static void moveEntity(Floor floor, Entity entity, Direction direction) {		
		if(isValidMovement(floor.getTiles(), entity, direction)) {
			// Update hitbox and image positions
			HitboxComponent hitboxComponent = ComponentMappers.hitboxMapper.get(entity);
			ImageComponent imageComponent = ComponentMappers.imageMapper.get(entity);
			Point hitboxPosition = hitboxComponent.getMapPosition();
			Point imagePosition = imageComponent.getMapPosition();
			int xNew = hitboxPosition.x;
			int yNew = hitboxPosition.y;
			
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
		
			// Update tangibleOccupant on all tiles in the floor that the entity's hitboxes now preside in
			Tile[][] tiles = floor.getTiles();
			HitboxType[][] hitbox = hitboxComponent.getHitbox();
			for(int x = xNew; x < xNew + hitbox.length; x++) {
				for(int y = yNew; y < yNew + hitbox[0].length; y++) {
					tiles[x][y].setTangibleOccupant(entity);
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
			
			//TODO: Activate all enemy AI within range of the player
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
		// If any of the tiles checked contains no tangible target, the tangible occupant in that tile is set to null
		for(int x = 0; x < hitbox.length; x++) {
			for(int y = 0; y < hitbox[x].length; y++) {
				try {
					if(hitbox[x][y].getTangible()
							&& tiles[xEntity + x][yEntity + y].isTangibleTile()) {
						return false;
					}
				} catch(TileDoesNotContainEntityException e) {
					tiles[xEntity + x][yEntity + y].setTangibleOccupant(null);
				}
			}
		}
		
		return true;
	}
}
