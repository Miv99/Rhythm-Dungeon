package com.miv;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import audio.Audio;
import audio.Song;
import audio.SongSelector.NoSelectableMusicException;
import data.AnimationLoader;
import dungeons.Dungeon;
import dungeons.DungeonFactory;
import factories.EntityFactory;
import graphics.Images;
import dungeons.Dungeon.ActionBar;
import systems.ActionBarSystem;
import systems.AnimationSystem;
import systems.RenderSystem;
import systems.TileRenderSystem;

public class Main extends ApplicationAdapter {
	private boolean paused;
	
	private EntityFactory entityFactory;
	
	private Engine engine;
	private ActionBarSystem actionBarSystem;
	private TileRenderSystem tileRenderSystem;
	
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
		
		Images images = new Images();
		
		entityFactory = new EntityFactory(images);
		
		AnimationLoader animationLoader = new AnimationLoader(images);
		animationLoader.loadAnimations();
		
		// Create systems
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new RenderSystem());
		actionBarSystem = new ActionBarSystem(options.getWindowWidth(), null, 200f);
		tileRenderSystem = new TileRenderSystem();
		
		// Create and set input handler
		im = new InputMultiplexer();
		inputHandler = new InputHandler(null);
		im.addProcessor(inputHandler);
		Gdx.input.setInputProcessor(im);
		
		//TODO: remove this
		//startNewGame();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(!paused) {
			// Update systems
			engine.update(deltaTime);
			actionBarSystem.update(deltaTime);
			tileRenderSystem.update(deltaTime);
		}
		
		//System.out.println(1/deltaTime);
	}
		
	@Override
	public void dispose() {
		
	}
	
	@Override
	public void resize(int width, int height) {
		actionBarSystem.setWindowWidth(width);
		
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
		
		Dungeon dungeon = DungeonFactory.generateDungeon(10, player, audio, tileRenderSystem);
		
		//TODO: fade screen from black
		
		dungeon.enterNewFloor(1);
	}
}