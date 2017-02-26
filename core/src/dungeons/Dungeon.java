package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.miv.Attack;
import com.miv.ComponentMappers;
import com.miv.GameCamera;
import com.miv.Movement;
import com.miv.Movement.Direction;
import com.miv.Options;

import audio.Audio;
import audio.Song;
import audio.SongSelector;
import audio.SongSelector.NoSelectableMusicException;
import components.AnimationComponent;
import components.HitboxComponent;
import components.ImageComponent;
import data.AnimationData;
import data.AnimationLoader;
import graphics.Images;
import hud.BeatLine;
import utils.GeneralUtils;

/**
 * Note: all calculations are optimized so that there is a maximum of 50 floors per dungeon.
 */
public class Dungeon {
	public static class DungeonParams {
		private int maxFloors;
		private AnimationLoader animationLoader;
		private Entity player;
		private Options options;
		private Audio audio;
		private Images images;
		
		public DungeonParams(int maxFloors, AnimationLoader animationLoader, Entity player, Options options, Audio audio, Images images) {
			this.maxFloors = maxFloors;
			this.animationLoader = animationLoader;
			this.player = player;
			this.options = options;
			this.audio = audio;
			this.images = images;
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
		actionBar = new ActionBar(dungeonParams.options);
		tileRenderSystem = new TileRenderSystem();
		songSelector = new SongSelector(dungeonParams.audio);
	}
	
	/**
	 * Returns a beat hit error margin value that decreases as floor increases
	 * TODO: tweak this
	 */
	public float calculateBeatHitErrorMarginFromFloor(int floor) {
		return Math.max(0.04f, 0.06f - ((float)floor * 0.0006f));
	}
	
	/**
	 * Returns a value proportional the time difference between each quarter beat
	 */
	public float calculateBeatMissErrorMarginFromFloor(int floor) {
		return (actionBar.getBeatLines().get(1).getTimeUntilCursorLineInSeconds() - actionBar.getBeatLines().first().getTimeUntilCursorLineInSeconds()) * 1.5f;
	}
	
	/**
	 * Returns a bpm value that scales increases as floor increases
	 */
	public static float calculateBpmFromFloor(int floor) {
		return Math.min(200f, 100f + ((float)Math.round(floor/5f) * 10f));		
	}
	
	public void enterNewFloor(int newFloor) {
		currentFloor = newFloor;

		Song song = selectNewSongByCurrentFloor();
		if(song != null) {
			dungeonParams.audio.playSong(song);
		} else {
			System.out.println("This should never appear. There is no song avaliable for floor " + newFloor + ": BPM = " + calculateBpmFromFloor(currentFloor));
		}
		
		dungeonParams.animationLoader.updateAllAnimationFrameDuration(calculateBpmFromFloor(currentFloor));
		
		actionBar.beatLines.clear();
		actionBar.spawnPrimaryBeatLines(song.getOffsetInSeconds());
		
		beatHitErrorMarginInSeconds = calculateBeatHitErrorMarginFromFloor(newFloor);
		beatMissErrorMarginInSeconds = calculateBeatMissErrorMarginFromFloor(newFloor);
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
		tileRenderSystem.update(deltaTime);
		actionBar.actionBarSystem.update(deltaTime);
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
		}
		
		public SpriteBatch getBatch() {
			return batch;
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
				
		private ActionBarSystem actionBarSystem;
		
		public ActionBar(Options options) {
			actionBarSystem = new ActionBarSystem(options.getWindowWidth());
		}
		
		/**
		 * Spawns a number of BeatLines such that the first BeatLine moves past the cursor line after [offset] seconds
		 * and there are enough BeatLines that the screen is always filled with BeatLines
		 */
		public void spawnPrimaryBeatLines(float offsetInSeconds) {
			int counter = 0;
			for(float time = offsetInSeconds; time < offsetInSeconds + dungeonParams.options.getActionBarScrollInterval(); time += (60f/(calculateBpmFromFloor(currentFloor) * 4f))) {
				if(counter % 4 == 0) {
					beatLines.add(new BeatLine(time, true));
				} else {
					beatLines.add(new BeatLine(time, false));
				}
				counter++;
			}
		}
		
		public void fireAttackAction() {
			BeatLine nearestLeft = getNearestCircleFromLeft();
			BeatLine nearestRight = getNearestCircleFromRight();
			
			if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
				nearestLeft.onAttackHit();
				
				Attack.entityAttack(floors[currentFloor], dungeonParams.player);
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
				nearestRight.onAttackHit();
				
				Attack.entityAttack(floors[currentFloor], dungeonParams.player);
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatMissErrorMarginInSeconds()) {
				nearestRight.onAttackMiss();
			}
		}
		
