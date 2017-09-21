package cn.rlstech.callnumber.application;

import android.app.Application;


public class GlobalApp extends Application {

    private static GlobalApp sInstance;

    public static GlobalApp getContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
