package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.AlertDialogHelper;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.User;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

/**
 * This class represents the login/registration screen logic
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class LoginOrRegisterActivity extends Activity {

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
		        = (ConnectivityManager) getSystemService(
		        		Context.CONNECTIVITY_SERVICE);
				NetworkUtils.init(connectivityManager);
		
		// Initiate our user profile
		m_User = new User(getSharedPreferences(
				"il.ac.idc.milab.soundscape", MODE_PRIVATE));
		
		// Try and login in case we already have the user credentials
		if(m_User.isKnown() && 
				successfulRequestToServer(buildRequestByAction(
						ServerRequests.REQUEST_ACTION_LOGIN))) {
			startGameLobbyActivity();
		}
		// Show the login/registration screen
		setContentView(R.layout.activity_login_or_register);
		
		// Init our flipper
		m_ViewFlipper = (ViewFlipper)findViewById(R.id.login_flipper_layout);
		
		// Init the login form
		initLoginForm();
	}
	
	/**
	 * This method initialize the login form with the proper fields
	 */
	private void initLoginForm() {
		TextView moveToRegister = (TextView)findViewById(
				R.id.login_move_to_register);
		moveToRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initRegistrationForm();
				updateLoginTitle(MSG_REGISTRATION);
				TextView moveToRegister = (TextView)findViewById(
						R.id.login_move_to_register);
				moveToRegister.setVisibility(View.INVISIBLE);
				TextView moveToLogin = (TextView)findViewById(
						R.id.login_move_to_login);
				moveToLogin.setVisibility(View.VISIBLE);
				m_ViewFlipper.setInAnimation(LoginOrRegisterActivity.this, 
						R.anim.view_transition_in_right);
				m_ViewFlipper.setOutAnimation(LoginOrRegisterActivity.this, 
						R.anim.view_transition_out_right);
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
						JSONObject request = buildRequestByAction(
								ServerRequests.REQUEST_ACTION_LOGIN);
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
	
	/**
	 * This method initialize the registration form with the proper fields
	 */
	private void initRegistrationForm() {
		TextView moveToLogin = (TextView)findViewById(R.id.login_move_to_login);
		moveToLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initLoginForm();
				updateLoginTitle(MSG_LOGIN);
				TextView moveToLogin = (TextView)findViewById(
						R.id.login_move_to_login);
				
				moveToLogin.setVisibility(View.INVISIBLE);
				TextView moveToRegister = (TextView)findViewById(
						R.id.login_move_to_register);
				moveToRegister.setVisibility(View.VISIBLE);
				m_ViewFlipper.setInAnimation(LoginOrRegisterActivity.this, 
						R.anim.view_transition_in_left);
				m_ViewFlipper.setOutAnimation(LoginOrRegisterActivity.this, 
						R.anim.view_transition_out_left);
				m_ViewFlipper.showNext();
			}
		});
		
		m_UserEmailAddress = (EditText)findViewById(
				R.id.registration_edittext_email);
		
		m_UserPassword = (EditText)findViewById(
				R.id.registration_edittext_password);
		
		m_UserRePassword = (EditText)findViewById(
				R.id.registration_edittext_repassword);
		
		m_SubmitButton = (Button)findViewById(
				R.id.registration_button_submit);
		m_SubmitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View i_View) {
				User.setEmailAddress(m_UserEmailAddress.getText().toString());
				User.setPassword(m_UserPassword.getText().toString());
				
				// Check if both required fields are in proper format
				if(emailFormatIsValid(User.getEmailAddress()) && 
						passwordFormatIsValid(User.getPassword()) &&
						passwordsMatch()) {
					try {
						JSONObject request = buildRequestByAction(
								ServerRequests.REQUEST_ACTION_REGISTER);
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

	/**
	 * This method checks whether the password that was entered in both fields
	 * of the registration form match
	 * @return true if passwords match, false otherwise
	 */
	protected boolean passwordsMatch() {
		boolean isValid = false;
		String password = m_UserPassword.getText().toString();
		String repassword = m_UserRePassword.getText().toString();
		
		if(password.equals(repassword)) {
			isValid = true;
		}
		else {
			String title = null;
			String message = "Passwords do not match.";
			AlertDialogHelper.buildErrorDialog(LoginOrRegisterActivity.this, 
					title, 
					message).show();
		}
		
		return isValid;
	}

	/**
	 * This method responsible for building a request based on the specified
	 * action (Login/Registration)
	 * @param i_Action
	 * @return
	 */
	protected JSONObject buildRequestByAction(String i_Action) {
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, i_Action);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, 
					User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_PASSWORD, 
					User.getPassword());
		}
		catch(JSONException e) {
			request = null;
			e.printStackTrace();
		}
		
		return request;
	}

	/**
	 * This method checks a given string if it's a correct password format
	 * @param i_Password the string we wish to check
	 * @return true if the string represents a valid password format, 
	 * false otherwise
	 */
	protected boolean passwordFormatIsValid(String i_Password) {
		boolean isValid = User.validatePasswordFormat(i_Password);
		
		if(isValid == false) {
			String title = null;
			String message = "Incorrect password format."; 
			AlertDialogHelper.buildErrorDialog(LoginOrRegisterActivity.this, 
					title, 
					message).show();
		}

		return isValid;
	}

	/**
	 * This is a helper method responsible to update the title based on the
	 * screen we are currently at (login/registration)
	 * @param i_Title the title
	 */
	protected void updateLoginTitle(String i_Title) {
		TextView title = (TextView)findViewById(R.id.login_title);
		title.setText(i_Title);
	}

	/**
	 * This method checks the email with our DB and returns whether it is there
	 * @param i_Request a JSONObject representing the request to the server
	 * @return true if the email is already in our DB, false otherwise
	 */
	private boolean successfulRequestToServer(JSONObject i_Request) {
		boolean success = false;
		if(i_Request != null) {
			JSONObject response = null;
			response = ServerRequests.sendRequestToServer(i_Request, 
					LoginOrRegisterActivity.this);
			if(ServerRequests.isValidResponse(response)) {
				success = true;
				String token = response.optString(
						ServerRequests.RESPONSE_FIELD_TOKEN);
				User.setToken(token);
			}
		}
		
		return success;
	}


	/**
	 * This method checks a given string if it's a correct email format
	 * @param i_Email the string we wish to check
	 * @return true if the string represents a valid email format, false 
	 * otherwise
	 */
	private boolean emailFormatIsValid(String i_Email) {
		boolean isValid = User.validateEmailFormat(i_Email);
		
		if(isValid == false) {
			String title = null;
			String message = "Incorrect Email Format."; 
			AlertDialogHelper.buildErrorDialog(LoginOrRegisterActivity.this, 
					title, 
					message).show();
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
