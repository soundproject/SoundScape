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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	public static final boolean DEBUG_MODE = false;
	
	public static ServerRequests serverRequests;
	public static NetworkInfo activeNetworkInfo;

	//public static final String k_ServerUrl = "http://soundscape.hostzi.com/index.php";
	//public static final String k_ServerUrl = "http://10.0.0.11/miLab/index.php";
	public static final String k_ServerUrl = "http://soundscape.milab.idc.ac.il/index.php";

	private NetworkUtils() {}

	/* This method will create an asynchronic POST request to a server and
	 * return the JSON response of the server   
	 */
	public static JSONObject sendJsonPostRequest(JSONObject json) throws NetworkErrorException
	{
		if(isNetworkAvailable()) {
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

	public static void init(ConnectivityManager connectivityManager) {
		activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		 serverRequests = new ServerRequests();

	}
	
	private static boolean isNetworkAvailable() {
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}