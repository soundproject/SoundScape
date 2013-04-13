package il.ac.idc.milab.soundscape.library;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.util.Log;

public class SoundRecorder {

	private static final String TAG = "SOUND_RECORDER";
	public static final int MAXIMUM_RECORDING_LENGTH = 20000;

	private boolean m_Recording = false;

	private MediaRecorder m_mediaRecorder = new MediaRecorder();

	private File m_FileDirectory;

	public SoundRecorder(File i_FileDirectory) {
		this.m_FileDirectory = i_FileDirectory;
		Log.i(TAG, "Setting directory to " + i_FileDirectory.toString());
		this.initAudioRecorder();
	}

	private void initAudioRecorder() {

		Log.i(TAG, "Initializing Audio Recorder...");
		Log.d(TAG, "Setting audio source");
		m_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		Log.d(TAG, "Setting output format");
		m_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		Log.d(TAG, "Output format set");
		Log.d(TAG, "Setting Audio Encoder");
		m_mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		Log.d(TAG, "Audio encoder set");
		Log.d(TAG, "Setting maximum recording length");
		m_mediaRecorder.setMaxDuration(MAXIMUM_RECORDING_LENGTH);
		Log.d(TAG, "Maximum recording length set");
		Log.i(TAG, "Audio Recorder intialized...");
	}

	public File startRecording() {
		// TODO Auto-generated method stub
		m_Recording = true;
		String filename = this.generateNextFile();

		Log.i(TAG, String.format("Setting output file to: %s", filename));
		m_mediaRecorder.setOutputFile(filename);

		Log.d(TAG, "preparing audio recorder...");
		try {
			m_mediaRecorder.prepare();
			Log.d(TAG, "audio recorder ready");
			Log.i(TAG, "Starting recording...");
			m_mediaRecorder.start();
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
			m_mediaRecorder.reset();
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
			m_mediaRecorder.reset();
			// e.printStackTrace();
		}
		return new File(filename);
	}

	private String generateNextFile() {
		// TODO Auto-generated method stub

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		String filename = String.format("recording_%s.amr",
				dateFormat.format(date));
		return new File(this.m_FileDirectory, filename).toString();
	}

	public void stopRecording() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Stopping recording...");
		this.m_Recording = false;
		this.m_mediaRecorder.stop();

		// re-initialize media recorder for use:
		this.initAudioRecorder();
	}

	public boolean isRecording() {
		return this.m_Recording;
	}

	public void release() {
		// TODO Auto-generated method stub
		this.m_mediaRecorder.release();
	}

}