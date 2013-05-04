package il.ac.idc.milab.soundscape.library;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

	private JSONHelper() {
	};

	public static HashMap<String, String> getMapFromJson(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		Iterator keys = object.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				map.put(key, object.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return map;
	}

	public static Object getJsonFromMap(Object object) throws JSONException {
		if (object instanceof Map) {
			JSONObject json = new JSONObject();
			Map map = (Map) object;
			for (Object key : map.keySet()) {
				json.put(key.toString(), map.get(key));
			}
			return json;
		} else if (object instanceof Iterable) {
			JSONArray json = new JSONArray();
			for (Object value : ((Iterable) object)) {
				json.put(value);
			}
			return json;
		} else {
			return object;
		}
	}
}
