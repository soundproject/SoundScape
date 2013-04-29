package il.ac.idc.milab.soundscape;

import java.util.Locale;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;

import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class WordSelectionActivity extends Activity implements OnClickListener {

	private static final String TAG = "WORD_SELECTION";
	public static final String k_FreeStyle = "freestyle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_selection);
		
		LinearLayout wordListLayout = (LinearLayout) findViewById(R.id.word_list_layout);

		// clear any exisitng buttons in case we are resuming and buttons are
		// already here
		wordListLayout.removeAllViews();

		// Generate button for freestyle words
		Button wordButton = new Button(getApplicationContext());
		wordButton.setText(k_FreeStyle);
		wordButton.setOnClickListener(this);
		wordListLayout.addView(wordButton, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		wordListLayout.addView(new TextView(this));
		wordListLayout.addView(new TextView(this));

		JSONObject response = new JSONObject();
		try {
			response = NetworkUtils.serverRequests.getRandomWords();
			if(response != null) {
				JSONObject words = response.optJSONObject(ServerRequests.RESPONSE_FIELD_WORDS);
				Log.d(TAG, "The words are: " + words);
				Log.d(TAG, "The length is: " + words.length());
				
				for (int i = 0; i < words.length(); i++)
				{
					String index = String.format(Locale.US, "%d", i + 1);
					String currentWord = words.optString(index);
					Log.d(TAG, "Got word " + currentWord);
					addWordButton(currentWord, index);	
				}
			}
		} catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}

	private void addWordButton(String i_Word, String i_Difficulty) {

		Button wordButton =  new Button(getApplicationContext());
		LinearLayout wordListLayout = (LinearLayout) findViewById(R.id.word_list_layout);
		
		// get proper resource with number of stars. star_big_on is placeholder
		Drawable stars = getResources().getDrawable((android.R.drawable.star_big_on));
		
		wordButton = new Button(getApplicationContext());
		wordButton.setText(i_Word);
		wordButton.setOnClickListener(this);
		wordButton.setCompoundDrawablesWithIntrinsicBounds(null, null, stars, null);
		wordButton.setTag(i_Difficulty);
		
		wordListLayout.addView(wordButton, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.word_selection, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		startRecordingActivity(button);
	}

	private void startRecordingActivity(Button button) {
		
		String word = button.getText().toString();
		Log.d(TAG, "Selected word was: " + word);
		Intent intent = new Intent(getApplicationContext(),
				SoundRecordingActivity.class);
		intent.putExtra("word", word);
		Log.d(TAG, "The TAG is: " + button.getTag());
		Integer difficulty = Integer.getInteger((String) button.getTag());

		if (difficulty != null)
		{
			intent.putExtra("difficulty", difficulty.intValue());
			Log.d(TAG, "**** DIFFICULTY IS " + intent.getExtras().getInt("difficulty"));
		}
		
		startActivity(intent);
		finish();
	}
}