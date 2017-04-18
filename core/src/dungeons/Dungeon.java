package dungeons;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.EntityActions;
import com.miv.EntityActions.Direction;
import com.miv.EntityActions.EntityAttackParams;
import com.miv.Options;

import audio.Audio;
import audio.Song;
import audio.SongSelector;
import audio.SongSelector.NoSelectableMusicException;
import components.AnimationComponent;
import components.AttackComponent;
import components.EntityAIComponent;
import components.HitboxComponent;
import components.MovementAIComponent;
import data.AnimationLoader;
import data.EntityLoader;
import factories.DungeonFactory;
import factories.EntityFactory;
import graphics.Images;
import hud.BeatLine;
import hud.BeatLine.CircleState;
import movement_ai.PathFinder;
import special_tiles.WarningTile;
import systems.DeathSystem;

/**
 * Note: all calculations are optimized so that there is a maximum of 50 floors per dungeon.
 */
public class Dungeon {
	public static class DungeonParams {
		private Engine engine;
		private int maxFloors;
		private EntityLoader entityLoader;
		private AnimationLoader animationLoader;
		private Entity player;
		private Options options;
		private Audio audio;
		private Images images;
		private EntityFactory entityFactory;
		private DeathSystem deathSystem;
		
		public DungeonParams(Engine engine, int maxFloors, AnimationLoader animationLoader, EntityLoader entityLoader,
				Entity player, Options options, Audio audio, Images images, EntityFactory entityFactory, DeathSystem deathSystem) {
			this.engine = engine;
			this.maxFloors = maxFloors;
			this.entityLoader = entityLoader;
			this.animationLoader = animationLoader;
			this.player = player;
			this.options = options;
			this.audio = audio;
			this.images = images;
			this.entityFactory = entityFactory;
			this.deathSystem = deathSystem;
		}
		
		public Engine getEngine() {
			return engine;
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
		
		public EntityFactory getEntityFactory() {
			return entityFactory;
		}
		
		public EntityLoader getEntityLoader() {
			return entityLoader;
		}
	}
	
	public static int INVISIBLE_BEATLINES_PER_BEAT = 4;
	
	private DungeonParams dungeonParams;
	
	private ActionBar actionBar;
	private TileRenderSystem tileRenderSystem;
	
	private SongSelector songSelector;
	private int currentFloor;
	private float bpm;
	
	private Floor[] floors;
	
	private float beatHitErrorMarginInSeconds;
	private float beatMissErrorMarginInSeconds;
	
	private float actionsDisabledTimeLeftInSeconds;
		
	public Dungeon(DungeonParams dungeonParams) {
		this.dungeonParams = dungeonParams;
		actionBar = new ActionBar();
		tileRenderSystem = new TileRenderSystem();
		songSelector = new SongSelector(dungeonParams.audio);
	}
	
	/**
	 * Returns a value proportional the time difference between each BeatLine
	 */
	public float calculateBeatHitErrorMargin() {
		return Math.max(dungeonParams.options.getDifficulty().getMaximumBeatHitWindowInSeconds(), (actionBar.getBeatLines().get(1).getTimePositionInSeconds() - actionBar.getBeatLines().first().getTimePositionInSeconds())/2.5f);
	}
	
	/**
	 * Returns a value proportional the time difference between each BeatLine
	 */
	public float calculateBeatMissErrorMargin() {
		return ((actionBar.getBeatLines().get(1).getTimePositionInSeconds() - actionBar.getBeatLines().first().getTimePositionInSeconds()) * (float)dungeonParams.options.getDifficulty().getBeatLinesPerBeat());
	}
	
	/**
	 * Returns a bpm value that scales increases as floor increases
	 * TODO: adjust growth function depending on bpmCap
	 */
	public static float calculateBpmFromFloor(Options options, int floor) {
		//return 200;
		return Math.min(options.getDifficulty().getBpmCap(), 100f + ((float)Math.round(floor/5f) * 10f));		
	}
	
