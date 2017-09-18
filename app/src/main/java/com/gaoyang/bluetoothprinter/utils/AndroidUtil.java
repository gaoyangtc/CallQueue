package com.gaoyang.bluetoothprinter.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.gaoyang.bluetoothprinter.application.GlobalApp;

/**
 * Project: CallQueue
 * Author: GaoYang
 * Date: 2017/9/14 0014
 */

public class AndroidUtil {

    /**
     * 获取手机IMEI
     *
     * @return String
     */
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) GlobalApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static Pair<Integer, Integer> getScreenSize() {
        Display display = ((WindowManager) GlobalApp.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return Pair.create(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * dp转px
     */
    public static int dip2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
