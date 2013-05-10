package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.JSONHelper;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class GameLobbyActivity extends Activity {

	private static final String TAG = "GAMELOBBY";
	private Button m_ButtonCreateGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting Game Lobby Activity");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_game_lobby);

		m_ButtonCreateGame = (Button) findViewById(R.id.lobby_button_create_game);
		m_ButtonCreateGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startNewGameActivity();
			}
		});
		
		JSONObject gameList;
		try {
			gameList = NetworkUtils.serverRequests.getUserGameList();
			
			if(gameList != null) {
				populateLobbyWithGames(gameList);	
			}
		} catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}

	protected void startNewGameActivity() {
		Intent intent = new Intent(getApplicationContext(),
				CreateGameActivity.class);
		startActivity(intent);
	}

	private void populateLobbyWithGames(JSONObject gameListObject) {
		// TODO:
		// [{"gid":"1","uEmail":"tal@tal.com","gTurn":"1","gTurnCount":"1","opponent":"gadi@gadi.com"}]
		Log.d(TAG, "Starting to populate buttons");
		try {
			// Extract all games from response and convert to hash map
			Log.d(TAG, "Converting response to rawMap");
			HashMap<String, String> rawMap = JSONHelper.getMapFromJson(gameListObject);

			String gamesString = rawMap.get(ServerRequests.RESPONSE_FIELD_GAMES);
			Log.d(TAG, "Extracting the games from the map: " + gamesString);
			Log.d(TAG, "Converting the The games into an array");
			JSONArray gameListArray = new JSONArray(gamesString);

			LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.lobby_list_view_container);
			RelativeLayout gameButton;

			for (int i = 0; i < gameListArray.length(); i++) {
				JSONObject game = gameListArray.getJSONObject(i);
				Log.d(TAG, "Converting the following game: " + game.toString());
				gameButton = createGameLayoutInstance(JSONHelper.getMapFromJson(game));
				buttonContainer.addView(gameButton);
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSON Error in GameLobby");
			e.printStackTrace();
		}
	}

	private RelativeLayout createGameLayoutInstance(final HashMap<String, String> map) {
		RelativeLayout game = new RelativeLayout(getApplicationContext());
		game.setBackground(getResources().getDrawable(R.drawable.border_black_button));

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
		String turnCount = map.get(ServerRequests.RESPONSE_FIELD_GAME_TURNCOUNT);
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
		String opponent = map.get(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT);
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
		String user = map.get(ServerRequests.RESPONSE_FIELD_GAME_USER);
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
		
		String state = map.get(ServerRequests.RESPONSE_FIELD_GAME_STATE);
		String whosTurn = map.get(ServerRequests.RESPONSE_FIELD_GAME_WHOSTURN);
		String buttonText = null;
		
		// If my turn 
		Log.d(TAG, "Current user: " + ServerRequests.getUserEmail());
		if(whosTurn.equalsIgnoreCase(ServerRequests.getUserEmail())) {
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
				Intent intent = new Intent(getApplicationContext(),
						MatchActivity.class);
				String game = null;
				try {
					game = JSONHelper.getJsonFromMap(map).toString();
				} catch (JSONException e) {
					Log.d(TAG, "Failed to convert map to JSON");
					e.printStackTrace();
				}
				Log.d(TAG, "Game details: " + game);
				intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME, game);
				Log.d(TAG, "Starting match!");
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_lobby, menu);
		return true;
	}
}