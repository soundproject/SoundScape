package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.User;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LoginOrRegisterActivity extends Activity {

	private static final String TAG = "MAIN";
	private static final String MSG_REGISTRATION = "Registration";
	private static final String MSG_LOGIN = "Log In";
	
	private User m_User;
	private EditText m_UserEmailAddress;
	private EditText m_UserPassword;
	private EditText m_UserRePassword;
	
	private ViewFlipper m_ViewFlipper;
	private Button m_SubmitButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init the connectivity manager
		ConnectivityManager connectivityManager 
		        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkUtils.init(connectivityManager);
		
		// Initiate our user profile
		m_User = new User(getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE));
		
		// Try and login in case we already have the user credentials
		if(m_User.isKnown() && 
				successfulRequestToServer(buildRequestByAction(ServerRequests.REQUEST_ACTION_LOGIN))) {
			startGameLobbyActivity();
		}
		// Show the login/registration screen
		setContentView(R.layout.activity_login_or_register);
		
		// Init our flipper
		m_ViewFlipper = (ViewFlipper)findViewById(R.id.login_flipper_layout);
		
		// Init the login form
		initLoginForm();
	}
	
	private void initLoginForm() {
		TextView moveToRegister = (TextView)findViewById(R.id.login_move_to_register);
		moveToRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initRegistrationForm();
				updateLoginTitle(MSG_REGISTRATION);
				TextView moveToRegister = (TextView)findViewById(R.id.login_move_to_register);
				moveToRegister.setVisibility(View.INVISIBLE);
				TextView moveToLogin = (TextView)findViewById(R.id.login_move_to_login);
				moveToLogin.setVisibility(View.VISIBLE);
				m_ViewFlipper.setInAnimation(LoginOrRegisterActivity.this, R.anim.view_transition_in_right);
				m_ViewFlipper.setOutAnimation(LoginOrRegisterActivity.this, R.anim.view_transition_out_right);
				m_ViewFlipper.showPrevious();
			}
		});
		
		m_UserEmailAddress = (EditText)findViewById(R.id.login_edittext_email);
		m_UserPassword = (EditText)findViewById(R.id.login_edittext_password);
		m_SubmitButton = (Button)findViewById(R.id.login_button_submit);
		m_SubmitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User.setEmailAddress(m_UserEmailAddress.getText().toString());
				User.setPassword(m_UserPassword.getText().toString());
				
				// Check if both required fields are in proper format
				if(emailFormatIsValid(User.getEmailAddress()) && 
						passwordFormatIsValid(User.getPassword())) {
					try {
						JSONObject request = buildRequestByAction(ServerRequests.REQUEST_ACTION_LOGIN);
						boolean success = successfulRequestToServer(request);
						if(success) {
							startGameLobbyActivity();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void initRegistrationForm() {
		TextView moveToLogin = (TextView)findViewById(R.id.login_move_to_login);
		moveToLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initLoginForm();
				updateLoginTitle(MSG_LOGIN);
				TextView moveToLogin = (TextView)findViewById(R.id.login_move_to_login);
				moveToLogin.setVisibility(View.INVISIBLE);
				TextView moveToRegister = (TextView)findViewById(R.id.login_move_to_register);
				moveToRegister.setVisibility(View.VISIBLE);
				m_ViewFlipper.setInAnimation(LoginOrRegisterActivity.this, R.anim.view_transition_in_left);
				m_ViewFlipper.setOutAnimation(LoginOrRegisterActivity.this, R.anim.view_transition_out_left);
				m_ViewFlipper.showNext();
			}
		});
		
		m_UserEmailAddress = (EditText)findViewById(R.id.registration_edittext_email);
		m_UserPassword = (EditText)findViewById(R.id.registration_edittext_password);
		m_UserRePassword = (EditText)findViewById(R.id.registration_edittext_repassword);
		m_SubmitButton = (Button)findViewById(R.id.registration_button_submit);
		m_SubmitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User.setEmailAddress(m_UserEmailAddress.getText().toString());
				User.setPassword(m_UserPassword.getText().toString());
				
				// Check if both required fields are in proper format
				if(emailFormatIsValid(User.getEmailAddress()) && 
						passwordFormatIsValid(User.getPassword()) &&
						passwordsMatch()) {
					try {
						JSONObject request = buildRequestByAction(ServerRequests.REQUEST_ACTION_REGISTER);
						boolean success = successfulRequestToServer(request);
						if(success) {
							startGameLobbyActivity();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected boolean passwordsMatch() {
		boolean isValid = false;
		String password = m_UserPassword.getText().toString();
		String repassword = m_UserRePassword.getText().toString();
		
		if(password.equals(repassword)) {
			isValid = true;
		}
		else {
			buildErrorDialog(null, "Passwords do not match.").show();
		}
		
		return isValid;
	}

	protected JSONObject buildRequestByAction(String action) {
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, action);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_PASSWORD, User.getPassword());
		}
		catch(JSONException e) {
			request = null;
			e.printStackTrace();
		}
		
		return request;
	}


	protected boolean passwordFormatIsValid(String password) {
		// TODO: add more logic if required
		boolean isValid = false;
		
		if(password.length() == 0) {
			String message = "Password cannot be empty."; 
			buildErrorDialog(null, message).show();
		}
		else {
			isValid = true;
		}
		return isValid;
	}

	protected void updateLoginTitle(String message) {
		TextView title = (TextView)findViewById(R.id.login_title);
		title.setText(message);
	}

	/**
	 * This method checks the email with our DB and returns whether it is there
	 * @param i_Request 
	 * @return true if the email is already in our DB, false otherwise
	 * @throws Exception 
	 */
	private boolean successfulRequestToServer(JSONObject i_Request) {
		boolean success = false;
		if(i_Request != null) {
			JSONObject response = null;
				response = NetworkUtils.serverRequests.sendRequestToServer(i_Request, LoginOrRegisterActivity.this);
				if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
					success = true;
					String token = response.optString(ServerRequests.RESPONSE_FIELD_TOKEN);
					User.setToken(token);
				}
		}
		
		return success;
	}


	/**
	 * This method checks a given string if it's a correct email format
	 * @param i_Email the string we wish to check
	 * @return true if the string represents a valid email format, false otherwise
	 * TODO: Make this method use regex to check for email validation
	 */
	private boolean emailFormatIsValid(String i_Email) {
		boolean isValid = false;
		
		int index = i_Email.indexOf("@");
		
		// Checks if there is a "@" somewhere in the string
		if(index != -1 && index > 0 && index < i_Email.length()) {
			isValid = true;
		}
		else {
			String message = "Incorrect Email Format"; 
			buildErrorDialog(null, message).show();
		}
		
		return isValid;
	}
	
	private AlertDialog buildErrorDialog(String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("Close", null);
		
		return alert.create();
	}

	/**
	 * Starts the game for the existing user
	 * @param token the authentication token of the user
	 */
	private void startGameLobbyActivity() {
		Intent intent = new Intent(this.getApplicationContext(), 
				GameLobbyActivity.class);
		
		User.saveUserDetails();
		
		// send them to the next intent
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_or_register, menu);
		return true;
	}
}
