package il.ac.idc.milab.soundscape.library;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class SoundRecorder {

	private static final String TAG = "SOUND_RECORDER";
	private MediaRecorder m_MediaRecorder = null;
	private int m_MaxRecordingLengthInMillis = 0;
	private boolean m_IsRecording = false;
	private File m_FileDirectory;
	private String m_FileName;

	public SoundRecorder(File i_FileDirectory, int i_MaxRecordingLengthInMillis) {
		m_FileDirectory = i_FileDirectory;
		m_MaxRecordingLengthInMillis = i_MaxRecordingLengthInMillis;
		Log.i(TAG, "Setting directory to " + i_FileDirectory.toString());
	}

	/**
	 * This method initialize the Media Recorder with our default values
	 */
	private void initMediaRecorder() {
		m_MediaRecorder = new MediaRecorder();
		Log.i(TAG, "Initializing Media Recorder...");
		Log.d(TAG, "Setting audio source");
		m_MediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		Log.d(TAG, "Setting output format");
		m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		Log.d(TAG, "Output format set");
		Log.d(TAG, "Setting Audio Encoder");
		m_MediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		Log.d(TAG, "Audio encoder set");
		Log.d(TAG, "Setting maximum recording length");
		m_MediaRecorder.setMaxDuration(m_MaxRecordingLengthInMillis);
		Log.d(TAG, "Maximum recording length set");
		Log.i(TAG, "Audio Recorder intialized...");
		
		m_FileName = this.generateNextFile();

		Log.i(TAG, String.format("Setting output file to: %s", m_FileName));
		m_MediaRecorder.setOutputFile(m_FileName);
	}

	/**
	 * This method generates a file name to be used for the recording process
	 * @return a string representing the absolute path to the file
	 */
	private String generateNextFile() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		String filename = String.format("recording_%s.amr",
				dateFormat.format(date));
		return new File(m_FileDirectory, filename).toString();
	}
	
	public void startRecording() {
		initMediaRecorder();
		m_IsRecording = true;

		Log.d(TAG, "preparing audio recorder...");
		try {
			m_MediaRecorder.prepare();
		} 
		catch (IOException e) {
			Log.e(TAG, "prepare() failed");
			Log.e(TAG, e.getMessage());
		}
		
		Log.d(TAG, "audio recorder ready");
		Log.i(TAG, "Starting recording...");
		m_MediaRecorder.start();
	}

	public void stopRecording() {
		Log.i(TAG, "Stopping recording...");
		m_IsRecording = false;
    	m_MediaRecorder.stop();
    	m_MediaRecorder.release();
    	m_MediaRecorder = null;
	}

	public boolean isRecording() {
		return m_IsRecording;
	}
	
	public String getFile() {
		return m_FileName;
	}
}