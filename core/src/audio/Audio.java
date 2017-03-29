package audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.miv.Options;

import data.SongData;
import dungeons.Dungeon;
import utils.FileUtils;
import utils.GeneralUtils;

public class Audio {
	private Options options;
	// Hashmap with key:value pairing of fileName:SoundData
	private HashMap<String, SongData> songsData = new HashMap<String, SongData>();
	// Hashmap with key:value pairing of fileName:Sound
	private HashMap<String, Sound> soundEffects = new HashMap<String, Sound>();
	// Hashmap with key:value pairing of fileName:Song
	private HashMap<String, Song> songs = new HashMap<String, Song>();
	// Hashmap with key:value pairing of folderName:(array of Sound)
	private HashMap<String, Array<Sound>> soundEffectsSubfolders = new HashMap<String, Array<Sound>>();
		
	private Song currentSong;
	private int currentSongLoopCount;
	private float currentSongPosition;
	
	private float songLoopSyncDelayInSeconds;
	
	public Audio(Options options) {
		this.options = options;
	}
	
	public void loadAudio() {
		loadSongsMetadata();
		loadSoundEffects();
		loadSongs();
	}
	
	private void loadSongsMetadata() {
		// Load text file containing the musics' metadata
		ArrayList<String> metadata = FileUtils.getTextFileContent(Options.songsMetadataFilePath);
		
		String songName = "";
		float bpm = 0;
		float offset = 0f;
		float loopStartMarker = 0f;
		float songEndMarker = 0f;
		int lineCount = 1;
		for(int i = 0; i < metadata.size(); i++) {
			String line = metadata.get(i);
			try {
				if(line.startsWith("name=")) {
					songName = line.replace("name=", "");
				} else if(line.startsWith("bpm=")) {
					bpm = GeneralUtils.toFloat(line.replace("bpm=", ""));
				} else if(line.startsWith("offset=")) {
					offset = GeneralUtils.toFloat(line.replace("offset=", ""));
				} else if(line.startsWith("loop_start=")) {
					loopStartMarker = GeneralUtils.toFloat(line.replace("loop_start=", ""));
				} else if(line.startsWith("song_end=")) {
					songEndMarker = GeneralUtils.toFloat(line.replace("song_end=", ""));
				} else if(line.equals("")) {
					songsData.put(songName, new SongData(songName, bpm, offset, loopStartMarker, songEndMarker));
					songName = "";
					bpm = 0;
					offset = 0f;
					loopStartMarker = 0f;
					songEndMarker = 0f;
				} else {
					System.out.println("Music metadata invalid format at line " + lineCount);
				}
			} catch(NumberFormatException e) {
				System.out.println("Music metadata invalid value at line " + lineCount);
				e.printStackTrace();
			}
			lineCount++;
		}
		if(!songName.equals("")) {
			songsData.put(songName, new SongData(songName, bpm, offset, loopStartMarker, songEndMarker));
		}
	}
	
	private void loadSoundEffects() {
		File soundEffectsFolder = new File(Options.soundEffectsFilePath);
		File[] soundEffectsFiles = soundEffectsFolder.listFiles();
		for(int i = 0; i < soundEffectsFiles.length; i++) {
			if(soundEffectsFiles[i].isFile() && isSupportedAudioFormat(FileUtils.getExtension(soundEffectsFiles[i]))) {
				soundEffects.put(soundEffectsFiles[i].getName().substring(0, soundEffectsFiles[i].getName().length() - 4), Gdx.audio.newSound(new FileHandle(soundEffectsFiles[i])));
			}
			// Get subfolders
			else if(soundEffectsFiles[i].isDirectory()) {				
				File soundEffectsSubfolder = soundEffectsFiles[i];
				File[] soundEffectsSubfolderFiles = soundEffectsSubfolder.listFiles();
				
				Array<Sound> subfolderSounds = new Array<Sound>();
				soundEffectsSubfolders.put(soundEffectsSubfolder.getName(), subfolderSounds);

				for(int a = 0; a < soundEffectsSubfolderFiles.length; a++) {
					if(soundEffectsSubfolderFiles[a].isFile() && isSupportedAudioFormat(FileUtils.getExtension(soundEffectsSubfolderFiles[a]))) {
						Sound sound = Gdx.audio.newSound(new FileHandle(soundEffectsSubfolderFiles[a]));
						soundEffects.put(soundEffectsSubfolderFiles[a].getName(), sound);
						subfolderSounds.add(sound);
					}
				}
			}
		}
	}
	
