package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.eEmotions;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SoundTaggingActivity extends Activity {

	private static final String TAG = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_tagging);
	}

	private SeekBar m_emotionsSeekBar;
	private TextView m_emotionNameTextView;
	private RatingBar m_difficultyRatingBar;
	private Button m_sendButton;
	private eEmotions m_emotion = eEmotions.NEUTRAL;
	private boolean m_freeStyle;
	private EditText m_soundNameTextView;

	@Override
	protected void onResume() {
		super.onResume();
		m_freeStyle = getIntent().getExtras().getString("word").equals("freestyle");
		initButtons();

	}

	private void initButtons() {

		m_emotionNameTextView = (TextView) findViewById(R.id.emotion_name_textView);
		m_emotionsSeekBar = (SeekBar) findViewById(R.id.emotion_value_seekBar);
		m_difficultyRatingBar = (RatingBar) findViewById(R.id.difficulty_RatingBar);
		m_sendButton = (Button) findViewById(R.id.send_sound_button);
		m_soundNameTextView = (EditText) findViewById(R.id.sound_tag_editText);
		m_difficultyRatingBar = (RatingBar) findViewById(R.id.difficulty_RatingBar);

		m_emotionsSeekBar.setMax(eEmotions.values().length - 1);
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
				m_emotion = eEmotions.parseEmotionFromInt(progress);
				m_emotionNameTextView.setText(m_emotion.getLabel());
			}
		});

		m_emotionsSeekBar.setProgress(this.m_emotion.getValue());


		m_sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendFile();
			}
		});
		
		if (m_freeStyle)
		{
			m_soundNameTextView.setEnabled(true);
		} else
		{
			m_soundNameTextView.setText(getIntent().getExtras().getString("word"));
			m_soundNameTextView.setEnabled(false);	
			m_difficultyRatingBar.setEnabled(false);
			m_difficultyRatingBar.setRating(getIntent().getExtras().getInt("difficulty"));
		}
	}

	protected void sendFile() {

		JSONObject fileMetaData = new JSONObject();
		try {
			String word = m_soundNameTextView.getText().toString();
			fileMetaData.put(NetworkUtils.k_JsonKeyWord, word);			
			int difficulty = (int)this.m_difficultyRatingBar.getRating();
			Log.d(TAG, "Difficulty is " + difficulty);
			fileMetaData.put(NetworkUtils.k_JsonKeyDifficulty, difficulty);
			fileMetaData.put(NetworkUtils.k_JsonKeyEmotion, this.m_emotion.getValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Get shared preferences
		SharedPreferences prefs = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = prefs.edit();

		// Try to send:
		JSONObject result = null;
		try {
			File file = new File(getIntent().getExtras().getString("filename"));
			result = new SendFileTask().execute(file.getAbsoluteFile().toString(), fileMetaData.toString()).get();

			if (result.optInt(NetworkUtils.k_JsonKeySuccess) == NetworkUtils.k_FlagOn)
			{
				Log.i(TAG, "Removing file " + file.getName() + " and deleting metadata");
				editor.remove(file.getName());
				deleteFile(file.getName());
				Log.i(TAG, "File " + file.getName() + " deleted");
				editor.commit();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sound_tagging, menu);
		return true;
	}
	private class SendFileTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
			return NetworkUtils.sendFile(credentials[0], credentials[1]);
		}
	}
}
