package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

	Button btnStartPlaying;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		btnStartPlaying = (Button)findViewById(R.id.login_button_start);
		
		btnStartPlaying.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(validateUserCredentials()) {
					// Move to main menu of the game
					Intent main = new Intent(getApplicationContext(), MainActivity.class);

					// Clear non relevant activities from the activity class 
					main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(main);
					
					finish();
				}
				else {
					// TODO: Implement error / register
				}
				
			}
		});
	}

	protected boolean validateUserCredentials() {
		// TODO: implement client-server validation
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
