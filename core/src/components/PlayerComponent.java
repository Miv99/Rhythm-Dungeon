package components;

import java.awt.Point;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
	// Previous position of the player
	private Point lastPosition = new Point(0, 0);
	// True if the player moved in the last beat; resets to false after every beat window
	private boolean movedInLastBeat;
	private String weaponEquipped;
	
	public void setWeaponEquipped(String weaponEquipped) {
		this.weaponEquipped = weaponEquipped;
	}
	
	public String getWeaponEquipped() {
		return weaponEquipped;
	}

	public Point getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(int x, int y) {
		lastPosition.x = x;
		lastPosition.y = y;
	}

	public boolean isMovedInLastBeat() {
		return movedInLastBeat;
	}

	public void setMovedInLastBeat(boolean movedInLastBeat) {
		this.movedInLastBeat = movedInLastBeat;
	}
}
