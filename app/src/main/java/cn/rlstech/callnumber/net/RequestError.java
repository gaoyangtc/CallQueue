package cn.rlstech.callnumber.net;

/**
 * 网络请求错误
 * Created by huangYx on 2016/11/3.
 */
public class RequestError {

    public static final int TIME_OUT = -1;
    public static final int NO_NET = -2;
    public static final int NET_ERROR = -3;
    public static final int SERVER_ERROR = -4;
    public static final int UNKNOWN = -5;
    public static final int TRANS_ERROR = -6;

    private int code;
    private String msg;

    public RequestError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
