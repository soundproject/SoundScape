package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.SoundPlayer;
import il.ac.idc.milab.soundscape.library.SoundRecorder;
import il.ac.idc.milab.soundscape.library.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
		
		// Check if freestyle or not
		m_SelectedWord = Game.getWord();
		
		// Check if word was passed successfully from the parent intent
		if(m_SelectedWord != null) {
			createCustomTitleFromWord(m_SelectedWord);
			
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
				public void onFinish() {
					onRecord(false);
				}
			};

			
			initControlButtons();
		}
		else {
			finish();
		}
	}
	
    @Override
    public void onResume() {
        super.onResume();
    }
	
    @Override
    public void onPause() {
        super.onPause();
        m_SoundRecorder.release();
        m_SoundPlayer.release();
    }

	/**
	 * This method generates the layout of the word we want to record
	 */
	private void createCustomTitleFromWord(String selectedWord) {
		String[] words = selectedWord.split(" ");

		for(int i = 0; i < words.length; i++) {
			String layoutName = "guessword_footer_word_letters_row" + (i + 1);
			int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			LinearLayout row = (LinearLayout)findViewById(layoutId);
			row.setVisibility(View.VISIBLE);

			String word = words[i].toUpperCase();

			// Remove the excessive views
			while(row.getChildCount() > word.length()) {
				row.removeViewAt(0);
			}
			
			for(int j = 0; j < row.getChildCount(); j++) {
				Button letter = (Button)row.getChildAt(j);
				letter.setText(String.valueOf(word.charAt(j)));
			}
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			row.setLayoutParams(params);
		}
	}

	private void initControlButtons() {
		// Init control buttons
		m_ButtonRecord = (ImageButton)findViewById(R.id.recording_button_record);
		m_ButtonRecord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRecord(m_IsRecording);
				m_IsRecording = !m_IsRecording;
			}
		});
		
		m_ButtonPlay = (ImageButton)findViewById(R.id.recording_button_play);
		m_ButtonPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPlay(m_IsPlaying);
			}
		});
		m_ButtonPlay.setVisibility(View.INVISIBLE);
		
		m_ButtonSave = (ImageButton)findViewById(R.id.recording_button_save);
		m_ButtonSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(m_SelectedWord.equalsIgnoreCase("freestyle")) {
					
					final EditText input = new EditText(RecordingActivity.this);
					new AlertDialog.Builder(RecordingActivity.this)
				    .setMessage("What did you record?")
				    .setView(input)
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            Game.setWord(input.getText().toString());
				            saveAndSendFile();
				        }
				    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            // Do nothing.
				        }
				    }).show();
				}
				else {
					saveAndSendFile();
				}
			}
		});
		m_ButtonSave.setVisibility(View.INVISIBLE);
	}
	
    private void onRecord(boolean start) {
        if (start) {
        	m_IsInProgress = true;
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	
        	// Init the progress bar values
        	m_ProgressBar.setProgress(0);
        	m_ProgressBar.setMax(MAX_RECORDING_TIME_IN_MILLIS);
        	m_Timer.start();
        	
            m_SoundRecorder.startRecording();
            m_ButtonSave.setVisibility(View.INVISIBLE);
            m_ButtonPlay.setVisibility(View.INVISIBLE);
        } else {
        	m_IsInProgress = false;
        	m_ProgressBar.setProgress(0);
        	m_ButtonRecord.setImageDrawable(getResources().getDrawable(R.drawable.btn_record));
        	m_Timer.cancel();
        	m_SoundRecorder.stopRecording();
        	m_SoundPlayer.initPlayer(m_SoundRecorder.getAbsolutePath());
            m_ButtonSave.setVisibility(View.VISIBLE);
            m_ButtonPlay.setVisibility(View.VISIBLE);
        }
    }

	private void onPlay(boolean start) {
        if (start && m_IsInProgress == false) {
        	m_IsInProgress = true;
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	
        	// Init the progress bar values
        	m_ProgressBar.setProgress(0);
        	m_ProgressBar.setMax(m_SoundPlayer.getFileDuration());
        	m_Timer.start();
            m_SoundPlayer.startPlaying();
        	m_SoundPlayer.getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					onPlay(false);
				}
			});
            m_ButtonSave.setVisibility(View.INVISIBLE);
            m_ButtonRecord.setVisibility(View.INVISIBLE);
        } else {
        	m_IsInProgress = false;
        	m_SoundPlayer.stopPlaying();
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
        	m_ProgressBar.setProgress(0);
        	m_Timer.cancel();
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
		// Build metadata
		JSONObject fileMetaData = new JSONObject();
		try {
			fileMetaData.put(ServerRequests.REQUEST_FIELD_WORD, m_SelectedWord);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(ServerRequests.REQUEST_FIELD_FILE_META, fileMetaData.toString());
		editor.commit();

		JSONObject request = buildRequestToSendFile();
		JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, RecordingActivity.this);
		
		if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
			editor.remove(m_SoundRecorder.getFileName());
			deleteFile(m_SoundRecorder.getFileName());
			editor.commit();
			new AlertDialog.Builder(RecordingActivity.this)
		    .setMessage("Your challenge has been sent!")
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	// Do Nothing
		        	finish();
		        }
		    }).show();
		}
	}

	private JSONObject buildRequestToSendFile() {
		
		byte[] encodedFile = encodeFile(new File(m_SoundRecorder.getAbsolutePath()));
		String fileEncoded = Base64.encodeToString(encodedFile, Base64.DEFAULT);

		JSONObject request = new JSONObject();
		try 
		{
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_SET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_FILE);
			request.put(ServerRequests.REQUEST_FIELD_FILE, fileEncoded);
			request.put(ServerRequests.REQUEST_FIELD_WORD, Game.getWord());
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_GAMEID, Game.getId());
		} 
		catch (JSONException e) {
			e.printStackTrace();
			request = null;
		}

		return request;
	}
	
	/**
	 * A helper function that returns a given file as a byte array
	 * @param i_file the file descriptor
	 * @return a byte array representing the file content
	 */
	private byte[] encodeFile(File i_file) {

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024;
		ByteArrayBuffer result = new ByteArrayBuffer(maxBufferSize);

		try {
			FileInputStream fileInputStream = new FileInputStream(i_file);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				result.append(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		return result.toByteArray();
	}
}
