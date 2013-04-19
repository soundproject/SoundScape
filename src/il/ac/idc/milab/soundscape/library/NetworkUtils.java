package il.ac.idc.milab.soundscape.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;



public class NetworkUtils {

	public static final boolean DEBUG_MODE = false; 

	public static final String k_ServerUrl = "http://soundscape.hostzi.com/index.php";
	//	public static final String k_ServerUrl = "http://10.0.0.16/miLab/index.php";

	// Client JSON request keys
	public static final String k_JsonKeyTag = "tag";
	public static final String k_JsonKeyToken = "token";
	public static final String k_JsonKeyName = "name";
	public static final String k_JsonKeyEmail = "email";
	public static final String k_JsonKeyPassword = "password";
	public static final String k_JsonKeyFile = "file";
	public static final String k_JsonKeyWord = "word";
	public static final String k_JsonKeyDifficulty = "difficulty";
	public static final String k_JsonKeyAction = "action";

	// Client JSON request values
	public static final String k_JsonValueTagLogin = "login";
	public static final String k_JsonValueTagRegister = "register";
	public static final String k_JsonValueTagValidate = "validate";
	public static final String k_JsonValueTagAction = "action";
	public static final String k_JsonValueTagGet = "get";

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

	// Server Status codes
	public static final int k_FlagOn = 1;
	public static final int k_FlagOff = 0;


	private NetworkUtils() {}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/* This method will create an asynchronic POST request to a server and
	 * return the JSON response of the server   
	 */
	public static JSONObject sendJsonPostRequest(JSONObject json)
	{
		Log.d("NETWORK", "Sending JSON to Server: " + json.toString());
		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpPost httpPost = new HttpPost(k_ServerUrl);

		String requestBody = json.toString();
		Log.d("NETWORK", "The request body is: " + requestBody);
		// Set the POST request entity (body)
		StringEntity stringEntity;

		try {
			stringEntity = new StringEntity(requestBody);
			httpPost.setEntity(stringEntity);
		} catch (UnsupportedEncodingException e1) {
			Log.e("NETWORK", "Malformed body in StringEntity");
		}

		//sets a request header so the page receiving the request
		//will know what to do with it
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");

		Log.d("NETWORK", "The request line is: " + httpPost.getRequestLine().toString());

		// Execute the request
		HttpResponse response = null;
		String responseBody = "";
		try {
			response = httpclient.execute(httpPost);
			Log.d("NETWORK", "The response status line is: " + response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();

			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				Log.d("NETWORK", "Got an entity!");
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				responseBody = convertStreamToString(instream);
				Log.d("NETWORK", "The response body: " + responseBody);
				json = new JSONObject(responseBody);
				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			Log.d("NETWORK", "HTTP protocol Error!");
			json = null;
		} catch (IOException e) {
			Log.d("NETWORK", "The connection was aborted!");
			e.printStackTrace();
			json = null;
		} catch (JSONException e) {
			json = null;
		}

		return json;
	}

	/**
	 * This function is responsible for creating the Login Request JSON object
	 * @param i_Email the email of the user
	 * @param i_Password the password of the user
	 * */
	public static JSONObject userLogin(String i_Email, String i_Password){
		// Building Parameters
		JSONObject json = new JSONObject();
		try {
			json.put(k_JsonKeyTag, k_JsonValueTagLogin);
			json.put(k_JsonKeyEmail, i_Email);
			json.put(k_JsonKeyPassword, i_Password);
		}
		catch(JSONException e) {
			Log.e("UserLogin", "Got an unexpected error.");
			return null;
		}

		return sendJsonPostRequest(json);
	}

	/**
	 * function make Login Request
	 * @param i_Name the name of the user
	 * @param i_Email the email address of the user
	 * @param i_Password the password of the user
	 * */
	public static JSONObject userRegister(String i_Name, String i_Email, String i_Password){
		JSONObject json = new JSONObject();
		try {
			json.put(k_JsonKeyTag, k_JsonValueTagRegister);
			json.put(k_JsonKeyName, i_Name);
			json.put(k_JsonKeyEmail, i_Email);
			json.put(k_JsonKeyPassword, i_Password);
		}
		catch(JSONException e) {
			Log.e("UserRegister", "Got an unexpected error.");
			return null;
		}

		return sendJsonPostRequest(json);
	}

	/**
	 * function make Login Request
	 * @param i_Token the token used for this session
	 * */
	public static JSONObject MainMenu(String i_Token){
		JSONObject json = new JSONObject();
		try {
			json.put(k_JsonKeyTag, k_JsonKeyToken);
		}
		catch(JSONException e) {
			Log.e("MainMenu", "Got an unexpected error.");
		}

		return sendJsonPostRequest(json);
	}

	/**
	 * Gets list of words from server
	 * @return response containing list of words as JSONobject mapped to "words"
	 */
	public static JSONObject getWords() {

		JSONObject result = new JSONObject();
		//		result.put(k_JsonKeyTag, k_JsonValueTagGetWords);
		JSONObject words = new JSONObject();
		try {
			JSONObject easy = new JSONObject();
			easy.put("0", "Cat");
			words.put("1", easy);
			
			JSONObject medium = new JSONObject();
			medium.put("0", "Dog");
			medium.put("1", "Laughter");
			words.put("2", medium);
			
			JSONObject hard = new JSONObject();
			hard.put("0", "Train");
			hard.put("1", "Silence");
			words.put("3", hard);
			result.put("words", words);
		} catch (JSONException e) {
			return null;
		}

		return result;
	}

	public static JSONObject checkToken(String token, String email) {
		JSONObject json = new JSONObject();
		try {
			json.put(k_JsonKeyTag, k_JsonValueTagValidate);
			json.put(k_JsonValueTagAction, k_JsonKeyToken);
			json.put(k_JsonKeyToken, token);
			json.put(k_JsonKeyEmail, email);
		}
		catch(JSONException e) {
			Log.d("NETWORK", "Got an unexpected error.");
			return null;
		}

		return json;
	}

	public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static JSONObject getUserGameList(String email) {
		JSONObject json = new JSONObject();
		try {
			json.put(k_JsonKeyTag, "lobby");
			json.put(k_JsonKeyEmail, email);
		} catch (JSONException e) {
			Log.d("NETWORK", "Got an unexpected error.");
			return null;
		}

		return json;
	}

	public static JSONObject sendFile(String i_fileName, String i_metadata) {
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
		Log.d("SEND_FILE_DEBUG", "Sending request: " + json.toString());
		Log.d("FOR_TAL", "Length is" + fileEncoded.length());
		return sendJsonPostRequest(json);
	}

	private static byte[] encodeFile(File i_file) {

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
}