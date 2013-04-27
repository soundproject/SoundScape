package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static final String TAG = "LOGIN";
	
	private Button m_ButtonLogin;
	private Button m_ButtonRegister;
	private TextView m_Result;
	private Intent m_Intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Started the login activity");
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
				else {
					m_Result.setText("Incorrect email or password!");
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

	/**
	 * Check user credentials
	 * @return true is user credentials are valid, false otherwise
	 */
	protected boolean isValidCredentials() {
		Log.d(TAG, "Checking if credentials are valid");
		boolean isValid = false;
		m_Result.setText("");
		
		String m_UserEmail = getUserEmail();
		String userHashedPassword = getUserPassword();
		Log.d(TAG, "Login information is:");
		Log.d(TAG, "Email: " + m_UserEmail);
		Log.d(TAG, "Password: " + userHashedPassword);
		if(m_UserEmail.length() == 0 || userHashedPassword.length() == 0) {
			m_Result.setText(R.string.login_message_auth_fail);
			Log.d(TAG, "Input is invalid!");
		}
		else {
			Log.d(TAG, "Input is valid, sending request to server");

			// Check credentials with the server
			Log.d(TAG, "Check if user email and password are valid");
			try {
				isValid = NetworkUtils.serverRequests.isValidUser(m_UserEmail, userHashedPassword);
				
				if(isValid) {
					ServerRequests.setUserEmail(m_UserEmail);
				}
			} catch (NetworkErrorException e) {
				String msg = "A network connection is required";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
}