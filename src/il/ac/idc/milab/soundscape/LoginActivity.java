package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private Button m_ButtonLogin;
	private Button m_ButtonRegister;
	private TextView m_Result;
	private Intent m_Intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("LOGIN", "Started the login activity");
		setContentView(R.layout.activity_login);

		m_Result = (TextView)findViewById(R.id.login_text_view_result);
		m_ButtonLogin = (Button)findViewById(R.id.login_button_login);
		m_ButtonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isValidCredentials()) {
					setResult(Activity.RESULT_OK, m_Intent);
					finish();
				}
			}
		});
		
		m_ButtonRegister = (Button)findViewById(R.id.login_button_register);
		m_ButtonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_FIRST_USER);
				finish();
			}
		});
	}

	protected boolean isValidCredentials() {
		Log.d("LOGIN", "Checking if credentials are valid");
		boolean isValid = true;
		m_Result.setText("");
		
		String userEmail = getUserEmail();
		String userHashedPassword = getUserPassword();
		Log.d("LOGIN", "Login information is:");
		Log.d("LOGIN", "Email: " + userEmail);
		Log.d("LOGIN", "Password: " + userHashedPassword);
		if(userEmail.length() == 0 || userHashedPassword.length() == 0) {
			m_Result.setText(R.string.login_message_auth_fail);
			Log.d("LOGIN", "Input is invalid!");
			isValid = false;
		}
		else {
			Log.d("LOGIN", "Input is valid, sending request to server");
			JSONObject response = null;
			try {
				response = new UserLoginTask().execute(
						userEmail, userHashedPassword).get();
				Log.d("LOGIN", "Got the response: " + response.toString());
				if(response.getInt(NetworkUtils.k_JsonKeySuccess) == 
						NetworkUtils.k_FlagOn) {
					String token = response.getString(NetworkUtils.k_JsonKeyToken);
					Log.d("LOGIN", "Login was successful!");
					Log.d("LOGIN", "Got back: ");
					Log.d("LOGIN", "Email: " + userEmail);
					Log.d("LOGIN", "Token: " + token);
					m_Intent = getIntent();
					m_Intent.putExtra(NetworkUtils.k_JsonKeyEmail, userEmail);
					m_Intent.putExtra(NetworkUtils.k_JsonKeyToken, token);
					isValid = true;
				}
				else {
					Log.d("LOGIN", "Login failed!");
					isValid = false;
					m_Result.setText(response.getString(NetworkUtils.k_JsonKeyErrorMessage));
				}
			} catch (Exception e) {
				Log.d("LOGIN", "Error, response is:" + response);
			} 
		}
		
		return isValid;
	}

	private String getUserPassword() {
		EditText userPassword = (EditText) findViewById(
				R.id.login_edit_text_password);
		
		String encryptedPassword = encryptPassword(
				userPassword.getText().toString()); 
		
		return encryptedPassword;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private class UserLoginTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
	    	return NetworkUtils.userLogin(credentials[0], credentials[1]);
	    }
	}
}