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
import android.util.Log;



public class NetworkUtils {

	public static final boolean DEBUG_MODE = false; 
	
	public static ServerRequests serverRequests;
	public static NetworkInfo activeNetworkInfo;

	public static final String k_ServerUrl = "http://soundscape.hostzi.com/index.php";
	//public static final String k_ServerUrl = "http://10.0.0.3/miLab/index.php";
//	public static final String k_ServerUrl = "http://10.0.0.149/miLab/index.php";

	private NetworkUtils() {}

	/* This method will create an asynchronic POST request to a server and
	 * return the JSON response of the server   
	 */
	public static JSONObject sendJsonPostRequest(JSONObject json) throws NetworkErrorException
	{
		if(isNetworkAvailable()) {
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
					
					/* HACK to deal with the auto generated code our web hosting is adding */
					/***********************************************************************/
					//int index = responseBody.indexOf("<!--");
					//responseBody = responseBody.substring(0, index);
					/***********************************************************************/
					/* HACK to deal with the auto generated code our web hosting is adding */
					
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