		public void fireMovementAction(Direction movementDirection) {
			BeatLine nearestLeft = getNearestCircleFromLeft();
			BeatLine nearestRight = getNearestCircleFromRight();
			System.out.println(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) + ", " + Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) + ", " + Dungeon.this.getBeatHitErrorMarginInSeconds());
			if(Math.abs(nearestLeft.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()
					&& nearestLeft.getStrongBeat()) {
				nearestLeft.onMovementHit();
				
				Movement.moveEntity(floors[currentFloor], dungeonParams.player, movementDirection);
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatHitErrorMarginInSeconds()
					&& nearestRight.getStrongBeat()) {
				nearestRight.onMovementHit();
				
				Movement.moveEntity(floors[currentFloor], dungeonParams.player, movementDirection);
			} else if(Math.abs(nearestRight.getTimeUntilCursorLineInSeconds()) <= Dungeon.this.getBeatMissErrorMarginInSeconds()) {
				nearestRight.onMovementMiss();
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
				if(b.getTimeUntilCursorLineInSeconds() >= 0) {
					if(b.getTimeUntilCursorLineInSeconds() < largest) {
						nearestRight = b;
						largest = b.getTimeUntilCursorLineInSeconds();
					}
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
			private Array<BeatLine> beatLineAdditionQueue;
			private Array<BeatLine> beatLineDeletionQueue;
			
			private int maxBeatCirclesOnScreen;
			
			private Sprite actionBarAxis;
			private Sprite cursorLine;
			private Sprite circleStrongBeat;
			private Sprite circleWeakBeat;
			
			private float circleStrongBeatWidth;
			private float circleWeakBeatWidth;
			private float actionBarAxisHeight;
			
			private float cursorLineYPos;
			private float circleStrongBeatYPos;
			private float circleWeakBeatYPos;
			
			private final float cursorLineXPos = 128f;
			private final float axisYPos = 64f;
			
			public ActionBarSystem(float windowWidth) {
				batch = new SpriteBatch();
				beatLineAdditionQueue = new Array<BeatLine>();
				beatLineDeletionQueue = new Array<BeatLine>();
				this.windowWidth = windowWidth;
				
				maxBeatCirclesOnScreen = calculateMaxBeatsOnScreen();
				
				// Get sprites
				actionBarAxis = dungeonParams.images.loadSprite("action_bar_axis");
				cursorLine = dungeonParams.images.loadSprite("action_bar_cursor_line");
				circleStrongBeat = dungeonParams.images.loadSprite("action_bar_circle_strong_beat");
				circleWeakBeat = dungeonParams.images.loadSprite("action_bar_circle_weak_beat");
				
				circleStrongBeatWidth = circleStrongBeat.getWidth();
				circleWeakBeatWidth = circleWeakBeat.getWidth();
				actionBarAxisHeight = actionBarAxis.getHeight();
				
				cursorLineYPos = axisYPos + (actionBarAxis.getHeight() - cursorLine.getHeight())/2f;
				circleStrongBeatYPos = axisYPos + (actionBarAxis.getHeight() - circleStrongBeat.getHeight())/2f;
				circleWeakBeatYPos = axisYPos + (actionBarAxis.getHeight() - circleWeakBeat.getHeight())/2f;
			}
			
			private int calculateMaxBeatsOnScreen() {
				 return Math.round(dungeonParams.options.getActionBarScrollInterval()/((60f/(calculateBpmFromFloor(currentFloor))))/4f);
			}
			
			public void setWindowWidth(float windowWidth) {
				this.windowWidth = windowWidth;
			}
			
			public SpriteBatch getBatch() {
				return batch;
			}
			
			public void update(float deltaTime) {
				batch.begin();
				
				batch.draw(actionBarAxis, 0, axisYPos, dungeonParams.options.getWindowWidth(), actionBarAxisHeight);
				batch.draw(cursorLine, cursorLineXPos, cursorLineYPos);
				
				if(!ActionBar.this.isPaused()) {
					for(BeatLine b : actionBar.getBeatLines()) {
						// Update BeatLine fields
						b.setTimeUntilCursorLineInSeconds(b.getTimeUntilCursorLineInSeconds() - deltaTime);
						if(b.getCircleIncreasingYPos()) {
							b.setCircleYPositionRelativeToAxis(b.getCircleYPositionRelativeToAxis() + (deltaTime * 500f));
						}
						
						// Draw BeatLines and circles
						if(b.getTimeUntilCursorLineInSeconds() < dungeonParams.options.getActionBarScrollInterval()) {
							float x = ((b.getTimeUntilCursorLineInSeconds()/dungeonParams.options.getActionBarScrollInterval()) * windowWidth * maxBeatCirclesOnScreen) + cursorLineXPos;
							//TODO: batch.draw the BeatLine image and the circle image
							if(b.getStrongBeat()) {
								batch.draw(circleStrongBeat, x - circleStrongBeatWidth/2f, circleStrongBeatYPos + b.getCircleYPositionRelativeToAxis());
							} else {
								batch.draw(circleWeakBeat, x - circleWeakBeatWidth/2f, circleWeakBeatYPos + b.getCircleYPositionRelativeToAxis());
							}
						}
						
						// Once the BeatLine crosses the cursor, spawn a new BeatLine
						if(b.getTimeUntilCursorLineInSeconds() <= 0
								&& !b.getReaddedToActionBar()) {
							queueBeatLineAddition(new BeatLine(b.getTimeUntilCursorLineInSeconds() + ((60f/(calculateBpmFromFloor(currentFloor) * 4f))) * 4 * maxBeatCirclesOnScreen, b.getStrongBeat()));
							b.setReaddedToActionBar(true);
						}
						if(b.getTimeUntilCursorLineInSeconds() <= -beatMissErrorMarginInSeconds * 2f
								&& !b.getDeletionQueued()) {
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
