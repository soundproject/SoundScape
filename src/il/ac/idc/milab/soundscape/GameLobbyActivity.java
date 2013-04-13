package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class GameLobbyActivity extends Activity {

	private String m_UserEmail;
	private String m_UserToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("STARTUP", "Starting Game Activity");
		setContentView(R.layout.activity_game_lobby);
		
		ConnectivityManager connectivityManager 
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(NetworkUtils.isNetworkAvailable(connectivityManager) == false) {
			String message = "This activity requires an Internet connection.";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
		else {
			// Get the User name and token
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				m_UserEmail = extras.getString(NetworkUtils.k_JsonKeyEmail);
				m_UserToken = extras.getString(NetworkUtils.k_JsonKeyToken);
			}
			
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		launchWordSelection();
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
}