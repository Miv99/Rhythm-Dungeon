package com.miv;

import com.badlogic.gdx.Gdx;

/**
 * Anything done using setter methods will not be saved until applyChanges() is called
 */
public class Options {
	public static final int TILE_SIZE = 32;
	
	private static String dataFilePath = Gdx.files.external("Rhythm Dungeon\\").file().getPath();
	
	//TODO: change this to Gdx.files.local
	public static String assetsFilePath = "C:\\Rhythm Dungeon\\assets\\";
	public static String soundEffectsFilePath = assetsFilePath + "audio\\sound effects\\";
	public static String musicFilePath = assetsFilePath + "audio\\music\\";
	public static String songsMetadataFilePath = assetsFilePath + "audio\\music\\metadata.txt";
	
	public static String spritesPackFilePath = assetsFilePath + "sprites\\sprites.pack";
	public static String spritesImageFileDir = assetsFilePath + "sprites\\";
	
	public static String animationsMetadataFilePath = assetsFilePath + "animations\\metadata.txt";
	
	private boolean fullscreen;
	private int windowWidth = 1024;
	private int windowHeight = 768;
	
	private float masterVolume = 1f;
	private float soundEffectsVolume = 1f;
	private float musicVolume = 1f;
	
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
		this.fullscreen = fullscreen;
	}
	
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}
	
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
	
	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;
	}
	
	public void setSoundEffectsVolume(float soundEffectsVolume) {
		this.soundEffectsVolume = soundEffectsVolume;
	}
	
	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
	}
	
	
	public boolean getFullscreen() {
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
}
