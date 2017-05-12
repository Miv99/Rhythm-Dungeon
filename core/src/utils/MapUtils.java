package utils;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
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
	
	public static Direction getRelativeDirection(int x1, int y1, int relativeToX, int relativeToY) {
		if(x1 - relativeToX == 0) {
			if(y1 - relativeToY > 0) {
				return Direction.UP;
			} else {
				return Direction.DOWN;
			}
		}
		
		float slope = (float)(y1 - relativeToY)/(x1 - relativeToX);
		if(x1 - relativeToX > 0) {
			if(slope > -1 && slope <= 1) {
				return Direction.RIGHT;
			} else if(slope > 1) {
				return Direction.UP;
			} else if(slope <= -1) {
				return Direction.DOWN;
			}
		} else {
			if(slope > -1 && slope <= 1) {
				return Direction.LEFT;
			} else if(slope > 1) {
				return Direction.DOWN;
			} else if(slope <= -1) {
				return Direction.UP;
			}
		}
		return null;
	}
	
	public static Direction getRelativeDirection(Point point, Point relativeTo) {
		return getRelativeDirection(point.x, point.y, relativeTo.x, relativeTo.y);
	}
	
	public static Array<Tile> getAllVisibleIntangibleTilesInDirection(Tile[][] mapTiles, int x, int y, Direction direction) {
		Array<Tile> tiles = new Array<Tile>();
		while(x >= 0 && x < mapTiles.length && y >= 0 && y < mapTiles[0].length) {
			if(mapTiles[x][y].getHitboxType().isTangible()) {
				break;
			} else {
				tiles.add(mapTiles[x][y]);
				x += direction.getDeltaX();
				y += direction.getDeltaY();
			}
		}
		return tiles;
	}
}
