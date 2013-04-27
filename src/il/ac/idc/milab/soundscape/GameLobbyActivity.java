package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameLobbyActivity extends Activity {

	private static final String TAG = "GAMELOBBY";
	private String m_UserEmail;
	private String m_UserToken;
	private Button m_ButtonCreateGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the user email
		m_UserEmail = getIntent().getStringExtra(NetworkUtils.k_JsonKeyEmail);
		Log.d(TAG, "Starting Game Lobby Activity");
		setContentView(R.layout.activity_game_lobby);
		
		m_ButtonCreateGame = (Button)findViewById(R.id.lobby_button_create_game);
		m_ButtonCreateGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startNewGameActivity();
			}
		});
		
		ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Log.d(TAG, "Checking for Internet");
		if(NetworkUtils.isNetworkAvailable(connectivityManager) == false) {
			Log.d(TAG, "No Intternet found!");
			String message = "This activity requires an Internet connection.";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(TAG, "We have internet!");
			// Get the User name and token
			
			Bundle extras = getIntent().getExtras();
			Log.d(TAG, "Get the user email and token");
			if(extras == null) {
				Log.d(TAG, "Somehow I got into the game lobby without credentials..");
				finish();
			}
			
			m_UserToken = extras.getString(NetworkUtils.k_JsonKeyToken);
			Log.d(TAG, "We got:");
			Log.d(TAG, "Email: " + m_UserEmail);
			Log.d(TAG, "Token: " + m_UserToken);
			
			JSONObject request = NetworkUtils.getUserGameList(m_UserEmail);
			Log.d(TAG, "The request: " + request);
			try {
				JSONObject gameList = new GetGameListTask().execute(request).get();
				Log.d(TAG, "The response: " + gameList);
				populateLobbyWithGames(gameList);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			
		}
	}

	protected void startNewGameActivity() {
		Intent intent = new Intent(getApplicationContext(), CreateGameActivity.class);
		intent.putExtra(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
		startActivity(intent);
	}

	private void populateLobbyWithGames(JSONObject gameListObject) {
		//  TODO: [{"gid":"1","uEmail":"tal@tal.com","gTurn":"1","gTurnCount":"1","opponent":"gadi@gadi.com"}]
		Log.d(TAG, "Starting to populate buttons");
		try {
			// Extract all games from response and convert to hash map
			Log.d(TAG, "Converting response to rawMap");
			HashMap<String, String> rawMap = getMapFromJson(gameListObject);
			String gamesString = rawMap.get(NetworkUtils.k_JsonValueTagGetGames);
			Log.d(TAG, "Extracting the games from the map: " + gamesString);
			Log.d(TAG, "Converting the The games into an array");
			JSONArray gameListArray = new JSONArray(gamesString);
			
			LinearLayout buttonContainer = (LinearLayout)findViewById(R.id.lobby_list_view_container);
			RelativeLayout gameButton;
			
			for(int i = 0; i < gameListArray.length(); i++) {
				JSONObject game = gameListArray.getJSONObject(i);
				
				gameButton = createGameLayoutInstance(getMapFromJson(game));
				buttonContainer.addView(gameButton);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 0, 0);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		String turnCount = map.get("gTurnCount");
		tvTurnCount.setText(turnCount);
		tvTurnCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvTurnCount.setTextColor(Color.BLACK);
		tvTurnCount.setLayoutParams(params);
		
		
		// Init the opponent name
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.LEFT_OF, tvVersus.getId());
		String opponent = map.get("opponent");
		opponent = opponent.split("@")[0];
		tvOpponentName.setText(opponent);
		tvOpponentName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvOpponentName.setTextColor(Color.BLACK);
		tvOpponentName.setLayoutParams(params);
		
		// Init the Versus sign
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		params.setMargins(10, 0, 10, 0);
		tvVersus.setText("VS.");
		tvVersus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		tvVersus.setTextColor(Color.BLACK);
		tvVersus.setLayoutParams(params);
		
		// Init the user name
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.RIGHT_OF, tvVersus.getId());
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		String user = map.get("uEmail");
		user = user.split("@")[0];
		tvMyName.setText(user);
		tvMyName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tvMyName.setTextColor(Color.BLACK);
		tvMyName.setLayoutParams(params);
		
		// Init the play button
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		String myTurn = map.get("gTurn");
		if(myTurn.equalsIgnoreCase("1")) {
			myTurn = "Play";
		}
		else {
			myTurn = "Waiting";
		}
		
		button.setText(myTurn);
		button.setLayoutParams(params);
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Click!");
				Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
				intent.putExtra("turn", map.get("gTurn"));
				intent.putExtra("turnCount", map.get("gTurnCount"));
				intent.putExtra("user", map.get("uEmail"));
				intent.putExtra("opponent", map.get("opponent"));
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

	private HashMap<String, String> getMapFromJson(JSONObject object) throws JSONException {
		HashMap<String, String> map = new HashMap<String, String>();
		Iterator keys = object.keys();
			while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, object.getString(key));
		}
		
		return map;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_lobby, menu);
		return true;
	}
	
	private class GetGameListTask extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(JSONObject... json) {
	    	return NetworkUtils.sendJsonPostRequest(json[0]);
	    }
	}
}