package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = "MAIN";
	
	private LoginDialogFragment loginDialog;
	
	private String m_UserEmail;
	private String m_UserToken;
	private EditText m_FullName;
	private EditText m_Email;
	private EditText m_Password;
	private EditText m_RePassword;
	private Button m_Register;
	private Button m_Login;
	private TextView m_Result;
	private Intent m_Intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loginDialog = new LoginDialogFragment();
		
		// Init the connectivity manager
		ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkUtils.init(connectivityManager);
		
		Log.d(TAG, "Starting Opening Screen Activity");
		// Check if token is available, if not that means it has either expired
		// or it doesn't exist, both cases require the user to login

		if(isUserCredentialsValid()) {
			Log.d(TAG, "Credentials are valid!");
			startGameLobbyActivity();
		}
		else {
			Log.d(TAG, "Credentials invalid!");
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
						startGameLobbyActivity();
						finish();
					}
					else {
						m_Result.setText("Registration failed, please try again.");
					}
				}
			});
			
			m_Login = (Button)findViewById(R.id.registration_button_login);
			m_Login.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					loginDialog.show(getFragmentManager(), "login");
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
	
	/**
	 * Saves the user details locally to remember the user in future sessions
	 */
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//TODO: change this method to static
	@SuppressLint("ValidFragment")
	public class LoginDialogFragment extends DialogFragment {
		
		private EditText m_UserEmail;
		private EditText m_UserPassword;
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	// Get the layout inflater
	        LayoutInflater inflater = getActivity().getLayoutInflater();
	    	
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.opening_dialog_user_existing);
	               
	        LinearLayout dialogLogin = (LinearLayout)inflater.inflate(R.layout.dialog_login, null);
	        builder.setView(dialogLogin).setPositiveButton(R.string.opening_dialog_login, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	if (isValidCredentials()) {
    					startGameLobbyActivity();
    					dismiss();
    				}
    				else {
    					m_Result.setText("Incorrect email or password!");
    				}
                }
            })
            .setNegativeButton(R.string.opening_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
	        
	    	m_UserEmail = (EditText)dialogLogin.findViewById(R.id.login_dialog_username);
	    	m_UserPassword = (EditText)dialogLogin.findViewById(R.id.login_dialog_password);
	    	
	    	Log.d(TAG, "Email: " + m_UserEmail);
	    	Log.d(TAG, "Password: " + m_UserPassword);
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
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
					isValid = NetworkUtils.serverRequests.isValidLogin(m_UserEmail, userHashedPassword);
					
					if(isValid) {
						ServerRequests.setUserEmail(m_UserEmail);
					}
				} catch (NetworkErrorException e) {
					String msg = "A network connection is required";
					//Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				}
			}
			
			return isValid;
		}
		
		private String getUserPassword() {
			String encryptedPassword = encryptPassword(m_UserPassword.getText().toString()); 
			
			return encryptedPassword;
		}

		private String encryptPassword(String password) {
			// TODO Implement a hash/salt for secure password
			return password;
		}

		private String getUserEmail() {
			return m_UserEmail.getText().toString();
		}
	}
}
