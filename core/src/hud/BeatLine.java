package hud;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.miv.Attack;
import com.miv.Movement;
import com.miv.Movement.Direction;
import com.miv.Options;

import dungeons.Dungeon;
import dungeons.Floor;

public class BeatLine {
	private enum CircleState {
		Alive,
		Dying,
		Dead, // Circle has been hit
		Locked // Circle has been missed
	}
	
	// If the beatline is on a full note as opposed to a quarter note
	private boolean strongBeat;
	// State of strong circle
	private CircleState circleStrongState;
	// State of weak circle
	private CircleState circleWeakState;
	// Time until the beat line reaches the cursor line
	private float timeUntilCursorLineInSeconds;
	// If the circle on the beatline has had the attack key pressed
	private boolean attackTriggered;
	private float circleStrongYPositionRelativeToAxis = 0f;
	private float circleWeakYPositionRelativeToAxis = 0f;
	// If circleYPositionRelativeToAxis is increased every frame update
	private boolean circleStrongIncreasingYPos;
	private boolean circleWeakIncreasingYPos;
	
	// If the BeatLine is queued to be deleted from the ActionBar
	private boolean deletionQueued;
	// If a duplicate BeatLine has been added to the ActionBar again
	private boolean readdedToActionBar;
	// If the BeatLine has fired the player action queue in ActionBarSystem
	private boolean firedPlayerActionQueue;
	
	public BeatLine(float timeUntiLCursorLineInSeconds, boolean strongBeat) {
		this.timeUntilCursorLineInSeconds = timeUntiLCursorLineInSeconds;
		this.strongBeat = strongBeat;
		
		if(strongBeat) {
			circleStrongState = CircleState.Alive;
		}
		circleWeakState = CircleState.Alive;
	}
	
	public void onAttackHit(Options options, Dungeon dungeon, Entity player, Entity target, String attackName) {
		if(circleWeakState.equals(CircleState.Alive)) {
			circleWeakState = CircleState.Dying;
			circleWeakIncreasingYPos = true;
			
			//TODO: play sound effect
			
			Attack.entityStartAttack(options, dungeon, player, target, attackName);
		}
	}
	
	public void onAttackMiss() {
		circleWeakState = CircleState.Locked;
	}
	
	public void onMovementHit(Engine engine, Floor floor, Entity player, Direction movementDirection) {
		if(circleStrongState.equals(CircleState.Alive)) {
			circleStrongState = CircleState.Dying;
			circleStrongIncreasingYPos = true;
			
			//TODO: play sound effect
			
			Movement.moveEntity(engine, floor, player, movementDirection);
		}
	}
	
	public void onMovementMiss() {
		circleStrongState = CircleState.Locked;
		//TODO: play sound effect
	}
	
	
	public void setCircleStrongYPositionRelativeToAxis(float circleStrongYPositionRelativeToAxis) {
		this.circleStrongYPositionRelativeToAxis = circleStrongYPositionRelativeToAxis;
	}
	
	public void setCircleWeakYPositionRelativeToAxis(float circleWeakYPositionRelativeToAxis) {
		this.circleWeakYPositionRelativeToAxis = circleWeakYPositionRelativeToAxis;
	}
	
	public void setDeletionQueued(boolean deletionQueued) {
		this.deletionQueued = deletionQueued;
	}
	
	public void setTimeUntilCursorLineInSeconds(float newTime) {
		timeUntilCursorLineInSeconds = newTime;
	}
	
	public void setReaddedToActionBar(boolean readdedToActionBar) {
		this.readdedToActionBar = readdedToActionBar;
	}
	
	public void setFiredPlayerActionQueue(boolean firedPlayerActionQueue) {
		this.firedPlayerActionQueue = firedPlayerActionQueue;
	}
	
	public boolean getDeletionQueued() {
		return deletionQueued;
	}
	
	public float getTimeUntilCursorLineInSeconds() {
		return timeUntilCursorLineInSeconds;
	}
	
	public boolean getFiredPlayerActionQueue() {
		return firedPlayerActionQueue;
	}
	
	public boolean getStrongBeat() {
		return strongBeat;
	}
	
	public CircleState getCircleWeakState() {
		return circleWeakState;
	}
	
	public CircleState getCircleStrongState() {
		return circleStrongState;
	}
	
	public boolean getAttackTriggered() {
		return attackTriggered;
	}
	
	public float getCircleStrongYPositionRelativeToAxis() {
		return circleStrongYPositionRelativeToAxis;
	}
	
	public float getCircleWeakYPositionRelativeToAxis() {
		return circleWeakYPositionRelativeToAxis;
	}
	
	public boolean getReaddedToActionBar() {
		return readdedToActionBar;
	}
	
	public boolean getCircleWeakIncreasingYPos() {
		return circleWeakIncreasingYPos;
	}
	
	public boolean getCircleStrongIncreasingYPos() {
		return circleStrongIncreasingYPos;
	}
}