	public void enterNewFloor(int newFloor) {
		currentFloor = newFloor;
		
		dungeonParams.deathSystem.setFloor(floors[currentFloor]);
		bpm = calculateBpmFromFloor(dungeonParams.options, currentFloor);
		
		// Generate floor if floor does not exist
		if(floors[currentFloor] == null) {
			floors[currentFloor] = DungeonFactory.generateFloor(this, dungeonParams, currentFloor);
		}
		
		// Spawn in entities
		for(Entity e : floors[currentFloor].getEntitiesToBeSpawned()) {
			dungeonParams.entityFactory.spawnEntity(floors[currentFloor].getTiles(), e);
		}
		
		// Choose song to play
		Song song = selectNewSongByCurrentFloor();
		if(song != null) {
			dungeonParams.audio.playSong(song);
		} else {
			System.out.println("This should never appear. There is no song avaliable for floor " + newFloor + ": BPM = " + bpm);
		}
		
		dungeonParams.animationLoader.updateAllAnimationFrameDuration(bpm);
		
		actionBar.beatLines.clear();
		actionBar.spawnPrimaryBeatLines(song.getOffsetInSeconds());
		
		actionBar.actionBarSystem.onEnterNewFloor();
		
		beatHitErrorMarginInSeconds = calculateBeatHitErrorMargin();
		beatMissErrorMarginInSeconds = calculateBeatMissErrorMargin();
		
		//TODO: quickly fade out music and quickly fade to black --> play some ladder climbing noises --> disable all entity movements --> play music
		
		// Disable entity actions for slightly less than the song's offset so that the player can move even if the movement action is fired early
		disableEntityActions(song.getOffsetInSeconds() - getBeatMissErrorMarginInSeconds());
	}
	
	private int getInvisibleBeatLineSpawnModulusDivisor() {
		if(dungeonParams.options.getDifficulty().getBeatLinesPerBeat() == 1) {
			return 4;
		} else if(dungeonParams.options.getDifficulty().getBeatLinesPerBeat() == 2) {
			return 2;
		} else if(dungeonParams.options.getDifficulty().getBeatLinesPerBeat() == 4) {
			return 1;
		} else {
			System.out.println("this should never appear");
			return 0;
		}
	}
	
	/**
	 * Uses the current bpm
	 */
	private Song selectNewSongByCurrentFloor() {
		Song song = null;
		try {
			song = songSelector.selectSongByBpm(bpm);
		} catch (NoSelectableMusicException e) {
			e.printStackTrace();
		}
		return song;
	}
	
	private void disableEntityActions(float timeInSeconds) {
		//TODO
	}
	
