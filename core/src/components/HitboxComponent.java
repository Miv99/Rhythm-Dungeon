package components;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.miv.Movement.Direction;

import data.HitboxData;
import data.HitboxData.HitboxType;
import utils.GeneralUtils;

public class HitboxComponent implements Component {
	private Point mapPosition;
	// Each grid point on the 2D array represents one map tile that the entire hitbox takes up
	private HitboxType[][] hitbox;
	private HashMap<Direction, HitboxType[][]> directionalHitboxes;
	private Direction facing;
	// The last horizontal direction that the entity has faced
	private Direction horizontalFacing;
	
	public HitboxComponent(HitboxData hitboxDataFacingRight, Point mapPosition) {
		hitbox = hitboxDataFacingRight.getHitbox();
		this.mapPosition = mapPosition;
		
		// Create directional hitboxes for facing left and right
		directionalHitboxes = new HashMap<Direction, HitboxType[][]>();
		directionalHitboxes.put(Direction.Right, hitbox);
		directionalHitboxes.put(Direction.Left, (HitboxType[][])GeneralUtils.horizontallyFlipArray(hitbox));
		
		facing = Direction.Right;
		horizontalFacing = Direction.Right;
	}
	
	public void faceDirection(Direction direction) {
		facing = direction;
		if(direction.equals(Direction.Left)
				|| direction.equals(Direction.Right)) {
			horizontalFacing = direction;
			hitbox = directionalHitboxes.get(direction);
		}
	}
	
	public void setMapPosition(int x, int y) {
		mapPosition.x = x;
		mapPosition.y = y;
	}
	
	public HitboxType[][] getHitbox() {
		return hitbox;
	}
	
	public Point getMapPosition() {
		return mapPosition;
	}
	
	public Direction getFacing() {
		return facing;
	}
	
	public Direction getHorizontalFacing() {
		return horizontalFacing;
	}
	
	public HashMap<Direction, HitboxType[][]> getDirectionalHitboxes() {
		return directionalHitboxes;
	}
}
