package il.ac.idc.milab.soundscape.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class ServerRequests {
	
	private static final String TAG = "SERVER_REQUEST";
	
	private static String m_UserEmail;
	private static String m_UserToken;
	
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

	public static final String RESPONSE_FIELD_SUCCESS = "success";
	public static final String RESPONSE_FIELD_TOKEN = "token";
	public static final String RESPONSE_FIELD_WORDS = "words";
	public static final String RESPONSE_FIELD_GAME = "game";
	public static final String RESPONSE_FIELD_OPPONENT = "opponent";
	public static final String RESPONSE_FIELD_GAMES = "games";
	public static final String RESPONSE_FIELD_EMAIL = "uEmail";
	public static final String RESPONSE_FIELD_FILE = "file";
	
	
	public static final String RESPONSE_FIELD_GAME_USER = "gUserEmail";
	public static final String RESPONSE_FIELD_GAME_OPPONENT = "gOpponentEmail";
	public static final String RESPONSE_FIELD_GAME_TURNCOUNT = "gTurnCount";
	public static final String RESPONSE_FIELD_GAME_STATE = "gState";
	public static final String RESPONSE_FIELD_GAME_WHOSTURN = "gWhosTurn";
	public static final String RESPONSE_FIELD_GAME_WORD = "gCurrentWord";
	public static final String RESPONSE_FIELD_GAME_ID = "gid";
	
	public static final int RESPONSE_VALUE_SUCCESS = 1;
	public static final int RESPONSE_VALUE_FAIL = 0;
	
	
	public ServerRequests(){}
	
	/**
	 * This function checks if a request is valid and was successful
	 * @param i_Request a JSONObject representing the JSON request
	 * @throws NetworkErrorException if no network connection available
	 * */
	public boolean isValidResponse(JSONObject i_Response) {
		Log.d(TAG, "Is valid response?");
		boolean isValid = false;
		if(i_Response != null) {
			try {
				int test = i_Response.getInt(RESPONSE_FIELD_SUCCESS);
				isValid = i_Response.getInt(RESPONSE_FIELD_SUCCESS) == 
						RESPONSE_VALUE_SUCCESS;
				Log.d(TAG, "From server: " + test);
				Log.d(TAG, "From client: " + RESPONSE_VALUE_SUCCESS);
			} 
			catch (JSONException e) {
				Log.d(TAG, "Couldn't get the response string!");
				e.printStackTrace();
			}
		
			Log.d(TAG, "Is valid request?: " + isValid);
		}
		return isValid;
	}
	
	/**
	 * This function execute a request to our server and returns the response
	 * @param i_Request a JSON request object
	 * @return a JSON response from the server or null if failed.
	 * @throws NetworkErrorException if no network connection available
	 */
	public JSONObject getServerResponse(JSONObject i_Request) throws NetworkErrorException{
		Log.d(TAG, "Get server response");
		Log.d(TAG, "The request is: " + i_Request);
		JSONObject response = new JSONObject();
		try {
			response = new ServerRequestTask().execute(i_Request).get();
			Log.d(TAG, "The response is: " + response);
			// If there was a network connection problem
			if(response == null) {
				Log.d(TAG, "Network problem!");
				throw new NetworkErrorException();
			}
		}
		catch (InterruptedException e) {
			Log.d(TAG, "AsyncTask error.");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.d(TAG, "AsyncTask error.");
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * This function checks whether a Token is valid and belongs to a given email
	 * @param i_Email the email of the user we validate
	 * @param i_Token the token he/she posses
	 * @return true if token is valid, false otherwise
	 * @throws NetworkErrorException if no network connection available
	 */
	public boolean isValidToken(String i_Email, String i_Token) throws NetworkErrorException {
		Log.d(TAG, "Is valid token?");
		boolean isValid = false;
		
		JSONObject request = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_VALIDATE);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_TOKEN);
			request.put(REQUEST_SUBJECT_EMAIL, i_Email);
			request.put(REQUEST_SUBJECT_TOKEN, i_Token);
			
			JSONObject response = getServerResponse(request);
			isValid = isValidResponse(response);
			
			m_UserEmail = i_Email;
			m_UserToken = response.optString(RESPONSE_FIELD_TOKEN, null);
		}
		catch(JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return isValid;
	}

	
	/**
	 * This function checks is a user given credentials are valid
	 * @param i_Email the email of the user
	 * @param i_Password the password of the user
	 * @return true if credentials are valid, false otherwise 
	 * @throws NetworkErrorException if no network connection available
	 * */
	public boolean isValidLogin(String i_Email, String i_Password) throws NetworkErrorException{
		Log.d(TAG, "Is valid login?");
		boolean isValid = false;
		
		Log.d(TAG, "User email is: " + m_UserEmail);
		Log.d(TAG, "Requested email is: " + i_Email);
		
		// Building Parameters
		JSONObject request = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_LOGIN);
			request.put(REQUEST_FIELD_EMAIL, i_Email);
			request.put(REQUEST_FIELD_PASSWORD, i_Password);
			
			JSONObject response = getServerResponse(request);
			isValid = isValidResponse(response);
			
			if(isValid) {
				m_UserEmail = i_Email;
				m_UserToken = response.optString(RESPONSE_FIELD_TOKEN, null); 
				Log.d(TAG, "The user email is: " + m_UserEmail);
				Log.d(TAG, "The user token is: " + m_UserToken);
			}
		}
		catch(JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return isValid;
	}

	/**
	 * This function checks if a user registration was successful
	 * @param i_Name the name of the user
	 * @param i_Email the email address of the user
	 * @param i_Password the password of the user
	 * @return true if registration was successful, false otherwise 
	 * @throws NetworkErrorException if no network connection available
	 * */
	public boolean isValidRegisteration(String i_Name, String i_Email, String i_Password) throws NetworkErrorException{
		Log.d(TAG, "Is valid registration?");
		boolean isValid = false;
		JSONObject request = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_REGISTER);
			request.put(REQUEST_FIELD_NAME, i_Name);
			request.put(REQUEST_FIELD_EMAIL, i_Email);
			request.put(REQUEST_FIELD_PASSWORD, i_Password);
			
			JSONObject response = getServerResponse(request);
			isValid = isValidResponse(response);
			
			if(isValid) {
				m_UserEmail = i_Email;
				m_UserToken = response.optString(RESPONSE_FIELD_TOKEN, null); 
				Log.d(TAG, "The user email is: " + m_UserEmail);
				Log.d(TAG, "The user token is: " + m_UserToken);
			}
		}
		catch(JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return isValid;
	}

	/**
	 * This function gets the user active game list
	 * @return a JSON object representing the user game list or null if 
	 * response was not valid
	 * @throws NetworkErrorException if no network connection available
	 */
	public JSONObject getUserGameList() throws NetworkErrorException {
		Log.d(TAG, "Get user game list");
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_GET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_GAMES);
			request.put(REQUEST_FIELD_EMAIL, m_UserEmail);
			request.put(REQUEST_FIELD_TOKEN, m_UserToken);
			
			Log.d(TAG, "action=" + REQUEST_ACTION_GET);
			Log.d(TAG, "subject=" + REQUEST_SUBJECT_GAMES);
			Log.d(TAG, "email=" + m_UserEmail);
			Log.d(TAG, "token=" + m_UserToken);
			
			response = getServerResponse(request);
			if(isValidResponse(response) == false) {
				response = null;
			}
			
		} catch (JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This function gets a random player from the DB that is not our player
	 * @return a JSON object representing the opponent e-mail or null if 
	 * response was not valid
	 * @throws NetworkErrorException if no network connection available
	 */
	public JSONObject getPlayer(String i_Email) throws NetworkErrorException {
		Log.d(TAG, "Get player");
		if(i_Email != null && i_Email.equalsIgnoreCase(m_UserEmail)) {
			return null;
		}
		
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_GET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_EMAIL);
			request.put(REQUEST_FIELD_EMAIL, m_UserEmail);
			
			// If random player needed
			if(i_Email == null) {
				request.put(REQUEST_FIELD_OPPONENT, REQUEST_FIELD_RANDOM);
			}
			else {
				request.put(REQUEST_FIELD_OPPONENT, i_Email);
			}
			request.put(REQUEST_FIELD_TOKEN, m_UserToken);
			
			response = getServerResponse(request);
			if(isValidResponse(response) == false) {
				response = null;
			}
		} 
		catch (JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * This function gets a random set of words from the server by difficulty
	 * @return a JSON object representing a random set of words with difficulty 
	 * tags or null if response was not valid
	 * @throws NetworkErrorException if no network connection available
	 */
	public JSONObject getRandomWords() throws NetworkErrorException {
		Log.d(TAG, "Get random words");
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_GET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_WORDS);
			
			response = getServerResponse(request);
			if(isValidResponse(response) == false) {
				response = null;
			}
		} catch (JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return response;
	}
	
	/**
	 * This function sends the recorded sound to the server
	 * @param i_FileName the name of the sound file we recorder earlier
	 * @param i_metadata the meta data of the sound file
	 * @return True if upload was successful, false otherwise
	 * @throws NetworkErrorException if no network connection available
	 */
	public boolean sendFile(String i_FileName, String i_Word, int i_Emotion, String i_GameID) throws NetworkErrorException {
		Log.d(TAG, "Sending file");
		boolean isValid = false;
		byte[] encodedFile = encodeFile(new File(i_FileName));
		String fileEncoded = Base64.encodeToString(encodedFile, Base64.DEFAULT);
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try 
		{
			request.put(REQUEST_ACTION, REQUEST_ACTION_SET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_FILE);
			request.put(REQUEST_FIELD_FILE, fileEncoded);
			request.put(REQUEST_FIELD_WORD, i_Word);			
			request.put(REQUEST_FIELD_EMOTION, i_Emotion);
			request.put(REQUEST_FIELD_EMAIL, m_UserEmail);
			request.put(REQUEST_FIELD_GAMEID, i_GameID);
			
			response = getServerResponse(request);
			isValid = isValidResponse(response);
		} 
		catch (JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}

		return isValid;
	}
	
	public byte[] getGameFile(String i_GameID) throws NetworkErrorException {
		Log.d(TAG, "Geting the game file");
		byte[] decodedFile = null;
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		
		try 
		{
			request.put(REQUEST_ACTION, REQUEST_ACTION_GET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_FILE);
			request.put(REQUEST_FIELD_GAMEID, i_GameID);
			
			response = getServerResponse(request);
			if(isValidResponse(response)) {
				String encodedFile = response.optString(ServerRequests.RESPONSE_FIELD_FILE);
				Log.d(TAG, "Encoded file: " + encodedFile);
				decodedFile = Base64.decode(encodedFile, Base64.DEFAULT);
			}
		} 
		catch (JSONException e) {
			Log.d(TAG, "Couldn't put stuff in our JSON object!");
			e.printStackTrace();
		}
		
		return decodedFile;
	}

	/**
	 * A helper function that returns a given file as a byte array
	 * @param i_file the file descriptor
	 * @return a byte array representing the file content
	 */
	private byte[] encodeFile(File i_file) {

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024;
		ByteArrayBuffer result = new ByteArrayBuffer(maxBufferSize);

		try {
			FileInputStream fileInputStream = new FileInputStream(i_file);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				result.append(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		return result.toByteArray();
	}
	
	/**
	 * Getter
	 * @return the user email address
	 */
	public static String getUserEmail() {
		return m_UserEmail;
	}
	
	/**
	 * Getter
	 * @return the user Token
	 */
	public static String getUserToken() {
		return m_UserToken;
	}
	
	/**
	 * Setter
	 * @param i_Email sets the user E-mail address
	 */
	public static void setUserEmail(String i_Email) {
		m_UserEmail = i_Email;
	}
	
	/**
	 * Setter
	 * @param i_Token sets the user Token
	 */
	public static void setUserToken(String i_Token) {
		m_UserToken = i_Token;
	}
	
	/**
	 * A Helper class that does the asynchronic task of communication with the
	 * server
	 */
	private class ServerRequestTask extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			
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
	}
}
