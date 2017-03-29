package com.miv;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Anything done to volumes/window size using setter methods will not be saved until applyChanges() is called
 */
public class Options {
	public static enum Difficulty {
		EASY(140, 1), // 140bpm cap, full beats only
		NORMAL(160, 2), // 160bpm cap, half beats
		HARD(180, 2), // 180bpm cap, half beats
		LUNATIC(200, 4); // 200bpm cap, all player attacks do 25% damage, quarter beats
		
		private int bpmCap;
		private int beatLinesPerBeat;
		private float playerDamageMultiplier;
		
		Difficulty(int bpmCap, int beatLinesPerBeat) {
			this.bpmCap = bpmCap;
			this.beatLinesPerBeat = beatLinesPerBeat;
			playerDamageMultiplier = 1f/(float)beatLinesPerBeat;
		}
		
		public int getBpmCap() {
			return bpmCap;
		}
		
		public int getBeatLinesPerBeat() {
			return beatLinesPerBeat;
		}
		
		public float getPlayerDamageMultiplier() {
			return playerDamageMultiplier;
		}
	}
	
	public static final int TILE_SIZE = 32;
		
	//TODO: change this to Gdx.files.local
	public static String assetsFilePath = "C:\\Rhythm Dungeon\\assets\\";
	
	public static String soundEffectsFilePath = assetsFilePath + "audio\\sound effects\\";
	public static String musicFilePath = assetsFilePath + "audio\\music\\";
	
	public static String spritesPackFilePath = assetsFilePath + "sprites\\sprites.pack";
	public static String spritesImageFileDir = assetsFilePath + "sprites\\";
	
	public static String animationsDataFilePath = assetsFilePath + "animations\\animations.txt";
	public static String songsMetadataFilePath = assetsFilePath + "audio\\music\\songs.txt";
	public static String hitboxesDataFilePath = assetsFilePath + "hitboxes\\hitboxes.txt";
	public static String attacksDataFilePath = assetsFilePath + "attacks\\attacks.txt";
	public static String entitiesDataFilePath = assetsFilePath + "entities\\entities.txt";
	
	private boolean debug;
	
	private boolean fullscreen;
	private int windowWidth = 1600;
	private int windowHeight = 900;
	
	private float masterVolume = 0.5f;
	private float soundEffectsVolume = 0.35f;
	private float musicVolume = 0.2f;
	
	private float actionBarScrollInterval = 16f;
	
	private Difficulty difficulty = Difficulty.EASY;
	
	private int attackKey1 = Input.Keys.G;
	private int attackKey2 = Input.Keys.H;
	
	// Temporary values that become permanent once changes are applied
	private boolean fullscreenTemp;
	private int windowWidthTemp = 1024;
	private int windowHeightTemp = 768;
	private transient float masterVolumeTemp = 1f;
	private transient float soundEffectsVolumeTemp = 1f;
	private transient float musicVolumeTemp = 1f;
	
	public static Options loadOptions() {
		//TODO: load options from an options file or something; don't use a txt file
		return new Options();
	}
	
	public void applyChanges() {
		fullscreen = fullscreenTemp;
		windowWidth = windowWidthTemp;
		windowHeight = windowHeightTemp;
		masterVolume = masterVolumeTemp;
		soundEffectsVolume = soundEffectsVolumeTemp;
		musicVolume = musicVolumeTemp;
		
		// Resize window
		if(fullscreenTemp) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
		}
	}
	
	public void setFullscreen(boolean fullscreen) {
		fullscreenTemp = fullscreen;
	}
	
	public void setWindowWidth(int windowWidth) {
		windowWidthTemp = windowWidth;
	}
	
	public void setWindowHeight(int windowHeight) {
		windowHeightTemp = windowHeight;
	}
	
	public void setMasterVolume(float masterVolume) {
		masterVolumeTemp = masterVolume;
	}
	
	public void setSoundEffectsVolume(float soundEffectsVolume) {
		soundEffectsVolumeTemp = soundEffectsVolume;
	}
	
	public void setMusicVolume(float musicVolume) {
		musicVolumeTemp = musicVolume;
	}
	
	public void setActionBarScrollInterval(float actionBarScrollInterval) {
		this.actionBarScrollInterval = actionBarScrollInterval;
	}
	
	public void setAttackKey1(int attackKey1) {
		this.attackKey1 = attackKey1;
	}
	
	public void setAttackKey2(int attackKey2) {
		this.attackKey2= attackKey2;
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	
	public boolean isDebug() {
		return debug;
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	public int getWindowWidth() {
		return windowWidth;
	}
	
	public int getWindowHeight() {
		return windowHeight;
	}
	
	public float getMasterVolume() {
		return masterVolume;
	}
	
	public float getSoundEffectsVolume() {
		return soundEffectsVolume;
	}
	
	public float getMusicVolume() {
		return musicVolume;
	}
	
	public float getActionBarScrollInterval() {
		return actionBarScrollInterval;
	}
	
	public int getAttackKey1() {
		return attackKey1;
	}
	
	public int getAttackKey2() {
		return attackKey2;
	}
	
	public Difficulty getDifficulty() {
		return difficulty;
	}
}
