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
	private String m_Turn;
	private String m_TurnCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the user email

		m_UserEmail = ServerRequests.getUserEmail();
		m_OpponentEmail = getIntent().getStringExtra("opponent");
		m_Turn = getIntent().getStringExtra("turn");
		m_TurnCount = getIntent().getStringExtra("turnCount");
		
		Log.d("MATCH", "Started new game, players are:");
		Log.d("MATCH", "User: " + m_UserEmail);
		Log.d("MATCH", "Opponent: " + m_OpponentEmail);
		Log.d("MATCH", "Turn: " + m_Turn);
		Log.d("MATCH", "Turn count: " + m_TurnCount);
		setContentView(R.layout.activity_match);
		
		// Set the names
		TextView playerUser = (TextView)findViewById(R.id.match_text_view_left_player);
		playerUser.setText(m_UserEmail.split("@")[0]);
		
		TextView playerOpponent = (TextView)findViewById(R.id.match_text_view_right_player);
		playerOpponent.setText(m_OpponentEmail.split("@")[0]);
		
		// Set the turn
		TextView turn = (TextView)findViewById(R.id.match_text_view_turn_number);
		
		// Check if this is an existing game
		if(m_Turn == null) {
			m_Turn = "1";
		}
		
		turn.setText(m_Turn);
		
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
