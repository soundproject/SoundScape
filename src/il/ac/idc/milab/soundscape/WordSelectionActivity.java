package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class WordSelectionActivity extends Activity implements OnClickListener {

	private static final String TAG = "WordSelection";
	private static final int NUMBER_OF_WORDS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_selection);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout wordListLayout = (LinearLayout) findViewById(R.id.word_list_layout);

		// clear any exisitng buttons in case we are resuming and buttons are
		// already here
		wordListLayout.removeAllViews();

		// get new list of words
		String[] words = getWords();

		// Generate button for freestyle words
		Button wordButton = new Button(getApplicationContext());
		wordButton.setText("FREE RECORDING");
		wordButton.setOnClickListener(this);
		wordListLayout.addView(wordButton, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// Generate buttons for each word and add it to the layout
		for (int i = 0; i < words.length; i++) {
			wordButton = new Button(getApplicationContext());
			wordButton.setText(words[i]);
			wordButton.setOnClickListener(this);
			wordListLayout.addView(wordButton, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	private String[] getWords() {
		// TODO Auto-generated method stub

		String[] words = { "Hello", "Goodbye", "Cat", "Dog" };

		// Disabled for now - Assumes response is in format:
		// {..... "words": {"1": word1, "2": word2 ....}}
		// where 1 is easy, 2 is medium etc.
		words = new String[NUMBER_OF_WORDS];
		JSONObject response = NetworkUtils.getWords();
		try 
		{
			JSONObject jsonWords = response.getJSONObject("words");
			for (int i = 0; i < jsonWords.length(); i++)
			{
				words[i] = (String)jsonWords.get(String.format("%d", i));
				Log.d(TAG, "Got word " + words[i]);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "List of words recieved");
		return words;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.word_selection, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button button = (Button) v;
		startRecordingActivity(button.getText().toString());
	}

	private void startRecordingActivity(String string) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Selected word was: " + string);
		Intent intent = new Intent(getApplicationContext(),
				SoundRecordingActivity.class);
		intent.putExtra("word", string);
		startActivity(intent);
	}
}