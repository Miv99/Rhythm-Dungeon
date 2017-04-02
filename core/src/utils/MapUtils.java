package utils;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.miv.ComponentMappers;
import com.miv.EntityActions.Direction;

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
	
	public static boolean boundingRectsIntersect(HitboxType[][] hitbox1, int hitbox1X, int hitbox1Y, HitboxType[][] hitbox2, int hitbox2X, int hitbox2Y) {
		return (Math.abs(hitbox2X - hitbox1X) < Math.min(hitbox1.length, hitbox2.length) 
				&& Math.abs(hitbox2Y - hitbox1Y) < Math.min(hitbox1[0].length, hitbox2[0].length));
	}
	
	public static Direction getRelativeDirection(Point point, Point relativeTo) {
		if(point.x - relativeTo.x == 0) {
			if(point.y - relativeTo.y > 0) {
				return Direction.UP;
			} else {
				return Direction.DOWN;
			}
		}
		
		float slope = (float)(point.y - relativeTo.y)/(point.x - relativeTo.x);
		if(point.x - relativeTo.x > 0) {
			if(slope > -1 && slope < 1) {
				return Direction.RIGHT;
			} else if(slope > 1) {
				return Direction.UP;
			} else if(slope < -1) {
				return Direction.DOWN;
			} else if(slope == 1) {
				if(MathUtils.randomBoolean()) {
					return Direction.RIGHT;
				} else {
					return Direction.UP;
				}
			} else if(slope == -1) {
				if(MathUtils.randomBoolean()) {
					return Direction.RIGHT;
				} else {
					return Direction.DOWN;
				}
			}
		} else {
			if(slope > -1 && slope < 1) {
				return Direction.LEFT;
			} else if(slope > 1) {
				return Direction.DOWN;
			} else if(slope < -1) {
				return Direction.UP;
			} else if(slope == 1) {
				if(MathUtils.randomBoolean()) {
					return Direction.LEFT;
				} else {
					return Direction.DOWN;
				}
			} else if(slope == -1) {
				if(MathUtils.randomBoolean()) {
					return Direction.LEFT;
				} else {
					return Direction.UP;
				}
			}
		}
		return null;
	}
}
