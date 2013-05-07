package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.ServerRequests;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
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
	private static final String TEMP_FILE = "tempSoundRecording";
	private static final String TEMP_DIR = "temp";
	private static final int MAX_RECORDING_TIME = 20;
	
	private SaveFileDialogFragment confirmSave;
	
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
	
	private String m_SelectedWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		Log.d(TAG, "Init progress bar");
		
		// Init confirm dialog
		confirmSave = new SaveFileDialogFragment();
		
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
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	starteProgressBar();
            startRecording();
        } else {
        	Log.d(TAG, "Stop Recording!");
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_record));
        	m_Timer.cancel();
            stopRecording();
            confirmSave.show(getFragmentManager(), "dialog");
        }
    }

	private void onPlay(boolean start) {
		m_IsInProgress = start;
        if (start) {
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	starteProgressBar();
            startPlaying();
        } else {
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
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
		
		intent.putExtra("filename", m_TempFile);
		intent.putExtra("difficulty", getIntent().getExtras().getInt("difficulty", 0));
		
		Log.d(TAG, "Starting soundTagging");

		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME, getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_GAME));
		startActivity(intent);
		finish();
	}
	
	public class SaveFileDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.recording_alert_title)
	               .setPositiveButton(R.string.recording_alert_save, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   saveAndSendFile();
	                   }
	               })
	               .setNegativeButton(R.string.recording_alert_cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

}
