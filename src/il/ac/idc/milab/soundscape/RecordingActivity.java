package il.ac.idc.milab.soundscape;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class RecordingActivity extends Activity {

	private static final String TAG = "SOUND_RECORDING";
	private static final String TEMP_FILE = "tempSoundRecording";
	private static final String TEMP_DIR = "temp";
	private static final int MAX_RECORDING_TIME = 20;
	
	private MediaRecorder m_MediaRecorder = null;
	private MediaPlayer m_MediaPlayer = null;
	private String m_TempFile = null;
	
	private ProgressBar m_ProgressBar = null;
	private CountDownTimer m_Timer;
	
	private ImageButton m_ButtonRecord = null;
	private ImageButton m_ButtonStop = null;
	private ImageButton m_ButtonPlay = null;
	
	private boolean m_IsRecording = true;
	private boolean m_IsPlaying = true;
	private boolean m_IsInProgress = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		Log.d(TAG, "Init progress bar");
		
		// Init progress bar
		m_ProgressBar = (ProgressBar)findViewById(R.id.recording_progressBar);
		m_Timer = new CountDownTimer(MAX_RECORDING_TIME * 20000, 50) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				if(m_IsInProgress) {
					m_ProgressBar.incrementProgressBy(1);
				}
			}
			
			@Override
			public void onFinish() {}
		};
		
		// Check if freestyle or not
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "Init control buttons");
		initControlButtons();
	}
	
    @Override
    public void onPause() {
        super.onPause();
        if (m_MediaRecorder != null) {
        	m_MediaRecorder.release();
        	m_MediaRecorder = null;
        }

        if (m_MediaPlayer != null) {
        	m_MediaPlayer.release();
        	m_MediaPlayer = null;
        }
    }
	
	private void initMediaRecorder() {
		// Init media recorder
		m_MediaRecorder = new MediaRecorder();
		m_MediaRecorder.setAudioSource(AudioSource.MIC);
		m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		
		m_TempFile = getDir(TEMP_DIR, MODE_PRIVATE).getAbsolutePath() + TEMP_FILE;
		m_MediaRecorder.setOutputFile(m_TempFile);
		m_MediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	}
	
	private void initMediaPlayer() {
		m_MediaPlayer = new MediaPlayer();
	}
	
	private void initControlButtons() {
		// Init control buttons
		m_ButtonRecord = (ImageButton)findViewById(R.id.recording_button_record);
		m_ButtonRecord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRecord(m_IsRecording);
				Log.d(TAG, "Record button pressed!");
				// Set progress bar
				if(m_IsRecording) {
					starteProgressBar();
				}
				
				m_IsRecording = !m_IsRecording;
			}
		});
		
		m_ButtonPlay = (ImageButton)findViewById(R.id.recording_button_play);
		m_ButtonPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPlay(m_IsPlaying);
				Log.d(TAG, "Play button pressed!");
				// Set progress bar
				if(m_IsPlaying) {
					starteProgressBar();
				}
				
				m_IsPlaying = !m_IsPlaying;
			}
		});
	}

    private void starteProgressBar() {
    	Log.d(TAG, "Starting Counter");
		m_ProgressBar.setProgress(0);
		m_ProgressBar.setMax(MAX_RECORDING_TIME * 50);
    	m_Timer.start();
	}
	
    private void onRecord(boolean start) {
    	m_IsInProgress = start;
        if (start) {
        	Log.d(TAG, "Start Recording!");
        	starteProgressBar();
            startRecording();
        } else {
        	Log.d(TAG, "Stop Recording!");
        	m_Timer.cancel();
            stopRecording();
        }
    }

	private void onPlay(boolean start) {
		m_IsInProgress = start;
        if (start) {
        	starteProgressBar();
            startPlaying();
        } else {
        	m_Timer.cancel();
            stopPlaying();
        }
    }

    private void startPlaying() {
    	initMediaPlayer();
    	
        try {
        	m_MediaPlayer.setDataSource(m_TempFile);
            m_MediaPlayer.prepare();
            m_MediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
    	m_MediaPlayer.release();
    	m_MediaPlayer = null;
    }

    private void startRecording() {
    	initMediaRecorder();
    	
        try {
        	m_MediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        m_MediaRecorder.start();
    }

    private void stopRecording() {
    	m_MediaRecorder.stop();
    	m_MediaRecorder.release();
    	m_MediaRecorder = null;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording, menu);
		return true;
	}

}
