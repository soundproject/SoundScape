package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class OpeningScreenActivity extends Activity {
	
	private static final String TAG = "STARTUP";
	
	private Button m_UserNew;
	private Button m_UserExisting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init the connectivity manager
		ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkUtils.init(connectivityManager);
		
		Log.d(TAG, "Starting Opening Screen Activity");
		// Check if token is available, if not that means it has either expired
		// or it doesn't exist, both cases require the user to login

		if(isUserCredentialsValid()) {
			startGameLobbyActivity();
			finish();
		}
		else {
			setContentView(R.layout.activity_opening_screen);
			m_UserNew = (Button)findViewById(R.id.opening_button_user_new);
			m_UserNew.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Starting Registration Activity");
					startRegistrationActivity();
				}
			});
			
			m_UserExisting = (Button)findViewById(R.id.opening_button_user_existing);
			m_UserExisting.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Starting Login Activity");
					startLoginActivity();
				}
			});
		}
	}

	/**
	 * This method checks if user credentials are valid
	 * @return true if user is recognized or false if a new login is required
	 */
	private boolean isUserCredentialsValid() {
		boolean isValid = false;
		
		Log.d(TAG, "Checking if email and token are present");
		String token = getSessionToken("token");
		String email = getSessionEmail("email");
		/*
		if(NetworkUtils.DEBUG_MODE) {
			email = "tal@tal.com";
			token = "5dhbmDxP59NIbtKbi+u4nff1z/VlNmVjYWY5Y2Q3";
		}
		else {
			email = "gadi@gadi.com";
			token = "wmr8+PWlDsjR6L6xlwWmH/DzNtoxYmFiMWRhM2U0";
		}
		*/
		Log.d(TAG, "Email :" + email);
		Log.d(TAG, "Token :" + token);
		
		// Check if we have valid local credentials
		if(token == null || email == null) {
			Log.d(TAG, "Email or Token are not present!");
			isValid = false;
		}
		else {
			Log.d(TAG, "Found Email and Token!");

			// Check credentials with the server
			Log.d(TAG, "Check if token is valid");
			try {
				isValid = NetworkUtils.serverRequests.isValidToken(email, token);
			} catch (NetworkErrorException e) {
				String msg = "This application requires an Internet connection.";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		}
		Log.d(TAG, "Is token valid? " + isValid);
		return isValid;
	}

	private String getSessionEmail(String i_Email) {
		// TODO Need to think about how to implement this
		// Get the preference file from device
		SharedPreferences settings = getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE);
		
		// returns the email value or null if does not exist
		return settings.getString(i_Email, null);
	}

	/**
	 * This method returns an existing authentication token if present or null
	 * @param kJsonkeytoken 
	 * @return a valid token or null if one does not exist
	 */
	private String getSessionToken(String i_Token) {
		// TODO Need to think about how to implement this
		// Get the preference file from device
		SharedPreferences settings = getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE);
		
		// returns the token value or null if does not exist
		return settings.getString(i_Token, null);
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
	private void startGameLobbyActivity() {
		Intent intent = new Intent(this.getApplicationContext(), 
				GameLobbyActivity.class);
		
		// Save the token and user locally
		setUserToken();
		
		// send them to the next intent
		startActivity(intent);
		finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "Got results!!");
		// If the activity successfully finished
		if(resultCode == Activity.RESULT_OK) {
			startGameLobbyActivity();
		}
		else if(resultCode == Activity.RESULT_FIRST_USER) {
			startRegistrationActivity();
		}
	}
	
	private void setUserToken() {
		SharedPreferences settings = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString("email", ServerRequests.getUserEmail());
		editor.putString("token", ServerRequests.getUserToken());
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opening_screen, menu);
		return true;
	}
}
