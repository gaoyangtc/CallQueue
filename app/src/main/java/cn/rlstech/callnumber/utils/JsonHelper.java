package cn.rlstech.callnumber.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * json帮助类
 * Created by gaouang.
 */
public class JsonHelper {

    public static String getString(JSONObject json, String key) {
        if(json == null) return null;
        if (!json.isNull(key)) {
            return json.optString(key);
        }
        return null;
    }

    public static int getInt(JSONObject json, String key) {
        return getInt(json, key, 0);
    }

    public static int getInt(JSONObject json, String key, int def) {
        if(json == null) return def;
        if (!json.isNull(key)) {
            return json.optInt(key, def);
        }
        return def;
    }

    public static double getDouble(JSONObject json, String key) {
        return getDouble(json, key, 0);
    }

    public static double getDouble(JSONObject json, String key, double def) {
        if(json == null) return def;
        if (!json.isNull(key)) {
            return json.optDouble(key, def);
        }
        return def;
    }

    public static JSONArray getJsonArray(JSONObject json, String key) {
        if(json == null) return null;
        if (!json.isNull(key)) {
            return json.optJSONArray(key);
        }
        return null;
    }

    public static JSONObject getJsonObject(JSONObject json, String key) {
        if(json == null) return null;
        if (!json.isNull(key)) {
            return json.optJSONObject(key);
        }
        return null;
    }
}
