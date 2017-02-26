package utils;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.miv.ComponentMappers;

import components.HitboxComponent;
import data.HitboxData.HitboxType;
import dungeons.Tile;

public class MapUtils {
	public static class TileDoesNotContainEntityException extends Exception {
		private static final long serialVersionUID = -3389415152123362585L;
		
		public TileDoesNotContainEntityException() {
			super();
		}
		
		public TileDoesNotContainEntityException(String message) {
			super(message);
		}
	}
	
	/**
	 * Given the entity and the tile, 
	 * returns the HitboxType of the entity's hitbox that resides on the tile.
	 */
	public static HitboxType getEntityHitboxTypeOnTile(Entity entity, Tile tile) throws TileDoesNotContainEntityException {
		HitboxComponent entityHitbox = ComponentMappers.hitboxMapper.get(entity);
		Point entityMapPosition = entityHitbox.getMapPosition();
		Point tileMapPosition = tile.getMapPosition();
		
		int relativeX = tileMapPosition.x - entityMapPosition.x;
		int relativeY = tileMapPosition.y - entityMapPosition.y;
		if(relativeX < 0
				|| relativeY < 0
				|| relativeX >= entityHitbox.getHitbox().length
				|| relativeY >= entityHitbox.getHitbox()[relativeX].length) {
			throw new TileDoesNotContainEntityException();
		}
		
		return entityHitbox.getHitbox()[relativeX][relativeY];
	}
}
