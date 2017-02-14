package dungeons;

import audio.Audio;
import audio.SongSelector;

/**
 * Note: all calculations are optimized so that there is a maximum of 50 floors per dungeon.
 */
public class Dungeon {
	private SongSelector songSelector;
	private int currentFloor;
	
	private float beatHitErrorMarginInSeconds;
	private float beatMissErrorMarginInSeconds;
	
	public Dungeon(Audio audio) {
		songSelector = new SongSelector(audio);
	}
	
	/**
	 * Returns a beat hit error margin value that decreases as floor increases
	 */
	public static float calculateBeatHitErrorMarginFromFloor(int floor) {
		return Math.max(0.02f, 0.05f - ((float)floor * 0.0006f));
	}
	
	/**
	 * Returns a value proportional to beat hit error margin
	 */
	public static float calculateBeatMissErrorMarginFromFloor(int floor) {
		return calculateBeatHitErrorMarginFromFloor(floor) * 1.5f;
	}
	
	/**
	 * Returns a bpm value that scales increases as floor increases
	 */
	public static float calculateBpmFromFloor(int floor) {
		return Math.min(200f, 100f + ((float)Math.round(floor/5f) * 10f));		
	}
	
	public void setCurrentFloor(int newFloor) {
		beatHitErrorMarginInSeconds = calculateBeatHitErrorMarginFromFloor(newFloor);
		beatMissErrorMarginInSeconds = calculateBeatMissErrorMarginFromFloor(newFloor);
		currentFloor = newFloor;
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	public float getBeatHitErrorMarginInSeconds() {
		return beatHitErrorMarginInSeconds;
	}
	
	public float getBeatMissErrorMarginInSeconds() {
		return beatMissErrorMarginInSeconds;
	}
	
	public SongSelector getSongSelector() {
		return songSelector;
	}
}
