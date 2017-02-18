package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import data.HitboxData.HitboxType;
import utils.MapUtils;
import utils.MapUtils.TileDoesNotContainEntityException;

public class Tile {
	// The tangible entity that is occupying the tile
	// The same entity may occupy multiple tiles
	private Entity tangibleOccupant;
	
	// Sprite of the tile
	private Sprite sprite;
	
	private HitboxType hitboxType;
	
	private Point mapPosition;
	
	/**
	 * Uses information on the tangibleOccupant and this tile
	 * to determine whether the tangibleOccupant can be attacked
	 */
	public boolean containsAttackableEntity() {
		if(tangibleOccupant != null) {
			try {
				if(MapUtils.getEntityHitboxTypeOnTile(tangibleOccupant, this).getAttackable()) {
					return true;
				} else {
					return false;
				}
			} catch(TileDoesNotContainEntityException e) {
				tangibleOccupant = null;
			}
		}
		return false;
	}
	
	/**
	 * Uses information on the tangibleOccupant (if any) and hitboxType to determine
	 * whether this tile can be moved onto
	 */
	public boolean isTangibleTile() {
		if(hitboxType.getTangible()) {
			return true;
		} else if(tangibleOccupant != null) {
			try {
				if(MapUtils.getEntityHitboxTypeOnTile(tangibleOccupant, this).getTangible()) {
					return true;
				} else {
					return false;
				}
			} catch(TileDoesNotContainEntityException e) {
				tangibleOccupant = null;
			}
		}
		return false;
	}
	
	public Entity getTangibleOccupant() {
		return tangibleOccupant;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public HitboxType getHitboxType() {
		return hitboxType;
	}
	
	public Point getMapPosition() {
		return mapPosition;
	}
}
