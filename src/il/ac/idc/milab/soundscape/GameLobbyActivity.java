package il.ac.idc.milab.soundscape;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GameLobbyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_lobby);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_lobby, menu);
		return true;
	}
}