package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindUserToPlayActivity extends Activity {

	private static final String TAG = "FIND_USER";
	private boolean m_IsRandom = false;
	private EditText m_Email;
	private Button m_Submit;
	private TextView m_Result;
	private String m_Game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Started FindUserToPlay activity");
		if(isRandomGame()) {
			Log.d(TAG, "User wants a RANDOM game");

			String email = getPlayer(null);
			Log.d(TAG, "Got random player: " + email);
			// If we got an email
			if(email != null) {
				startGameActivity(email);
				finish();
			}
			else {
				String msg = "There was a problem getting a player.. :(";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		}
		
		Log.d(TAG, "User wants to specify an email");
		// If not random
		setContentView(R.layout.activity_find_user_to_play);
		
		m_Email = (EditText)findViewById(R.id.finduser_edit_text_email);
		
		m_Submit = (Button)findViewById(R.id.finduser_button_submit);
		m_Submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = m_Email.getText().toString();
				Log.d(TAG, String.format("Checking if email '%s' is valid", email));
				if(getPlayer(email) != null) {
					startGameActivity(m_Email.getText().toString());
				}
				else {
					m_Result.setText("The email provided cannot be found :(");
				}
			}
		});
		
		m_Result = (TextView)findViewById(R.id.finduser_text_view_result);
	}

	private String getPlayer(String i_Email) {
		String opponentEmail = null;
		JSONObject response = new JSONObject();
		try {
			response = NetworkUtils.serverRequests.getPlayer(i_Email);
			if(response != null) {
				HashMap<String, String> map = JSONHelper.getMapFromJson(response);
				m_Game = map.get(ServerRequests.RESPONSE_FIELD_GAME);
				Log.d(TAG, "Got game details: " + m_Game);
				JSONObject gameDetails = new JSONObject(m_Game);
				map = JSONHelper.getMapFromJson(gameDetails);
				opponentEmail = map.get(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT);
			}
		} catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			Log.d(TAG, "Unable to parse game details!");
			e.printStackTrace();
		}
		
		return opponentEmail;
	}

	private void startGameActivity(String i_OpponentEmail) {
		Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME, m_Game);
		startActivity(intent);
		finish();
	}

	private boolean isRandomGame() {
		Bundle extras = getIntent().getExtras();
		m_IsRandom = extras.getBoolean("random");
		
		return m_IsRandom;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_user_to_play, menu);
		return true;
	}
}
