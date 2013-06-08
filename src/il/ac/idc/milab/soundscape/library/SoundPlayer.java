package il.ac.idc.milab.soundscape.library;

import java.io.IOException;

import android.media.MediaPlayer;

/**
 * This class represents the media player of the device
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class SoundPlayer {
	
	private MediaPlayer m_MediaPlayer = null;
	private String m_FilePathToPlay;
	private int m_Duration;
	
	public SoundPlayer() {}

	/**
	 * Initialize our media player with the given file to play
	 * @param m_FilePathToPlay the absolute path to the file
	 */
	public void initPlayer(String i_FilePathToPlay) {
    	m_MediaPlayer = new MediaPlayer();
    	m_FilePathToPlay = i_FilePathToPlay;
        try {
        	m_MediaPlayer.setDataSource(m_FilePathToPlay);
            m_MediaPlayer.prepare();
            m_Duration = m_MediaPlayer.getDuration();
        } catch (IOException e) {
        }
	}
	
	
	/**
	 * This function starts to play the file that has been initialize prior
	 */
    public void startPlaying() {
    	initPlayer(m_FilePathToPlay);
    	if(m_MediaPlayer != null && !m_MediaPlayer.isPlaying()) {
    		m_MediaPlayer.start();
    	}
    }

    /**
     * This function stops a previously started playing
     */
    public void stopPlaying() {
    	if(m_MediaPlayer != null && m_MediaPlayer.isPlaying()) {
    		m_MediaPlayer.stop();
    		m_MediaPlayer.release();
    		m_MediaPlayer = null;
    	}
    }
    
    /**
     * This function releases the player
     */
    public void release() {
        if (m_MediaPlayer != null) {
        	m_MediaPlayer.release();
        	m_MediaPlayer = null;
        }
    }
    
    /**
     * This function gets the sound file duration in miliseconds
     * @return the duration of the sound file in miliseconds
     */
    public int getFileDuration() {
    	return m_Duration;
    }
    
    /**
     * This function returns the active media player
     * @return the media player used for playing the sounds
     */
    public MediaPlayer getActiveMediaPlayer() {
    	return m_MediaPlayer;
    }
}