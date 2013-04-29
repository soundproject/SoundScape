package il.ac.idc.milab.soundscape;

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

	private String m_UserEmail;
	private String m_OpponentEmail;
	private String m_State;
	private String m_TurnCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the user email

		m_UserEmail = ServerRequests.getUserEmail();
		m_OpponentEmail = getIntent().getStringExtra(ServerRequests.REQUEST_FIELD_OPPONENT);
		m_State = getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_STATE);
		m_TurnCount = getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_TURNCOUNT);
		
		Log.d("MATCH", "Started new game, players are:");
		Log.d("MATCH", "User: " + m_UserEmail);
		Log.d("MATCH", "Opponent: " + m_OpponentEmail);
		Log.d("MATCH", "State: " + m_State);
		Log.d("MATCH", "Turn count: " + m_TurnCount);
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
		
		// Check if this is an existing game
		if(m_TurnCount == null) {
			m_TurnCount = "1";
		}
		
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
		Intent intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
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
