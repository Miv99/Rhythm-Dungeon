package dungeons;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import data.HitboxData.HitboxType;
import special_tiles.SpecialTile;
import utils.MapUtils;

/**
 * Breakable tiles have an invisible entity with a HealthComponent, HitboxComponent, TileComponent, ImageComponent, and AnimationComponent on it that blocks movement
 * Breaking tiles simply kills the entity to allow movement
 */
public class Tile {
	private Set<Entity> attackableOccupants;
	private Set<Entity> tangibleOccupants;
	
	// Sprite of the tile
	private Sprite sprite;
	// Sprites overlayed on the tile (not including specialTile)
	private Array<Sprite> spriteOverlays;
	private HitboxType hitboxType;
	private Point mapPosition;
	private SpecialTile specialTile;
	
	public Tile(Point mapPosition) {
		this.mapPosition = mapPosition;
		attackableOccupants = new HashSet<Entity>();
		tangibleOccupants = new HashSet<Entity>();
	}
	
	public boolean containsAttackableEntity() {
		return attackableOccupants.size() > 0;
	}
	
	public boolean isTangibleTile() {
		if(hitboxType.isTangible()
				|| tangibleOccupants.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if this tile is a tangible tile type or has any tangible occupants
	 * that do not include the entityToIgnore
	 */
	public boolean isTangibleTile(Entity entityToIgnore) {
		if(hitboxType.isTangible()
				|| tangibleOccupants.size() > 1) {
			return true;
		} else if(tangibleOccupants.size() == 1) {
			return !tangibleOccupants.contains(entityToIgnore);
		} else {
			return false;
		}
	}
	
	public void addSpriteOverlay(Sprite overlay) {
		if(spriteOverlays == null) {
			spriteOverlays = new Array<Sprite>();
		}
		spriteOverlays.add(overlay);
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void setHitboxType(HitboxType hitboxType) {
		this.hitboxType = hitboxType;
	}
	
	public void setSpecialTile(SpecialTile specialTile) {
		this.specialTile = specialTile;
	}
	
	public Array<Sprite> getSpriteOverlays() {
		return spriteOverlays;
	}

	public Set<Entity> getAttackableOccupants() {
		return attackableOccupants;
	}
	
	public Set<Entity> getTangibleOccupants() {
		return tangibleOccupants;
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
	
	public SpecialTile getSpecialTile() {
		return specialTile;
	}
}
