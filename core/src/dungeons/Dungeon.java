package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.GameCamera;
import com.miv.Movement.Direction;
import com.miv.Options;

import audio.Audio;
import audio.Song;
import audio.SongSelector;
import audio.SongSelector.NoSelectableMusicException;
import components.HitboxComponent;
import components.WeaponComponent;
import graphics.Images;
import hud.BeatLine;

/**
 * Note: all calculations are optimized so that there is a maximum of 50 floors per dungeon.
 */
public class Dungeon {
	public static class DungeonParams {
		private int maxFloors;
		private Entity player;
		private Options options;
		private Audio audio;
		private Images images;
		private GameCamera camera;
		
		public DungeonParams(int maxFloors, Entity player, Options options, Audio audio, Images images, GameCamera camera) {
			this.maxFloors = maxFloors;
			this.player = player;
			this.options = options;
			this.audio = audio;
			this.images = images;
			this.camera = camera;
		}
		
		public int getMaxFloors() {
			return maxFloors;
		}
		
		public Entity getPlayer() {
			return player;
		}
		
		public Options getOptions() {
			return options;
		}
		
		public Audio getAudio() {
			return audio;
		}
		
		public Images getImages() {
			return images;
		}
		
		public GameCamera getCamera() {
			return camera;
		}
	}
	
	private DungeonParams dungeonParams;
	
	private ActionBar actionBar;
	private TileRenderSystem tileRenderSystem;
	
	private SongSelector songSelector;
	private int currentFloor;
	
	private Floor[] floors;
	
	private float beatHitErrorMarginInSeconds;
	private float beatMissErrorMarginInSeconds;
	
