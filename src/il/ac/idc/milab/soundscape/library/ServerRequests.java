package il.ac.idc.milab.soundscape.library;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.util.Log;

public class ServerRequests {
	
	private static final String TAG = "SERVER_REQUEST";
	
	private static String m_UserEmail;
	private static String m_UserToken;
	
	// Server request actions
	private static final String REQUEST_ACTION = "action";
	private static final String REQUEST_ACTION_VALIDATE = "validate";
	private static final String REQUEST_ACTION_LOGIN = "login";
	private static final String REQUEST_ACTION_REGISTER = "register";
	private static final String REQUEST_ACTION_GET = "get";
	private static final String REQUEST_ACTION_SET = "set";
	
	// Server request subjects
	private static final String REQUEST_SUBJECT = "subject";
	private static final String REQUEST_SUBJECT_EMAIL = "email";
	private static final String REQUEST_SUBJECT_TOKEN = "token";
	private static final String REQUEST_SUBJECT_GAMES = "games";
	
	// Server request Fields
	private static final String REQUEST_FIELD_EMAIL = "email";
	private static final String REQUEST_FIELD_PASSWORD = "password";
	private static final String REQUEST_FIELD_NAME = "name";
	private static final String REQUEST_FIELD_TOKEN = "token";
	
	private static final String RESPONSE_FIELD_SUCCESS = "success";
	private static final String RESPONSE_FIELD_TOKEN = "token";
	private static final int RESPONSE_VALUE_SUCCESS = 1;
	private static final int RESPONSE_VALUE_FAIL = 0;
	
	
	// Client JSON request keys
	public static final String k_JsonKeyName = "name";
	public static final String k_JsonKeyEmail = "email";
	public static final String k_JsonKeyPassword = "password";
	public static final String k_JsonKeyFile = "file";
	public static final String k_JsonKeyWord = "word";
	public static final String k_JsonKeyDifficulty = "difficulty";
	public static final String k_JsonKeyAction = "action";
	public static final String k_JsonKeyEmotion = "emotion";

	// Client JSON request values
	public static final String k_JsonValueTagLogin = "login";
	public static final String k_JsonValueTagRegister = "register";
	public static final String k_JsonValueTagValidate = "validate";
	public static final String k_JsonValueTagAction = "action";
	public static final String k_JsonValueTagGet = "get";
	public static final String k_JsonValueTagGetGames = "games";

	// Server JSON response keys
	public static final String k_JsonValueTagSendFile = "file";
	public static final String k_JsonValueTagGetWords = "words";
	public static final String k_JsonValueFileSend = "set";
	public static final String k_JsonValueFileGet = "get";

	// Server JSON response keys
	public static final String k_JsonKeyError = "error";
	public static final String k_JsonKeyErrorMessage = "error_msg";
	public static final String k_JsonKeySuccess = "success";
	public static final String k_JsonKeyWords = "words";
	
	public ServerRequests(){}
	
	/**
	 * This function checks if a request is valid and was successful
	 * @param i_Request a JSONObject representing the JSON request
	 * @throws NetworkErrorException if no network connection available
	 * */
	public boolean isValidResponse(JSONObject i_Response) throws NetworkErrorException{
		boolean isValid = false;
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
		return isValid;
	}
	
	/**
	 * This function execute a request to our server and returns the response
	 * @param i_Request a JSON request object
	 * @return a JSON response from the server or null if failed.
	 * @throws NetworkErrorException if no network connection available
	 */
	public JSONObject getServerResponse(JSONObject i_Request) throws NetworkErrorException{

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
	public boolean isValidUser(String i_Email, String i_Password) throws NetworkErrorException{
		boolean isValid = false;
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

	public JSONObject getUserGameList() throws NetworkErrorException {
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
	
	public JSONObject getRandomPlayer() throws NetworkErrorException {
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try {
			request.put(REQUEST_ACTION, REQUEST_ACTION_GET);
			request.put(REQUEST_SUBJECT, REQUEST_SUBJECT_EMAIL);
			request.put(REQUEST_FIELD_EMAIL, m_UserEmail);
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
/*
	public JSONObject getWords() {

		JSONObject request = new JSONObject();
		try {
			request.put(k_JsonKeyTag, k_JsonValueTagGet);
			request.put(k_JsonKeyAction, k_JsonValueTagGetWords);
		} catch (JSONException e) {
			return null;
		}

		return request;
	}
	
	public JSONObject sendFile(String i_fileName, String i_metadata) throws NetworkErrorException {
		// TODO Auto-generated method stub
		byte[] encodedFile = encodeFile(new File(i_fileName));
		JSONObject json = null;
		String fileEncoded = null;
		try 
		{
			json = new JSONObject(i_metadata);
			json.put(k_JsonKeyTag, k_JsonValueTagSendFile);
			json.put(k_JsonKeyAction, k_JsonValueFileSend);
			json.put(k_JsonKeyFile, Base64.encodeToString(encodedFile, Base64.DEFAULT));
			fileEncoded = Base64.encodeToString(encodedFile, Base64.DEFAULT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("NETWORK", "Sending request: " + json.toString());
		Log.d("NETWORK", "Length is" + fileEncoded.length());
		return NetworkUtils.sendJsonPostRequest(json);
	}

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

	*/
	
	public static String getUserEmail() {
		return m_UserEmail;
	}
	
	public static String getUserToken() {
		return m_UserToken;
	}
	
	public static void setUserEmail(String i_Email) {
		m_UserEmail = i_Email;
	}
	
	public static void setUserToken(String i_Token) {
		m_UserToken = i_Token;
	}
	
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
