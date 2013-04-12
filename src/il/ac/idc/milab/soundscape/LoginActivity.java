package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONObject;

import android.app.Activity;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		m_Result = (TextView)findViewById(R.id.login_text_view_result);
		Log.i("RESULT", m_Result.getText().toString());
		m_ButtonLogin = (Button)findViewById(R.id.login_button_login);
		m_ButtonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isvalidCredentials()) {
					setResult(Activity.RESULT_OK);
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

	protected boolean isvalidCredentials() {
		boolean isValid = true;
		m_Result.setText("");
		
		String userEmail = getUserEmail();
		String userHashedPassword = getUserPassword();
		
		if(userEmail.length() == 0 || userHashedPassword.length() == 0) {
			m_Result.setText(R.string.login_message_auth_fail);
			Log.i("RESULT", m_Result.getText().toString());
			isValid = false;
		}
		else {
			JSONObject json = null;
			try {
				json = new UserLoginTask().execute(
						userEmail, userHashedPassword).get();
				Log.i("RESPONSE", json.toString());
				if(json.getInt(NetworkUtils.k_JsonKeySuccess) == 
						NetworkUtils.k_FlagOn) {
					isValid = true;
				}
				else {
					isValid = false;
					m_Result.setText(json.getString(NetworkUtils.k_JsonKeyErrorMessage));
				}
			} catch (Exception e) {
				Log.e("LOGIN", "Error: " + json.toString());
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
/*
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
*/


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	private class UserLoginTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
	    	return NetworkUtils.UserLogin(credentials[0], credentials[1]);
	    }
	}
}