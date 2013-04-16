package il.ac.idc.milab.soundscape;

import java.util.concurrent.ExecutionException;

import il.ac.idc.milab.soundscape.library.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

public class FindUserToPlayActivity extends Activity {

	private boolean m_IsRandom = false;
	private String m_UserEmail;
	private EditText m_Email;
	private Button m_Submit;
	private TextView m_Result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the user email
		m_UserEmail = getIntent().getStringExtra(NetworkUtils.k_JsonKeyEmail);
		
		Log.d("FINDUSER", "Started FindUserToPlay activity");
		if(isRandomGame()) {
			Log.d("FINDUSER", "User wants a RANDOM game");
			startGameActivity(getRandomEmail());
			finish();
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
		boolean isValid = false;
		String opponentEmail = null;
		JSONObject request = new JSONObject();
		try {
			request.put(NetworkUtils.k_JsonKeyTag, NetworkUtils.k_JsonValueTagGet);
			request.put(NetworkUtils.k_JsonValueTagAction, NetworkUtils.k_JsonKeyEmail);
			request.put(NetworkUtils.k_JsonKeyEmail, m_UserEmail);
			
			Log.d("FINDUSER", "The request is: " + request.toString());
			JSONObject response = new GetEmailTask().execute(request).get();
			Log.d("FINDUSER", "The response is: " + response.toString());
			
			isValid = response.getInt(NetworkUtils.k_JsonKeySuccess) ==
					NetworkUtils.k_FlagOn;
			
			if(isValid) {
				opponentEmail = response.getString("opponent");
			}
			
		} catch (JSONException e) {
			Log.d("FINDUSER", "Got a JSON exception!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.d("FINDUSER", "Got an Interrupted Exception!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.d("FINDUSER", "Got an Execution Exception!");
			e.printStackTrace();
		}
		return opponentEmail;
	}

	protected boolean isValidEmail() {
		boolean isValid = false;
		
		String email = m_Email.getText().toString();
		Log.d("FINDUSER", String.format("Checking if email '%s' is valid", email));
		
		JSONObject request = new JSONObject();
		try {
			request.put(NetworkUtils.k_JsonKeyTag, NetworkUtils.k_JsonValueTagValidate);
			request.put(NetworkUtils.k_JsonValueTagAction, NetworkUtils.k_JsonKeyEmail);
			request.put(NetworkUtils.k_JsonKeyEmail, email);
			
			Log.d("FINDUSER", "The request is: " + request.toString());
			JSONObject response = new CheckEmailTask().execute(request).get();
			Log.d("FINDUSER", "The response is: " + response.toString());
			
			isValid = response.getInt(NetworkUtils.k_JsonKeySuccess) ==
					NetworkUtils.k_FlagOn;
			
		} catch (JSONException e) {
			Log.d("FINDUSER", "Got a JSON exception!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.d("FINDUSER", "Got an Interrupted Exception!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.d("FINDUSER", "Got an Execution Exception!");
			e.printStackTrace();
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
	
	private class CheckEmailTask extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(JSONObject... credentials) {
	    	return NetworkUtils.sendJsonPostRequest(credentials[0]);
	    }
	}
	
	private class GetEmailTask extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(JSONObject... credentials) {
	    	return NetworkUtils.sendJsonPostRequest(credentials[0]);
	    }
	}
}
