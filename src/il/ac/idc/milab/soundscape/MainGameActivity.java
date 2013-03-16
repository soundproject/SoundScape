package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.SingleMatch.eTurnState;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * Activity that handles a game against a single oponnent
 * 
 * @author Gadi. Tal
 *
 */
public class MainGameActivity extends Activity {

	private SingleMatch m_Match;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_game);

		Intent startingIntent = getIntent();
		User user = new User(startingIntent.getStringExtra("Opponent"));
		m_Match = new SingleMatch(user, eTurnState.YOUR_RECORDING);
		this.setTitle("Playing against: " + user.getName());

//		Button[] m_GamesButtons = initGamesButtons();

	}

	private Button[] initGamesButtons() {
		// TODO Auto-generated method stub

		// TODO: get games from server?
		int numberOfGames = 2;
		Button[] Buttons = new Button[numberOfGames];

		LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		
		LinearLayout linear = (LinearLayout) findViewById(R.id.gameLayoutLinear);

		for(int i = 0; i < Buttons.length; i++)
		{
			Button button = new Button(getApplicationContext());
			button.setTextSize(20);
			button.setHeight(100);
			button.setLayoutParams(param);
			button.setPadding(15, 5, 15, 5);
			linear.addView(button);
		}

		return Buttons;
	}

	View.OnClickListener createOnClickListener(final Button button)  {
		return new View.OnClickListener() {
			public void onClick(View v) {
				button.setText("text now set.. " + button.getId());    
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_game, menu);
		return true;
	}

}
