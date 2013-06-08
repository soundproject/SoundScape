package il.ac.idc.milab.soundscape.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * This class represents our server communication link.
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class ServerRequests {

	// Our server 
	public static final String k_ServerUrl = 
			"http://soundscape.milab.idc.ac.il/index.php";
		
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

	// Server response Fields
	public static final String RESPONSE_FIELD_SUCCESS = "success";
	public static final String RESPONSE_FIELD_TOKEN = "token";
	public static final String RESPONSE_FIELD_WORDS = "words";
	public static final String RESPONSE_FIELD_OPPONENT = "opponent";
	public static final String RESPONSE_FIELD_GAMES = "games";
	public static final String RESPONSE_FIELD_EMAIL = "uEmail";
	public static final String RESPONSE_FIELD_FILE = "file";
	public static final String RESPONSE_FIELD_ERROR_MESSAGE = "error_msg";
	
	// Server response Fields related to game
	public static final String RESPONSE_FIELD_GAME = "game";
	public static final String RESPONSE_FIELD_GAME_USER = "gUserEmail";
	public static final String RESPONSE_FIELD_GAME_OPPONENT = "gOpponentEmail";
	public static final String RESPONSE_FIELD_GAME_TURNCOUNT = "gTurnCount";
	public static final String RESPONSE_FIELD_GAME_STATE = "gState";
	public static final String RESPONSE_FIELD_GAME_WHOSTURN = "gWhosTurn";
	public static final String RESPONSE_FIELD_GAME_WORD = "gCurrentWord";
	public static final String RESPONSE_FIELD_GAME_ID = "gid";
	public static final String RESPONSE_FIELD_GAME_DIFFICUALTY = "gLevel";
	
	// Server response Fields related to the success of the request	
	public static final int RESPONSE_VALUE_SUCCESS = 1;
	public static final int RESPONSE_VALUE_FAIL = 0;
	
	public ServerRequests(){}
	
	/**
	 * This function given a JSON object representing a request and the context
	 * of the activity that sent it, send and validate the request to the server
	 * and returns a response if applicable.
	 * @param i_Request a JSONObject representing the body of a request 
	 * @param i_Context the context of the activity that sent the request
	 * 
	 * @return a JSONObject representing the response or null otherwise
	 */
	public static JSONObject sendRequestToServer(JSONObject i_Request, 
			Context i_Context) {
		m_Context = i_Context;
		JSONObject response = null;

		if(i_Request != null) {
			try {
				ServerRequests task = new ServerRequests();
				response = task.new ServerRequestTask().execute(i_Request).get();
			} catch (Exception e) {
				// An error related to the async task occured
				e.printStackTrace();
				response = null;
			}
			
			// Check if login was successful
			if(isValidResponse(response) == false) {
				handleErrorResponse(response);
			}
		}
		
		return response;
	}
	
	/**
	 * This function checks if a request is valid and was successful
	 * @param i_Request a JSONObject representing the JSON request
	 * @throws NetworkErrorException if no network connection available
	 * */
	public static boolean isValidResponse(JSONObject i_Response) {
		boolean isValid = false;
		if(i_Response != null) {
			try {
				isValid = i_Response.getInt(RESPONSE_FIELD_SUCCESS) == 
						RESPONSE_VALUE_SUCCESS;
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return isValid;
	}
	
	/**
	 * A helper function that handles communication errors whether it with the 
	 * network or an error from the server
	 * @param i_Response the response that the server returned
	 */
	private static void handleErrorResponse(JSONObject i_Response) {
		String title = null;
		String message;
		if(i_Response == null) {
			title = "Network Problem:";
			message = "Can't connect to server.\n" +
					"Please check your network connection and try again.";
		}
		else if(i_Response.isNull(ServerRequests.RESPONSE_FIELD_SUCCESS)) {
			title = "Server Problem:";
			message = "Can't connect to server.\n" + 
					"Please try again in a few seconds.";
		}
		else {
			message = i_Response.optString(
					ServerRequests.RESPONSE_FIELD_ERROR_MESSAGE);
		}
		
		buildErrorDialog(title, message).show();
	}
	
	/**
	 * A helper function that returns a custom alert dialog with a given title
	 * and message
	 * @param title the title of the alert window
	 * @param message the message of the alert window
	 * 
	 * @return a customized alert dialog
	 */
	private static AlertDialog buildErrorDialog(String title, String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(m_Context)
		.setTitle(title)
		.setMessage(message)
		.setNeutralButton("Close", null);
		
		return alert.create();
	}
	
	/* This method will create an asynchronic POST request to a server and
	 * return the JSON response of the server   
	 */
	public static JSONObject sendJsonPostRequest(JSONObject json) 
			throws NetworkErrorException
	{
		if(NetworkUtils.isNetworkAvailable()) {
			HttpClient httpclient = new DefaultHttpClient();
			
			// Prepare a request object
			HttpPost httpPost = new HttpPost(k_ServerUrl);
			
			String requestBody = json.toString();
			
			// Set the POST request entity (body)
			StringEntity stringEntity;

			try {
				stringEntity = new StringEntity(requestBody);
				httpPost.setEntity(stringEntity);
			} catch (UnsupportedEncodingException e1) {
			}
			
			//sets a request header so the page receiving the request
			//will know what to do with it
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			
			// Execute the request
			HttpResponse response = null;
			String responseBody = "";
			try {
				response = httpclient.execute(httpPost);
				
				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				
				// If the response does not enclose an entity, there is no need
				// to worry about connection release
				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					responseBody = convertStreamToString(instream);

					json = new JSONObject(responseBody);
					// Closing the input stream will trigger connection release
					instream.close();
				}
				
			} catch (ClientProtocolException e) {
				json = null;
			} catch (IOException e) {
				e.printStackTrace();
				json = null;
			} catch (JSONException e) {
				json = new JSONObject();
			}
			
			return json;
		}
		else {
			throw new NetworkErrorException();
		}
	}
	
	/**
	 * This is a helper function used to convert an input stream into a string
	 * @param i_InputStream an input stream
	 * @return a string representing the data inside the input stream
	 */
	private static String convertStreamToString(InputStream i_InputStream) {
		/*
		 * To convert the InputStream to String we use the 
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. 
		 * Each line will appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(i_InputStream));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				i_InputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * A Helper class that does the asynchronic task of communication with the
	 * server
	 */
	private class ServerRequestTask 
			extends AsyncTask<JSONObject, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			// This will init the progress bar of the request
			m_ProgressDialog = new ProgressDialog(m_Context);
			m_ProgressDialog.setCancelable(false);
			m_ProgressDialog.setIndeterminate(true);
			m_ProgressDialog.show();
//			m_ProgressDialog = ProgressDialog.show(m_Context, "", "");
		}
		
		@Override
		protected JSONObject doInBackground(JSONObject... credentials) {
	    	try {
				return sendJsonPostRequest(credentials[0]);
			} 
	    	catch (NetworkErrorException e) {
				return null;
			}
	    }
		
       @Override
        protected void onPostExecute(JSONObject result) {
    	   // Dismiss the progress bar once the async task is done
    	   m_ProgressDialog.dismiss();
        }
	}
}
