package cn.rlstech.callnumber.utils;

import android.util.Log;

/**
 * Log工具类
 * Created by huangYx on 2016/7/5.
 */
public class LogUtil {
    public static boolean sDisable = false;

    public static void setDisable(boolean disable) {
        sDisable = disable;
    }

    public static void e(String tag, String msg) {
        if (!sDisable) {
            Log.e(tag == null ? "no tag" : tag, msg == null ? "null" : msg);
        }
    }

    public static void d(String tag, String msg) {
        if (!sDisable) {
            Log.d(tag == null ? "no tag" : tag, msg == null ? "null" : msg);
        }

    }

    public static void i(String tag, String msg) {
        if (!sDisable) {
            Log.i(tag == null ? "no tag" : tag, msg == null ? "null" : msg);
        }

    }

    public static void w(String tag, String msg) {
        if (!sDisable) {
            Log.w(tag == null ? "no tag" : tag, msg == null ? "null" : msg);
        }
    }
}
