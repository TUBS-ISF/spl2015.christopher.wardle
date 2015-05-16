package spl.mediaPlayer.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MediaPlayer {

    private Player player;

    private final Object playerLock = new Object();

    public enum PlayerState {
	IDLE, PLAYING, PAUSED, STOPPED
    };

    public PlayerState currentState = PlayerState.IDLE;

    public void play(File file) throws JavaLayerException {
	synchronized (playerLock) {
	    switch (currentState) {
	    case IDLE:
	    case STOPPED:
		try {
		    this.player = new Player(new BufferedInputStream(new FileInputStream(file)));
		}
		catch (FileNotFoundException e) {
		    System.err.println("File " + file + " not found.");
		    e.printStackTrace();
		}
		final Runnable r = new Runnable() {
		    public void run() {
			playInternal();
		    }
		};
		final Thread t = new Thread(r);
		t.setDaemon(true);
		t.setPriority(Thread.MAX_PRIORITY);
		currentState = PlayerState.PLAYING;
		t.start();
		break;
	    case PAUSED:
		resume();
		break;
	    case PLAYING:
		stop();
		// close();
		play(file);
	    default:
		break;
	    }
	}
    }

    private void playInternal() {
	while (currentState != PlayerState.STOPPED) {
	    try {
		if (!player.play(1)) {
		    break;
		}
	    }
	    catch (JavaLayerException e) {
		e.printStackTrace();
		break;
	    }
	    synchronized (playerLock) {
		while (currentState == PlayerState.PAUSED) {
		    try {
			playerLock.wait();
		    }
		    catch (InterruptedException e) {
			e.printStackTrace();
			break;
		    }
		}
	    }
	}
	close();
    }

    private void close() {
	synchronized (playerLock) {
	    currentState = PlayerState.STOPPED;
	}
	try {
	    player.close();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public boolean resume() {
	synchronized (playerLock) {
	    if (currentState == PlayerState.PAUSED) {
		currentState = PlayerState.PLAYING;
		playerLock.notifyAll();
	    }
	    return currentState == PlayerState.PLAYING;
	}

    }

    public boolean pause() {
	synchronized (playerLock) {
	    if (currentState == PlayerState.PLAYING) {
		currentState = PlayerState.PAUSED;
	    }
	    return currentState == PlayerState.PAUSED;
	}
    }

    public void stop() {
	synchronized (playerLock) {
	    currentState = PlayerState.STOPPED;
	    playerLock.notifyAll();
	}
    }
    
    public String getDuration(File file) throws UnsupportedAudioFileException, IOException{
	AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	if(fileFormat instanceof TAudioFileFormat){
	    Map<?,?> properties = ((TAudioFileFormat) fileFormat).properties();
	    String key = "duration";
	    Long microseconds = (Long) properties.get(key);
	    int ms = (int) (microseconds / 1000);
	    int sec = (ms/1000)%60;
	    int min = (ms/1000)/60;
	    return min+":"+sec;
	}
	else{
	    throw new UnsupportedAudioFileException();
	}
    }

    // public void setVolume(float value) {
    // if (mediaPlayer != null) {
    // // set Volume
    // }
    //
    // }
    //
    // public float getVolume() {
    // float currentVolume = 0F;
    // if (mediaPlayer != null) {
    // // get Volume
    // }
    // return currentVolume;
    // }
    //
    // public void setPosition(long pos) {
    // if (mediaPlayer != null) {
    // // set Position
    // }
    //
    // }
    //
    // public long getPosition() {
    // long currentPos = 0L;
    // if (mediaPlayer != null) {
    // // get Position
    // }
    // return currentPos;
    // }
}
