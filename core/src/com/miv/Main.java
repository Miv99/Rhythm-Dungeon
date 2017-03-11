package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miv.Movement.Direction;

import audio.Audio;
import components.EnemyAIComponent;
import components.ImageComponent;
import data.AnimationLoader;
import data.AttackLoader;
import dungeons.Dungeon;
import dungeons.Dungeon.DungeonParams;
import factories.DungeonFactory;
import factories.EntityFactory;
import graphics.Images;
import systems.AnimationSystem;
import systems.DeathSystem;
import systems.RenderSystem;
import systems.TileWarningSystem;

public class Main extends ApplicationAdapter {	
	private boolean paused;
	
	private EntityFactory entityFactory;
	
	private GameCamera camera;
	
	private Images images;
	private AttackLoader attackLoader;
	private AnimationLoader animationLoader;
	private Engine engine;
	private Dungeon dungeon;
	
	private Options options;
	private Audio audio;
	
	private InputHandler inputHandler;
	private InputMultiplexer im;
	
	@Override
	public void create() {
		engine = new Engine();
		
		options = Options.loadOptions();
		
		Gdx.graphics.setWindowedMode(options.getWindowWidth(), options.getWindowHeight());
		camera = new GameCamera(options.getWindowWidth(), options.getWindowHeight());
						
		audio = new Audio(options);
		audio.loadAudio();
		
		images = new Images();
				
		animationLoader = new AnimationLoader(images);
		animationLoader.loadAnimations();
		
		attackLoader = new AttackLoader();
		attackLoader.loadAttacks();
		
		entityFactory = new EntityFactory(images, animationLoader, attackLoader, engine);
		
		// Create systems
		TileWarningSystem tileWarningSystem = new TileWarningSystem();
		engine.addSystem(tileWarningSystem);
		engine.addSystem(new DeathSystem(engine));
		engine.addSystem(new AnimationSystem(engine));
		RenderSystem renderSystem = new RenderSystem();
		engine.addSystem(renderSystem);
		
		camera.setRenderSystem(renderSystem);
		camera.setTileWarningSystem(tileWarningSystem);
		
		// Create and set input handler
		im = new InputMultiplexer();
		inputHandler = new InputHandler();
		im.addProcessor(inputHandler);
		Gdx.input.setInputProcessor(im);
				
		//TODO: remove this
		startNewGame();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
				
		if(!paused) {
			if(dungeon != null) {
				dungeon.update(deltaTime);
			}
			
			// Update systems
			engine.update(deltaTime);
			
			if(camera != null) {
				camera.frameUpdate(deltaTime);
			}
		} else {
			if(dungeon != null) {
				dungeon.getTileRenderSystem().update(deltaTime);
			}
			
			// Continue rendering
			engine.getSystem(RenderSystem.class).update(deltaTime);
		}
	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public void resize(int width, int height) {
		if(dungeon != null) {
			dungeon.getActionBar().getActionBarSystem().setWindowWidth(width);
		}
		
		//TODO: update stage
	}

	@Override
	public void pause() {
		paused = true;
		audio.pauseMusic();
	}

	@Override
	public void resume() {
		paused = false;
		audio.resumeMusic();
	}
	
	public void startNewGame() {
		final int startingFloor = 0;
				
		//TODO: show cutscene of story intro
		
		Entity player = entityFactory.createPlayer(new Point(2, 2), Dungeon.calculateBpmFromFloor(options, startingFloor));
		camera.setFocus(player);
		engine.addEntity(player);
		
		DungeonParams dungeonParams = new DungeonParams(engine, 10, animationLoader, player, options, audio, images, entityFactory);
		dungeon = DungeonFactory.generateDungeon(dungeonParams);
		camera.setDungeon(dungeon);
		
		dungeon.getFloors()[startingFloor].setActionsDisabled(true);
		
		//TODO: fade screen from black
		
		dungeon.enterNewFloor(startingFloor);
	}
	
	
	
	public class InputHandler implements InputProcessor {
		@Override
		public boolean keyDown(int keycode) {
			if(dungeon != null) {
				if(keycode == Input.Keys.LEFT) {
					dungeon.getActionBar().fireMovementAction(Direction.Left);
				} else if(keycode == Input.Keys.RIGHT) {
					dungeon.getActionBar().fireMovementAction(Direction.Right);
				} else if(keycode == Input.Keys.UP) {
					dungeon.getActionBar().fireMovementAction(Direction.Up);
				} else if(keycode == Input.Keys.DOWN) {
					dungeon.getActionBar().fireMovementAction(Direction.Down);
				} else if(keycode == options.getAttackKey1()
						|| keycode == options.getAttackKey2()) {
					dungeon.getActionBar().fireAttackAction();
				} else if(keycode == options.getAttackBurstKey()) {
					
				} else if(keycode == Input.Keys.ESCAPE) {
					if(paused) {
						resume();
					} else {
						pause();
					}
				}
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			return false;
		}
		
	}
}