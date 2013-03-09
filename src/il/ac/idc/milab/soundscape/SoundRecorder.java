package il.ac.idc.milab.soundscape;

import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class SoundRecorder {

	private static final String TAG = "SOUND_RECORDER";
	private static final int MAXIMUM_RECORDING_LENGTH = 20000;
	
	private boolean m_Recording = false;
	
	private MediaRecorder m_mediaRecorder = new MediaRecorder(); 
	
	public SoundRecorder()
	{
		m_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		m_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		m_mediaRecorder.setMaxDuration(MAXIMUM_RECORDING_LENGTH);
	}
	
	public void startRecording() {
		// TODO Auto-generated method stub
		m_Recording = true;
		String filename = this.generateNextFile();
		
		Log.i(TAG, String.format("Setting output file to: %s", filename));
		m_mediaRecorder.setOutputFile(filename);
		
		Log.i(TAG, "preparing audio recorder...");
		try {
			m_mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
//			e.printStackTrace();
		}
		Log.i(TAG, "audio recorder ready");
		
		Log.i(TAG,"Starting recording...");
//		m_mediaRecorder.
		
	}

	private String generateNextFile() {
		// TODO Auto-generated method stub
		
		return "/sdcard0/media/recording1.amr";
	}

	public void stopRecording() {
		// TODO Auto-generated method stub
		Log.i(TAG,"Stopping recording...");
		m_Recording = false;
	}
	
	public boolean isRecording()
	{
		return this.m_Recording;
	}

}
