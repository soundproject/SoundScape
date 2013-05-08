package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.eEmotions;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SoundTaggingActivity extends Activity {

	private static final String TAG = "SoundTagging";

	private SeekBar m_emotionsSeekBar;

	private Button m_sendButton;

	private boolean m_freeStyle;
	private EditText m_soundNameEditText;
	private TextView m_soundNameTextView;
	private int m_emotion;
	private String m_GameDetails = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Started Sound Tagging Activity");
		setContentView(R.layout.activity_sound_tagging);

		m_GameDetails = getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_GAME);
		m_freeStyle = getIntent().getExtras().getString("word").equals("freestyle");
		Log.d(TAG, "Initializing buttons");
		initButtons();
	}

	private void initButtons() {

		m_emotionsSeekBar = (SeekBar) findViewById(R.id.emotion_value_seekBar);
		m_sendButton = (Button) findViewById(R.id.send_sound_button);
		m_soundNameEditText = (EditText) findViewById(R.id.sound_tag_editText);

		m_soundNameTextView = (TextView) findViewById(R.id.sound_name_textView);
		m_emotionsSeekBar.setMax(eEmotions.values().length - 1);
		
		if (!m_freeStyle)
		{
		
		}
		
		m_emotionsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				m_emotion = progress;
			}
		});

		m_emotionsSeekBar.setProgress(m_emotionsSeekBar.getMax() / 2);


		m_sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendFile();
			}
		});
		
		if (m_freeStyle)
		{
			m_soundNameEditText.setEnabled(true);
		} else
		{

			Log.i(TAG, "Hiding Buttons");

			this.m_soundNameTextView.setText("You just recorded:");
			m_soundNameEditText.setText(getIntent().getExtras().getString("word"));
			m_soundNameEditText.setEnabled(false);	
		}
	}

	protected void sendFile() {
		Log.d(TAG, "Sending File!");
		JSONObject fileMetaData = new JSONObject();
		String word = m_soundNameEditText.getText().toString();

		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		try {
			File file = new File(getIntent().getExtras().getString("filename"));
			String fileName = file.getAbsoluteFile().toString();
			JSONObject gameDetails = null;
			gameDetails = new JSONObject(m_GameDetails);
			HashMap<String, String> game = JSONHelper.getMapFromJson(gameDetails);
			String gameID = game.get(ServerRequests.RESPONSE_FIELD_GAME_ID);
			if (NetworkUtils.serverRequests.sendFile(fileName, word, m_emotion, gameID)) {
				Log.d(TAG, "Removing file " + file.getName() + 
						" and deleting metadata");
				editor.remove(file.getName());
				deleteFile(file.getName());
				Log.d(TAG, "File " + file.getName() + " deleted");
				editor.commit();
				if(getApplicationContext().deleteFile(fileName)) {
					Log.d(TAG, "Successfully deleted the file!");
				}
				finish();
			}
		} 
		catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			Log.d(TAG, "Couldn't create MAP from JSON");
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sound_tagging, menu);
		return true;
	}
}
