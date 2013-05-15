package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class CreateGameActivity extends Activity {

	private boolean m_RandomGame = false;
	private Button m_ButtonEmail;
	private Button m_ButtonRandom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

		m_ButtonEmail = (Button)findViewById(R.id.newgame_button_email);
		m_ButtonEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create game by specifying an email address
				m_RandomGame = false;
				startFindUserToPlayActivity(m_RandomGame);
				finish();
			}
		});
		
		m_ButtonRandom = (Button)findViewById(R.id.newgame_button_random);
		m_ButtonRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create a game with a random player
				m_RandomGame = true;
				startFindUserToPlayActivity(m_RandomGame);
				finish();
			}
		});
	}

	protected void startFindUserToPlayActivity(boolean random) {
		Intent intent = new Intent(this, FindUserToPlayActivity.class);
		intent.putExtra("random", random);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}