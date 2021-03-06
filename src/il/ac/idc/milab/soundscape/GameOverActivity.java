package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
	}
	
	@Override
	protected void onResume() {
	
		super.onResume();
		
		initButtons();
		
	}
	
	private Button m_OKButton;
	private TextView m_WordTextView;

	private void initButtons() {
		
		this.m_OKButton = (Button) findViewById(R.id.game_over_ok_button);
		this.m_WordTextView = (TextView) findViewById(R.id.game_over_answer_textView);
		
		Bundle extras = getIntent().getExtras();		
		String word = extras.getString("word");		
		this.m_WordTextView.setText(word);
		
		this.m_OKButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGameLobbyActivity();
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game_over, menu);
		return true;
	}
	
	protected void startGameLobbyActivity() {
		// TODO Tal how do I start GameLobby 
		// TODO and let server know about gameover?
		
	}

}
