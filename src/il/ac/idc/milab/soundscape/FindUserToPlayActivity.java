package il.ac.idc.milab.soundscape;

import java.util.concurrent.ExecutionException;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindUserToPlayActivity extends Activity {

	private boolean m_IsRandom = false;
	private String m_UserEmail;
	private EditText m_Email;
	private Button m_Submit;
	private TextView m_Result;
	private String m_GameId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("FINDUSER", "Started FindUserToPlay activity");
		if(isRandomGame()) {
			Log.d("FINDUSER", "User wants a RANDOM game");
			String email = getRandomEmail();
			
			// If we got an email
			if(email != null) {
				startGameActivity(email);
				finish();
			}
			else {
				String msg = "There was a problem getting a player.. :(";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		}
		
		Log.d("FINDUSER", "User wants to specify an email");
		// If not random
		setContentView(R.layout.activity_find_user_to_play);
		
		m_Email = (EditText)findViewById(R.id.finduser_edit_text_email);
		
		m_Submit = (Button)findViewById(R.id.finduser_button_submit);
		m_Submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isValidEmail()) {
					startGameActivity(m_Email.getText().toString());
				}
				else {
					m_Result.setText("The email provided cannot be found :(");
				}
			}
		});
		
		m_Result = (TextView)findViewById(R.id.finduser_text_view_result);
		
	}

	private String getRandomEmail() {
		String opponentEmail = null;
		JSONObject response = new JSONObject();
		try {
			response = NetworkUtils.serverRequests.getRandomPlayer();
			if(response != null) {
				opponentEmail = response.optString("opponent");
			}
		} catch (NetworkErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return opponentEmail;
	}

	protected boolean isValidEmail() {
		boolean isValid = false;
		
		String email = m_Email.getText().toString();
		Log.d("FINDUSER", String.format("Checking if email '%s' is valid", email));
		
		try {
			isValid = NetworkUtils.serverRequests.isValidUser(email, null);
		} catch (NetworkErrorException e) {
			String msg = "A network connection is required";
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}

		return isValid;
	}

	private void startGameActivity(String email) {
		Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
		intent.putExtra("opponent", email);
		intent.putExtra(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
		startActivity(intent);
		finish();
	}

	private boolean isRandomGame() {
		Bundle extras = getIntent().getExtras();
		m_IsRandom = extras.getBoolean("random");
		
		return m_IsRandom;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_user_to_play, menu);
		return true;
	}
}
