package audio;

import com.badlogic.gdx.audio.Music;

import data.SongData;

public class Song {
	private Music music;
	private String name;
	private float bpm;
	// Seconds after music begins that the bpm stays consistent at the specified number
	private float offsetInSeconds;
	// Marks where the music begins to loop, if it does
	private float loopStartMarkerInSeconds;
	private float songEndMarkerInSeconds;
	private boolean loops;
	
	// If song was paused while being played
	private boolean paused;
	
	public Song(Music music, SongData songData) {
		this.music = music;
		this.name = songData.getName();
		this.bpm = songData.getBpm();
		this.offsetInSeconds = songData.getOffsetInSeconds();
		this.loopStartMarkerInSeconds = songData.getLoopStartMarkerInSeconds();
		this.songEndMarkerInSeconds = songData.getSongEndMarkerInSeconds();
		this.loops = songData.isLoops();
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public Music getMusic() {
		return music;
	}
	
	public String getName() {
		return name;
	}
	
	public float getBpm() {
		return bpm;
	}
	
	public float getOffsetInSeconds() {
		return offsetInSeconds;
	}
	
	public float getLoopStartMarkerInSeconds() {
		return loopStartMarkerInSeconds;
	}
	
	public float getSongEndMarkerInSeconds() {
		return songEndMarkerInSeconds;
	}
	
	public boolean isLoops() {
		return loops;
	}
	
	public boolean isPaused() {
		return paused;
	}
}
