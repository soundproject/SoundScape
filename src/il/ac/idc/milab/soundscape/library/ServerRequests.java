package il.ac.idc.milab.soundscape.library;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ServerRequests {
	
	private static final String TAG = "SERVER_REQUEST";
	private static ProgressDialog m_ProgressDialog;
	private static Context m_Context;
	
	// Server request actions
	public static final String REQUEST_ACTION = "action";
	public static final String REQUEST_ACTION_VALIDATE = "validate";
	public static final String REQUEST_ACTION_LOGIN = "login";
	public static final String REQUEST_ACTION_REGISTER = "register";
	public static final String REQUEST_ACTION_GET = "get";
	public static final String REQUEST_ACTION_SET = "set";
	
	// Server request subjects
	public static final String REQUEST_SUBJECT = "subject";
	public static final String REQUEST_SUBJECT_EMAIL = "email";
	public static final String REQUEST_SUBJECT_TOKEN = "token";
	public static final String REQUEST_SUBJECT_GAMES = "games";
	public static final String REQUEST_SUBJECT_WORDS = "words";
	public static final String REQUEST_SUBJECT_FILE = "file";
	public static final String REQUEST_SUBJECT_GAME = "game";
	public static final String REQUEST_SUBJECT_GUESS = "guess";
	
	// Server request Fields
	public static final String REQUEST_FIELD_EMAIL = "email";
	public static final String REQUEST_FIELD_PASSWORD = "password";
	public static final String REQUEST_FIELD_NAME = "name";
	public static final String REQUEST_FIELD_TOKEN = "token";
	public static final String REQUEST_FIELD_OPPONENT = "opponent";
	public static final String REQUEST_FIELD_FILE = "file";
	public static final String REQUEST_FIELD_FILE_META = "meta";
	public static final String REQUEST_FIELD_WORD = "word";
	public static final String REQUEST_FIELD_EMOTION = "emotion";
	public static final String REQUEST_FIELD_RANDOM = "random";
	public static final String REQUEST_FIELD_GAMEID = "gid";
	public static final String REQUEST_FIELD_GUESS = "guess";

	public static final String RESPONSE_FIELD_SUCCESS = "success";
	public static final String RESPONSE_FIELD_TOKEN = "token";
	public static final String RESPONSE_FIELD_WORDS = "words";
	public static final String RESPONSE_FIELD_GAME = "game";
	public static final String RESPONSE_FIELD_OPPONENT = "opponent";
	public static final String RESPONSE_FIELD_GAMES = "games";
	public static final String RESPONSE_FIELD_EMAIL = "uEmail";
	public static final String RESPONSE_FIELD_FILE = "file";
	public static final String RESPONSE_FIELD_ERROR_MESSAGE = "error_msg";
	
	public static final String RESPONSE_FIELD_GAME_USER = "gUserEmail";
	public static final String RESPONSE_FIELD_GAME_OPPONENT = "gOpponentEmail";
	public static final String RESPONSE_FIELD_GAME_TURNCOUNT = "gTurnCount";
	public static final String RESPONSE_FIELD_GAME_STATE = "gState";
	public static final String RESPONSE_FIELD_GAME_WHOSTURN = "gWhosTurn";
	public static final String RESPONSE_FIELD_GAME_WORD = "gCurrentWord";
	public static final String RESPONSE_FIELD_GAME_ID = "gid";
	public static final String RESPONSE_FIELD_GAME_DIFFICUALTY = "gLevel";
	
	public static final int RESPONSE_VALUE_SUCCESS = 1;
	public static final int RESPONSE_VALUE_FAIL = 0;
	
	
	public ServerRequests(){}
	
	/**
	 * This function checks if a request is valid and was successful
	 * @param i_Request a JSONObject representing the JSON request
	 * @throws NetworkErrorException if no network connection available
	 * */
	public boolean isValidResponse(JSONObject i_Response) {
		boolean isValid = false;
		if(i_Response != null) {
			try {
				int test = i_Response.getInt(RESPONSE_FIELD_SUCCESS);
				isValid = i_Response.getInt(RESPONSE_FIELD_SUCCESS) == 
						RESPONSE_VALUE_SUCCESS;
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return isValid;
	}
	
	public JSONObject sendRequestToServer(JSONObject i_Request, Context i_Context) {
		m_Context = i_Context;
		JSONObject response = null;

		if(i_Request != null) {
			try {
				response = new ServerRequestTask().execute(i_Request).get();
			} catch (Exception e) {
				// An error related to the async task occured
				e.printStackTrace();
				response = null;
			}
			
			// Check if login was successful
			if(NetworkUtils.serverRequests.isValidResponse(response) == false) {
				handleErrorResponse(response);
			}
		}
		
		return response;
	}
	
	private static void handleErrorResponse(JSONObject i_Response) {
		String title = null;
		String message;
		if(i_Response == null) {
			title = "Network Problem:";
			message = "Can't connect to server.\nPlease check your network connection and try again.";
		}
		else if(i_Response.isNull(ServerRequests.RESPONSE_FIELD_SUCCESS)) {
			title = "Server Problem:";
			message = "Can't connect to server.\nPlease try again in a few seconds.";
		}
		else {
			message = i_Response.optString(ServerRequests.RESPONSE_FIELD_ERROR_MESSAGE);
		}
		
		buildErrorDialog(title, message).show();
	}
	
	private static AlertDialog buildErrorDialog(String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(m_Context)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("Close", null);
		
		return alert.create();
	}
	
	/**
	 * A Helper class that does the asynchronic task of communication with the
	 * server
	 */
	private class ServerRequestTask extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			m_ProgressDialog = ProgressDialog.show(m_Context, "", "");
		}
		
		@Override
		protected JSONObject doInBackground(JSONObject... credentials) {
	    	try {
				return NetworkUtils.sendJsonPostRequest(credentials[0]);
			} 
	    	catch (NetworkErrorException e) {
				return null;
			}
	    }
		
       @Override
        protected void onPostExecute(JSONObject result) {
    	   m_ProgressDialog.dismiss();
        }
	}
}
