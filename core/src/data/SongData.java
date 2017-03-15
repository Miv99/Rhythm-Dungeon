package data;

public class SongData {
	private String name;
	private float bpm;
	// Seconds after music begins that the bpm stays consistent at the specified number
	private float offsetInSeconds;
	// Marks where the music begins to loop, if it does
	private float loopStartMarkerInSeconds;
	private boolean loops;
	
	public SongData(String name, float bpm, float offsetInSeconds, float loopStartMarkerInSeconds, boolean loops) {
		this.name = name;
		this.bpm = bpm;
		this.offsetInSeconds = offsetInSeconds;
		this.loopStartMarkerInSeconds = loopStartMarkerInSeconds;
		this.loops = loops;
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
	
	public boolean isLoops() {
		return loops;
	}
}
