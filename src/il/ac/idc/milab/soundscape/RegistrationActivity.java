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

public class RegistrationActivity extends Activity {

	private String m_UserEmail;
	private String m_UserToken;
	private EditText m_FullName;
	private EditText m_Email;
	private EditText m_Password;
	private EditText m_RePassword;
	private Button m_Register;
	private TextView m_Result;
	private Intent m_Intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("REGISTRATION", "Starting Registration Activity");
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
					setResult(Activity.RESULT_OK, m_Intent);
					finish();
				}
				else {
					m_Result.setText("Registration failed, please try again.");
				}
			}
		});
	}

	protected boolean isRegistrationValid() {
		Log.d("REGISTRATION", "Starting input validation");
		boolean isValid = true;
		
		String name = m_FullName.getText().toString();
		String email = m_Email.getText().toString();
		String password = m_Password.getText().toString();
		String repassword = m_RePassword.getText().toString();
		
		Log.d("REGISTRATION", "Input is:");
		Log.d("REGISTRATION", "Name:" + name);
		Log.d("REGISTRATION", "Email:" + email);
		Log.d("REGISTRATION", "Password:" + password);
		Log.d("REGISTRATION", "repassword:" + repassword);
		if(name.length() == 0 || 
				email.length() == 0 || 
				password.length() == 0 || 
				repassword.length() == 0) {
			m_Result.setText("Input cannot be blank.");
			Log.d("REGISTRATION", "Input is invalid!");
			isValid = false;
		}
		else if(password.compareTo(repassword) != 0) {
			m_Result.setText("Passwords must match in both fields.");
			Log.d("REGISTRATION", "Passwords do not match!");
			isValid = false;
		}
		else {
			Log.d("REGISTRATION", "Input is valid, starting registration process");
			try {
				isValid = NetworkUtils.serverRequests.isValidRegisteration(name, email, password);
			} catch (NetworkErrorException e) {
				String msg = "A network connection is required";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
}
