package com.miv;

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
import dungeons.Dungeon;

public class Main extends ApplicationAdapter {
	private Options options;
	private Audio audio;
	
	private InputHandler inputHandler;
	private InputMultiplexer im;
	
	@Override
	public void create () {
		options = Options.loadOptions();
		
		audio = new Audio(options);
		
		// Create and set input handler
		im = new InputMultiplexer();
		inputHandler = new InputHandler(null);
		im.addProcessor(inputHandler);
		Gdx.input.setInputProcessor(im);
		
		//TODO: remove this
		//startNewGame();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		//System.out.println(1/deltaTime);
	}
		
	@Override
	public void dispose () {
		
	}
	
	public void startNewGame() {
		//TODO: disable all entities' movement
		
		//TODO: show cutscene of story intro
		
		Dungeon dungeon = new Dungeon(audio);
		dungeon.setCurrentFloor(1);
		float bpm = Dungeon.calculateBpmFromFloor(dungeon.getCurrentFloor());
		Song song = null;
		try {
			song = dungeon.getSongSelector().selectSongByBpm(bpm);
		} catch (NoSelectableMusicException e) {
			e.printStackTrace();
		}
		
		//TODO: fade screen from black
		
		if(song != null) {
			audio.playSong(song);
		}
		
		//TODO: wait [song.offsetInSeconds] and then enable all entities' movement
	}
}