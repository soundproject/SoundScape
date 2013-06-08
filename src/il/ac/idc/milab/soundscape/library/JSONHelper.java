package il.ac.idc.milab.soundscape.library;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a helper class that used to handle JSON related conversion of data
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
public class JSONHelper {

	private JSONHelper() {
	};

	/**
	 * This function given a JSONObject representing a hashmap, converts the
	 * data from json into a hashmap and returns it
	 * @param i_JsonMap the JSONObject that represents a hashmap
	 * @return a HashMap representing the data that was in JSON format
	 */
	public static HashMap<String, String> getMapFromJson(JSONObject i_JsonMap) {
		HashMap<String, String> map = new HashMap<String, String>();
		Iterator keys = i_JsonMap.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				map.put(key, i_JsonMap.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return map;
	}

	/**
	 * This function given a map, converts it into JSON format
	 * @param i_MapObject an object of type map
	 * @return a JSON object representing the the object that was given
	 * or the object itself if it isn't a JSON object
	 * @throws JSONException in case of an error.
	 */
	public static Object getJsonFromMap(Object i_MapObject) 
			throws JSONException {
		if (i_MapObject instanceof Map) {
			JSONObject json = new JSONObject();
			Map map = (Map) i_MapObject;
			for (Object key : map.keySet()) {
				json.put(key.toString(), map.get(key));
			}
			return json;
		} else if (i_MapObject instanceof Iterable) {
			JSONArray json = new JSONArray();
			for (Object value : ((Iterable) i_MapObject)) {
				json.put(value);
			}
			return json;
		} else {
			return i_MapObject;
		}
	}
}
