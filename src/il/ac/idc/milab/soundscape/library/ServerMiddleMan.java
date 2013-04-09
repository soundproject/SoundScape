package il.ac.idc.milab.soundscape.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.webkit.JsPromptResult;
 
public class ServerMiddleMan {

	private String m_ServerUrl;
	
	public ServerMiddleMan(String i_ServerUrl) {
		this.m_ServerUrl = i_ServerUrl;
	}
 
    private String convertStreamToString(InputStream is) {
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
 
    /* This is a test function which will connects to a given
     * rest service and prints it's response to Android Log with
     * labels "SERVER".
     */
    public JSONObject sendJsonPostRequest(JSONObject json)
    {
 
        HttpClient httpclient = new DefaultHttpClient();
 
        // Prepare a request object
        HttpPost httpPost = new HttpPost(m_ServerUrl);
        
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
        HttpResponse response;
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
                String result= convertStreamToString(instream);
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
        
		return json;
    }
    
    /**
     * Function get Login status
     * *
    public boolean isUserLoggedIn(){
    	JsonHandler.requestLoginUser(i_Email, i_Password)
    }
 
    /**
     * Function to logout user
     * Reset Database
     * *
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
    */
}