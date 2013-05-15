package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.User;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FindUserToPlayActivity extends Activity {

	private static final String TAG = "FIND_USER";
	private boolean m_IsRandom = false;
	private EditText m_Email;
	private Button m_Submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean randomPlayer = isRandomPlayer();
		if(randomPlayer) {
			JSONObject request = buildRequestForPlayer(null);
			JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, FindUserToPlayActivity.this);
			
			if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
				JSONObject game;
				try {
					game = new JSONObject(response.optString(ServerRequests.RESPONSE_FIELD_GAME));
					Game.init(game);
					startGameActivity(Game.getOpponent());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		// If not random
		setContentView(R.layout.activity_find_user_to_play);
		
		m_Email = (EditText)findViewById(R.id.finduser_edit_text_email);
		
		m_Submit = (Button)findViewById(R.id.finduser_button_submit);
		m_Submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = m_Email.getText().toString();
				
				// Check if the given email address is valid and is not the user's email
				if(emailFormatIsValid(email) && email.equalsIgnoreCase(User.getEmailAddress()) == false) {
					JSONObject request = buildRequestForPlayer(email);
					JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, FindUserToPlayActivity.this);
					
					if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
						JSONObject game;
						try {
							game = new JSONObject(response.optString(ServerRequests.RESPONSE_FIELD_GAME));
							Game.init(game);
							startGameActivity(Game.getOpponent());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				else {
					buildErrorDialog("Error:", "The email provided is invalid.");
				}
			}
		});
	}

	private JSONObject buildRequestForPlayer(String i_Email) {
		
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_EMAIL);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			
			// If random player needed
			if(i_Email == null) {
				request.put(ServerRequests.REQUEST_FIELD_OPPONENT, ServerRequests.REQUEST_FIELD_RANDOM);
			}
			else {
				request.put(ServerRequests.REQUEST_FIELD_OPPONENT, i_Email);
			}
			request.put(ServerRequests.REQUEST_FIELD_TOKEN, User.getToken());
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return request;
	}


	private void startGameActivity(String i_OpponentEmail) {
		Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
		startActivity(intent);
		finish();
	}

	private boolean isRandomPlayer() {
		Bundle extras = getIntent().getExtras();
		m_IsRandom = extras.getBoolean("random");
		
		return m_IsRandom;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_user_to_play, menu);
		return true;
	}
}
