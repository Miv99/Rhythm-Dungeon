package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import audio.Audio;
import data.AnimationLoader;
import dungeons.Dungeon;
import dungeons.Dungeon.DungeonParams;
import factories.DungeonFactory;
import factories.EntityFactory;
import graphics.Images;
import systems.AnimationSystem;
import systems.RenderSystem;

public class Main extends ApplicationAdapter {
	private boolean paused;
	
	private EntityFactory entityFactory;
	
	private GameCamera camera;
	
	private Images images;
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
		
		entityFactory = new EntityFactory(images, animationLoader);
		
		// Create systems
		engine.addSystem(new AnimationSystem());
		RenderSystem renderSystem = new RenderSystem();
		engine.addSystem(renderSystem);
		
		camera.setRenderSystem(renderSystem);
		
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
		}
		
		//System.out.println(1/deltaTime);
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
	}

	@Override
	public void resume() {
		paused = false;
	}
	
	public void startNewGame() {
		final int startingFloor = 1;
		
		//TODO: disable all entities' movement
		
		//TODO: show cutscene of story intro
		
		Entity player = entityFactory.createPlayer(new Point(2, 2), Dungeon.calculateBpmFromFloor(startingFloor));
		camera.setFocus(player);
		engine.addEntity(player);
		
		DungeonParams dungeonParams = new DungeonParams(10, animationLoader, player, options, audio, images);
		dungeon = DungeonFactory.generateDungeon(dungeonParams);
		inputHandler.setDungeon(dungeon);
		camera.setDungeon(dungeon);
		
		//TODO: fade screen from black
		
		dungeon.enterNewFloor(startingFloor);
	}
}