package il.ac.idc.milab.soundscape.library;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
 
public class JsonHandler {
 
    // JSON request tags
    private static String k_JsonKeyTag = "tag";
    private static String k_JsonKeyToken = "token";
    private static String k_JsonKeyName = "name";
    private static String k_JsonKeyEmail = "email";
    private static String k_JsonKeyPassword = "password";
    
    private static String k_LoginTag = "login";
    private static String k_RegisterTag = "register";
 
    /**
     * This function is responsible for creating the Login Request JSON object
     * @param i_Email the email of the user
     * @param i_Password the password of the user
     * */
    public static JSONObject requestLoginUser(String i_Email, String i_Password){
        // Building Parameters
    	JSONObject json = new JSONObject();
    	try {
	    	json.put(k_JsonKeyTag, k_LoginTag);
	    	json.put(k_JsonKeyEmail, i_Email);
	    	json.put(k_JsonKeyPassword, i_Password);
    	}
    	catch(JSONException e) {
    		Log.e("LoginUser", "Got an unexpected error.");
    	}

        return json;
    }
 
    /**
     * function make Login Request
     * @param i_Name the name of the user
     * @param i_Email the email address of the user
     * @param i_Password the password of the user
     * */
    public static JSONObject requestRegisterUser(String i_Name, String i_Email, String i_Password){
    	JSONObject json = new JSONObject();
    	try {
    		json.put(k_JsonKeyTag, k_RegisterTag);
    		json.put(k_JsonKeyName, i_Name);
    		json.put(k_JsonKeyEmail, i_Email);
    		json.put(k_JsonKeyPassword, i_Password);
    	}
    	catch(JSONException e) {
    		Log.e("RegisterUser", "Got an unexpected error.");
    	}

        return json;
    }
    
    /**
     * function make Login Request
     * @param i_Token the token used for this session
     * */
    public static JSONObject requestMainMenu(String i_Token){
    	JSONObject json = new JSONObject();
    	try {
    		json.put(k_JsonKeyTag, k_JsonKeyToken);
    	}
    	catch(JSONException e) {
    		Log.e("MainMenu", "Got an unexpected error.");
    	}

        return json;
    }
}