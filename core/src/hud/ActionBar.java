package hud;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.Movement.Direction;

import components.HitboxComponent;
import components.WeaponComponent;
import dungeons.Dungeon;

public class ActionBar {
	private Array<BeatLine> beatLines = new Array<BeatLine>();
	
	private boolean paused;
	
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
		//TODO
		beatLines.add(new BeatLine(offset, true));
		
		// Create timer that spawns a BeatLine every (60/(bpm * 4)) seconds
	}
	
	public void fireAttackAction() {
		BeatLine nearestLeft = getNearestCircleFromLeft();
		BeatLine nearestRight = getNearestCircleFromRight();
		
		if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatHitErrorMarginInSeconds()) {
			nearestLeft.onAttackHit();
		} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatHitErrorMarginInSeconds()) {
			nearestRight.onAttackHit();
		} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatMissErrorMarginInSeconds()) {
			nearestRight.onAttackMiss();
		}
	}
	
	public void fireMovementAction(Direction movementDirection) {
		BeatLine nearestLeft = getNearestCircleFromLeft();
		BeatLine nearestRight = getNearestCircleFromRight();
		
		if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatHitErrorMarginInSeconds()
				&& nearestLeft.getStrongBeat()) {
			nearestLeft.onAttackHit();
		} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatHitErrorMarginInSeconds()
				&& nearestRight.getStrongBeat()) {
			nearestRight.onAttackHit();
		} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= dungeon.getBeatMissErrorMarginInSeconds()) {
			nearestRight.onAttackMiss();
		}
	}
	
	/**
	 * Returns the BeatLine nearest to the cursor line from its left side that contains a circle 
	 */
	private BeatLine getNearestCircleFromLeft() {
		BeatLine nearestLeft = null;
		float smallest = -999f;
		for(BeatLine b : beatLines) {
			if(b.getTimeUntilCursorLineInSeconds() <= 0) {
				if(b.getTimeUntilCursorLineInSeconds() > smallest) {
					nearestLeft = b;
					smallest = b.getTimeUntilCursorLineInSeconds();
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
			if(b.getTimeUntilCursorLineInSeconds() <= 0) {
				if(b.getTimeUntilCursorLineInSeconds() < largest) {
					nearestRight = b;
					largest = b.getTimeUntilCursorLineInSeconds();
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
	
	public boolean isPaused() {
		return paused;
	}
}