	public void update(float deltaTime) {
		if(floors[currentFloor].isActionsDisabled()) {
			actionsDisabledTimeLeftInSeconds -= deltaTime;
			if(actionsDisabledTimeLeftInSeconds <= 0) {
				floors[currentFloor].setActionsDisabled(false);
			}
		}
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
			
			// Draw tiles from top-->bottom, right-->left of map
			Tile[][] tiles = floors[currentFloor].getTiles();
			for(int x = tiles.length - 1; x >= 0; x--) {
				for(int y = tiles[x].length - 1; y >= 0; y--) {
					Tile tile = tiles[x][y];
					
					Point mapPosition = tile.getMapPosition();
					if(tile.getSprite() != null) {
						batch.draw(tile.getSprite(), mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
					}
					
					// Draw sprite overlays, if any
					Array<Sprite> overlays = tile.getSpriteOverlays();
					if(overlays != null) {
						for(Sprite sprite : overlays) {
							batch.draw(sprite, mapPosition.x * Options.TILE_SIZE, mapPosition.y * Options.TILE_SIZE);
						}
					}
					
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
		private class PlayerAttack {
			private String weaponEquipped;
			
			PlayerAttack(String weaponEquipped) {
				this.weaponEquipped = weaponEquipped;
			}
		}
		
		private Array<BeatLine> beatLines = new Array<BeatLine>();
		private Array<PlayerAttack> playerAttackQueue = new Array<PlayerAttack>();
		
		private float cursorPositionInSeconds;
		
		private boolean paused;
				
		private ActionBarSystem actionBarSystem;
				
		public ActionBar() {
			beatLines = new Array<BeatLine>();
			actionBarSystem = new ActionBarSystem(dungeonParams.options.getWindowWidth());
		}
		
		/**
		 * Spawns a number of BeatLines such that the first BeatLine moves past the cursor line after [offset] seconds
		 * and there are enough BeatLines that the screen is always filled with BeatLines
		 */
		public void spawnPrimaryBeatLines(float offsetInSeconds) {
			beatLines.clear();
			
			float time = offsetInSeconds;
			for(int i = 0; i < actionBarSystem.calculateMaxBeatsOnScreen() * INVISIBLE_BEATLINES_PER_BEAT; i++) {
				boolean strongBeat = false;
				boolean invisible = true;
				
				int invisibleBeatLineSpawnModulusDivisor = getInvisibleBeatLineSpawnModulusDivisor();
				if(i % INVISIBLE_BEATLINES_PER_BEAT == 0) {
					strongBeat = true;
				}
				if(i % invisibleBeatLineSpawnModulusDivisor == 0) {
					invisible = false;
				}
				
				beatLines.add(new BeatLine(time, strongBeat, invisible));
				
				time += (60f/(bpm * (float)INVISIBLE_BEATLINES_PER_BEAT));
			}
		}
		
		public void fireAttackAction() {
			if(!floors[currentFloor].isActionsDisabled()) {
				BeatLine nearestLeft = getNearestCircleFromLeft(false);
				BeatLine nearestRight = getNearestCircleFromRight(false);
				
				if(nearestLeft != null && nearestLeft.getCircleWeakState().equals(CircleState.Alive)
						&& Math.abs(nearestLeft.getTimePositionInSeconds() - cursorPositionInSeconds) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
					playerAttackQueue.add(new PlayerAttack(ComponentMappers.playerMapper.get(dungeonParams.player).getWeaponEquipped()));
					nearestLeft.onAttackHit(dungeonParams.audio);
				} else if(nearestRight != null && nearestRight.getCircleWeakState().equals(CircleState.Alive)
						&& Math.abs(nearestRight.getTimePositionInSeconds() - cursorPositionInSeconds) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
					playerAttackQueue.add(new PlayerAttack(ComponentMappers.playerMapper.get(dungeonParams.player).getWeaponEquipped()));
					nearestRight.onAttackHit(dungeonParams.audio);
				} else if(nearestRight != null) {
					nearestRight.onAttackMiss();
				}
			}
		}
		
		public void fireMovementAction(Direction movementDirection) {
			if(!floors[currentFloor].isActionsDisabled() && !ComponentMappers.hitboxMapper.get(dungeonParams.player).isMovementDisabled()) {
				BeatLine nearestLeft = getNearestCircleFromLeft(true);
				BeatLine nearestRight = getNearestCircleFromRight(true);

				if(nearestLeft != null && nearestLeft.getCircleWeakState().equals(CircleState.Alive)
						&& Math.abs(nearestLeft.getTimePositionInSeconds() - cursorPositionInSeconds) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
					nearestLeft.onMovementHit(dungeonParams.audio, dungeonParams.engine, floors[currentFloor], dungeonParams.player, movementDirection);
				} else if(nearestRight != null && nearestRight.getCircleWeakState().equals(CircleState.Alive)
						&& Math.abs(nearestRight.getTimePositionInSeconds() - cursorPositionInSeconds) <= Dungeon.this.getBeatHitErrorMarginInSeconds()) {
					nearestRight.onMovementHit(dungeonParams.audio, dungeonParams.engine, floors[currentFloor], dungeonParams.player, movementDirection);
				} else if(nearestRight != null) {
					nearestRight.onMovementMiss();
				}
			}
		}
		
		public void firePlayerActionsQueue() {
			for(PlayerAttack attack : playerAttackQueue) {
				EntityActions.entityStartAttack(dungeonParams.engine, dungeonParams.options, dungeonParams.audio, Dungeon.this, dungeonParams.player, null, attack.weaponEquipped, dungeonParams.entityFactory);
			}
			playerAttackQueue.clear();
		}
		
		public void setCursorPosition(float cursorPositionInSeconds) {
			this.cursorPositionInSeconds = cursorPositionInSeconds;
		}
		
		/**
		 * Returns the BeatLine nearest to the cursor line from its left side that contains a circle 
		 */
		private BeatLine getNearestCircleFromLeft(boolean requireStrongBeat) {
			BeatLine nearestLeft = null;
			float smallest = -999f;
			if(requireStrongBeat) {
				for(BeatLine b : beatLines) {
					if(b.isStrongBeat()
							&& !b.isInvisible()
							&& b.getTimePositionInSeconds() <= cursorPositionInSeconds) {
						if(b.getTimePositionInSeconds() > smallest) {
							nearestLeft = b;
							smallest = b.getTimePositionInSeconds();
						}
					}
				}
			} else {
				for(BeatLine b : beatLines) {
					if(b.getTimePositionInSeconds() <= cursorPositionInSeconds
							&& !b.isInvisible()) {
						if(b.getTimePositionInSeconds() > smallest) {
							nearestLeft = b;
							smallest = b.getTimePositionInSeconds();
						}
					}
				}
			}
			return nearestLeft;
		}
		
		/**
		 * Returns the BeatLine nearest to the cursor line from its right side that contains a circle 
		 */
		private BeatLine getNearestCircleFromRight(boolean requireStrongBeat) {
			BeatLine nearestRight = null;
			float largest = 999f;
			if(requireStrongBeat) {
				for(BeatLine b : beatLines) {
					if(b.isStrongBeat()
							&& !b.isInvisible()
							&& b.getTimePositionInSeconds() > cursorPositionInSeconds) {
						if(b.getTimePositionInSeconds() < largest) {
							nearestRight = b;
							largest = b.getTimePositionInSeconds();
						}
					}
				}
			} else {
				for(BeatLine b : beatLines) {
					if(b.getTimePositionInSeconds() > cursorPositionInSeconds
							&& !b.isInvisible()) {
						if(b.getTimePositionInSeconds() < largest) {
							nearestRight = b;
							largest = b.getTimePositionInSeconds();
						}
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
			private Array<EntityAttackParams> entityAttackQueue;
			private Array<EntityAttackParams> entityAttackDeletionQueue;
			private Array<EntityAttackParams> entityAttackDamageCalculationsQueue;
			private Array<EntityAttackParams> entityAttackDamageCalculationsDeletionQueue;
			
			private int maxBeatCirclesOnScreen;
			
			private Sprite actionBarAxis;
			private Sprite cursorLine;
			private Sprite circleStrongBeat;
			private Sprite circleWeakBeat;
			
			private float circleWeakXOffset;
			private float actionBarAxisHeight;
			
			private float cursorLineYPos;
			private float circleStrongBeatYPos;
			private float circleWeakBeatYPos;
			
			private final float cursorLineXPos = 512f;
			private final float axisYPos = 64f;
			
			private int lastKnownLoopCount;
			private boolean syncedNextLoop;
			
			private int newBeatCounter;
			private int invisibleBeatLineSpawnModulusDivisor;
			
			public ActionBarSystem(float windowWidth) {
				batch = new SpriteBatch();
				beatLineAdditionQueue = new Array<BeatLine>();
				beatLineDeletionQueue = new Array<BeatLine>();
				entityAttackQueue = new Array<EntityAttackParams>();
				entityAttackDeletionQueue = new Array<EntityAttackParams>();
				entityAttackDamageCalculationsQueue = new Array<EntityAttackParams>();
				entityAttackDamageCalculationsDeletionQueue = new Array<EntityAttackParams>();
				this.windowWidth = windowWidth;
				
				maxBeatCirclesOnScreen = calculateMaxBeatsOnScreen();
				
				// Get sprites
				actionBarAxis = dungeonParams.images.loadSprite("action_bar_axis");
				cursorLine = dungeonParams.images.loadSprite("action_bar_cursor_line");
				circleStrongBeat = dungeonParams.images.loadSprite("action_bar_circle_strong_beat");
				circleWeakBeat = dungeonParams.images.loadSprite("action_bar_circle_weak_beat");
				
				circleWeakXOffset = (circleStrongBeat.getWidth() - circleWeakBeat.getWidth())/2f;
				actionBarAxisHeight = actionBarAxis.getHeight();
				
				cursorLineYPos = axisYPos + (actionBarAxis.getHeight() - cursorLine.getHeight())/2f;
				circleStrongBeatYPos = axisYPos + (actionBarAxis.getHeight() - circleStrongBeat.getHeight())/2f;
				circleWeakBeatYPos = axisYPos + (actionBarAxis.getHeight() - circleWeakBeat.getHeight())/2f;
				
				invisibleBeatLineSpawnModulusDivisor = getInvisibleBeatLineSpawnModulusDivisor();
			}
			
			private void onEnterNewFloor() {
				syncedNextLoop = false;
				lastKnownLoopCount = 0;
				clearQueues();
				maxBeatCirclesOnScreen = calculateMaxBeatsOnScreen();
			}
			
			private int calculateMaxBeatsOnScreen() {
				 return Math.round(dungeonParams.options.getActionBarScrollInterval()/((60f/(bpm)))/4f);
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
				
				if(lastKnownLoopCount != dungeonParams.audio.getCurrentSongLoopCount()) {
					lastKnownLoopCount = dungeonParams.audio.getCurrentSongLoopCount();
					syncedNextLoop = false;
				}
				
				if(!ActionBar.this.isPaused()) {					
					for(BeatLine b : actionBar.getBeatLines()) {
						// Update BeatLine fields
						if(b.isCircleStrongIncreasingYPos()) {
							b.setCircleStrongYPositionRelativeToAxis(b.getCircleStrongYPositionRelativeToAxis() + (deltaTime * 500f));
						}
						if(b.isCircleWeakIncreasingYPos()) {
							b.setCircleWeakYPositionRelativeToAxis(b.getCircleWeakYPositionRelativeToAxis() + (deltaTime * 500f));
						}
						
						// Draw BeatLines and circles
						float x = (((b.getTimePositionInSeconds() - cursorPositionInSeconds)/dungeonParams.options.getActionBarScrollInterval()) * windowWidth * maxBeatCirclesOnScreen) + cursorLineXPos;
						if(!b.isInvisible()
								&& b.getTimePositionInSeconds() - cursorPositionInSeconds < dungeonParams.options.getActionBarScrollInterval()) {
							batch.draw(circleWeakBeat, x + circleWeakXOffset, circleWeakBeatYPos + b.getCircleWeakYPositionRelativeToAxis());
							if(b.isStrongBeat()) {
								batch.draw(circleStrongBeat, x , circleStrongBeatYPos + b.getCircleStrongYPositionRelativeToAxis());
							}
						}
						
						// Once the BeatLine crosses the cursor, spawn a new BeatLine
						if(b.getTimePositionInSeconds() <= cursorPositionInSeconds
								&& !b.isReaddedToActionBar()) {
							onNewBeat(b.isStrongBeat(), b.isInvisible());
							
							float time = 0;
							if(beatLineAdditionQueue.size > 0) {
								time = beatLineAdditionQueue.get(beatLineAdditionQueue.size - 1).getTimePositionInSeconds() + ((60f/(bpm*INVISIBLE_BEATLINES_PER_BEAT)));
							} else {
								time = actionBar.getBeatLines().get(actionBar.getBeatLines().size - 1).getTimePositionInSeconds() + ((60f/(bpm*INVISIBLE_BEATLINES_PER_BEAT)));
							}
							
							boolean isStrongBeat = false;
							if(newBeatCounter % INVISIBLE_BEATLINES_PER_BEAT == 0) {
								isStrongBeat = true;
							}
							
							boolean isInvisibleBeat = true;
							if(newBeatCounter % invisibleBeatLineSpawnModulusDivisor == 0) {
								isInvisibleBeat = false;
							}
							
							newBeatCounter++;
							
							// Delays the first BeatLine of each song loop so that the beat timing is maintained
							float nextSongEndTime = dungeonParams.audio.getCurrentSong().getOffsetInSeconds() + (dungeonParams.audio.getCurrentSongLoopCount() + 1)*(dungeonParams.audio.getCurrentSong().getSongEndMarkerInSeconds() - dungeonParams.audio.getCurrentSong().getLoopStartMarkerInSeconds());
							if(!syncedNextLoop
									&& time > nextSongEndTime) {
								time = nextSongEndTime + dungeonParams.audio.getSongLoopSyncDelayInSeconds();
								syncedNextLoop = true;
								newBeatCounter = 0;
								// This "fixes" some bug where an extra beat line would be placed at the start of every loop
								isInvisibleBeat = true;
							}
							queueBeatLineAddition(new BeatLine(time, isStrongBeat, isInvisibleBeat));
														
							b.setReaddedToActionBar(true);
						}
						if(!b.isFiredPlayerActionQueue()
								&& (b.getTimePositionInSeconds() - cursorPositionInSeconds) < -beatHitErrorMarginInSeconds) {
							onEndOfBeatHitWindow(b.isStrongBeat(), b.isInvisible());
							if(!b.isInvisible()) {
								firePlayerActionsQueue();
							}
							b.setFiredPlayerActionQueue(true);
						}
						if(x + 40f < 0
								&& !b.isDeletionQueued()) {
							queueBeatLineDeletion(b);
						}
					}
					
					fireBeatLineAdditionQueue();
					fireBeatLineDeletionQueue();
				}
				batch.end();
			}
			
			/**
			 * Called when a beat is no longer able to be hit by the player
			 */
			private void onEndOfBeatHitWindow(boolean isStrongBeat, boolean isInvisibleBeat) {
				if(!isInvisibleBeat) {
					float deltaBeat = 1f/INVISIBLE_BEATLINES_PER_BEAT;
					/**
					// Entity attack damage calculations queue
					// Placed in onEndOfBeatHitWindow instead of onNewBeat to resolve entity movements before damage calculations from attacks
					for(EntityAttackParams params : entityAttackDamageCalculationsQueue) {
						EntityActions.calculateEntityAttackDamage(params);
						entityAttackDamageCalculationsDeletionQueue.add(params);
					}
					fireEntityAttackDamageCalculationsDeletionQueue();
					*/
					
					// Lower beat delay on all entity attack queues
					for(EntityAttackParams params : entityAttackQueue) {
						params.setBeatDelay(params.getBeatDelay() - deltaBeat);
						if(params.getBeatDelay() <= 0) {
							EntityActions.runEntityAttackAnimations(params);
							entityAttackDamageCalculationsQueue.add(params);
							entityAttackDeletionQueue.add(params);
						}
					}
					fireEntityAttackDeletionQueue();
					
					if(isStrongBeat) {
						ComponentMappers.playerMapper.get(dungeonParams.player).setMovedInLastBeat(false);
						
						for(Entity entity : dungeonParams.engine.getEntitiesFor(Family.all(EntityAIComponent.class).get())) {
							ComponentMappers.entityAIMapper.get(entity).getEntityAI().onNewBeat();
						}
					}
				}
			}
			
			private void onNewBeat(boolean isStrongBeat, boolean isInvisibleBeat) {
				float deltaBeat = 1f/INVISIBLE_BEATLINES_PER_BEAT;
				
				for(Entity entity : dungeonParams.engine.getEntitiesFor(Family.all(AttackComponent.class).get())) {
					ComponentMappers.attackMapper.get(entity).onNewBeat(deltaBeat);
				}
				
				for(Entity entity : dungeonParams.engine.getEntitiesFor(Family.all(HitboxComponent.class).get())) {
					ComponentMappers.hitboxMapper.get(entity).onNewBeat(deltaBeat);
				}
				
				// Entity attack damage calculations queue
				for(EntityAttackParams params : entityAttackDamageCalculationsQueue) {
					EntityActions.calculateEntityAttackDamage(params);
					entityAttackDamageCalculationsDeletionQueue.add(params);
				}
				fireEntityAttackDamageCalculationsDeletionQueue();
				
				if(isStrongBeat) {
					// Start idle animations on any entities that aren't currently doing any animations
					for(Entity entity : dungeonParams.engine.getEntitiesFor(Family.all(AnimationComponent.class).get())) {
						AnimationComponent animationComponent = ComponentMappers.animationMapper.get(entity);
						if(animationComponent.isPlayingIdleAnimation()
								&& !animationComponent.isRemoveEntityOnAnimationFinish()) {
							animationComponent.setQueuedIdleAnimation(true);
						}
					}
					
					for(Entity entity : dungeonParams.engine.getEntitiesFor(Family.all(MovementAIComponent.class).get())) {
						ComponentMappers.movementAIMapper.get(entity).getMovementAI().onNewBeat();
					}
				}
			}
			
			/**
			 * Used by Attack.class to wait a certain amount of beats before doing attack animations
			 */
			public void queueEntityAttack(EntityAttackParams params) {
				entityAttackQueue.add(params);
			}
			
			/**
			 * Used by Attack.class to wait a certain amount of beats before doing damage calculations
			 */
			public void queueEntityAttackDamageCalculations(EntityAttackParams params) {
				entityAttackDamageCalculationsQueue.add(params);
			}
			
			private void queueBeatLineAddition(BeatLine newBeatLine) {
				beatLineAdditionQueue.add(newBeatLine);
			}
			
			private void queueBeatLineDeletion(BeatLine target) {
				target.setDeletionQueued(true);
				beatLineDeletionQueue.add(target);
			}
			
			public void clearQueues() {
				beatLineAdditionQueue.clear();
				beatLineDeletionQueue.clear();
				entityAttackQueue.clear();
				entityAttackDeletionQueue.clear();
				entityAttackDamageCalculationsQueue.clear();
				entityAttackDamageCalculationsDeletionQueue.clear();
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
			
			private void fireEntityAttackDeletionQueue() {
				if(entityAttackDeletionQueue.size > 0) {
					entityAttackQueue.removeAll(entityAttackDeletionQueue, false);
					entityAttackDeletionQueue.clear();
				}
			}
			
			private void fireEntityAttackDamageCalculationsDeletionQueue() {
				if(entityAttackDamageCalculationsDeletionQueue.size > 0) {
					entityAttackDamageCalculationsQueue.removeAll(entityAttackDamageCalculationsDeletionQueue, false);
					entityAttackDamageCalculationsDeletionQueue.clear();
				}
			}
		}
	}
}
