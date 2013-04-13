package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class OpeningScreenActivity extends Activity {

	private Button m_UserNew;
	private Button m_UserExisting;
	private String m_UserToken;
	private String m_UserEmail;
	ConnectivityManager connectivityManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("STARTUP", "Starting Opening Screen Activity");
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// TODO: Change the following methods into a more secure method
		boolean isValid = isUserCredentialsValid();

		// Check if token is available, if not that means it has either expired
		// or it doesn't exist, both cases require the user to login
		if(isValid) {
			Intent intent = getIntent();
			intent.putExtra(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
			intent.putExtra(NetworkUtils.k_JsonKeyToken, m_UserToken);
			startGameLobbyActivity();
			finish();
		}
		else {
			setContentView(R.layout.activity_opening_screen);
			m_UserNew = (Button)findViewById(R.id.opening_button_user_new);
			m_UserNew.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
						startRegistrationActivity();
				}
			});
			
			m_UserExisting = (Button)findViewById(R.id.opening_button_user_existing);
			m_UserExisting.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.d("STARTUP", "Starting Login Activity");
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
		Log.d("STARTUP", "Checking if email and token are present");
		m_UserToken = getSessionToken(NetworkUtils.k_JsonKeyToken);
		m_UserEmail = getSessionEmail(NetworkUtils.k_JsonKeyEmail);
		
		Log.d("STARTUP", "Email :" + m_UserEmail);
		Log.d("STARTUP", "Token :" + m_UserToken);
		
		// Check if we have valid local credentials
		if(m_UserToken == null || m_UserEmail == null) {
			Log.d("STARTUP", "Email or Token are not present!");
			isValid = false;
		}
		else {
			Log.d("STARTUP", "Found Email and Token!");
			// Check credentials with the server
			try {
				Log.d("STARTUP", "Check if token is valid");
				JSONObject userCredentials = new JSONObject();
				userCredentials.put(NetworkUtils.k_JsonKeyToken, m_UserToken);
				userCredentials.put(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
				Log.d("STARTUP", "The json request is: " + userCredentials.toString());
				JSONObject response = new CheckTokenTask().execute(m_UserToken, m_UserEmail).get();
				Log.d("STARTUP", "The json response is: " + response.toString());
				isValid = response.getInt(NetworkUtils.k_JsonKeySuccess) == 
						NetworkUtils.k_FlagOn;
				
			} catch (JSONException e) {
				Log.d("STARTUP", "Couldn't put stuff in our JSON object!");
				e.printStackTrace();
			} catch (Exception e) {
				Log.d("STARTUP", "Got an unexpected error.");
			}
		}
		return isValid;
	}

	private String getSessionEmail(String kJsonkeyemail) {
		// TODO Need to think about how to implement this
		// Get the preference file from device
		SharedPreferences settings = getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE);
		
		// returns the token value or null if does not exist
		return settings.getString(NetworkUtils.k_JsonKeyEmail, null);
	}

	/**
	 * This method returns an existing authentication token if present or null
	 * @param kJsonkeytoken 
	 * @return a valid token or null if one does not exist
	 */
	private String getSessionToken(String kJsonkeytoken) {
		// TODO Need to think about how to implement this
		// Get the preference file from device
		SharedPreferences settings = getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE);
		
		// returns the token value or null if does not exist
		return settings.getString(NetworkUtils.k_JsonKeyToken, null);
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
		
		// Update our application with the given token
		intent.putExtra(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
		intent.putExtra(NetworkUtils.k_JsonKeyToken, m_UserToken);
		
		// Save the token and user locally
		setUserToken(m_UserEmail, m_UserToken);
		
		// send them to the next intent
		startActivity(intent);
		finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("STARTUP", "Got results!!");
		// If the activity successfully finished
		if(resultCode == Activity.RESULT_OK) {
			Log.d("STARTUP", "Results are ok!");
			// Get the User name and token from the registration/login
			Bundle extras = data.getExtras();
			if(extras != null) {
				m_UserEmail = extras.getString(NetworkUtils.k_JsonKeyEmail);
				m_UserToken = extras.getString(NetworkUtils.k_JsonKeyToken);
				Log.d("STARTUP", "Email: " + m_UserEmail);
				Log.d("STARTUP", "Token: " + m_UserToken);
				
			}
			
			startGameLobbyActivity();
		}
		else if(resultCode == Activity.RESULT_FIRST_USER) {
			startRegistrationActivity();
		}
	}
	
	private void setUserToken(String email, String token) {
		SharedPreferences settings = getSharedPreferences("il.ac.idc.milab.soundscape", MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(NetworkUtils.k_JsonKeyEmail, email);
		editor.putString(NetworkUtils.k_JsonKeyToken, token);
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opening_screen, menu);
		return true;
	}
	
	private class CheckTokenTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
	    	return NetworkUtils.checkToken(credentials[0], credentials[1]);
	    }
	}

}
