package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.RandomLetterGenerator;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GuessWordActivity extends Activity {

	private static final int NUMBER_OF_LETTERS = 14;
	private static final String TAG = "GuessWordActivity";
	private String m_word;
	private int m_difficulty;
	private int m_nextFreeGuessLocation;
	private RandomLetterGenerator m_letterGenerator;
	private Character[] m_randomCharacters;
	private GridLayout m_randomLettersgridLayout;
	private ImageButton m_checkGuessButton;
	private ImageButton m_playRecordingButton;
	private LinearLayout m_layout1;
	private Character[] m_guess;
	private MediaPlayer m_MediaPlayer;
	private Uri m_recording_file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess_word);
	}

	@Override
	protected void onResume() {
		super.onResume();


		getRecording();		
		//		initSoundPlayer();

		m_letterGenerator = new RandomLetterGenerator(m_word);
		m_guess = new Character[m_word.length()];

		for (int i = 0; i < m_guess.length; i++) {
			m_guess[i] = ' ';
		}
		m_nextFreeGuessLocation = 0;

		generateRandomLetters();		
		initButtons();		

		populateLetters();

	}



	private void initSoundPlayer() {
		this.m_MediaPlayer = MediaPlayer.create(this, this.m_recording_file);		
	}

	private void getRecording() {
		Log.i(TAG, "Getting recording");
		Bundle extras = getIntent().getExtras();
		this.m_difficulty = extras.getInt("difficulty");
		this.m_word = extras.getString("word");
		Log.i(TAG, "Done getting recording");

		//		this.m_recording_file = Uri.fromFile(new File(getFilesDir(), extras.getString("filename")));
	}

	private void populateLetters() {
		int col = 0;
		int row = 0;
		for (Character character : this.m_randomCharacters) {
			Button button = new Button(this);			
			Log.d(TAG, "Character is: " + character);
			button.setText("" + character.toUpperCase(character));

			// set the button to be in row and column as needed with GridLayout.spec
			GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
			this.m_randomLettersgridLayout.addView(button, params);

			// Increment column - move to second row if needed
			col++;
			if (col > this.m_randomCharacters.length / 2)
			{
				row++;
				col = 0;
			}

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button clickedButton = (Button) v;


					clickedButton.setTag(clickedButton.getLayoutParams());

					addLetterToGuess(clickedButton);

				}
			});
		}
		m_randomLettersgridLayout.setMinimumWidth(m_randomLettersgridLayout.getWidth());
	}

	protected void removeLetterFromGuess(Button clickedButton) {
		Object[] Tags = (Object[])clickedButton.getTag();
		int letterIndex = (Integer)Tags[0];
		this.m_guess[letterIndex] = ' ';
		this.m_nextFreeGuessLocation = Math.min(m_nextFreeGuessLocation, letterIndex);	
		Button randomLetterButton = (Button) Tags[1];
		randomLetterButton.setVisibility(View.VISIBLE);
		randomLetterButton.setEnabled(true);
	}

	protected void addLetterToGuess(Button clickedButton) {

		if (m_nextFreeGuessLocation < this.m_guess.length)
		{
			this.m_guess[m_nextFreeGuessLocation] = clickedButton.getText().charAt(0);
			Log.i(TAG, "New guess is: " + this.m_guess.toString().trim());
			Button guessButton  = (Button)this.m_layout1.getChildAt(this.m_nextFreeGuessLocation);
			guessButton.setText(clickedButton.getText());
			Object[] Tags = (Object[])guessButton.getTag();
			Tags[1] = clickedButton;
			Log.i(TAG, "Guess Button tag is " + ((Object[])guessButton.getTag())[1]);
			for (;m_nextFreeGuessLocation < this.m_guess.length; m_nextFreeGuessLocation++)
			{

				if (this.m_guess[m_nextFreeGuessLocation] == ' ')
				{
					break;
				}
			}							
			clickedButton.setVisibility(View.INVISIBLE);
			clickedButton.setEnabled(false);
		}
	}

	private void initButtons() {
		this.m_randomLettersgridLayout = (GridLayout) findViewById(R.id.letters_layout);
		this.m_playRecordingButton = (ImageButton) findViewById(R.id.play_sound_guess_button);
		this.m_checkGuessButton = (ImageButton) findViewById(R.id.check_guess_imageButton);
		this.m_layout1 = (LinearLayout) findViewById(R.id.linear1);



		// set onclick listener for checkGuessButton
		this.m_checkGuessButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (m_letterGenerator.checkGuess(getGuess()))
				{
					startGuessSuccessActivity();
					finish();
				}
				else
				{
					showMessage();
				}
			}
		});

		// set onclick listener for guess letter buttons
		for(int i = 0; i < this.m_word.length(); i++)
		{
			Button button = new Button(this);
			button.setBackgroundResource(R.drawable.border_black);
			Object[] tags = new Object[2];
			tags[0] = i;
			button.setTag(tags);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button clickedButton = (Button) v;
					clickedButton.setText("");
					removeLetterFromGuess((Button) v);					
				}
			});

			this.m_layout1.addView(button);
		}
	}



	protected String getGuess() {
		StringBuilder result = new StringBuilder();
		for (Character character : this.m_guess) {
			result.append(character);
		}

		return result.toString();
	}

	protected void showMessage() {
		Log.i(TAG, "Showing message");
		Toast.makeText(this, "Wrong guess", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Done showing message");

	}

	protected void startGuessSuccessActivity() {
		// TODO Auto-generated method stub

	}

	private void generateRandomLetters() 
	{
		this.m_randomCharacters = this.m_letterGenerator.GetRandomLetters(NUMBER_OF_LETTERS);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_guess_word, menu);
		return true;
	}

}
