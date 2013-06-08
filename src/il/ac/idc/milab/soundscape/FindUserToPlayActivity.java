package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.AlertDialogHelper;
import il.ac.idc.milab.soundscape.library.Game;
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

/**
 * This class represents the new game activity where the user search for either
 * a random player to start a game with or a specific person (by e-mail)
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class FindUserToPlayActivity extends Activity {

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
			startGameWithPlayer(null);
		}
		
		// If not random
		setContentView(R.layout.activity_find_user_to_play);
		
		m_Email = (EditText)findViewById(R.id.finduser_edit_text_email);
		
		m_Submit = (Button)findViewById(R.id.finduser_button_submit);
		m_Submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = m_Email.getText().toString();
				
				// Check if the given email address is valid and is not 
				// the user's email
				if(User.validateEmailFormat(email) && 
						email.equalsIgnoreCase(
								User.getEmailAddress()) == false) {
					startGameWithPlayer(email);
				}
				else {
					String title = "Error:";
					String message = "The email provided is invalid.";
					AlertDialogHelper.buildErrorDialog(
							FindUserToPlayActivity.this, 
							title, 
							message).show();
				}
			}
		});
	}

	/**
	 * This method given an e-mail, searches for a player with that e-mail to
	 * start a game with, if e-mail provided is null, search for random player
	 * @param i_Email the e-mail of the person you wish to start playing with
	 * or null for random player
	 */
	private void startGameWithPlayer(String i_Email) {
		JSONObject request = buildRequestForPlayer(i_Email);
		JSONObject response = ServerRequests.sendRequestToServer(request, 
				FindUserToPlayActivity.this);
		
		if(ServerRequests.isValidResponse(response)) {
			JSONObject game;
			try {
				String gameDetails = response.getString(
						ServerRequests.RESPONSE_FIELD_GAME);
				game = new JSONObject(gameDetails);
				Game.init(game);
				startGameActivity(Game.getOpponent());
			} catch (JSONException e) {
				String title = "Error:";
				String message = "Couldn't create the game requested. " + 
						"Please try again";
				AlertDialogHelper.buildErrorDialog(getApplicationContext(), 
						title, 
						message).show();
			}
		}
	}

	/**
	 * This method is responsible for building the JSON request to start a game
	 * with a player
	 * @param i_Email the e-mail of the opponent you wish to start a game with
	 * @return a JSONObject representing the request for a game against a player
	 */
	private JSONObject buildRequestForPlayer(String i_Email) {
		
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, 
					ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, 
					ServerRequests.REQUEST_SUBJECT_EMAIL);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, 
					User.getEmailAddress());
			
			// If random player needed
			if(i_Email == null) {
				request.put(ServerRequests.REQUEST_FIELD_OPPONENT, 
						ServerRequests.REQUEST_FIELD_RANDOM);
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

	/**
	 * This method starts the game against the opponent we got
	 * @param i_OpponentEmail the opponent e-mail of this game
	 */
	private void startGameActivity(String i_OpponentEmail) {
		Intent intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * This is a helper method to check if the user wants a random player
	 * @return true is user requested a random player, false otherwise
	 */
	private boolean isRandomPlayer() {
		Bundle extras = getIntent().getExtras();
		m_IsRandom = extras.getBoolean("random");
		
		return m_IsRandom;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_user_to_play, menu);
		return true;
	}
}
