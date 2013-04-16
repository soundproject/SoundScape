package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GameLobbyActivity extends Activity {

	private String m_UserEmail;
	private String m_UserToken;
	private Button m_ButtonCreateGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("GAMELOBBY", "Starting Game Lobby Activity");
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
		Log.d("GAMELOBBY", "Checking for Internet");
		if(NetworkUtils.isNetworkAvailable(connectivityManager) == false) {
			Log.d("GAMELOBBY", "No Intternet found!");
			String message = "This activity requires an Internet connection.";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
		else {
			Log.d("GAMELOBBY", "We have internet!");
			// Get the User name and token
			
			Bundle extras = getIntent().getExtras();
			Log.d("GAMELOBBY", "Get the user email and token");
			if(extras == null) {
				Log.d("GAMELOBBY", "Somehow I got into the game lobby without credentials..");
				finish();
			}
			
			m_UserEmail = extras.getString(NetworkUtils.k_JsonKeyEmail);
			m_UserToken = extras.getString(NetworkUtils.k_JsonKeyToken);
			Log.d("GAMELOBBY", "We got:");
			Log.d("GAMELOBBY", "Email: " + m_UserEmail);
			Log.d("GAMELOBBY", "Token: " + m_UserToken);
			
			JSONObject request = NetworkUtils.getUserGameList(m_UserEmail);
			Log.d("GAMELOBBY", "The request: " + m_UserToken);
			try {
				JSONObject gameList = new GetGameListTask().execute(request).get();
				Log.d("GAMELOBBY", "The response: " + gameList);
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

	private void populateLobbyWithGames(JSONObject gameList) {
		// TODO Auto-generated method stub
		
	}

	private void launchWordSelection() {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getApplicationContext(), WordSelectionActivity.class);
		startActivity(intent);
		
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