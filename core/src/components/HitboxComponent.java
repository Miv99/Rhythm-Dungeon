package components;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.miv.EntityActions.Direction;

import data.HitboxData;
import data.HitboxData.HitboxType;

public class HitboxComponent implements Component {
	private Point mapPosition;
	// Each grid point on the 2D array represents one map tile that the entire hitbox takes up
	private HitboxType[][] hitbox;
	private HashMap<String, HitboxData> hitboxesData = new HashMap<String, HitboxData>();
	private Direction facing;
	// The last horizontal direction that the entity faced
	private Direction horizontalFacing;
	// Point on hitbox where attacks originate from; defaulted to (0, 0) if none specified from HitboxData
	private Point attackOrigin;
	private String hitboxName;
	
	private boolean movementDisabled;
	private float movementDisabledTimeInBeats;
	
	public HitboxComponent(String hitboxName, HashMap<String, HitboxData> hitboxesData, Point mapPosition) {
		facing = Direction.RIGHT;
		horizontalFacing = Direction.RIGHT;
		
		this.hitboxName = hitboxName;
		this.hitboxesData = hitboxesData;
		this.mapPosition = mapPosition;
		try {
			hitbox = hitboxesData.get(hitboxName + "_" + horizontalFacing.getStringRepresentation()).getHitbox();
		} catch(NullPointerException e) {
			System.out.println("Hitbox data for \"" + hitboxName + "\" does not exist.");
		}
		
		// Find attack origin
		attackOrigin = new Point(0, 0);
		for(int x = 0; x < hitbox.length; x++) {
			for(int y = 0; y < hitbox[x].length; y++) {
				if(hitbox[x][y].isAttackOrigin()) {
					attackOrigin.x = x;
					attackOrigin.y = y;
					break;
				}
			}
		}
	}
	
	public void onNewBeat(float deltaBeat) {
		if(movementDisabled) {
			movementDisabledTimeInBeats -= deltaBeat;
			if(movementDisabledTimeInBeats <= 0) {
				movementDisabled = false;
			}
		}
	}
	
	public void faceDirection(Direction direction) {
		facing = direction;
		if(direction.isHorizontal()) {
			horizontalFacing = direction;
			hitbox = hitboxesData.get(hitboxName + "_" + horizontalFacing.getStringRepresentation()).getHitbox();
			
			// Flip attack origin horizontally
			attackOrigin.x = hitbox.length - attackOrigin.x - 1;
		}
	}
	
	public void setMapPosition(int x, int y) {
		mapPosition.x = x;
		mapPosition.y = y;
	}
	
	public void disableMovement(float movementDisabledTimeInBeats) {
		movementDisabled = true;
		this.movementDisabledTimeInBeats = movementDisabledTimeInBeats;
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
	
	public Point getAttackOrigin() {
		return attackOrigin;
	}
	
	public boolean isMovementDisabled() {
		return movementDisabled;
	}
	
	public HashMap<String, HitboxData> getHitboxesData() {
		return hitboxesData;
	}
	
	public String getHitboxName() {
		return hitboxName;
	}
}
