package il.ac.idc.milab.soundscape;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GuessWordActivity extends Activity {
	
	private static final String TAG = "GUESS_WORD";
	private static final int MAX_NUMBER_OF_LETTERS = 16;
	private static final int MAX_ALLOWED_STRIKES = 3;
	
	private SoundPlayer m_SoundPlayer = null;
	private boolean m_IsPlaying = false;
	private ProgressBar m_ProgressBar = null;
	private CountDownTimer m_Timer = null;
	private int m_CurrentStrike = 1;
	private int m_WordLength = 0;
	private int m_CurrentGuessLength = 0;
	
	private JSONObject m_GameDetails;
	private String m_SoundFileAbsPath;

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
		String soundFileName = "temp" + gameId;
		m_SoundFileAbsPath = getFilesDir() + "/" + soundFileName;
		Log.i(TAG, "File path: " + m_SoundFileAbsPath);
		try {
			byte[] soundFile = NetworkUtils.serverRequests.getGameFile(gameId);
			FileOutputStream fileOutputStream = openFileOutput(soundFileName, Context.MODE_PRIVATE);
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
	
    @Override
    public void onPause() {
        super.onPause();
        m_SoundPlayer.release();
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
				ImageButton button = (ImageButton)v;
				if(m_IsPlaying) {
					button.setImageResource(R.drawable.btn_stop);
					m_SoundPlayer.stopPlaying();
				}
				else {
					button.setImageResource(R.drawable.btn_play);
					m_SoundPlayer.startPlaying(m_SoundFileAbsPath);
				}
				
				m_IsPlaying = !m_IsPlaying;
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
		generateGuessWordLayout();
		generateGuessLettersLayout();
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

	/**
	 * This method generates the layout of the word we want to guess
	 */
	private void generateGuessWordLayout() {
		String phrase = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		String[] words = phrase.split(" ");
		Log.d(TAG, "Length: " + words.length);

		// For every 
		for(int i = 0; i < words.length; i++) {
			String layoutName = "guessword_footer_word_letters_row" + (i + 1);
			Log.i(TAG, "Layout name: " + layoutName);
			int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			Log.i(TAG, "Layout id: " + layoutId);
			LinearLayout row = (LinearLayout)findViewById(layoutId);

			row.setOrientation(LinearLayout.HORIZONTAL);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					57, // Width
					LayoutParams.WRAP_CONTENT // Height
					);
			params.rightMargin = 5;

			Log.i("LETTERS", "Creating the word letters");
			String word = words[i];
			Log.d(TAG, "Word is: " + word);
			for (int j = 0; j < word.length(); j++) {
				TextView letter = new TextView(this);
				letter.setId(100 + i*100 + j);
				letter.setGravity(Gravity.CENTER);
				letter.setTextSize(20);
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
		}
	}

	private void generateGuessLettersLayout() {
		String phrase = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		String[] words = phrase.split(" ");
		String word = words.length > 1 ? words[0] + words[1] : words[0];
		m_WordLength = word.length();
		Log.i(TAG, word);
		Log.i(TAG, "Length: " + word.length());
		String[] letters = generateRandomLetters(word);
		Button[] letterBoxes = getGuessLetters(letters); // TODO: This can
																// be merged
																// with
																// populateWordLetters()

		// Row 1
		LinearLayout guessLettersRow1Layout = (LinearLayout)findViewById(R.id.guessword_footer_guess_letters_row1);
		
		// Row 2
		LinearLayout guessLettersRow2Layout = (LinearLayout)findViewById(R.id.guessword_footer_guess_letters_row2);


		Log.v("LETTERS", "Populating the guess letters");
		// Populate the guess letters
		for (int i = 0; i < letterBoxes.length; i++) {
			Log.d(TAG, "Adding letter: " + letterBoxes[i].getText());
			if(i < MAX_NUMBER_OF_LETTERS / 2) {
				guessLettersRow1Layout.addView(letterBoxes[i]);	
			}
			else {
				guessLettersRow2Layout.addView(letterBoxes[i]);	
			}
		}
	}

	private Button[] getGuessLetters(String[] letters) {

		Button[] lettersBoxes = new Button[MAX_NUMBER_OF_LETTERS];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				57, // Width
				LayoutParams.WRAP_CONTENT // Height
		);

		params.rightMargin = 3;
		Log.v("LETTERS", "Creating the guess letters");

		for (int i = 0; i < letters.length; i++) {
			Button letter = new Button(this);

			letter.setText(letters[i]);
			letter.setTextSize(26);
			letter.setGravity(Gravity.CENTER);
			letter.setBackgroundResource(R.drawable.border_black_letter);
			letter.setLayoutParams(params);
			letter.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setVisibility(View.INVISIBLE);
					populateAnswer(v);
				}
			});

			lettersBoxes[i] = letter;
		}

		return lettersBoxes;
	}

	protected void populateAnswer(View v) {
		m_CurrentGuessLength++;
		TextView letter = (TextView)v;
		String phrase = m_GameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		String[] words = phrase.split(" ");
		Log.d(TAG, "Length: " + words.length);

		// For every word in the guess word, look for the empty space to populate
		for(int i = 0; i < words.length; i++) {
			boolean success = false;
			String word = words[i];
			
			// Try and populate a letter
			for (int j = 0; j < word.length(); j++) {
				TextView textView = (TextView) findViewById(100 + i*100 + j);
	
				// If found an empty space - populate it with the pressed letter
				if (textView != null && textView.getText().length() == 0) {
					String text = (String)letter.getText();
					textView.setText(text);
					textView.setTag(letter);
					textView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							m_CurrentGuessLength--;
							TextView view = (TextView)v;
							view.setText("");
							Log.d(TAG, "Tag is: " + view.getTag());
							TextView letter = (TextView)view.getTag();
							letter.setVisibility(View.VISIBLE);
						}
					});
					success = true;
					break;
				}
			}
			// If successfully populated a letter, break
			if(success && m_CurrentGuessLength == m_WordLength) {
				
				break;
			}
		}
	}

	/**
	 * This method generates the random letters with the word letters mixed into it
	 * @param word the given word to mix with the random letters
	 * @return an array of random letters (capitalized) with the word mixed in it
	 */
	private String[] generateRandomLetters(String word) {
		
		// generate MAX_NUMBER_OF_LETTERS random letters
		String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z" };

		String[] randomLetters = new String[MAX_NUMBER_OF_LETTERS];
		Random rand = new Random();
		int randomIndex;

		// populate our word letters into the letters
		for (int i = 0; i < word.length(); i++) {
			do {
				Log.i(TAG, "WORD!");
				randomIndex = rand.nextInt(randomLetters.length);
			}
			while(randomLetters[randomIndex] != null);
			
			char wordLetter = word.charAt(i);
			randomLetters[randomIndex] = String.valueOf(wordLetter).toUpperCase(Locale.ENGLISH);
			Log.i(TAG, "Word letter: " + randomLetters[randomIndex]);
			Log.i(TAG, "Word letter index: " + randomIndex);
		}
		
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < randomLetters.length; i++) {
			build.append(randomLetters[i]);
		}
		Log.d(TAG, build.toString());

		// Populate random letters into the array
		for (int i = 0; i < randomLetters.length; i++) {
			if(randomLetters[i] == null) {
				randomIndex = rand.nextInt(letters.length);
				randomLetters[i] = letters[randomIndex].toUpperCase(Locale.ENGLISH);
			}
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
