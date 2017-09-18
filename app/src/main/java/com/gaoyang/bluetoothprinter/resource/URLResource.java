package com.gaoyang.bluetoothprinter.resource;

/**
 * Project: CallQueue
 * Author: GaoYang
 * Date: 2017/9/14 0014
 */

public class URLResource {

    private static final String DOMIN = "http://call-number.wuchengjun.cn/api/device/";
    private static final String SOCKET_DOMIN = "http://call-number.wuchengjun.cn";

    public static String getHomeListUrl() {
        return DOMIN + "business";
    }

    public static String getBindDeviceUrl() {
        return DOMIN + "bind";
    }

    public static String getOrderUrl(){
        return DOMIN + "bespeak";
    }
}
