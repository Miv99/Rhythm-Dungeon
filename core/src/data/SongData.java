package data;

public class SongData {
	private String name;
	private float bpm;
	// Seconds after music begins that the bpm stays consistent at the specified number
	private float offsetInSeconds;
	// Marks where the music begins to loop, if it does
	private float loopStartMarkerInSeconds;
	private float songEndMarkerInSeconds;
	private boolean loops;
	
	public SongData(String name, float bpm, float offsetInSeconds, float loopStartMarkerInSeconds, float songEndMarkerInSeconds) {
		this.name = name;
		this.bpm = bpm;
		this.offsetInSeconds = offsetInSeconds;
		this.loopStartMarkerInSeconds = loopStartMarkerInSeconds;
		this.songEndMarkerInSeconds = songEndMarkerInSeconds;
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
}
