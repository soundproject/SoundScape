package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.SoundPlayer;
import il.ac.idc.milab.soundscape.library.SoundRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecordingActivity extends Activity {

	private static final String TAG = "SOUND_RECORDING";
	private static final int MAX_RECORDING_TIME_IN_MILLIS = 20000;
	
	private SoundRecorder m_SoundRecorder = null;
	private SoundPlayer m_SoundPlayer = null;
	
	private ProgressBar m_ProgressBar = null;
	private CountDownTimer m_Timer;
	
	private ImageButton m_ButtonRecord = null;
	private ImageButton m_ButtonSave = null;
	private ImageButton m_ButtonPlay = null;
	
	private boolean m_IsRecording = true;
	private boolean m_IsPlaying = true;
	private boolean m_IsInProgress = true;
	
	private String m_SelectedWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		Log.d(TAG, "Init progress bar");
		
		// Init Sound Recorder
		m_SoundRecorder = new SoundRecorder(getFilesDir(), MAX_RECORDING_TIME_IN_MILLIS);
		
		// Init Sound Player
		m_SoundPlayer = new SoundPlayer();
		
		// Init progress bar
		m_ProgressBar = (ProgressBar)findViewById(R.id.recording_progressBar);
		m_Timer = new CountDownTimer(MAX_RECORDING_TIME_IN_MILLIS, 50) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				if(m_IsInProgress) {
					m_ProgressBar.incrementProgressBy(50);
				}
			}
			
			@Override
			public void onFinish() {}
		};
		
		// Check if freestyle or not
		m_SelectedWord = getIntent().getStringExtra("word");
		Log.d(TAG, "Selected word: " + m_SelectedWord);
		
		if(m_SelectedWord != null) {
			LinearLayout customTitleContainer = (LinearLayout)findViewById(R.id.recording_linear_layout_title_container);
			if(m_SelectedWord.equalsIgnoreCase("freestyle")) {
				// Get the layout inflater (the service that help us add xml files to layout)
				LayoutInflater inflater =
					    (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				customTitleContainer.addView(inflater.inflate(R.layout.recording_freestyle_header, null));
			}
			else {
				customTitleContainer.addView(createCustomTitleFromWord(m_SelectedWord));
			}
		}
		
		Log.d(TAG, "Init control buttons");
		initControlButtons();
	}
	
    @Override
    public void onPause() {
        super.onPause();
        m_SoundRecorder.release();
        m_SoundPlayer.release();
    }

	private LinearLayout createCustomTitleFromWord(String selectedWord) {
		// Init the words container
		LinearLayout container = new LinearLayout(getApplicationContext());
		container.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		container.setLayoutParams(params);
		
		String[] words = selectedWord.split(" ");
		
		// For each word, create the custom TextView letters
		for(int i = 0; i < words.length; i++) {
			LinearLayout row = new LinearLayout(getApplicationContext());
			
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = 20;
			row.setLayoutParams(params);
			
			String word = words[i].toUpperCase();
			Log.d(TAG, "Word is: " + word);
			
			// Create the custom letters
			for(int j = 0; j < word.length(); j++) {
				TextView letterView = (TextView)getLayoutInflater().inflate(R.layout.textview_letter_style, null);
				params = new LinearLayout.LayoutParams(45, 50);
				params.rightMargin = 3;
				params.topMargin = 10;
				letterView.setLayoutParams(params);
				char letter = word.charAt(j);
				Log.d(TAG, "Letter is: " + letter);
				letterView.setText(String.valueOf(letter));

				row.addView(letterView);
			}
			
			container.addView(row);
		}
		
		return container;
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
		m_ButtonPlay.setVisibility(View.INVISIBLE);
		
		m_ButtonSave = (ImageButton)findViewById(R.id.recording_button_save);
		m_ButtonSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Save button pressed!");
				saveAndSendFile();
			}
		});
		m_ButtonSave.setVisibility(View.INVISIBLE);
	}

    private void starteProgressBar() {
    	Log.d(TAG, "Starting Counter");
		m_ProgressBar.setProgress(0);
		m_ProgressBar.setMax(MAX_RECORDING_TIME_IN_MILLIS);
    	m_Timer.start();
	}
	
    private void onRecord(boolean start) {
    	m_IsInProgress = start;
        if (start) {
        	Log.d(TAG, "Start Recording!");
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	starteProgressBar();
            m_SoundRecorder.startRecording();
            m_ButtonSave.setVisibility(View.INVISIBLE);
            m_ButtonPlay.setVisibility(View.INVISIBLE);
        } else {
        	Log.d(TAG, "Stop Recording!");
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_record));
        	m_Timer.cancel();
        	m_SoundRecorder.stopRecording();
            m_ButtonSave.setVisibility(View.VISIBLE);
            m_ButtonPlay.setVisibility(View.VISIBLE);
        }
    }

	private void onPlay(boolean start) {
		m_IsInProgress = start;
        if (start) {
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	starteProgressBar();
            m_SoundPlayer.startPlaying(m_SoundRecorder.getFile());
            m_ButtonSave.setVisibility(View.INVISIBLE);
            m_ButtonRecord.setVisibility(View.INVISIBLE);
        } else {
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
        	m_Timer.cancel();
            m_SoundPlayer.stopPlaying();
            m_ButtonSave.setVisibility(View.VISIBLE);
            m_ButtonRecord.setVisibility(View.VISIBLE);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording, menu);
		return true;
	}
	
	private void saveAndSendFile() 
	{
		Intent intent = new Intent(this, SoundTaggingActivity.class);

		// Build metadata
		JSONObject fileMetaData = new JSONObject();
		try {
			fileMetaData.put(ServerRequests.REQUEST_FIELD_WORD, m_SelectedWord);
			intent.putExtra(ServerRequests.REQUEST_FIELD_WORD, m_SelectedWord);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(ServerRequests.REQUEST_FIELD_FILE_META, fileMetaData.toString());
		editor.commit();
		
		intent.putExtra("filename", m_SoundRecorder.getFile());
		intent.putExtra("difficulty", getIntent().getExtras().getInt("difficulty", 0));
		
		Log.d(TAG, "Starting soundTagging");

		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME, getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_GAME));
		startActivity(intent);
		finish();
	}
}
