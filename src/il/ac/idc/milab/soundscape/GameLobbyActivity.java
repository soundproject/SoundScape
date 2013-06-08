package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.AlertDialogHelper;
import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.User;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class represents the main lobby of the games.
 * The player can see here all games that he/she are currently playing and 
 * start a new game if he wishes
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class GameLobbyActivity extends Activity {

	private boolean m_RandomGame = false;
	private Button m_ButtonEmail;
	private Button m_ButtonRandom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game_lobby);
		m_ButtonEmail = (Button)findViewById(R.id.newgame_button_email);
		m_ButtonEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create game by specifying an email address
				m_RandomGame = false;
				startFindUserToPlayActivity(m_RandomGame);
			}
		});
		
		m_ButtonRandom = (Button)findViewById(R.id.newgame_button_random);
		m_ButtonRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Create a game with a random player
				m_RandomGame = true;
				startFindUserToPlayActivity(m_RandomGame);
			}
		});
	}
	
	private void startFindUserToPlayActivity(boolean random) {
		Intent intent = new Intent(this, FindUserToPlayActivity.class);
		intent.putExtra("random", random);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		JSONObject request = buildRequestForGameList();
		JSONObject response = ServerRequests.sendRequestToServer(request, GameLobbyActivity.this);
		
		if(ServerRequests.isValidResponse(response)) {
			populateLobbyWithGames(response);
		}
	}

	private void populateLobbyWithGames(JSONObject i_Response) {

		try {
			// Extract all games from response and convert to hash map
			HashMap<String, String> rawMap = JSONHelper.getMapFromJson(i_Response);

			String gamesString = rawMap.get(ServerRequests.RESPONSE_FIELD_GAMES);
			JSONArray gameListArray = new JSONArray(gamesString);
			
			LinearLayout gamesContainer = (LinearLayout) findViewById(R.id.lobby_list_view_container);
			gamesContainer.removeAllViews();
			LinearLayout game;
			
			for (int i = 0; i < gameListArray.length(); i++) {
				JSONObject gameDetails = gameListArray.getJSONObject(i);
				
				Game.init(gameDetails);
				game = createGameLayoutInstance();
				
				// Saving the game details to use when the user chose
				game.setTag(gameDetails);
				
				// Sort games that games where I can play will show at the top
				String whosTurn = Game.getWhosTurn();
				if(whosTurn.equalsIgnoreCase(User.getEmailAddress())) {
					gamesContainer.addView(game, 0);
				}
				else {
					gamesContainer.addView(game);
				}
				
			}

		} catch (JSONException e) {
			String title = "Error:";
			String message = "The was an error getting all games in the list"; 
			AlertDialogHelper.buildErrorDialog(GameLobbyActivity.this, 
					title, 
					message).show();
		}
	}

	/**
	 * This method creates a single line representing a single game between the
	 * user and an opponent
	 * @return a LinearLayout that represents a game
	 */
	private LinearLayout createGameLayoutInstance() {
		LinearLayout game = new LinearLayout(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		game.setBackgroundColor(getResources().getColor(R.color.AliceBlue));
		game.setLayoutParams(params);
		
		TextView tvTurnCount = new TextView(getApplicationContext());
		TextView tvOpponentName = new TextView(getApplicationContext());
		TextView tvVersus = new TextView(getApplicationContext());
		TextView tvMyName = new TextView(getApplicationContext());
		Button button = new Button(getApplicationContext());

		// Init turn count text view
		params = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.MATCH_PARENT,
				1f);

		String turnCount = Game.getTurnCount();
		tvTurnCount.setText(turnCount);
		tvTurnCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvTurnCount.setTextColor(Color.BLACK);
		tvTurnCount.setGravity(Gravity.CENTER);
		tvTurnCount.setLayoutParams(params);

		// Init the opponent name
		params = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.MATCH_PARENT,
				3f);

		String opponent = Game.getOpponent();
		if(opponent.equalsIgnoreCase(User.getEmailAddress())) {
			opponent = "You";
		}
		else {
			opponent = opponent.split("@")[0];
		}
		opponent = opponent.split("@")[0];
		tvOpponentName.setText(opponent);
		tvOpponentName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvOpponentName.setTextColor(Color.BLACK);
		tvOpponentName.setGravity(Gravity.CENTER);
		tvOpponentName.setLayoutParams(params);

		// Init the Versus sign
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		tvVersus.setText("VS.");
		tvVersus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvVersus.setTextColor(Color.BLACK);
		tvVersus.setGravity(Gravity.CENTER);
		tvVersus.setLayoutParams(params);

		// Init the user name
		params = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.MATCH_PARENT,
				3f);

		String user = Game.getUser();
		if(user.equalsIgnoreCase(User.getEmailAddress())) {
			user = "You";
		}
		else {
			user = user.split("@")[0];
		}
		tvMyName.setText(user);
		tvMyName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvMyName.setTextColor(Color.BLACK);
		tvMyName.setGravity(Gravity.CENTER);
		tvMyName.setLayoutParams(params);

		// Init the play button
		params = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.MATCH_PARENT,
				2f);
		
		String whosTurn = Game.getWhosTurn();
		String buttonText = null;
		
		// If my turn 
		if(whosTurn.equalsIgnoreCase(User.getEmailAddress())) {
			buttonText = "Play";
		}
		else {
			buttonText = "Waiting";
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			button.setTextColor(Color.BLACK);
			button.setEnabled(false);
		}

		button.setText(buttonText);
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Update the chosen game details and launch game activity
				LinearLayout parent = (LinearLayout)v.getParent();
				JSONObject chosenGameDetails = (JSONObject)parent.getTag();
				Game.init(chosenGameDetails);
				startGameActivity();
			}
		});
		
		// Init the layout
		game.addView(tvTurnCount);
		game.addView(tvOpponentName);
		game.addView(tvVersus);
		game.addView(tvMyName);
		game.addView(button);

		return game;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_lobby, menu);
		return true;
	}
	
	/**
	 * This function gets the user active game list
	 * @return a JSON object representing the user game list or null if 
	 * response was not valid
	 */
	private JSONObject buildRequestForGameList() {
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_GAMES);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_TOKEN, User.getToken());
			
		} catch (JSONException e) {
			String title = "Error:";
			String message = "Couldn't build the request for the games."; 
			AlertDialogHelper.buildErrorDialog(GameLobbyActivity.this, 
					title, 
					message).show();
			request = null;
		}

		return request;
	}
	
	/**
	 * This method is responsible for starting the next activity according to
	 * the state (guessing/recording)
	 */
	private void startGameActivity() {
		Intent intent = null;
		
		if (Game.getState().equalsIgnoreCase("1")) {
			intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
		} 
		else if(Game.getState().equalsIgnoreCase("0")) {
			intent = new Intent(getApplicationContext(), GuessWordActivity.class);
		}
		else {
			finish();
		}

		startActivity(intent);
	}
}