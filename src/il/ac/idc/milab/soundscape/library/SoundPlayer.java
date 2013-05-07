package il.ac.idc.milab.soundscape.library;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class SoundPlayer {
	
	private static final String TAG = "SOUND_PLAYER";
	private MediaPlayer m_MediaPlayer = null;
	
	public SoundPlayer() {
		
	}

    public void startPlaying(String m_FilePathToPlay) {
    	m_MediaPlayer = new MediaPlayer();
    	
        try {
        	m_MediaPlayer.setDataSource(m_FilePathToPlay);
            m_MediaPlayer.prepare();
            m_MediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
    	m_MediaPlayer.release();
    	m_MediaPlayer = null;
    }
}
