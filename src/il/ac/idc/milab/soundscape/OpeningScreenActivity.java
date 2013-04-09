package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class OpeningScreenActivity extends Activity {

    // The location of our server
    private static String k_ServerURL = "http://soundscape.hostzi.com/index.php";
    
    // The Token name we use to check if user already logged in
    private static String k_Token = "token";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opening_screen);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(isNetworkAvailable()) {
			Toast.makeText(this, "Yey! internet! :)", Toast.LENGTH_LONG).show();
		}
		
		// TODO: Add user session/validation
		//if(isUserLoggedIn()) {}
		
		startGameLobbyActivity();
	}
	
	private void startGameLobbyActivity() {
		Intent intent = new Intent(this.getApplicationContext(), GameLobbyActivity.class);
		startActivity(intent);
	}

/*
	private boolean isUserLogged() {
		class MyTask extends AsyncTask<String, Void, Void> {
			protected Void doInBackground(String... urls) {
				try {
					ServerMiddleMan.connect(urls[0]);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}
		
		new MyTask().execute("http://soundscape.hostzi.com/index.php");
		
		return false;
	}
*/
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opening_screen, menu);
		return true;
	}

}
