package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class OpeningScreenActivity extends Activity {

	private Button m_UserNew;
	private Button m_UserExisting;
	private String m_Token;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: Change the following methods into a more secure method
		m_Token = getSessionToken();
		Log.i("TOKEN", "The token is: " + m_Token);
		
		// Check if token is available, if not that means it has either expired
		// or it doesn't exist, both cases require the user to login
		if(m_Token != null) {
			Log.i("GAME", "Starting Game Activity");
			startGameLobbyActivity(m_Token);
		}
		else {
			setContentView(R.layout.activity_opening_screen);
			m_UserNew = (Button)findViewById(R.id.opening_button_user_new);
			m_UserNew.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("REGISTRATION", "Starting Registration Activity");
					startRegistrationActivity();
				}
			});
			
			m_UserExisting = (Button)findViewById(R.id.opening_button_user_existing);
			m_UserExisting.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i("LOGIN", "Starting Login Activity");
					startLoginActivity();
				}
			});
			/*
			// Move to main menu of the game
			Intent main = new Intent(getApplicationContext(),
					GameLobbyActivity.class);

			// Clear non relevant activities from the activity class
			main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(main);
			*/

		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If the activity successfully finished
		if(resultCode == Activity.RESULT_OK) {
			startGameLobbyActivity(m_Token);
		}
		else if(resultCode == Activity.RESULT_FIRST_USER) {
			startRegistrationActivity();
		}
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
	 * Starts the registration process for the user 
	 */
	protected void startRegistrationActivity() {
		Intent registration = new Intent(getApplicationContext(), 
				RegistrationActivity.class);
		startActivityForResult(registration, 1);
	}
	
	/**
	 * Starts the login process for the user
	 */
	private void startLoginActivity() {
		Intent intent = new Intent(this.getApplicationContext(), 
				LoginActivity.class);
		startActivityForResult(intent, 2);
	}
	
	/**
	 * Starts the game for the existing user
	 * @param token the authentication token of the user
	 */
	private void startGameLobbyActivity(String token) {
		Intent intent = new Intent(this.getApplicationContext(), 
				GameLobbyActivity.class);
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