	public Dungeon(DungeonParams dungeonParams) {
		this.dungeonParams = dungeonParams;
		actionBar = new ActionBar(dungeonParams.player, dungeonParams.options);
		tileRenderSystem = new TileRenderSystem();
		songSelector = new SongSelector(dungeonParams.audio);
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
				
		Song song = selectNewSongByCurrentFloor();
		if(song != null) {
			dungeonParams.audio.playSong(song);
		} else {
			System.out.println("This should never appear. There is no song avaliable for floor " + newFloor + ": BPM = " + calculateBpmFromFloor(currentFloor));
		}
		
		actionBar.actionBarSystem.setScrollInterval(actionBar.actionBarSystem.calculateScrollInterval(calculateBpmFromFloor(currentFloor)));
		actionBar.beatLines.clear();
		actionBar.spawnPrimaryBeatLines(song.getOffsetInSeconds());
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
	
	public void update(float deltaTime) {
		actionBar.actionBarSystem.update(deltaTime);
		tileRenderSystem.update(deltaTime);
	}
	
	public void setFloors(Floor[] floors) {
		this.floors = floors;
	}
	
	public TileRenderSystem getTileRenderSystem() {
		return tileRenderSystem;
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
	
	public ActionBar getActionBar() {
		return actionBar;
	}
	
	public Floor[] getFloors() {
		return floors;
	}
	
	public Entity getPlayer() {
		return dungeonParams.player;
	}
	
	
	
	public class TileRenderSystem {
		private SpriteBatch batch;
		
		public TileRenderSystem() {
			batch = new SpriteBatch();
			batch.setProjectionMatrix(dungeonParams.camera.combined);
		}
		
		public void onCameraPositionUpdate() {
			batch.setProjectionMatrix(dungeonParams.camera.combined);
		}
		
		public void update(float deltaTime) {
			batch.begin();
			for(Tile[] col : floors[currentFloor].getTiles()) {
				for(Tile tile : col) {
					Point mapPosition = tile.getMapPosition();
					batch.draw(tile.getSprite(), mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
					
					// Draw special tile, if any
					if(tile.getSpecialTile() != null
							&& tile.getSpecialTile().getTileOverlay() != null) {
						batch.draw(tile.getSpecialTile().getTileOverlay(), mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
					}
				}
			}
			batch.end();
		}
	}
	
	
	public class ActionBar {
		private Array<BeatLine> beatLines = new Array<BeatLine>();
		
		private boolean paused;
		
		private WeaponComponent playerWeapon;
		private HitboxComponent playerHitbox;
		
		private ActionBarSystem actionBarSystem;
		
		public ActionBar(Entity player, Options options) {
			actionBarSystem = new ActionBarSystem(options.getWindowWidth());
			playerWeapon = ComponentMappers.wm.get(player);
			playerHitbox = ComponentMappers.hm.get(player);
		}
		
		/**
		 * Spawns a number of BeatLines such that the first BeatLine moves past the cursor line after [offset] seconds
		 * and there are enough BeatLines that the screen is always filled with BeatLines
		 */
		public void spawnPrimaryBeatLines(float offsetInSeconds) {			
			for(float time = offsetInSeconds; time < offsetInSeconds + actionBarSystem.scrollIntervalInSeconds * 2; time += (60f/(calculateBpmFromFloor(currentFloor) * 4f))) {
				beatLines.add(new BeatLine(time, true));
			}
		}
		
		public void fireAttackAction() {
			BeatLine nearestLeft = getNearestCircleFromLeft();
			BeatLine nearestRight = getNearestCircleFromRight();
			
			if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
				nearestLeft.onAttackHit();
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
				nearestRight.onAttackHit();
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatMissErrorMarginInSeconds()) {
				nearestRight.onAttackMiss();
			}
		}
		
		public void fireMovementAction(Direction movementDirection) {
			BeatLine nearestLeft = getNearestCircleFromLeft();
			BeatLine nearestRight = getNearestCircleFromRight();
			
			if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()
					&& nearestLeft.getStrongBeat()) {
				nearestLeft.onAttackHit();
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()
					&& nearestRight.getStrongBeat()) {
				nearestRight.onAttackHit();
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatMissErrorMarginInSeconds()) {
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
		
		public ActionBarSystem getActionBarSystem() {
			return actionBarSystem;
		}
		
		
		/**
		 * Updates and renders the Action Bar
		 */
		public class ActionBarSystem {
			private SpriteBatch batch;
			
			private float windowWidth;
			// Time it takes in seconds for a BeatLine to travel the entire width of the window
			private float scrollIntervalInSeconds;
			private Array<BeatLine> beatLineAdditionQueue;
			private Array<BeatLine> beatLineDeletionQueue;
			
			private final float cursorLineXPos = 50f;
			
			public ActionBarSystem(float windowWidth) {
				batch = new SpriteBatch();
				beatLineAdditionQueue = new Array<BeatLine>();
				beatLineDeletionQueue = new Array<BeatLine>();
				this.windowWidth = windowWidth;
			}
			
			public float calculateScrollInterval(float bpm) {
				//TODO: tweak magic number
				return 160f/bpm;
			}
			
			public void setWindowWidth(float windowWidth) {
				this.windowWidth = windowWidth;
			}
			
			public void setScrollInterval(float scrollIntervalInSeconds) {
				this.scrollIntervalInSeconds = scrollIntervalInSeconds;
			}
			
			public void update(float deltaTime) {
				batch.begin();
				if(!ActionBar.this.isPaused()) {
					for(BeatLine b : actionBar.getBeatLines()) {
						b.setTimeUntilCursorLineInSeconds(b.getTimeUntilCursorLineInSeconds() - deltaTime);
						
						if(b.getTimeUntilCursorLineInSeconds() < scrollIntervalInSeconds) {
							float x = ((b.getTimeUntilCursorLineInSeconds()/scrollIntervalInSeconds) * windowWidth) + cursorLineXPos;
							//TODO: batch.draw the BeatLine image and the circle image
						}
						
						// Once the BeatLine crosses the cursor, a new BeatLine is spawned
						if(b.getTimeUntilCursorLineInSeconds() <= 0
								&& !b.getDeletionQueued()) {
							queueBeatLineAddition(new BeatLine(b.getTimeUntilCursorLineInSeconds() + scrollIntervalInSeconds, b.getStrongBeat()));
							queueBeatLineDeletion(b);
						}
					}
					
					fireBeatLineAdditionQueue();
					fireBeatLineDeletionQueue();
				}
				batch.end();
			}
			
			private void queueBeatLineAddition(BeatLine newBeatLine) {
				beatLineAdditionQueue.add(newBeatLine);
			}
			
			private void queueBeatLineDeletion(BeatLine target) {
				target.setDeletionQueued(true);
				beatLineDeletionQueue.add(target);
			}
			
			private void fireBeatLineAdditionQueue() {
				if(beatLineAdditionQueue.size > 0) {
					actionBar.getBeatLines().addAll(beatLineAdditionQueue);
					beatLineAdditionQueue.clear();
				}
			}
			
			private void fireBeatLineDeletionQueue() {
				if(beatLineDeletionQueue.size > 0) {
					actionBar.getBeatLines().removeAll(beatLineDeletionQueue, false);
					beatLineDeletionQueue.clear();
				}
			}
		}
	}
}
