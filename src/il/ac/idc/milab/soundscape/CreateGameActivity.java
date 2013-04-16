package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class CreateGameActivity extends Activity {

	private String m_UserEmail;
	private Button m_ButtonEmail;
	private Button m_ButtonRandom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the user email
		m_UserEmail = getIntent().getStringExtra(NetworkUtils.k_JsonKeyEmail);
		Log.d("CREATE", "Started the CreateGame activity");
		setContentView(R.layout.activity_create_game);

		m_ButtonEmail = (Button)findViewById(R.id.newgame_button_email);
		m_ButtonEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("CREATE", "User wants to search for a player");
				startFindUserToPlayActivity(false);
				finish();
			}
		});
		
		m_ButtonRandom = (Button)findViewById(R.id.newgame_button_random);
		m_ButtonRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("CREATE", "User wants to pick a random player");
				startFindUserToPlayActivity(true);
				finish();
			}
		});
	}

	protected void startFindUserToPlayActivity(boolean random) {
		Intent intent = new Intent(this, FindUserToPlayActivity.class);
		intent.putExtra("random", random);
		intent.putExtra(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
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