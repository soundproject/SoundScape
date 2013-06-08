package il.ac.idc.milab.soundscape.library;

import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogHelper {

	public AlertDialogHelper() {}
	
	/**
	 * This is a helper method to raise error messages
	 * @param i_Title the title of the error message
	 * @param i_Message the error message
	 * @return an alert dialog containing the title and message given
	 */
	public static AlertDialog buildErrorDialog(Context i_Context, 
			String i_Title, 
			String i_Message) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(i_Context)
		.setTitle(i_Title)
		.setMessage(i_Message)
		.setNeutralButton("Close", null);
		
		return alert.create();
	}
}
