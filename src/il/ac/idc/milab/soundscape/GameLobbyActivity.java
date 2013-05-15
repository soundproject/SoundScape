package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class GameLobbyActivity extends Activity {

	private static final String TAG = "GAMELOBBY";
	private Button m_ButtonCreateGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game_lobby);
		m_ButtonCreateGame = (Button) findViewById(R.id.lobby_button_create_game);
		m_ButtonCreateGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startNewGameActivity();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		JSONObject request = buildRequestForGameList();
		JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, GameLobbyActivity.this);
		
		if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
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
			RelativeLayout game;
			
			for (int i = 0; i < gameListArray.length(); i++) {
				JSONObject gameDetails = gameListArray.getJSONObject(i);
				
				Game.init(gameDetails);
				game = createGameLayoutInstance();
				
				// Saving the game details to use when the user chose
				game.setTag(gameDetails);
				gamesContainer.addView(game);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private RelativeLayout createGameLayoutInstance() {
		RelativeLayout game = new RelativeLayout(getApplicationContext());

		RelativeLayout.LayoutParams params;
		TextView tvTurnCount = new TextView(getApplicationContext());
		TextView tvOpponentName = new TextView(getApplicationContext());
		TextView tvVersus = new TextView(getApplicationContext());
		TextView tvMyName = new TextView(getApplicationContext());
		Button button = new Button(getApplicationContext());

		// Set the ID of all views
		tvTurnCount.setId(100);
		tvOpponentName.setId(101);
		tvVersus.setId(102);
		tvMyName.setId(103);
		button.setId(104);

		// Init turn count text view
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.setMargins(10, 0, 0, 0);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		String turnCount = Game.getTurnCount();
		tvTurnCount.setText(turnCount);
		tvTurnCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvTurnCount.setTextColor(Color.BLACK);
		tvTurnCount.setLayoutParams(params);

		// Init the opponent name
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.LEFT_OF, tvVersus.getId());
		String opponent = Game.getOpponent();
		opponent = opponent.split("@")[0];
		tvOpponentName.setText(opponent);
		tvOpponentName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvOpponentName.setTextColor(Color.BLACK);
		tvOpponentName.setLayoutParams(params);

		// Init the Versus sign
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		params.setMargins(10, 0, 10, 0);
		tvVersus.setText("VS.");
		tvVersus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvVersus.setTextColor(Color.BLACK);
		tvVersus.setLayoutParams(params);

		// Init the user name
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.RIGHT_OF, tvVersus.getId());
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		String user = Game.getUser();
		user = user.split("@")[0];
		tvMyName.setText(user);
		tvMyName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvMyName.setTextColor(Color.BLACK);
		tvMyName.setLayoutParams(params);

		// Init the play button
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		
		String whosTurn = Game.getWhosTurn();
		String buttonText = null;
		
		// If my turn 
		if(whosTurn.equalsIgnoreCase(User.getEmailAddress())) {
			buttonText = "Play";
		}
		else {
			buttonText = "Waiting";
			button.setEnabled(false);
		}

		button.setText(buttonText);
		button.setLayoutParams(params);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Update the chosen game details and launch Match activity
				Intent intent = new Intent(getApplicationContext(),
						MatchActivity.class);
				RelativeLayout parent = (RelativeLayout)v.getParent();
				JSONObject chosenGameDetails = (JSONObject)parent.getTag();
				Game.init(chosenGameDetails);
				startActivity(intent);
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
		
	protected void startNewGameActivity() {
		Intent intent = new Intent(getApplicationContext(),
				CreateGameActivity.class);
		startActivity(intent);
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
	public JSONObject buildRequestForGameList() {
		JSONObject request = new JSONObject();
		try {
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_GAMES);
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_TOKEN, User.getToken());
			
		} catch (JSONException e) {
			e.printStackTrace();
			request = null;
		}

		return request;
	}
}