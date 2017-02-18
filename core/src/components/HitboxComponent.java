package components;

import java.awt.Point;

import com.badlogic.ashley.core.Component;

import data.HitboxData;
import data.HitboxData.HitboxType;

public class HitboxComponent implements Component {
	private Point mapPosition;
	// Each grid point on the 2D array represents one map tile that the entire hitbox takes up
	private HitboxType[][] hitbox;
	
	public HitboxComponent(HitboxData hitboxData, Point mapPosition) {
		hitbox = hitboxData.getHitbox();
		this.mapPosition = mapPosition;
	}
	
	public HitboxType[][] getHitbox() {
		return hitbox;
	}
	
	public Point getMapPosition() {
		return mapPosition;
	}
}
