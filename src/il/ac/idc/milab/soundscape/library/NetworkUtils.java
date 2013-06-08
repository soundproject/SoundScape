package il.ac.idc.milab.soundscape.library;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class represents the network utilities
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class NetworkUtils {
	
	public static ConnectivityManager m_ConnectivityManager;
	public static NetworkInfo m_ActiveNetworkInfo;

	private NetworkUtils() {}

	/**
	 * This function inits the network utils with the device's connectivity
	 * manager
	 * @param i_ConnectivityManager the connectivity manager of the device
	 */
	public static void init(ConnectivityManager i_ConnectivityManager) {
		m_ConnectivityManager = i_ConnectivityManager;
		m_ActiveNetworkInfo = i_ConnectivityManager.getActiveNetworkInfo();
	}
	
	/**
	 * This functions checks if network is available on the device
	 * @return true if network is available, false otherwise
	 */
	public static boolean isNetworkAvailable() {
		m_ActiveNetworkInfo = m_ConnectivityManager.getActiveNetworkInfo(); 
		return m_ActiveNetworkInfo != null && m_ActiveNetworkInfo.isConnected();
	}
}