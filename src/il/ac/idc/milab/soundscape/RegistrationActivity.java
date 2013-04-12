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

public class RegistrationActivity extends Activity {

	private EditText m_FullName;
	private EditText m_Email;
	private EditText m_Password;
	private EditText m_RePassword;
	private Button m_Register;
	private TextView m_Result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		m_FullName = (EditText)findViewById(R.id.registration_edit_text_name);
		m_Email = (EditText)findViewById(R.id.registration_edit_text_email);
		m_Password = (EditText)findViewById(R.id.registration_edit_text_password);
		m_RePassword = (EditText)findViewById(R.id.registration_edit_text_repassword);
		m_Result = (TextView)findViewById(R.id.registration_text_view_result);
		m_Register = (Button)findViewById(R.id.registration_button_register);
		m_Register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isRegistrationValid()) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		});
	}

	protected boolean isRegistrationValid() {
		boolean isValid = true;
		
		String name = m_FullName.getText().toString();
		String email = m_Email.getText().toString();
		String password = m_Password.getText().toString();
		String repassword = m_RePassword.getText().toString();
		
		if(name.length() == 0 || 
				email.length() == 0 || 
				password.length() == 0 || 
				repassword.length() == 0) {
			m_Result.setText("Input cannot be blank.");
			isValid = false;
		}
		else if(password.compareTo(repassword) != 0) {
			m_Result.setText("Passwords must match in both fields.");
			isValid = false;
		}
		else {
			JSONObject json = null;
			try {
				json = new UserRegisterTask().execute(
						name, email, password).get();
				Log.i("RESPONSE", json.toString());
				if(json.getInt(NetworkUtils.k_JsonKeySuccess) == 
						NetworkUtils.k_FlagOn) {
					isValid = true;
				}
				else {
					isValid = false;
					this.m_Result.setText(json.getString(NetworkUtils.k_JsonKeyErrorMessage));
				}
			} catch (Exception e) {
				Log.e("REGISTRATION", "Error: " + json.toString());
			} 
		}
		
		return isValid;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	private class UserRegisterTask extends AsyncTask<String, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(String... credentials) {
	    	return NetworkUtils.UserRegister(credentials[0], credentials[1], credentials[2]);
	    }
	}
	
}
