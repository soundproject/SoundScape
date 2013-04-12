package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class OpeningScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opening_screen);

		// TODO: Change the following methods into a more secure method
		String token = getSessionToken();
		Log.i("TOKEN", "The token is: " + token);
		
		// Check if token is available, if not that means it has either expired
		// or it doesn't exist, both cases require the user to login
		if(token == null) {
			Log.i("LOGIN", "Starting Login Activity");
			startLoginActivity();
		}
		
		Log.i("GAME", "Starting Game Activity");
		startGameLobbyActivity(token);
	}
	
	/**
	 * This method returns an existing authentication token if present or null
	 * @return a valid token or null if one does not exist
	 */
	private String getSessionToken() {
		// TODO Need to think about how to implement this
		// Get the preference file from device
		SharedPreferences settings = getSharedPreferences(
				"il.ac.idc.milab.soundscape", 0);
		
		// returns the token value or null if does not exist
		return settings.getString("TOKEN", null);
	}

	/**
	 * Start a new login process for the user
	 */
	private void startLoginActivity() {
		Intent intent = new Intent(this.getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * Starts the game for the existing user
	 * @param token the authentication token of the user
	 */
	private void startGameLobbyActivity(String token) {
		Intent intent = new Intent(this.getApplicationContext(), GameLobbyActivity.class);
		intent.putExtra("TOKEN", token);
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opening_screen, menu);
		return true;
	}

}
