package hud;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;

import components.HitboxComponent;
import components.WeaponComponent;
import dungeons.Dungeon;

public class ActionBar {
	public enum Direction {
		Up,
		Down,
		Left,
		Right
	}
	private enum CircleState {
		Alive,
		Dying,
		Dead, // Circle has been hit
		Locked // Circle has been missed
	}
	
	private Array<BeatLine> beatLines = new Array<BeatLine>();
	
	private Dungeon dungeon;
	private WeaponComponent playerWeapon;
	private HitboxComponent playerHitbox;
	
	public ActionBar(Entity player, Dungeon dungeon) {
		playerWeapon = ComponentMappers.wm.get(player);
		playerHitbox = ComponentMappers.hm.get(player);
		this.dungeon = dungeon;
	}
	
	/**
	 * Begins spawning BeatLines such that the first BeatLine moves past the cursor line after [offset] seconds
	 */
	public void beginBeatLineSpawning(float offset) {
		
	}
	
	public void fireAttackAction() {
		BeatLine nearestLeft = getNearestCircleFromLeft();
		BeatLine nearestRight = getNearestCircleFromRight();
		
		if(Math.abs(nearestLeft.timeUntilCursorLineInSeconds) <= dungeon.getBeatHitErrorMarginInSeconds()) {
			nearestLeft.onAttackHit();
		} else if(Math.abs(nearestRight.timeUntilCursorLineInSeconds) <= dungeon.getBeatHitErrorMarginInSeconds()) {
			nearestRight.onAttackHit();
		} else if(Math.abs(nearestRight.timeUntilCursorLineInSeconds) <= dungeon.getBeatMissErrorMarginInSeconds()) {
			nearestRight.onAttackMiss();
		}
	}
	
	public void fireMovementAction(Direction movementDirection) {
		//TODO
	}
	
	/**
	 * Returns the BeatLine nearest to the cursor line from its left side that contains a circle 
	 */
	private BeatLine getNearestCircleFromLeft() {
		BeatLine nearestLeft = null;
		float smallest = -999f;
		for(BeatLine b : beatLines) {
			if(b.timeUntilCursorLineInSeconds <= 0) {
				if(b.timeUntilCursorLineInSeconds > smallest) {
					nearestLeft = b;
					smallest = b.timeUntilCursorLineInSeconds;
				}
			} else {
				break;
			}
		}
		return nearestLeft;
	}
	
	/**
	 * Returns the BeatLine nearest to the cursor line from its right side that contains a circle 
	 */
	private BeatLine getNearestCircleFromRight() {
		BeatLine nearestRight = null;
		float largest = 999f;
		for(BeatLine b : beatLines) {
			if(b.timeUntilCursorLineInSeconds <= 0) {
				if(b.timeUntilCursorLineInSeconds < largest) {
					nearestRight = b;
					largest = b.timeUntilCursorLineInSeconds;
				}
			} else {
				break;
			}
		}
		return nearestRight;
	}
	
	public Array<BeatLine> getBeatLines() {
		return beatLines;
	}
	
	public class BeatLine {
		// If the beatline is on a full note as opposed to a quarter note
		private boolean strongBeat;
		private CircleState circleState;
		// Time until the beat line reaches the cursor line
		private float timeUntilCursorLineInSeconds;
		// If the circle on the beatline has had the attack key pressed
		private boolean attackTriggered;
		// If the circle on the beatline has had the movement key pressed
		private boolean movementTriggered;
		private float circleYPositionRelativeToAxis;
		private float circleAlpha;
		
		public void onAttackHit() {
			if(movementTriggered) {
				circleState = CircleState.Dying;
			} else {
				if(!strongBeat) {
					circleState = CircleState.Dying;
				}
			}
			//TODO: play sound effect
		}
		
		public void onAttackMiss() {
			circleState = CircleState.Locked;
			//TODO: play sound effect
		}
	}
}
