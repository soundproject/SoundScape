package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class WordSelectionActivity extends Activity implements OnClickListener {

	private static final String TAG = "WordSelection";
	public static final String k_FreeStyle = "freestyle";
	private JSONObject m_jsonWords;

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

		
		// TODO: refactor
//		try 
//		{
//			// get new list of words
//			JSONObject request = NetworkUtils.serverRequests.getWords();
//			Log.d(TAG, "The request is: " + request);
//			JSONObject response = new GetWordsTask().execute(request).get();
//			Log.d(TAG, "The response is: " + response);
//			this.m_jsonWords = response.optJSONObject(NetworkUtils.k_JsonKeyWords);
//			Log.d(TAG, "The words are: " + m_jsonWords);
//			Log.d(TAG, "The length is: " + this.m_jsonWords.length());
//			for (int i = 0; i < this.m_jsonWords.length(); i++)
//			{
//				String index = String.format("%d", i + 1);
//				String currentWord = m_jsonWords.getString(index);
//				Log.d(TAG, "Got word " + currentWord);
//				addWordButton(currentWord, index);	
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
		// TODO Auto-generated method stub
		Button button = (Button) v;
		startRecordingActivity(button);
	}

	private void startRecordingActivity(Button button) {
		// TODO Auto-generated method stub
		
		String word = button.getText().toString();
		Log.d(TAG, "Selected word was: " + word);
		Intent intent = new Intent(getApplicationContext(),
				SoundRecordingActivity.class);
		intent.putExtra("word", word);
		/*Integer difficulty = (Integer) button.getTag();

		if (difficulty != null)
		{
			intent.putExtra("difficulty", difficulty.intValue());
			Log.e(TAG, "**** DIFFICULTY IS " + intent.getExtras().getInt("difficulty"));
		}
		*/
		startActivity(intent);
//		finish();
	}

//	private class GetWordsTask extends AsyncTask<JSONObject, Void, JSONObject> 
//	{		
//		@Override
//<<<<<<< HEAD
//		protected JSONObject doInBackground(JSONObject... params) {
//			return NetworkUtils.sendJsonPostRequest(params[0]);
//=======
//		protected JSONObject doInBackground(Void... params) {
//			//return NetworkUtils.getWords();
//			return null;
//>>>>>>> c4ba1a5... Refactored the whole server-client process
//		}
//	}
}