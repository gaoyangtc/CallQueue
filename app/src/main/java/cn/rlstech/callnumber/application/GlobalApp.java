package cn.rlstech.callnumber.application;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.rlstech.callnumber.utils.CrashHandler;


public class GlobalApp extends Application {

    public static final String EXIT_ACTION = "com.iwanvi.wwlive.EXIT";

    private static GlobalApp sInstance;

    private ExitReceiver mExitReceiver;

    private static Vector<Activity> sActivities = new Vector<Activity>();

    public static GlobalApp getContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        mExitReceiver = new ExitReceiver();
        registerReceiver(mExitReceiver, new IntentFilter(EXIT_ACTION));

        CrashHandler.getInstance().init(getApplicationContext());
    }

    synchronized public void onActivityCreate(Activity activity) {
        if (activity != null) {
            sActivities.add(activity);
        }
    }

    synchronized public void onActivityDestroy(Activity activity) {
        if (activity != null) {
            sActivities.remove(activity);
        }
    }

    public List<Activity> getActivities() {
        return new ArrayList<Activity>(sActivities);
    }

    synchronized public void exitApp() {
        sendBroadcast(new Intent(EXIT_ACTION));
        killApp();
    }

    private void killApp() {
        unregisterReceiver(mExitReceiver);
        if (!sActivities.isEmpty()) {
            List<Activity> tmp = new ArrayList<Activity>();
            tmp.addAll(sActivities);
            for (Activity a : tmp) {
                a.finish();
            }
            tmp.clear();
            tmp = null;
        }
        System.gc();
        System.gc();
        System.gc();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    class ExitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            killApp();
        }
    }
}
