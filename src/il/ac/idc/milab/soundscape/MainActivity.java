package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button m_ButtonStartGame;
	public static boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check if user is logged in
		if(userLoggedIn() == false) {
			flag = true;
			Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			
			login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			
			finish();
		}
		
		setContentView(R.layout.activity_main);
		
		Button button = (Button)findViewById(R.id.main_button_new_game_header);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGame();
			}
		});
		
		button = (Button)findViewById(R.id.main_button_new_game_footer);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGame();
			}
		});
	}

	private boolean userLoggedIn() {
		return flag;
	}
	
	public void startGame() {
		Intent game = new Intent(getApplicationContext(), GameActivity.class);
		
		game.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(game);
		
		finish();
	}
}