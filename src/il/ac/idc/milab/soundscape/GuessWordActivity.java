package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.Game;
import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.ServerRequests;
import il.ac.idc.milab.soundscape.library.SoundPlayer;
import il.ac.idc.milab.soundscape.library.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GuessWordActivity extends Activity {
	
	private static final String TAG = "GUESS_WORD";
	private static final int MAX_NUMBER_OF_GUESS_LETTERS = 16;
	private static final int MAX_ALLOWED_STRIKES = 3;
	private static final int MAX_WORD_LETTERS_IN_ROW = 10;
	private static final int MAX_GUESS_LETTERS_IN_ROW = 8;
	
	private SoundPlayer m_SoundPlayer = null;
	private ProgressBar m_ProgressBar = null;
	private CountDownTimer m_Timer = null;
	
	private boolean m_IsPlaying = true;
	private boolean m_IsInProgress = false;
	private ImageButton m_ButtonPlay = null;

	Button[] m_GuessLettersButtons = new Button[MAX_NUMBER_OF_GUESS_LETTERS];
	private int m_CurrentStrike = 1;
	private int m_WordLength = 0;
	private String m_WordToGuess;
	private StringBuilder m_CurrentGuess;

	private int m_NumberOfVisibleLetters = MAX_NUMBER_OF_GUESS_LETTERS;
	private int m_NumOfLettersToRemove = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guess_word);
		
		// Init sound player
		m_SoundPlayer = new SoundPlayer();
		
		// Init progress bar
		m_ProgressBar = (ProgressBar)findViewById(R.id.guessword_progressBar);
		
		m_CurrentGuess = new StringBuilder();
		
		// Request the sound file from the server
		String soundFileName = "temp" + Game.getId();
		JSONObject request = buildRequestForGameFile();
		JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, GuessWordActivity.this);

		if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
			String encodedFile = response.optString(ServerRequests.RESPONSE_FIELD_FILE);
			byte[] soundFile = Base64.decode(encodedFile, Base64.DEFAULT);
			
			try {
				FileOutputStream fileOutputStream = openFileOutput(soundFileName, Context.MODE_PRIVATE);
				fileOutputStream.write(soundFile);
				fileOutputStream.close();
				
				// Init our sound player
				m_SoundPlayer.initPlayer(getFilesDir() + "/" + soundFileName);
				
				m_Timer = new CountDownTimer(m_SoundPlayer.getFileDuration(), 50) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						if(m_IsInProgress) {
							m_ProgressBar.incrementProgressBy(50);
						}
					}
					
					@Override
					public void onFinish() {
						onPlay(false);
					}
				};
			}
			catch(IOException e) {
				finish();
			}
			
			// Init UI with game details
			initUIWithDetails();
		}
		else {
			//TODO: exit gracefully with a message to user
			finish();
		}
	}
	
	/**
	 * This method is called when the user presses the "play/stop" button 
	 * @param start
	 */
	private void onPlay(boolean start) {
        if (start && m_IsInProgress == false) {
        	m_IsInProgress = true;
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_stop));
        	
        	// Init the progress bar values
        	m_ProgressBar.setProgress(0);
        	m_ProgressBar.setMax(m_SoundPlayer.getFileDuration());
        	m_Timer.start();
            m_SoundPlayer.startPlaying();
        	m_SoundPlayer.getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					onPlay(false);
				}
			});
        } else {
        	m_IsInProgress = false;
        	m_SoundPlayer.stopPlaying();
        	m_ButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_play));
        	m_ProgressBar.setProgress(0);
        	m_Timer.cancel();
        }
    }

	/**
	 * This method builds the JSON request for the sound file to guess 
	 * @return a JSON object representing the request to server
	 */
	private JSONObject buildRequestForGameFile() {
		JSONObject request = new JSONObject();
		try 
		{
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_GET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_FILE);
			request.put(ServerRequests.REQUEST_FIELD_GAMEID, Game.getId());
		} 
		catch (JSONException e) {
			e.printStackTrace();
			request = null;
		}
		
		return request;
	}
	
    @Override
    public void onPause() {
        super.onPause();
        m_SoundPlayer.release();
    }
	
    /**
     * This method initialize the UI with the all the details
     */
	private void initUIWithDetails() {
		// Init title with player names
		TextView userView = (TextView)findViewById(R.id.guessword_leftPlayerName);
		String user = Game.getUser().split("@")[0];
		userView.setText(user);
		
		TextView opponentView = (TextView)findViewById(R.id.guessword_rightPlayerName);
		String opponent = Game.getOpponent().split("@")[0];
		opponentView.setText(opponent);
		
		// Init play button
		m_ButtonPlay = (ImageButton)findViewById(R.id.guessword_play_sound);
		m_ButtonPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPlay(m_IsPlaying);
			}
		});
		
		// Init bomb button
		ImageButton bombButton = (ImageButton)findViewById(R.id.guessword_footer_bomb);
		bombButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(m_CurrentStrike <= MAX_ALLOWED_STRIKES) {
					addStrike();
					updateBombCount();
					removeLetters();
				}
			}

			private void updateBombCount() {
				TextView bombCount = (TextView)findViewById(R.id.header_text_view_bomb);
				int currentCount = Integer.parseInt((String) bombCount.getText());
				if(currentCount > 0) {
					currentCount--;
					bombCount.setText(String.valueOf(currentCount));
				}
			}
		});
		
		//TODO: finish this
		initWordLayout();
		initGuessLettersLayout();
	}
	
	/**
	 * This method is used when the user pressed the bomb to remove random 
	 * letters
	 */
	protected void removeLetters() {
		Random random = new Random();
		int numOfLettersToRemove = m_NumOfLettersToRemove;
		
		String wordInUpperCase = m_WordToGuess.toUpperCase(Locale.ENGLISH);
		
		while(numOfLettersToRemove > 0 && m_NumberOfVisibleLetters > m_WordLength) {
			//TODO: If have time, need to refactor this UGLY way of doing this
			// Init the indexes of the guess buttons that are available
			int[] availableIndexesToRemove = new int[m_NumberOfVisibleLetters];
			int index = 0;
			for(int i = 0; i < m_GuessLettersButtons.length; i++) {
				if(m_GuessLettersButtons[i].isShown()) {
					availableIndexesToRemove[index] = i;
					index++;
				}
			}
			int randomIndex = random.nextInt(availableIndexesToRemove.length);
			
			// according to the index, decide from which row to remove a letter
			index = availableIndexesToRemove[randomIndex];
			
			// Check if letter is part of the main word so we won't remove it
			boolean letterInWord = wordInUpperCase.contains(m_GuessLettersButtons[index].getText().toString());
			if(letterInWord == false) {
				numOfLettersToRemove--;
				m_NumberOfVisibleLetters--;
				m_GuessLettersButtons[index].setVisibility(View.INVISIBLE);
			}
		}
		
		m_NumOfLettersToRemove = m_NumOfLettersToRemove / 2;
	}

	protected void addStrike() {
		// Build the name of the resource that represent the needed strike view
		String strikeName = "guessword_text_view_strike" + m_CurrentStrike;
		
		// Get the resource ID of the relevant strike text view
		int resID = getResources().getIdentifier(strikeName, "id", getPackageName());

		TextView strike = (TextView)findViewById(resID);
		strike.setBackgroundResource(R.drawable.bg_cross);
		
		m_CurrentStrike++;
	}

	/**
	 * This method generates the layout of the word we want to guess
	 */
	private void initWordLayout() {
		String phrase = Game.getWord();
		String[] words = phrase.split(" ");

		for(int i = 0; i < words.length; i++) {
			String layoutName = "guessword_footer_word_letters_row" + (i + 1);
			int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			LinearLayout row = (LinearLayout)findViewById(layoutId);
			row.setVisibility(View.VISIBLE);

			String word = words[i];
			
			// Remove the excessive views
			while(row.getChildCount() > word.length()) {
				row.removeViewAt(0);
			}
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			row.setLayoutParams(params);
		}
	}

	/**
	 * This method initialize the guess letters layout
	 */
	private void initGuessLettersLayout() {
		String phrase = Game.getWord();
		String[] words = phrase.split(" ");
		String word = words.length > 1 ? words[0] + words[1] : words[0];
		m_WordLength = word.length();
		m_WordToGuess = word;
		String[] letters = generateRandomLetters(word);
		initGuessLetters(letters);
	}

	/**
	 * This method initialize the guess buttons with the random letters and 
	 * sets their action on click
	 * @param i_RandomLetters the random letters to populate the buttons text with
	 */
	private void initGuessLetters(String[] i_RandomLetters) {
		// Get Row 1
		LinearLayout guessLettersRow1Layout = (LinearLayout)findViewById(R.id.guessword_footer_guess_letters_row1);
		
		// Get Row 2
		LinearLayout guessLettersRow2Layout = (LinearLayout)findViewById(R.id.guessword_footer_guess_letters_row2);
		
		Button letter;
		for(int i = 0; i < i_RandomLetters.length; i++) {
			if(i < MAX_GUESS_LETTERS_IN_ROW) {
				letter = (Button)guessLettersRow1Layout.getChildAt(i);
			}
			else {
				letter = (Button)guessLettersRow2Layout.getChildAt(i % MAX_GUESS_LETTERS_IN_ROW);
			}
			letter.setText(i_RandomLetters[i]);
			letter.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					m_NumberOfVisibleLetters--;
					v.setVisibility(View.INVISIBLE);
					populateAnswer(v);
				}
			});
			
			m_GuessLettersButtons[i] = letter;
		}
	}

	/**
	 * This method populate the first free space in the word letter layout and
	 * checks the answer in case it populated all available letters
	 * @param v the button that was clicked
	 */
	protected void populateAnswer(View v) {
		Button guessLetter = (Button)v;
		m_CurrentGuess.append(guessLetter.getText());

		String phrase = Game.getWord();
		String[] words = phrase.split(" ");
		boolean successPopulatingLetter = false;

		// For every word in the guess word, look for the empty space to populate
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			
			String layoutName = "guessword_footer_word_letters_row" + (i + 1);
			int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			LinearLayout row = (LinearLayout)findViewById(layoutId);
			
			// Try and populate a letter
			for (int j = 0; j < word.length(); j++) {
				Button wordLetter = (Button)row.getChildAt(j);
				
				// If found an empty space - populate it with the pressed letter
				if (wordLetter.getText().length() == 0) {
					String text = (String)guessLetter.getText();
					wordLetter.setText(text);
					wordLetter.setTag(guessLetter);
					wordLetter.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							m_CurrentGuess.deleteCharAt(m_CurrentGuess.length() - 1);
							Button view = (Button)v;
							view.setText("");
							view.setOnClickListener(null);
							Button letter = (Button)view.getTag();
							m_NumberOfVisibleLetters++;
							letter.setVisibility(View.VISIBLE);
						}
					});
					successPopulatingLetter = true;
					break;
				}
			}
			// If successfully populated a letter, break
			if(successPopulatingLetter) {
				break;
			}
		}
		
		// If populated all letters, check guess
		if(m_CurrentGuess.length() == m_WordLength) {
			if(isCorrectGuess()) {
				if(updateServerOfSuccess(true)) {
					startGuessSuccessfulActivity();;
				}
				else {
					printErrorToUser();
				}
			}
			else if(m_CurrentStrike < MAX_ALLOWED_STRIKES) {
				addStrike();
				clearLetters();
			}
			else {
				if(updateServerOfSuccess(false)) {
				}
				else {
					printErrorToUser();
				}
				startGameOverActivity();
			}
		}
	}

	private void printErrorToUser() {
		new AlertDialog.Builder(GuessWordActivity.this)
	    .setMessage("Couldn't connect to server, please try again.")
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {

	        }
	    }).show();
		
		//TODO: Change this to exit more gracefully
		finish();
	}

	private boolean updateServerOfSuccess(boolean success) {
		boolean isSuccessful = false;
		JSONObject request = buildRequestToUpdateGuess(success);
		JSONObject response = NetworkUtils.serverRequests.sendRequestToServer(request, GuessWordActivity.this);
		if(response != null && response.optInt(ServerRequests.RESPONSE_FIELD_SUCCESS) == ServerRequests.RESPONSE_VALUE_SUCCESS) {
			isSuccessful = true;
		}
		else {
		}
		
		return isSuccessful;
	}

	private JSONObject buildRequestToUpdateGuess(boolean success) {
		JSONObject request = new JSONObject();
		try 
		{
			request.put(ServerRequests.REQUEST_ACTION, ServerRequests.REQUEST_ACTION_SET);
			request.put(ServerRequests.REQUEST_SUBJECT, ServerRequests.REQUEST_SUBJECT_GAME);
			request.put(ServerRequests.REQUEST_FIELD_GAMEID, Game.getId());
			request.put(ServerRequests.REQUEST_FIELD_EMAIL, User.getEmailAddress());
			request.put(ServerRequests.REQUEST_FIELD_GUESS, success);
		} 
		catch (JSONException e) {
			e.printStackTrace();
			request = null;
		}

		return request;
	}

	private void clearLetters() {
		String phrase = Game.getWord();
		String[] words = phrase.split(" ");
		
		// For every word in the guess word, remove all letters
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			
			String layoutName = "guessword_footer_word_letters_row" + (i + 1);
			int layoutId = getResources().getIdentifier(layoutName, "id", getPackageName());
			LinearLayout row = (LinearLayout)findViewById(layoutId);
			
			// remove the letter
			for (int j = 0; j < word.length(); j++) {
				m_CurrentGuess.deleteCharAt(m_CurrentGuess.length() - 1);
				Button button = (Button)row.getChildAt(j);
				button.setText("");
				button.setOnClickListener(null);
				Button letter = (Button)button.getTag();
				letter.setVisibility(View.VISIBLE);
				m_NumberOfVisibleLetters++;
			}
		}
	}

	private boolean isCorrectGuess() {
		return m_CurrentGuess.toString().equalsIgnoreCase(m_WordToGuess);
	}

	//TODO: Do we need this?
	private void startGuessSuccessfulActivity() {
		Intent intent = new Intent(this, GuessSuccessActivity.class);

		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME_DIFFICUALTY, Game.getDifficualty());
		startActivity(intent);
		finish();
	}

	private void startGameOverActivity() {
		Intent intent = new Intent(this, GameOverActivity.class);
		
		//TODO: Remove? change?
		intent.putExtra(ServerRequests.RESPONSE_FIELD_GAME_DIFFICUALTY, Game.getDifficualty());
		startActivity(intent);
		finish();
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

		String[] randomLetters = new String[MAX_NUMBER_OF_GUESS_LETTERS];
		Random rand = new Random();
		int randomIndex;

		// populate our word letters into the letters
		for (int i = 0; i < word.length(); i++) {
			do {
				randomIndex = rand.nextInt(randomLetters.length);
			}
			while(randomLetters[randomIndex] != null);
			
			char wordLetter = word.charAt(i);
			randomLetters[randomIndex] = String.valueOf(wordLetter).toUpperCase(Locale.ENGLISH);
		}
		
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < randomLetters.length; i++) {
			build.append(randomLetters[i]);
		}

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
