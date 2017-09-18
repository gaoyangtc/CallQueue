package com.gaoyang.bluetoothprinter.application;

import android.app.Application;

/**
 * Project: CallQueue
 * Author: GaoYang
 * Date: 2017/9/14 0014
 */

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
