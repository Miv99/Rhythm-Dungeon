package special_tiles;

public class WarningTile {
	private float maxTimeUntilAttackInBeats;
	private float timeUntilAttackInBeats;
	private int x;
	private int y;
	
	public WarningTile(float timeUntilAttackInBeats, int x, int y) {
		maxTimeUntilAttackInBeats = timeUntilAttackInBeats;
		this.timeUntilAttackInBeats = timeUntilAttackInBeats;
		this.x = x;
		this.y = y;
	}
	
	public void onNewBeat(float deltaBeat) {
		timeUntilAttackInBeats -= deltaBeat;
	}
	
	public void setTimeUntilAttackInBeats(int timeUntilAttackInBeats) {
		this.timeUntilAttackInBeats = timeUntilAttackInBeats;
	}
	
	public float getMaxTimeUntilAttackInBeats() {
		return maxTimeUntilAttackInBeats;
	}
	
	public float getTimeUntilAttackInBeats() {
		return timeUntilAttackInBeats;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
