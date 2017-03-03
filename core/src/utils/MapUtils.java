package utils;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.miv.ComponentMappers;
import com.miv.Movement.Direction;

import components.HitboxComponent;
import data.HitboxData.HitboxType;
import dungeons.Tile;

public class MapUtils {
	/**
	 * Given the entity and the tile, 
	 * returns the HitboxType of the entity's hitbox that resides on the tile.
	 */
	public static HitboxType getEntityHitboxTypeOnTile(Entity entity, Tile tile) {
		HitboxComponent entityHitbox = ComponentMappers.hitboxMapper.get(entity);
		Point entityMapPosition = entityHitbox.getMapPosition();
		Point tileMapPosition = tile.getMapPosition();
		
		int relativeX = tileMapPosition.x - entityMapPosition.x;
		int relativeY = tileMapPosition.y - entityMapPosition.y;
		
		return entityHitbox.getHitbox()[relativeX][relativeY];
	}
	
	public static Direction getRelativeDirection(Point point, Point relativeTo) {
		//TODO: random direction if on the border between two of them
		return Direction.Right;
	}
}