	private void loadSongs() {
		File musicFolder = new File(Options.musicFilePath);
		File[] musicFiles = musicFolder.listFiles();
		for(int i = 0; i < musicFiles.length; i++) {
			if(musicFiles[i].isFile() && isSupportedAudioFormat(FileUtils.getExtension(musicFiles[i]))) {
				Song song = new Song(Gdx.audio.newMusic(new FileHandle(musicFiles[i])), songsData.get(GeneralUtils.removeExtension(musicFiles[i].getName())));
				songs.put(musicFiles[i].getName(), song);
			}
		}
	}
	
	public void playSoundEffect(String fileName) {
		Sound s;
		// If the fileName is of a folder, play a random sound file from that folder
		if(soundEffectsSubfolders.containsKey(fileName)) {
			s = soundEffectsSubfolders.get(fileName).random();
		} else {
			s = soundEffects.get(fileName);
		}
		
		if(s != null) {
			s.play(options.getMasterVolume() * options.getSoundEffectsVolume());
		} else {
			System.out.println("\"" + fileName + "\" does not exist in sound effects folder.");
		}
	}
	
	public void playSong(String fileName) {
		playSong(songs.get(fileName));
	}
	
	/**
	 * Only one song can be playing at a time
	 */
	public void playSong(Song song) {
		if(song != null) {
			currentSongLoopCount = 0;
			currentSongPosition = 0f;
			currentSong = song;
			songLoopSyncDelayInSeconds = calculateSongLoopSyncDelayInSeconds();
			
			song.getMusic().setVolume(options.getMasterVolume() * options.getMusicVolume());
			/**
			song.getMusic().setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(Music music) {
					
				}
			});
			*/
			song.getMusic().play();
		}
	}
	
	public void update() {
		if(currentSong != null) {
			// Loop song if it ends
			if(currentSong.getMusic().getPosition() >= currentSong.getSongEndMarkerInSeconds()) {
				currentSong.getMusic().setPosition(currentSong.getLoopStartMarkerInSeconds());
				currentSongLoopCount++;
			}
			
			// Update song position
			currentSongPosition = currentSong.getMusic().getPosition() + currentSongLoopCount*(currentSong.getSongEndMarkerInSeconds() - currentSong.getLoopStartMarkerInSeconds());
		}
	}
	
	/**
	 * Returns the song loop number that the given time is part of
	 */
	public float getSongLoopNumber(float timeInSeconds) {
		int i = 0;
		while(true) {
			if(timeInSeconds < (currentSong.getOffsetInSeconds() + (i + 1) * (currentSong.getSongEndMarkerInSeconds() - currentSong.getLoopStartMarkerInSeconds()))) {
				return i;
			}
			i++;
		}
	}
	
	public float getCurrentSongPosition() {
		return currentSongPosition;
	}
	
	public void pauseMusic() {
		for(Song song : songs.values()) {
			if(song.getMusic().isPlaying()) {
				song.getMusic().pause();
				song.setPaused(true);
			}
		}
	}
	
	public void resumeMusic() {
		for(Song song : songs.values()) {
			if(song.isPaused()) {
				song.getMusic().play();
				song.setPaused(false);
			}
		}
	}
	
	/**
	 * Returns the number of seconds that between the current song's end and the beginning of the song's
	 * loop that the beat lines after the song's end must be delayed for
	 */
	public float calculateSongLoopSyncDelayInSeconds() {
		float secondsPerBeat = 60f/currentSong.getBpm();
		float loopStartBeatOffset = (currentSong.getLoopStartMarkerInSeconds() - currentSong.getOffsetInSeconds()) % secondsPerBeat;
		float songEndBeatOffset = (currentSong.getSongEndMarkerInSeconds() - currentSong.getOffsetInSeconds()) % secondsPerBeat;
		// I have no idea why this works
		songEndBeatOffset += 0.5f;
		if(songEndBeatOffset > loopStartBeatOffset) {
			return (1f - songEndBeatOffset + loopStartBeatOffset) * secondsPerBeat;
		} else {
			return (loopStartBeatOffset - songEndBeatOffset) * secondsPerBeat;
		}
	}
	
	public float getSongLoopSyncDelayInSeconds() {
		return songLoopSyncDelayInSeconds;
	}
	
	public int getCurrentSongLoopCount() {
		return currentSongLoopCount;
	}
	
	public Song getCurrentSong() {
		return currentSong;
	}
	
	public Song getSong(String fileName) {
		return songs.get(fileName);
	}
	
	public HashMap<String, Song> getSongs() {
		return songs;
	}
	
	private boolean isSupportedAudioFormat(String fileExtension) {
		if(fileExtension.equals("mp3") || fileExtension.equals("ogg") || fileExtension.equals("wav")) {
			return true;
		} else {
			return false;
		}
	}
}
