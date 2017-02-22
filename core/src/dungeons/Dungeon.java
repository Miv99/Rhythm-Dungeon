package dungeons;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.Movement.Direction;

import audio.Audio;
import audio.Song;
import audio.SongSelector;
import audio.SongSelector.NoSelectableMusicException;
import components.HitboxComponent;
import components.WeaponComponent;
import hud.BeatLine;
import systems.TileRenderSystem;

/**
 * Note: all calculations are optimized so that there is a maximum of 50 floors per dungeon.
 */
public class Dungeon {
	private ActionBar actionBar;
	private Audio audio;
	private SongSelector songSelector;
	private TileRenderSystem tileRenderSystem;
	private int currentFloor;
	
	private Floor[] floors;
	
	private float beatHitErrorMarginInSeconds;
	private float beatMissErrorMarginInSeconds;
	
	public Dungeon(Entity player, Audio audio, TileRenderSystem tileRenderSystem) {
		actionBar = new ActionBar(player, this);
		this.audio = audio;
		songSelector = new SongSelector(audio);
		this.tileRenderSystem = tileRenderSystem;
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
		return calculateBeatHitErrorMarginFromFloor(floor) * 2f;
	}
	
	/**
	 * Returns a bpm value that scales increases as floor increases
	 */
	public static float calculateBpmFromFloor(int floor) {
		return Math.min(200f, 100f + ((float)Math.round(floor/5f) * 10f));		
	}
	
	public void enterNewFloor(int newFloor) {
		beatHitErrorMarginInSeconds = calculateBeatHitErrorMarginFromFloor(newFloor);
		beatMissErrorMarginInSeconds = calculateBeatMissErrorMarginFromFloor(newFloor);
		currentFloor = newFloor;
		
		tileRenderSystem.setTiles(floors[currentFloor].getTiles());
		
		Song song = selectNewSongByCurrentFloor();
		if(song != null) {
			audio.playSong(song);
		} else {
			System.out.println("This should never appear. There is no song avaliable for floor " + newFloor + ": BPM = " + calculateBpmFromFloor(currentFloor));
		}
		
		actionBar.beginBeatLineSpawning(song.getOffsetInSeconds());
	}
	
	/**
	 * Uses the current floor to select a new song to play
	 */
	private Song selectNewSongByCurrentFloor() {
		float bpm = calculateBpmFromFloor(currentFloor);
		Song song = null;
		try {
			song = songSelector.selectSongByBpm(bpm);
		} catch (NoSelectableMusicException e) {
			e.printStackTrace();
		}
		return song;
	}
	
	public void setFloors(Floor[] floors) {
		this.floors = floors;
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
		public void beginBeatLineSpawning(float offsetInSeconds) {
			//TODO
			beatLines.add(new BeatLine(offsetInSeconds, true));
			
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
}
