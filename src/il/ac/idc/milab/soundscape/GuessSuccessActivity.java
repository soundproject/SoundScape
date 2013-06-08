package il.ac.idc.milab.soundscape;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GuessSuccessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess_success);
	}
	
	private Button m_OKButton;
	private TextView m_NumberOfStarsTextView;
	private TextView m_NumberOfBombsTextView;
	
	@Override
	protected void onResume() {
		super.onResume();
		initButtons();
		
	}

	private void initButtons() {
		
		this.m_OKButton = (Button) findViewById(R.id.guess_success_back_to_menu_button);
		this.m_NumberOfBombsTextView = (TextView) findViewById(R.id.guess_success_bomb_textView);
		this.m_NumberOfStarsTextView = (TextView) findViewById(R.id.guess_success_stars_textView);

		Bundle extras = getIntent().getExtras();		
		String difficulty = extras.getString("difficulty");
		
		this.m_NumberOfBombsTextView.setText(String.format("%d", 2));		
		this.m_NumberOfStarsTextView.setText(String.format("%d", difficulty));		
		
		this.m_OKButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_guess_success, menu);
		return true;
	}

}
