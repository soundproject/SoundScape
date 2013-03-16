package il.ac.idc.milab.soundscape;

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity that handles a game against a single oponnent
 * 
 * @author Gadi. Tal
 * 
 */
public class GameActivity extends Activity {

	
	private static String word = "rain"; //TODO: Get the word from the server
	private static final int MAX_NUMBER_OF_LETTERS = 12;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mp = MediaPlayer.create(this, R.raw.rain);
		setContentView(getGameLayout());
		//setContentView(R.layout.activity_game);

	}

	private LinearLayout getGameLayout() {
		float d = this.getResources().getDisplayMetrics().density;
		
		LinearLayout container = new LinearLayout(this);
		container.setOrientation(LinearLayout.VERTICAL);
		
		// "Give up" button
		Button button = new Button(this);
		button.setText(R.string.game_button_give_up);
		button.setGravity(Gravity.CENTER);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, // Width 
				LinearLayout.LayoutParams.WRAP_CONTENT  // Height
				);
		button.setLayoutParams(params);
		container.addView(button);
		
		// Image view
		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.music_note);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, // Width 
				LinearLayout.LayoutParams.WRAP_CONTENT  // Height
				);
		
		// Center picture
		params.setMargins((int)(100 * d), (int)(20 * d), (int)(100 * d), 0);
		image.setLayoutParams(params);
		
		container.addView(image);
		
		// "Tap to hear" button
		button = new Button(this);
		button.setText(R.string.game_button_audio_text);
		button.setGravity(Gravity.CENTER);
		
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, // Width 
				LinearLayout.LayoutParams.WRAP_CONTENT // Height
				);
		
		params.topMargin = (int)(10 * d);
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playAudioFile();
			}
		});
		
		container.addView(button);
		
		
		Log.v("WORD", "Creating the word layout");
		
		// Word guess letters
		LinearLayout wordLayout = new LinearLayout(this);
		wordLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, // Width 
				LinearLayout.LayoutParams.WRAP_CONTENT  // Height
				);
		wordLayout.setBackgroundResource(R.drawable.border);
		wordLayout.setGravity(Gravity.CENTER);
		populateWordLetters(wordLayout, d, word);
		
		container.addView(wordLayout);
		
		// Guess letters
		LinearLayout guessLettersLayout = new LinearLayout(this);
		guessLettersLayout.setOrientation(LinearLayout.VERTICAL);
		
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, // Width 
				LinearLayout.LayoutParams.WRAP_CONTENT  // Height
				);
		
		guessLettersLayout.setBackgroundResource(R.drawable.border);
		guessLettersLayout.setGravity(Gravity.CENTER);
		
		String[] letters = generateRandomLetters(word);
		TextView[] letterBoxes = getGuessLetters(d, letters); // TODO: This can be merged with populateWordLetters()
		
		// Row 1
		LinearLayout guessLettersRow1Layout = new LinearLayout(this);
		guessLettersRow1Layout.setOrientation(LinearLayout.HORIZONTAL);
		guessLettersRow1Layout.setGravity(Gravity.CENTER);
		guessLettersRow1Layout.setLayoutParams(params);
		
		// Row 2
		LinearLayout guessLettersRow2Layout = new LinearLayout(this);
		guessLettersRow2Layout.setOrientation(LinearLayout.HORIZONTAL);
		guessLettersRow2Layout.setGravity(Gravity.CENTER);
		params.topMargin = (int)(10 * d);
		guessLettersRow2Layout.setLayoutParams(params);
		
		Log.v("LETTERS", "Populating the guess letters");
		// Populate the guess letters
		for(int i = 0; i < letterBoxes.length; i++) {
			if(i < 6) {
				// Row 1
				guessLettersRow1Layout.addView(letterBoxes[i]);
			}
			else {
				guessLettersRow2Layout.addView(letterBoxes[i]);
			}
		}
		
		guessLettersLayout.addView(guessLettersRow1Layout);
		guessLettersLayout.addView(guessLettersRow2Layout);
		
		container.addView(guessLettersLayout);
		
		return container;
	}

	private TextView[] getGuessLetters(float d, String[] letters) {
		
		TextView[] lettersBoxes = new TextView[MAX_NUMBER_OF_LETTERS];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int)(40 * d), // Width 
				(int)(40 * d)  // Height
				);
		
		params.rightMargin = (int)(10 * d);
		Log.v("LETTERS", "Creating the guess letters");
		
		for(int i = 0; i < letters.length; i++) {
			TextView letter = new TextView(this);
			
			letter.setText(letters[i]);
			letter.setGravity(Gravity.CENTER);
			letter.setTextSize(10 * d);
			letter.setBackgroundResource(R.drawable.border);
			letter.setLayoutParams(params);
			letter.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					populateAnswer(v);
				}
			});
			
			lettersBoxes[i] = letter;
		}
		
		return lettersBoxes;
		
	}

	protected void populateAnswer(View v) {
		for(int i = 0; i < word.length(); i++) {
			TextView textView = (TextView)findViewById(100 + i);

			if(textView != null && textView.getText().length() == 0) {
				String text = (String) ((TextView)v).getText();
				textView.setText(text);
				break;
			}
		}
	}

	private String[] generateRandomLetters(String word) {
		// generate MAX_NUMBER_OF_LETTERS random letters
		String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z"};
		
		String[] randomLetters = new String[MAX_NUMBER_OF_LETTERS];
		Random rand = new Random();
		
		// Insert our word letters into the array randomly
		for(int i = 0; i < word.length(); i++) {
			int randomIndex = rand.nextInt(randomLetters.length);
			while(randomLetters[randomIndex] != null) {
				randomIndex = rand.nextInt(randomLetters.length);
			}
			
			randomLetters[randomIndex] = String.valueOf(word.charAt(i)).toUpperCase(Locale.ENGLISH);
		}
		
		// populate the other letters at random
		for(int i = 0; i < randomLetters.length; i++) {
			int randomIndex = rand.nextInt(letters.length);
			if(randomLetters[i] != null) {
				continue;
			}
			
			randomLetters[i] = letters[randomIndex].toUpperCase(Locale.ENGLISH);
		}
		
		return randomLetters;
	}

	private void populateWordLetters(LinearLayout wordLayout, float d, String word) {
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int)(40 * d), // Width 
				(int)(40 * d)  // Height
				);
		
		params.rightMargin = (int)(10 * d);
		
		Log.v("LETTERS", "Creating the word letters");
		
		for(int i = 0; i < word.length(); i++) {
			TextView letter = new TextView(this);
			letter.setId(100 + i);
			letter.setGravity(Gravity.CENTER);
			letter.setTextSize(10 * d);
			letter.setBackgroundResource(R.drawable.border);
			letter.setLayoutParams(params);
			
			wordLayout.addView(letter);
		}
	}

	protected void playAudioFile() {
		
		if(mp.isPlaying()) {
			mp.stop();
		}
		else {
			mp.start();
		}

	}
}