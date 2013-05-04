package il.ac.idc.milab.soundscape;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MatchActivity extends Activity {

	private static final String TAG = "MatchActivity";
	private String m_GameDetails = null;
	private String m_UserEmail;
	private String m_OpponentEmail;
	private String m_State;
	private String m_TurnCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_GameDetails = getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_GAME);
		JSONObject gameDetails = null;
		try {
			gameDetails = new JSONObject(m_GameDetails);
		} catch (JSONException e) {
			Log.d(TAG, "Failed to create a JSONObject");
			e.printStackTrace();
		}
		
		HashMap<String, String> game = JSONHelper.getMapFromJson(gameDetails);
		m_UserEmail = game.get(ServerRequests.RESPONSE_FIELD_GAME_USER);
		m_OpponentEmail = game.get(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT);
		m_State = game.get(ServerRequests.RESPONSE_FIELD_GAME_STATE);
		m_TurnCount = game.get(ServerRequests.RESPONSE_FIELD_GAME_TURNCOUNT);
		
		Log.d(TAG, "Started new game, players are:");
		Log.d(TAG, "User: " + m_UserEmail);
		Log.d(TAG, "Opponent: " + m_OpponentEmail);
		Log.d(TAG, "State: " + m_State);
		Log.d(TAG, "Turn count: " + m_TurnCount);
		setContentView(R.layout.activity_match);
		
		// Set the names
		TextView playerUser = (TextView)findViewById(R.id.match_text_view_left_player);
		String user = m_UserEmail.split("@")[0];
		user = user.length() > 5 ? user.substring(0, 5) : user;
		playerUser.setText(user);
		
		TextView playerOpponent = (TextView)findViewById(R.id.match_text_view_right_player);
		String opponent = m_OpponentEmail.split("@")[0];
		opponent = opponent.length() > 5 ? opponent.substring(0, 5) : opponent;
		playerOpponent.setText(opponent);
		
		// Set the turn
		TextView turnCount = (TextView)findViewById(R.id.match_text_view_turn_number);
		
		turnCount.setText(m_TurnCount);
		
		// Start the game!
		Button go = (Button)findViewById(R.id.match_button_go);
		go.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGameActivity();
			}
		});
		
	}

	protected void startGameActivity() {
		Intent intent = null;
		
		if (m_State.equalsIgnoreCase("1")) {
			intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
		} 
		else if(m_State.equalsIgnoreCase("0")) {
			intent = new Intent(getApplicationContext(), GuessWordActivity.class);
		}
		else {
			Log.d(TAG, "I got no state somehow :(");
			finish();
		}

		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME, m_GameDetails);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

}
