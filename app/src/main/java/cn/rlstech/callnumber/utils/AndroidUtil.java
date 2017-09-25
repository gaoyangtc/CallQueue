package cn.rlstech.callnumber.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import cn.rlstech.callnumber.application.GlobalApp;

/**
 * Project: CallQueue
 * Author: GaoYang
 * Date: 2017/9/14 0014
 */

public class AndroidUtil {

    public enum NetType {
        WIFI, _2G, _3G, _4G, NO_NET, UNKNOWN, ETHERNET
    }

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

    /**
     * 判断网络类型
     *
     * @return NetType
     */
    public static NetType getNetworkType() {
        NetType netType = NetType.NO_NET;
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) GlobalApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    netType = NetType.WIFI;
                } else if (type == ConnectivityManager.TYPE_ETHERNET) {
                    netType = NetType.ETHERNET;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String subTypeName = networkInfo.getSubtypeName();
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            netType = NetType._2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            netType = NetType._3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            netType = NetType._4G;
                            break;
                        default:
                            //中国移动、联通、电信三种3G制式
                            if ("TD-SCDMA".equalsIgnoreCase(subTypeName) || "WCDMA".equalsIgnoreCase(subTypeName)
                                    || "CDMA2000".equalsIgnoreCase(subTypeName)) {
                                netType = NetType._3G;
                            } else if ("FDD-LTE".equalsIgnoreCase(subTypeName)) {
                                netType = NetType._4G;
                            } else {
                                netType = NetType.UNKNOWN;
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return netType;
    }

    public static void toWIFISetting(Activity activity) {
        Intent intent;
        /**
         * 判断手机系统的版本！如果API大于10 就是3.0+
         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
         */
        if (Build.VERSION.SDK_INT > 10) {
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        activity.startActivity(intent);
    }
}
