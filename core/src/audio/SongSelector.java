package audio;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

public class SongSelector {
	public class NoSelectableMusicException extends Exception {
		private static final long serialVersionUID = -4720322221789056122L;

		public NoSelectableMusicException(String message) {
			super(message);
		}
	}
	
	private Audio audio;
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	
	public SongSelector(Audio audio) {
		this.audio = audio;
				
		populateSongs(audio.getSongs());
	}
	
	public Song selectSongByBpm(float bpm) throws NoSelectableMusicException {
		Array<String> suitableMusics = new Array<String>();
		
		for(Map.Entry<String, Song> entry : songs.entrySet()) {
			if(entry.getValue().getBpm() == bpm) {
				suitableMusics.add(entry.getKey());
			}
		}
		
		if(suitableMusics.size == 0) {
			refillSongsByBpm(bpm);
			// If no music with the specified bpm exists after refill, throw exception
			if(countSongsByBpm(bpm) == 0) {
				throw new NoSelectableMusicException("No musics of " + bpm + " bpm.");
			} else {
				return selectSongByBpm(bpm);
			}
		} else {
			String musicName = suitableMusics.random();
			songs.remove(musicName);
			return audio.getSong(musicName);
		}
	}
	
	/**
	 * Counts number of entries in musics with the specified bpm
	 */
	private int countSongsByBpm(float bpm) {
		int count = 0;
		for(Map.Entry<String, Song> entry : songs.entrySet()) {
			if(entry.getValue().getBpm() == bpm) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Put all entries from audio.songs
	 */
	private void refillSongsByBpm(float bpm) {
		for(Map.Entry<String, Song> entry : audio.getSongs().entrySet()) {
			if(entry.getValue().getBpm() == bpm) {
				songs.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	private void populateSongs(HashMap<String, Song> source) {
		for(Map.Entry<String, Song> entry : source.entrySet()) {
			songs.put(entry.getKey(), entry.getValue());
		}
	}
}
