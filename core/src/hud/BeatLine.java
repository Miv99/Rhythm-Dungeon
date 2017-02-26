package hud;

public class BeatLine {
	private enum CircleState {
		Alive,
		Dying,
		Dead, // Circle has been hit
		Locked // Circle has been missed
	}
	
	// If the beatline is on a full note as opposed to a quarter note
	private boolean strongBeat;
	private CircleState circleState;
	// Time until the beat line reaches the cursor line
	private float timeUntilCursorLineInSeconds;
	// If the circle on the beatline has had the attack key pressed
	private boolean attackTriggered;
	// If the circle on the beatline has had the movement key pressed
	private boolean movementTriggered;
	private float circleYPositionRelativeToAxis = 0f;
	private float circleAlpha;
	// If circleYPositionRelativeToAxis is increased every frame update
	private boolean circleIncreasingYPos;
	
	// If the BeatLine is queued to be deleted from the ActionBar
	private boolean deletionQueued;
	// If a duplicate BeatLine has been added to the ActionBar again
	private boolean readdedToActionBar;
	
	public BeatLine(float timeUntiLCursorLineInSeconds, boolean strongBeat) {
		this.timeUntilCursorLineInSeconds = timeUntiLCursorLineInSeconds;
		this.strongBeat = strongBeat;
	}
	
	public void onAttackHit() {
		if(movementTriggered) {
			setCircleState(CircleState.Dying);
		} else {
			if(!strongBeat) {
				setCircleState(CircleState.Dying);
			}
		}
		//TODO: play sound effect
	}
	
	public void onAttackMiss() {
		setCircleState(CircleState.Locked);
		//TODO: play sound effect
	}
	
	public void onMovementHit() {
		if(movementTriggered) {
			setCircleState(CircleState.Dying);
		} else {
			if(!strongBeat) {
				setCircleState(CircleState.Dying);
			}
		}
		//TODO: play sound effect
	}
	
	public void onMovementMiss() {
		setCircleState(CircleState.Locked);
		//TODO: play sound effect
	}
	
	public void setCircleState(CircleState newState) {
		circleState = newState;
		if(circleState.equals(CircleState.Dying)) {
			this.setCircleIncreasingYPos(true);
		}
	}
	
	
	public void setCircleYPositionRelativeToAxis(float circleYPositionRelativeToAxis) {
		this.circleYPositionRelativeToAxis = circleYPositionRelativeToAxis;
	}
	
	public void setCircleIncreasingYPos(boolean circleIncreasingYPos) {
		this.circleIncreasingYPos = circleIncreasingYPos;
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
	
	public boolean getDeletionQueued() {
		return deletionQueued;
	}
	
	public float getTimeUntilCursorLineInSeconds() {
		return timeUntilCursorLineInSeconds;
	}
	
	public boolean getStrongBeat() {
		return strongBeat;
	}
	
	public CircleState getCircleState() {
		return circleState;
	}
	
	public boolean getAttackTriggered() {
		return attackTriggered;
	}
	
	public boolean getMovementTriggered() {
		return movementTriggered;
	}
	
	public float getCircleYPositionRelativeToAxis() {
		return circleYPositionRelativeToAxis;
	}
	
	public float getCircleAlpha() {
		return circleAlpha;
	}
	
	public boolean getReaddedToActionBar() {
		return readdedToActionBar;
	}
	
	public boolean getCircleIncreasingYPos() {
		return circleIncreasingYPos;
	}
}