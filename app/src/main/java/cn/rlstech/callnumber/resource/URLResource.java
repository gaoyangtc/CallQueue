package cn.rlstech.callnumber.resource;


public class URLResource {

    private static final String DOMIN = "http://call-number.rlstech.cn/api/device/";
    private static final String SOCKET_DOMIN = "http://call-number.rlstech.cn";

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
