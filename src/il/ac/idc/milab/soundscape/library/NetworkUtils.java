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

import android.util.Log;
 
public class NetworkUtils {

//	public static final String k_ServerUrl = "http://soundscape.hostzi.com/index.php";
	public static final String k_ServerUrl = "http://10.0.0.4/index.php";
	
    // Client JSON request keys
	public static final String k_JsonKeyTag = "tag";
	public static final String k_JsonKeyToken = "token";
	public static final String k_JsonKeyName = "name";
	public static final String k_JsonKeyEmail = "email";
	public static final String k_JsonKeyPassword = "password";
    
    // Client JSON request values
	public static final String k_JsonValueTagLogin = "login";
	public static final String k_JsonValueTagRegister = "register";
    
    // Server JSON response keys
	public static final String k_JsonKeyError = "error";
	public static final String k_JsonKeyErrorMessage = "error_msg";
	public static final String k_JsonKeySuccess = "success";
	
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
			Log.e("sendJsonPostRequest", "Malformed body in StringEntity");
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
 
                // Closing the input stream will trigger connection release
                instream.close();
            }
 
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
		try {
			json = new JSONObject(responseBody);
		} catch (JSONException e) {
			Log.e("RESPONSE", "Malformed JSON string: " + responseBody);
		}
		
		return json;
    }
 
    /**
     * This function is responsible for creating the Login Request JSON object
     * @param i_Email the email of the user
     * @param i_Password the password of the user
     * */
    public static JSONObject UserLogin(String i_Email, String i_Password){
        // Building Parameters
    	JSONObject json = new JSONObject();
    	try {
	    	json.put(k_JsonKeyTag, k_JsonValueTagLogin);
	    	json.put(k_JsonKeyEmail, i_Email);
	    	json.put(k_JsonKeyPassword, i_Password);
    	}
    	catch(JSONException e) {
    		Log.e("UserLogin", "Got an unexpected error.");
    	}

        return sendJsonPostRequest(json);
    }
 
    /**
     * function make Login Request
     * @param i_Name the name of the user
     * @param i_Email the email address of the user
     * @param i_Password the password of the user
     * */
    public static JSONObject UserRegister(String i_Name, String i_Email, String i_Password){
    	JSONObject json = new JSONObject();
    	try {
    		json.put(k_JsonKeyTag, k_JsonValueTagRegister);
    		json.put(k_JsonKeyName, i_Name);
    		json.put(k_JsonKeyEmail, i_Email);
    		json.put(k_JsonKeyPassword, i_Password);
    	}
    	catch(JSONException e) {
    		Log.e("UserRegister", "Got an unexpected error.");
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
		JSONObject words = new JSONObject();
		try {
			words.put("1", "Cat");
			words.put("2", "Dog");
			words.put("3", "Laughter");
			words.put("4", "Train");
			result.put("words", words);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("WORDS_DEBUG", result.toString());
		return result;
	}
}