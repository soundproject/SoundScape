package il.ac.idc.milab.soundscape.library;

import java.io.IOException;

import android.media.MediaPlayer;

public class SoundPlayer {
	
	private static final String TAG = "SOUND_PLAYER";
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
	 * Starts to play the file that has been initialize prior
	 */
    public void startPlaying() {
    	initPlayer(m_FilePathToPlay);
    	if(m_MediaPlayer != null && !m_MediaPlayer.isPlaying()) {
    		m_MediaPlayer.start();
    	}
    }

    /**
     * Stops a previously started playing
     */
    public void stopPlaying() {
    	if(m_MediaPlayer != null && m_MediaPlayer.isPlaying()) {
    		m_MediaPlayer.stop();
    		m_MediaPlayer.release();
    		m_MediaPlayer = null;
    	}
    }
    
    /**
     * Release the player
     */
    public void release() {
        if (m_MediaPlayer != null) {
        	m_MediaPlayer.release();
        	m_MediaPlayer = null;
        }
    }
    
    public int getFileDuration() {
    	return m_Duration;
    }
    
    /**
     * Returns the active media player
     * @return the media player used for playing the sounds
     */
    public MediaPlayer getActiveMediaPlayer() {
    	return m_MediaPlayer;
    }
}