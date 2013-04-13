package il.ac.idc.milab.soundscape;



import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.SoundRecorder;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SoundRecordingActivity extends Activity implements OnClickListener {

	private static final Integer MAX_RECORDING_TIME = SoundRecorder.MAXIMUM_RECORDING_LENGTH / 1000;
	private static final String TAG = "SOUND_RECORDING";
	private SoundRecorder m_soundRecorder;
	private MediaPlayer m_mediaPlayer;
	private Button m_recordingButton;
	private Button m_deleteButton;
	private Button m_saveButton;
	private Button m_playRecordingButton;
	private TextView m_recordingWordTextView;
	private File m_file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_recording);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initButtons();


		// init sound recorder:
		initSoundRecorder();

		this.m_recordingButton.setText("Start Recording");
		TextView availableTime = (TextView) findViewById(R.id.recording_time_text_view);
		availableTime.setText(MAX_RECORDING_TIME.toString());

		this.m_recordingButton.setOnClickListener(this);
	}




	private void initButtons() {
		
		// Get buttons
		m_recordingButton = (Button) findViewById(R.id.record_button);
		m_deleteButton = (Button) findViewById(R.id.delete_sound_button);
		m_saveButton = (Button) findViewById(R.id.save_sound_button);
		m_playRecordingButton = (Button) findViewById(R.id.play_recording_button);
		m_recordingWordTextView = (TextView) findViewById(R.id.recording_word_text_view);
		
		// set text
		String recordingWord = getIntent().getExtras().getString("word");
		m_recordingWordTextView.setText(String.format("Try to record: %s", recordingWord));

		// Hide appropriate buttons:
		toggleButton(m_deleteButton);
		toggleButton(m_playRecordingButton);
		toggleButton(m_saveButton);

		// Add listeners to buttons
		this.m_playRecordingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (m_mediaPlayer.isPlaying())
				{
					m_mediaPlayer.stop();
					try {
						m_mediaPlayer.prepare();
						m_playRecordingButton.setText("Play");
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else
				{
					m_mediaPlayer.start();
					m_playRecordingButton.setText("Stop");
				}
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
				// TODO Auto-generated method stub
				saveAndSendFile();
				Intent intent = new Intent(getApplicationContext(), GameLobbyActivity.class);
				startActivity(intent);
				finish();
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
			fileMetaData.put("word", getIntent().getExtras().getString("word"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(this.m_file.getName(), fileMetaData.toString());
		editor.commit();
		
		// Try to send:
		if (NetworkUtils.sendFile(this.m_file))
		{
			editor.remove(this.m_file.getName());
			deleteFile(this.m_file.getName());
		} else
		{
			// TODO: what??
		}
		
	}


	@Override
	public void onClick(View v) 
	{
		if (m_soundRecorder != null) {
			if (m_soundRecorder.isRecording()) {
				m_soundRecorder.stopRecording();
				this.m_recordingButton.setText("Start Recording");
				toggleSaveDeleteMode();
			} else {
				m_file = m_soundRecorder.startRecording();
				this.m_recordingButton.setText("Stop Recording");
				Log.i(TAG, "Current file is " + this.m_file);
			}
		}
	}

	private boolean m_isPlayDeleteMode = false;

	private void toggleSaveDeleteMode() 
	{
		m_isPlayDeleteMode = !m_isPlayDeleteMode;
		// Add save/delete/play buttons
		toggleButton(this.m_saveButton);
		toggleButton(this.m_deleteButton);
		toggleButton(this.m_playRecordingButton);

		// Initialize sound recorder as needed
		if(m_isPlayDeleteMode)
		{
			this.m_mediaPlayer = MediaPlayer.create(this, Uri.fromFile(this.m_file));
			this.m_mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// Set text back to Start on completion
					m_playRecordingButton.setText("Start");
				}
			});
		} else 
		{
			Log.d(TAG, "Deleting file " + this.m_file.getName());
			deleteFile(this.m_file.getName());
		}

		// toggle record button
		toggleButton(this.m_recordingButton);
	}

	private void toggleButton(Button i_button)
	{
		i_button.setEnabled(!i_button.isEnabled());
		int visibility = i_button.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
		i_button.setVisibility(visibility);
	}

	private void initSoundRecorder() {
		// TODO Auto-generated method stub
		this.m_soundRecorder = new SoundRecorder(this.getFilesDir());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sound_recording, menu);
		return true;
	}

}