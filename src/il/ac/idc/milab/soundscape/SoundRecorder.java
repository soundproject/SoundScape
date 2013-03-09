package il.ac.idc.milab.soundscape;

import android.util.Log;

public class SoundRecorder {

	private static final String TAG = "SOUND_RECORDER";
	
	private boolean m_Recording = false;
	
	public void startRecording() {
		// TODO Auto-generated method stub
		Log.i(TAG,"Starting recording...");
		m_Recording = true;
	}

	public void stopRecording() {
		// TODO Auto-generated method stub
		Log.i(TAG,"Stopping recording...");
		m_Recording = false;
	}

}
