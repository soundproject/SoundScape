package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	Button btnStartPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btnStartPlaying = (Button) findViewById(R.id.login_button_start);

		btnStartPlaying.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateUserCredentials()) {
					// Move to main menu of the game
					Intent main = new Intent(getApplicationContext(),
							GameLobbyActivity.class);

					// Clear non relevant activities from the activity class
					main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(main);

					finish();
				} else {
					// TODO: Implement error / register
				}

			}
		});
	}

	protected boolean validateUserCredentials() {
		String userEmail = getUserEmail();
		String userHashedPassword = getUserPassword();
		
		JSONObject response = NetworkUtils.UserLogin(
				userEmail,
				userHashedPassword);
		
		return true;
	}

	private String getUserPassword() {
		EditText userPassword = (EditText) findViewById(
				R.id.login_edit_text_password);
		String hashedPassword = encryptPassword(
				userPassword.getText().toString()); 
		return hashedPassword;
	}

	private String encryptPassword(String password) {
		// TODO Implement a hash/salt for secure password
		return password;
	}

	private String getUserEmail() {
		EditText userEmail = (EditText) findViewById(
				R.id.login_edit_text_email);
		return userEmail.getText().toString();
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}