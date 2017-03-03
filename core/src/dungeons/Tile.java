package dungeons;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import data.HitboxData.HitboxType;
import special_tiles.SpecialTile;
import utils.MapUtils;

public class Tile {
	private Set<Entity> attackableOccupants;
	private Set<Entity> tangibleOccupants;
	
	// Sprite of the tile
	private Sprite sprite;
	
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
		if(hitboxType.getTangible()
				|| tangibleOccupants.size() > 0) {
			return true;
		} else {
			return false;
		}
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
