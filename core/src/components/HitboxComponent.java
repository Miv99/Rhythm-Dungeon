package components;

import com.badlogic.ashley.core.Component;

import data.HitboxData;
import data.HitboxData.HitboxType;
import utils.Point;

public class HitboxComponent implements Component {
	private Point position;
	private HitboxType[][] hitbox;
	
	public HitboxComponent(HitboxData hitboxData, Point position) {
		hitbox = hitboxData.getHitbox();
		this.position = position;
	}
	
	public HitboxType[][] getHitbox() {
		return hitbox;
	}
	
	public Point getPosition() {
		return position;
	}
}
