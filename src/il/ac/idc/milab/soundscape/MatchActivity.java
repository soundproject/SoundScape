package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MatchActivity extends Activity {

	private static final String TAG = "MatchActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_match);
		
		// Set the names
		TextView playerUser = (TextView)findViewById(R.id.match_text_view_left_player);
		String user = Game.getUser().split("@")[0];
		user = user.length() > 5 ? user.substring(0, 5) : user;
		playerUser.setText(user);
		
		TextView playerOpponent = (TextView)findViewById(R.id.match_text_view_right_player);
		String opponent = Game.getOpponent().split("@")[0];
		opponent = opponent.length() > 5 ? opponent.substring(0, 5) : opponent;
		playerOpponent.setText(opponent);
		
		// Set the turn
		TextView turnCount = (TextView)findViewById(R.id.match_text_view_turn_number);
		
		turnCount.setText(Game.getTurnCount());
		
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
		
		if (Game.getState().equalsIgnoreCase("1")) {
			intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
		} 
		else if(Game.getState().equalsIgnoreCase("0")) {
			intent = new Intent(getApplicationContext(), GuessWordActivity.class);
		}
		else {
			finish();
		}

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
