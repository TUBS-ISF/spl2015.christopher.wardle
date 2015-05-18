package spl.mediaPlayer.main;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;

public class MediaPlayer {

    public enum PlayerState {
	IDLE, PLAYING, PAUSED, STOPPED
    };
    
    public enum RepeatMode {
	REPEAT_OFF, REPEAT_ONE, REPEAT_ALL
    };

    public PlayerState currentState = PlayerState.IDLE;
    public int songProgress;
    private AudioInputStream audioStream;
    private FloatControl volumeControl;
    private Clip clip;
    private float currentVolume;

    private File[] mediaFiles;
    private boolean[] played;
    private boolean shuffle;
    private int playingMediaIndex;
    private RepeatMode currentRepeatMode = RepeatMode.REPEAT_OFF;
    private final int REPEAT_MODES = RepeatMode.values().length;

    private static Random rnd = new Random();

    /**
     * Creates a new MP3Player from given InputStream.
     * 
     * @param filename
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public MediaPlayer(File[] files) {
	mediaFiles = files;
	played = new boolean[mediaFiles.length];
	if (shuffle) {
	    playingMediaIndex = rnd.nextInt(mediaFiles.length);
	}
	else {
	    playingMediaIndex = 0;
	}
	loadNewFile(files[playingMediaIndex]);
    }

    public void loadNewFile(File file) {
	try {
	    audioStream = AudioSystem.getAudioInputStream(file);
	    AudioFormat baseFormat = audioStream.getFormat();
	    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
		    baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
	    AudioInputStream stream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);
	    clip = AudioSystem.getClip();
	    clip.open(stream);
	    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	    currentVolume = volumeControl.getValue();
	}
	catch (UnsupportedAudioFileException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (LineUnavailableException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Starts playback (resumes if paused).
     * 
     * @throws JavaLayerException
     *             when there is a problem decoding the file.
     */
    public void play() {
	synchronized (clip) {
	    if (clip != null && clip.isOpen()) {
		clip.start();
		played[playingMediaIndex] = true;
		currentState = PlayerState.PLAYING;
	    }
	}
    }

    /**
     * Pauses playback.
     */
    public void pause() {
	synchronized (clip) {
	    if (currentState == PlayerState.PLAYING) {
		clip.stop();
		currentState = PlayerState.PAUSED;
	    }
	}
    }

    /**
     * Resumes playback.
     */
    public void resume() {
	synchronized (clip) {
	    if (currentState == PlayerState.PAUSED) {
		clip.start();
		currentState = PlayerState.PLAYING;
	    }
	}
    }

    /**
     * Stops playback. If not playing, does nothing.
     */
    public void stop() {
	synchronized (clip) {
	    clip.stop();
	    clip.setFramePosition(0);
	    currentState = PlayerState.STOPPED;
	}
    }

    public void onFinish() {
	clip.stop();
	switch (currentRepeatMode) {
	case REPEAT_OFF:
	    boolean finished = true;
	    for (boolean p : played) {
		if (!p) {
		    finished = false;
		    break;
		}
	    }
	    if (finished) {
		stop();
	    }
	    else {
		do {
		    playingMediaIndex = getNewIndex(true);
		} while (played[playingMediaIndex]);
		loadNewFile(mediaFiles[playingMediaIndex]);
		play();
	    }
	    break;
	case REPEAT_ONE:
	    clip.setFramePosition(0);
	    play();
	    break;
	case REPEAT_ALL:
	    playingMediaIndex = getNewIndex(true);
	    loadNewFile(mediaFiles[playingMediaIndex]);
	    play();
	    break;
	}
    }

    /**
     * Returns the current player status.
     * 
     * @return the player status
     */
    public PlayerState getPlayerStatus() {
	return this.currentState;
    }

    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
	synchronized (clip) {
	    if (clip != null && clip.isOpen()) {
		clip.stop();
		clip.close();
	    }
	}
	try {
	    audioStream.close();
	    audioStream = null;
	}
	catch (IOException e) {
	    // we are terminating, thus ignore exception
	}
    }

    public int getDuration() {
	return (int) clip.getMicrosecondLength() / 1000;
    }

    public int getCurrentPosition() {
	return (int) clip.getMicrosecondPosition() / 1000;
    }

    public int getSongProgress() {
	if (currentState == PlayerState.PLAYING && clip.isOpen()) {
	    return getCurrentPosition();
	}
	else {
	    return 0;
	}
    }

    public void setCurrentPosition(long pos) {
	clip.setMicrosecondPosition(pos * 1000);
    }


    public int getCurrentlyPlayingSongIndex() {
	return playingMediaIndex;
    }

    private int getNewIndex(boolean direction) {
	int newIndex = 0;
	if (shuffle) {
	    do {
		newIndex = rnd.nextInt(mediaFiles.length);
	    } while (newIndex == playingMediaIndex && mediaFiles.length > 1);
	}
	else {
	    // get next media
	    if (direction) {
		newIndex = (playingMediaIndex + 1) % mediaFiles.length;
	    }
	    // get previous media
	    else {
		newIndex = (playingMediaIndex - 1 + mediaFiles.length) % mediaFiles.length;
	    }
	}
	return newIndex;
    }

    public void playNewSong(boolean direction) {
	clip.stop();
	playingMediaIndex = getNewIndex(direction);
	loadNewFile(mediaFiles[playingMediaIndex]);
	play();

    }
    
    public void setShuffle(){
	shuffle = !shuffle;
    }
    
    public String getShuffle(){
	return shuffle ? "Shuffle On" : "Shuffle Off";
    }
    
    public void setRepeatMode(){
	currentRepeatMode = RepeatMode.values()[(currentRepeatMode.ordinal() + 1) % REPEAT_MODES];
    }
    
    public int getRepeatMode(){
	return currentRepeatMode.ordinal();
    }
    
    public void setVolume(float diff){
	if(clip != null && clip.isOpen()){
	    float newVolume = volumeControl.getValue() + diff;
	    volumeControl.setValue(Math.min(volumeControl.getMaximum(), Math.max(volumeControl.getMinimum(), newVolume)));
	    currentVolume = volumeControl.getValue();
	}
    }
    
    public int getFloatControlMinimum(){
	return (int) volumeControl.getMinimum();
    }
    
    public int getFloatControlMaximum(){
	return (int) volumeControl.getMaximum();
    }
}
