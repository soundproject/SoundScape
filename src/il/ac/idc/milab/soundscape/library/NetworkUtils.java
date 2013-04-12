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

	public static final String k_ServerUrl = "http://soundscape.hostzi.com/index.php";
	
	public NetworkUtils() {}
 
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
        
        String body = json.toString();
        
        // Set the POST request entity (body)
        StringEntity stringEntity;
		try {
			stringEntity = new StringEntity(body);
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
		//PostRequestTask task = new PostRequestTask().execute(httpPost);
        try {
            response = httpclient.execute(httpPost);
            // Examine the response status
            Log.i("SERVER",response.getStatusLine().toString());
 
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                Log.i("SERVER",result);
 
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
        
        String responseEntity = response.getEntity().toString();
        
		try {
			json = new JSONObject(responseEntity);
		} catch (JSONException e) {
			Log.e("JSON", responseEntity);
		}
		
		return json;
    }
    
    // JSON request keys
    private static final String k_JsonKeyTag = "tag";
    private static final String k_JsonKeyToken = "token";
    private static final String k_JsonKeyName = "name";
    private static final String k_JsonKeyEmail = "email";
    private static final String k_JsonKeyPassword = "password";
    
    // JSON request values
    private static final String k_JsonValueTagLogin = "login";
    private static final String k_JsonValueTagRegister = "register";
 
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
    /*
	private class PostRequestTask extends AsyncTask<JSONObject, Integer, HttpResponse> {
	    protected JSONObject doInBackground(JSONObject... json) {
	    	
	    }

	    protected void onProgressUpdate(Integer... progress) {
	        setProgressPercent(progress[0]);
	    }

	    protected void onPostExecute(Long result) {
	        
	    }
	}
	*/
}