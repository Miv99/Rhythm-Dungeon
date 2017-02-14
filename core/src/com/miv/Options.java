package com.miv;

import com.badlogic.gdx.Gdx;

public class Options {
	private static String dataFilePath = Gdx.files.external("Rhythm Dungeon\\").file().getPath();
	public static String soundEffectsFilePath = dataFilePath + "Audio\\Sound Effects\\";
	public static String musicFilePath = dataFilePath + "Audio\\Music\\";
	public static String songsMetadataFilePath = dataFilePath + "Audio\\Music\\metadata.txt";
	
	private float masterVolume = 1f;
	private float soundEffectsVolume = 1f;
	private float musicVolume = 1f;
	
	public static Options loadOptions() {
		//TODO: load options from an options file or something; don't use a txt file
		return new Options();
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
