package il.ac.idc.milab.soundscape.library;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * This class represents the user playing this game
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class User {

	private static final String TAG = "USER";
	public static final String USER_NAME = "name";
	public static final String USER_EMAIL = "email";
	public static final String USER_PASSWORD = "password";
	public static final String USER_TOKEN = "token";
	
	private static SharedPreferences m_UserDetails;
	private static String m_Name;
	private static String m_EmailAddress;
	private static String m_Password;
	private static String m_Token;

	public User(SharedPreferences i_Preferences) {
		m_UserDetails = i_Preferences;
		m_Name = m_UserDetails.getString(USER_NAME, null);
		m_EmailAddress = m_UserDetails.getString(USER_EMAIL, null);
		m_Password = m_UserDetails.getString(USER_PASSWORD, null);
		m_Token = m_UserDetails.getString(USER_TOKEN, null);
	}
	
	/**
	 * Getter for the user's name
	 * @return the user's name
	 */
	public static String getName() {
		return m_Name;
	}
	
	/**
	 * Setter for the user's name
	 */
	public static void setName(String i_Name) {
		m_Name = i_Name;
	}
	
	/**
	 * Getter for the user's email address
	 * @return
	 */
	public static String getEmailAddress() {
		return m_EmailAddress;
	}
	
	/**
	 * Setter for the user's email address
	 */
	public static void setEmailAddress(String i_EmailAddress) {
		m_EmailAddress = i_EmailAddress;
	}
	
	/**
	 * Getter for the user's password
	 * @return the user's password
	 */
	public static String getPassword() {
		return m_Password;
	}
	
	/**
	 * Setter for the user's password
	 */
	public static void setPassword(String i_Password) {
		m_Password = i_Password;
	}
	
	/**
	 * Getter for the user's token
	 * @return the user's token
	 */
	public static String getToken() {
		return m_Token;
	}
	
	/**
	 * Setter for the user's token
	 */
	public static void setToken(String i_Token) {
		m_Token = i_Token;
	}
	
	/**
	 * Checks whether we already have the user's credentials
	 * @return true if user credentials were found, false otherwise
	 */
	public boolean isKnown() {
		return m_Token != null && m_EmailAddress != null;
	}
	
	/**
	 * Saves the user details locally to remember the user in future sessions
	 */
	public static void saveUserDetails() {
		Editor editor = m_UserDetails.edit();
		editor.putString(ServerRequests.REQUEST_FIELD_NAME, m_Name);
		editor.putString(ServerRequests.REQUEST_FIELD_EMAIL, m_EmailAddress);
		editor.putString(ServerRequests.REQUEST_FIELD_PASSWORD, m_Password);
		editor.putString(ServerRequests.REQUEST_FIELD_TOKEN, m_Token);
		
		editor.commit();
		Log.d(TAG, "Name: " + m_Name);
		Log.d(TAG, "Email: " + m_EmailAddress);
		Log.d(TAG, "Password: " + m_Password);
		Log.d(TAG, "Token: " + m_Token);
	}
	
	/**
	 * This is a helper method responsible for checking the format of an e-mail
	 * @param i_Email a string representing an e-mail address we wish to check 
	 * @return true, if the format is valid, false otherwise
	 * TODO: if a stronger format validation will be needed, we should consider
	 * moving to regex and implement something like: 
	 * [a-zA-Z][a-zA-Z0-9-_.]{1,20}@[a-zA-Z0-9]{2,20}(\.[a-zA-Z]{2,5}){1,2}
	 */
	public static boolean validateEmailFormat(String i_Email) {
		boolean isValid = false;
		
		int index = i_Email.indexOf("@");
		
		// Checks if there is a "@" somewhere in the string
		if(index != -1 && index > 0 && index < i_Email.length()) {
			isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * This is a helper method responsible for checking the format of a password
	 * @param i_Password a string representing a password
	 * @return true, if the format is valid, false otherwise
	 * TODO: add more logic if required
	 */
	public static boolean validatePasswordFormat(String i_Password) {
		boolean isValid = false;
		
		if(i_Password.length() != 0) {
			isValid = true;
		}
		
		return isValid;
	}
}