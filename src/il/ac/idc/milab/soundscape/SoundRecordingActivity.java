package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.SoundRecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SoundRecordingActivity extends Activity implements OnClickListener, OnCompletionListener {

	private static final Integer MAX_RECORDING_TIME = SoundRecorder.MAXIMUM_RECORDING_LENGTH / 1000;
	private static final String TAG = "SOUND_RECORDING";
	private SoundRecorder m_soundRecorder;
	private MediaPlayer m_mediaPlayer;
	private ImageButton m_recordingButton;
	private ImageButton m_deleteButton;
	private ImageButton m_saveButton;
	private ImageButton m_playRecordingButton;
	private ImageButton m_pauseButton;
	private TextView m_recordingWordTextView;
	private TextView m_soundNameTextView;
	private EditText m_soundNameEditText;
	private TextView m_difficultyTextView;
	private EditText m_difficultyEditText;
	private File m_file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_recording);
	}


	@Override
	protected void onResume() {
		super.onResume();

		initButtons();

		// init sound recorder:
		initSoundRecorder();

		this.m_recordingButton.setOnClickListener(this);
	}


	private void initButtons() {

		Log.d(TAG, "Init buttons");

		// Get buttons
		m_recordingButton = (ImageButton) findViewById(R.id.record_button);
		m_deleteButton = (ImageButton) findViewById(R.id.delete_sound_button);
		m_saveButton = (ImageButton) findViewById(R.id.save_sound_button);
		m_playRecordingButton = (ImageButton) findViewById(R.id.play_recording_button);
		m_pauseButton = (ImageButton) findViewById(R.id.pause_recording_button);
		m_recordingWordTextView = (TextView) findViewById(R.id.recording_word_text_view);
		m_soundNameEditText = (EditText) findViewById(R.id.sound_name_editText);
		m_soundNameTextView = (TextView) findViewById(R.id.sound_name_textView);
		m_difficultyEditText = (EditText) findViewById(R.id.enter_difficulty_editText);
		m_difficultyTextView = (TextView) findViewById(R.id.enter_difficulty_textView);

		Log.d(TAG, "Setting text");
		// set text
		String recordingWord = getIntent().getExtras().getString("word");
		m_recordingWordTextView.setText(recordingWord);
		this.m_isFreeStyleRecording = recordingWord.equals(WordSelectionActivity.k_FreeStyle);

		Log.d(TAG, "Freestyle = " + this.m_isFreeStyleRecording);

		Log.d(TAG, "Hiding buttons");
		// Hide appropriate buttons:
		toggleControl(m_deleteButton);
		toggleControl(m_playRecordingButton);
		toggleControl(m_pauseButton);
		toggleControl(m_saveButton);
		toggleControl(m_soundNameEditText);
		toggleControl(m_soundNameTextView);
		toggleControl(m_difficultyEditText);
		toggleControl(m_difficultyTextView);
		m_soundNameEditText.setVisibility(View.INVISIBLE);
		m_soundNameTextView.setVisibility(View.INVISIBLE);
		m_difficultyEditText.setVisibility(View.INVISIBLE);
		m_difficultyTextView.setVisibility(View.INVISIBLE);

		// Add listeners to buttons
		Log.d(TAG, "Setting onClickListeners");
		this.m_playRecordingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				m_mediaPlayer.start();
				toggleControl(v);
				toggleControl(m_pauseButton);

			}
		});

		this.m_deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleSaveDeleteMode();				
			}
		});

		this.m_saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//				Log.d("************", "STATUS: " + m_isFreeStyleRecording);
				if (!m_isFreeStyleRecording || !m_soundNameEditText.getText().toString().trim().isEmpty() ||
						!m_difficultyEditText.getText().toString().trim().isEmpty())
				{	
					Log.d(TAG, "Text is " + m_soundNameEditText.getText().toString());
					saveAndSendFile();
					Intent intent = new Intent(getApplicationContext(), GameLobbyActivity.class);
					startActivity(intent);
					finish();
				} else
				{
					Log.e(TAG, "Freestyle and name is empty");
					
				}
			}
		});

		this.m_pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(m_mediaPlayer.isPlaying())
				{
					m_mediaPlayer.pause();
					toggleControl(v);
					toggleControl(m_playRecordingButton);
				}				
			}
		});
	}


	private void saveAndSendFile() 
	{

		this.m_mediaPlayer.release();
		this.m_soundRecorder.release();

		// Build metadata
		JSONObject fileMetaData = new JSONObject();
		try {
			String word = m_isFreeStyleRecording ? m_soundNameEditText.getText().toString() :
				getIntent().getExtras().getString("word");
			fileMetaData.put(NetworkUtils.k_JsonKeyWord, word);
			int difficulty;
			if (m_isFreeStyleRecording)
			{
				difficulty = Integer.parseInt(m_difficultyEditText.getText().toString());
			} else
			{
				difficulty = getIntent().getExtras().getInt("difficulty");
			}
			Log.d(TAG, "Difficulty is " + difficulty);
			fileMetaData.put(NetworkUtils.k_JsonKeyDifficulty, difficulty);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(this.m_file.getName(), fileMetaData.toString());
		editor.commit();

		// Try to send:
		JSONObject result = null;
		try {
			result = new SendFileTask().execute(this.m_file.getAbsoluteFile().toString(), fileMetaData.toString()).get();

			if (result.optInt(NetworkUtils.k_JsonKeySuccess) == NetworkUtils.k_FlagOn)
			{
				Log.i(TAG, "Removing file " + this.m_file.getName() + " and deleting metadata");
				editor.remove(this.m_file.getName());
				deleteFile(this.m_file.getName());
				Log.i(TAG, "File " + this.m_file.getName() + " deleted");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onClick(View v) 
	{
		if (m_soundRecorder != null) {
			if (m_soundRecorder.isRecording()) {
				m_soundRecorder.stopRecording();
				//				this.m_recordingButton.setText("Start Recording");
				toggleSaveDeleteMode();
			} else {
				m_file = m_soundRecorder.startRecording();
				//				this.m_recordingButton.setText("Stop Recording");
				Log.i(TAG, "Current file is " + this.m_file);
			}
		}
	}

	private boolean m_isPlayDeleteMode = false;
	private boolean m_isFreeStyleRecording;

	private void toggleSaveDeleteMode() 
	{
		m_isPlayDeleteMode = !m_isPlayDeleteMode;
		// Add save/delete/play buttons
		toggleControl(this.m_saveButton);
		toggleControl(this.m_deleteButton);
		toggleControl(this.m_playRecordingButton);

		// Initialize sound recorder as needed
		if(m_isPlayDeleteMode)
		{
			this.m_mediaPlayer = MediaPlayer.create(this, Uri.fromFile(this.m_file));
			this.m_mediaPlayer.setOnCompletionListener(this);

			if (m_isFreeStyleRecording)
			{
				toggleControl(m_soundNameEditText);
				toggleControl(m_soundNameTextView);
				toggleControl(m_difficultyEditText);
				toggleControl(m_difficultyTextView);
				m_soundNameEditText.setVisibility(View.VISIBLE);
				m_soundNameTextView.setVisibility(View.VISIBLE);
				m_difficultyEditText.setVisibility(View.VISIBLE);
				m_difficultyTextView.setVisibility(View.VISIBLE);
			}

		} else 
		{
			Log.d(TAG, "Deleting file " + this.m_file.getName());
			deleteFile(this.m_file.getName());

			if (m_isFreeStyleRecording)
			{
				toggleControl(m_soundNameEditText);
				toggleControl(m_soundNameTextView);
				toggleControl(m_difficultyEditText);
				toggleControl(m_difficultyTextView);
				m_difficultyEditText.setVisibility(View.INVISIBLE);
				m_difficultyTextView.setVisibility(View.INVISIBLE);
				m_soundNameEditText.setVisibility(View.INVISIBLE);
				m_soundNameTextView.setVisibility(View.INVISIBLE);
			}
		}

		// toggle record button
		toggleControl(this.m_recordingButton);
	}

	private void toggleControl(View i_control)
	{
		i_control.setEnabled(!i_control.isEnabled());
	}

	private void initSoundRecorder() {
		this.m_soundRecorder = new SoundRecorder(this.getFilesDir());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sound_recording, menu);
		return true;
	}

	private class SendFileTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
			return NetworkUtils.sendFile(credentials[0], credentials[1]);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		toggleControl(m_pauseButton);
		toggleControl(m_playRecordingButton);

	}

}