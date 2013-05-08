package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.FlowLayout;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.SoundPlayer;

import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class GuessWordActivity extends Activity {
	
	private static final String TAG = "GUESS_WORD";
	private static final int MAX_NUMBER_OF_LETTERS = 14;
	private static final int MAX_ALLOWED_STRIKES = 3;
	
	private SoundPlayer m_SoundPlayer = null;
	private ProgressBar m_ProgressBar = null;
	private CountDownTimer m_Timer = null;
	private int m_CurrentStrike = 1;
	
	private JSONObject m_GameDetails;
	private String m_SoundFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting guess words activity");
		setContentView(R.layout.activity_guess_word);
		
		// Init sound player
		m_SoundPlayer = new SoundPlayer();
		
		// Extract game details
		String game = getIntent().getStringExtra(ServerRequests.RESPONSE_FIELD_GAME);
		Log.d(TAG, "Game: " + game);
		try {
			m_GameDetails = new JSONObject(game);
		} catch (JSONException e) {
			Log.e(TAG, "Not a valid game details string!");
			e.printStackTrace();
		}
		
		// Request the sound file from the server
		String gameId = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_ID);
		m_SoundFileName = getFilesDir() + "/temp" + gameId;
		Log.i(TAG, "File path: " + m_SoundFileName);
		try {
			byte[] soundFile = NetworkUtils.serverRequests.getGameFile(gameId);
			FileOutputStream fileOutputStream = openFileOutput(m_SoundFileName, Context.MODE_PRIVATE);
			fileOutputStream.write(soundFile);
			fileOutputStream.close();
		} 
		catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Init UI with game details
		initUIWithDetails();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void initUIWithDetails() {
		// Init title with player names
		TextView userView = (TextView)findViewById(R.id.guessword_leftPlayerName);
		String user = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_USER, "Player1").split("@")[0];
		userView.setText(user);
		
		TextView opponentView = (TextView)findViewById(R.id.guessword_rightPlayerName);
		String opponent = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT, "Player2").split("@")[0];
		opponentView.setText(opponent);
		
		// Init play button
		ImageButton playButton = (ImageButton)findViewById(R.id.guessword_play_sound);
		playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_SoundPlayer.startPlaying(m_SoundFileName);
			}
		});
		
		// Init bomb button
		ImageView bombButton = (ImageView)findViewById(R.id.header_bomb_icon);
		bombButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(m_CurrentStrike <= MAX_ALLOWED_STRIKES) {
					addStrike();
					updateBombCount();
				}
			}

			private void updateBombCount() {
				TextView bombCount = (TextView)findViewById(R.id.header_text_view_bomb_number);
				int currentCount = Integer.parseInt((String) bombCount.getText());
				if(currentCount > 0) {
					currentCount--;
					bombCount.setText(String.valueOf(currentCount));
				}
			}
		});
		
		//TODO: finish this
		//generateGuessWordLayout();
		generateLettersLayout();
	}
	
	protected void addStrike() {
		// Build the name of the resource that represent the needed strike view
		String strikeName = "guessword_text_view_strike" + m_CurrentStrike;
		Log.i(TAG, "Text view name: " + strikeName);
		
		// Get the resource ID of the relevant strike text view
		int resID = getResources().getIdentifier(strikeName, "id", getPackageName());

		TextView strike = (TextView)findViewById(resID);
		strike.setBackgroundResource(R.drawable.bg_cross);
		
		m_CurrentStrike++;
	}

	private void generateGuessWordLayout() {
		LinearLayout wordLayout = (LinearLayout)findViewById(R.id.guessword_footer_word_container);
		String phrase = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		String[] words = phrase.split(" ");
		Log.d(TAG, "Length: " + words.length);

		// For every 
		for(int i = 0; i < words.length; i++) {
			//String layoutName = "guessword_footer_letters_row" + i + 1;
			//int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			//LinearLayout row = (LinearLayout)findViewById(layoutId);
			LinearLayout row = new LinearLayout(getApplicationContext());
			row.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					50, // Width
					50 // Height
					);
			params.rightMargin = 5;

			Log.i("LETTERS", "Creating the word letters");
			String word = words[i];
			Log.d(TAG, "Word is: " + word);
			for (int j = 0; j < word.length(); j++) {
				TextView letter = new TextView(this);
				letter.setId(100 + j);
				letter.setGravity(Gravity.CENTER);
				letter.setBackgroundResource(R.drawable.border_black_letter);
				letter.setLayoutParams(params);
				letter.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((TextView)v).setText("");
					}
				});
				row.addView(letter);
			}
			
			wordLayout.addView(row);
		}
	}

	private void generateLettersLayout() {

		String[] letters = generateRandomLetters(m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD));
		TextView[] letterBoxes = getGuessLetters(letters); // TODO: This can
																// be merged
																// with
																// populateWordLetters()

		FlowLayout guessLettersContainerLayout = (FlowLayout)findViewById(R.id.guessword_footer_letters_container);


		Log.v("LETTERS", "Populating the guess letters");
		// Populate the guess letters
		for (int i = 0; i < letterBoxes.length; i++) {
			guessLettersContainerLayout.addView(letterBoxes[i]);
		}
	}

	private TextView[] getGuessLetters(String[] letters) {

		TextView[] lettersBoxes = new TextView[MAX_NUMBER_OF_LETTERS];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, // Width
				LayoutParams.WRAP_CONTENT // Height
		);

		params.rightMargin = 3;
		Log.v("LETTERS", "Creating the guess letters");

		for (int i = 0; i < letters.length; i++) {
			TextView letter = new TextView(this);

			letter.setText(letters[i]);
			letter.setTextSize(30);
			letter.setGravity(Gravity.CENTER);
			letter.setBackgroundResource(R.drawable.border_black_letter);
			letter.setLayoutParams(params);
			letter.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					FlowLayout parent = (FlowLayout)(v.getParent());
					parent.removeView(v);
					FlowLayout newParent;
					if(parent.getId() == R.id.guessword_footer_word_container) {
						newParent = (FlowLayout)findViewById(R.id.guessword_footer_letters_container);
					}
					else {
						newParent = (FlowLayout)findViewById(R.id.guessword_footer_word_container);	
					}
					 
					newParent.addView(v);
				}
			});

			lettersBoxes[i] = letter;
		}

		return lettersBoxes;

	}

	protected void populateAnswer(View v) {
		
		
		/* String word = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		for (int i = 0; i < word.length(); i++) {
			TextView textView = (TextView) findViewById(100 + i);

			if (textView != null && textView.getText().length() == 0) {
				String text = (String) ((TextView) v).getText();
				textView.setText(text);
				break;
			}
		}
		*/
	}

	private String[] generateRandomLetters(String optString) {
		String word = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		// generate MAX_NUMBER_OF_LETTERS random letters
		String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z" };

		String[] randomLetters = new String[MAX_NUMBER_OF_LETTERS];
		Random rand = new Random();

		// Insert our word letters into the array randomly
		for (int i = 0; i < word.length(); i++) {
			int randomIndex = rand.nextInt(randomLetters.length);
			while (randomLetters[randomIndex] != null) {
				randomIndex = rand.nextInt(randomLetters.length);
			}

			randomLetters[randomIndex] = String.valueOf(word.charAt(i))
					.toUpperCase(Locale.ENGLISH);
		}

		// populate the other letters at random
		for (int i = 0; i < randomLetters.length; i++) {
			int randomIndex = rand.nextInt(letters.length);
			if (randomLetters[i] != null) {
				continue;
			}

			randomLetters[i] = letters[randomIndex].toUpperCase(Locale.ENGLISH);
		}

		return randomLetters;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_guess_word, menu);
		return true;
	}

}
