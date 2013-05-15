package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordSelectionActivity extends Activity implements OnClickListener {

	private static final String TAG = "WORD_SELECTION";
	public static final String k_FreeStyle = "freestyle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_selection);

		JSONObject request;
		JSONObject response = null;
		request = buildRequestForRandomWords();
		response = NetworkUtils.serverRequests.sendRequestToServer(request, WordSelectionActivity.this);
		
		if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
			JSONObject words = response.optJSONObject(ServerRequests.RESPONSE_FIELD_WORDS);

			LinearLayout wordListLayout = (LinearLayout)findViewById(R.id.word_list_layout);
			
			for(int i = 0; i < words.length(); i++) {
				String index = String.format(Locale.US, "%d", i + 1);
				String currentWord = words.optString(index);
				
				TextView text = (TextView) wordListLayout.getChildAt(i);
				text.setOnClickListener(this);
				text.setText(currentWord);
				text.setTag(index);
			}
				
			
			TextView freestyle = (TextView)findViewById(R.id.word_freestyle);
			freestyle.setOnClickListener(this);
		}
		else {
			finish();
		}
	}
	
	/**
	 * This function gets a random set of words from the server by difficulty
	 * @return a JSON object representing a random set of words with difficulty 
	 * tags or null if response was not valid
	 */
	public JSONObject buildRequestForRandomWords() {
		JSONObject request = new JSONObject();
		
		try {
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_WORDS);
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}

		return request;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.word_selection, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		TextView text = (TextView) v;
		startRecordingActivity(text);
	}

	
	private void startRecordingActivity(TextView i_TextView) {
		
		String word = i_TextView.getText().toString();
		if(word.equalsIgnoreCase("record your own sound")) {
			word = "freestyle";
		}
		Intent intent = new Intent(getApplicationContext(),
				RecordingActivity.class);
		
		Game.setWord(word);
		Game.setDifficualty((String)i_TextView.getTag());
		
		startActivity(intent);
		finish();
	}
}