package il.ac.idc.milab.soundscape;

import il.ac.idc.milab.soundscape.library.NetworkUtils;
import il.ac.idc.milab.soundscape.library.RandomLetterGenerator;
import il.ac.idc.milab.soundscape.library.ServerRequests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuessWordActivity extends Activity {

	private static final int NUMBER_OF_LETTERS = 12;
	private static final String TAG = "GuessWordActivity";
	private String m_word;
	private int m_difficulty;
	private int m_nextFreeGuessLocation;
	private RandomLetterGenerator m_letterGenerator;
	private Character[] m_randomCharacters;
	private ImageButton m_checkGuessButton;
	private ImageButton m_playRecordingButton;
	private LinearLayout m_layout1;
	private Character[] m_guess;
	private MediaPlayer m_MediaPlayer = null;
	private Uri m_recording_file;
	private int m_guessesLeft = 3;
	private TextView m_guessesLeftTextView;

	private boolean m_bombUsed = false;
	private TextView m_bombNumbertextView;
	private ImageButton m_useBombButton;
	private String m_GameDetails = null;
	private String fileName = "GuessSound";
	private String m_GameID = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting guess words activity");
		setContentView(R.layout.activity_guess_word);
		m_GameDetails = getIntent().getStringExtra(
				ServerRequests.RESPONSE_FIELD_GAME);
		JSONObject gameDetails = null;

		try {
			gameDetails = new JSONObject(m_GameDetails);
			Log.d(TAG, "Game details: " + gameDetails);
			m_word = gameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_WORD);
			m_GameID = gameDetails.optString(ServerRequests.RESPONSE_FIELD_GAME_ID);
			Log.d(TAG, "Got word: " + m_word);
			byte[] gameFile = NetworkUtils.serverRequests
					.getGameFile(gameDetails
							.optString(ServerRequests.RESPONSE_FIELD_GAME_ID));
			
			File myDir = getFilesDir();
			try {
		        File file = new File(myDir + "/test/", fileName);
		        if (file.getParentFile().mkdirs()) {
		            file.createNewFile();
		            FileOutputStream fos = new FileOutputStream(file);

		            fos.write(gameFile);
		            fos.flush();
		            fos.close();
		        }

				m_recording_file = Uri.fromFile(file);
				Log.i(TAG, "File: " + m_recording_file);
			} catch (Exception e) {
				Log.d(TAG, "Was not able to write file to disk");
				e.printStackTrace();
			}
			
			initSoundPlayer();

		} catch (NetworkErrorException e) {
			String msg = "A network connection is required";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			Log.d(TAG, "Could not convert to JSON");
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
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
		if(m_MediaPlayer == null) {
			m_MediaPlayer = MediaPlayer.create(this, m_recording_file);
			m_MediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer m_MediaPlayer) {
                    // TODO Auto-generated method stub
                	try {
						m_MediaPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }

            });   
		}
	}

	private LinearLayout m_firstRowLayout;
	private LinearLayout m_secondRowLayout;

	private void populateLetters() {

		int col = 0;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x / 6;

		for (Character character : this.m_randomCharacters) {
			Button button = new Button(this);
			Log.d(TAG, "Character is: " + character);
			button.setText("" + character.toUpperCase(character));
			LinearLayout parent = col >= NUMBER_OF_LETTERS / 2 ? m_secondRowLayout
					: m_firstRowLayout;
			parent.addView(button);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					button.getLayoutParams());
			button.setWidth(width);
			col++;

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Button clickedButton = (Button) v;
					clickedButton.setTag(clickedButton.getLayoutParams());
					addLetterToGuess(clickedButton);
				}
			});
		}
	}

	protected void removeLetterFromGuess(Button clickedButton) {
		Object[] Tags = (Object[]) clickedButton.getTag();
		int letterIndex = (Integer) Tags[0];
		this.m_guess[letterIndex] = ' ';
		this.m_nextFreeGuessLocation = Math.min(m_nextFreeGuessLocation,
				letterIndex);
		Button randomLetterButton = (Button) Tags[1];
		randomLetterButton.setVisibility(View.VISIBLE);
		randomLetterButton.setEnabled(true);
	}

	protected void addLetterToGuess(Button clickedButton) {

		if (m_nextFreeGuessLocation < this.m_guess.length) {
			this.m_guess[m_nextFreeGuessLocation] = clickedButton.getText()
					.charAt(0);
			Log.i(TAG, "New guess is: " + this.m_guess.toString().trim());
			Button guessButton = (Button) this.m_layout1
					.getChildAt(this.m_nextFreeGuessLocation);
			guessButton.setText(clickedButton.getText());
			Object[] Tags = (Object[]) guessButton.getTag();
			Tags[1] = clickedButton;
			Log.i(TAG,
					"Guess Button tag is "
							+ ((Object[]) guessButton.getTag())[1]);
			for (; m_nextFreeGuessLocation < this.m_guess.length; m_nextFreeGuessLocation++) {

				if (this.m_guess[m_nextFreeGuessLocation] == ' ') {
					break;
				}
			}
			clickedButton.setVisibility(View.INVISIBLE);
			clickedButton.setEnabled(false);
		}
	}

	private ImageView m_bombButton;
	private int m_bombsLeft = 3;

	private void initButtons() {

		initHeader();

		Log.d(TAG, "Initializing buttons");
		this.m_playRecordingButton = (ImageButton) findViewById(R.id.play_sound_guess_button);
		this.m_playRecordingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_MediaPlayer.start();
			}
		});
		
		
		this.m_checkGuessButton = (ImageButton) findViewById(R.id.check_guess_imageButton);
		this.m_layout1 = (LinearLayout) findViewById(R.id.linear1);
		this.m_bombButton = (ImageView) findViewById(R.id.header_bomb_icon);
		this.m_firstRowLayout = (LinearLayout) findViewById(R.id.random_letters_first_rowLayout);
		this.m_secondRowLayout = (LinearLayout) findViewById(R.id.random_letters_second_rowLayout);
		this.m_useBombButton = (ImageButton) findViewById(R.id.use_bomb_guess_button);
		this.m_guessesLeftTextView = (TextView) findViewById(R.id.guesses_left_textView);
		Log.d(TAG, "Done initializing buttons");

		this.m_guessesLeftTextView.setText("Guesses Left: " + m_guessesLeft);

		Log.d(TAG, "Setting onClickListeners");
		// set Bomb onclickListener
		this.m_useBombButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!m_bombUsed && hasBombsLeft()) {
					m_bombUsed = true;
					useBomb();
					// get character to remove
					int[] charsToRemove = m_letterGenerator.RemoveRandomChars();

					// remove characters:
					removeCharacters(charsToRemove);
				}

			}
		});

		// set onclick listener for checkGuessButton
		this.m_checkGuessButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (m_letterGenerator.checkGuess(getGuess())) {
					sendParamsToServer();
					startGuessSuccessActivity();
					finish();
				} else {
					showMessage();
					m_guessesLeft--;
					m_guessesLeftTextView.setText("Guesses Left: "
							+ m_guessesLeft);

					if (m_guessesLeft == 0) {
						gameOver();
					}
				}
			}
		});

		// Create guess letter buttons and
		// set onclick listener for guess letter buttons
		for (int i = 0; i < this.m_word.length(); i++) {
			Button button = new Button(this);
			button.setBackgroundResource(R.drawable.border_black_button);
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
		Log.d(TAG, "Done onClickListeners");
	}

	protected void sendParamsToServer() {
		try {
			NetworkUtils.serverRequests.updateGameStatus(m_GameID);
		} 
		catch (NetworkErrorException e) {
			String msg = "This application requires an Internet connection.";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
		
	}

	protected void gameOver() {

		Intent intent = new Intent(this, GameOverActivity.class);
		intent.putExtra("word", this.m_word);

		startActivity(intent);
		finish();

	}

	private void initHeader() {
		this.m_bombNumbertextView = (TextView) findViewById(R.id.header_text_view_bomb_number);
		this.m_bombsLeft = Integer.parseInt(this.m_bombNumbertextView.getText()
				.toString());

	}

	protected boolean hasBombsLeft() {

		return m_bombsLeft > 0;
	}

	protected void useBomb() {

		m_bombsLeft--;
		m_bombNumbertextView.setText(String.format("%d", m_bombsLeft));

		// TODO: tell server bomb was used
	}

	protected void removeCharacters(int[] charsToRemove) {

		for (int index : charsToRemove) {
			LinearLayout parent = index >= NUMBER_OF_LETTERS / 2 ? m_secondRowLayout
					: m_firstRowLayout;
			int actualLocation = parent == m_firstRowLayout ? index : index
					- (NUMBER_OF_LETTERS / 2);
			Button buttonToRemove = (Button) parent.getChildAt(actualLocation);
			buttonToRemove.setEnabled(false);
			buttonToRemove.setVisibility(View.INVISIBLE);
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
		// TODO TAL: send success to server

		Intent intent = new Intent(this, GuessSuccessActivity.class);

		intent.putExtra("difficulty", this.m_difficulty);
		startActivity(intent);
		finish();
	}

	private void generateRandomLetters() {
		this.m_randomCharacters = this.m_letterGenerator
				.GetRandomLetters(NUMBER_OF_LETTERS);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_guess_word, menu);
		return true;
	}

}
