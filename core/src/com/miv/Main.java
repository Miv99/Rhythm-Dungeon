package com.miv;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;

import audio.Audio;
import data.AnimationLoader;
import dungeons.Dungeon;
import dungeons.DungeonFactory;
import factories.EntityFactory;
import graphics.Images;
import systems.AnimationSystem;
import systems.RenderSystem;

public class Main extends ApplicationAdapter {
	private boolean paused;
	
	private EntityFactory entityFactory;
	
	private Images images;
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
		
		audio = new Audio(options);
		audio.loadAudio();
		
		images = new Images();
		
		entityFactory = new EntityFactory(images);
		
		AnimationLoader animationLoader = new AnimationLoader(images);
		animationLoader.loadAnimations();
		
		// Create systems
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new RenderSystem());
		
		// Create and set input handler
		im = new InputMultiplexer();
		inputHandler = new InputHandler(null);
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
			// Update systems
			engine.update(deltaTime);
			
			if(dungeon != null) {
				dungeon.update(deltaTime);
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
		//TODO: disable all entities' movement
		
		//TODO: show cutscene of story intro
		
		Entity player = entityFactory.createPlayer();
		
		dungeon = DungeonFactory.generateDungeon(10, player, options, audio, images);
		
		//TODO: fade screen from black
		
		dungeon.enterNewFloor(1);
	}
}