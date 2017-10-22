package cn.rlstech.callnumber.net;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

/**
 * 接口请求信息
 * Created by huangYx on 2016/11/4.
 */
public class RequestInfo {

    private String url;
    private int method;
    private HashMap<String, String> params;

    public RequestInfo(int method, String url) {
        this.method = method;
        this.url = url;
    }

    public String getUrl() {
        if (method == Request.Method.GET) {
            if (params != null && params.size() > 0) {
                StringBuilder builder = new StringBuilder(url);
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    if (builder.indexOf("?") != -1) {
                        builder.append("&");
                    } else {
                        builder.append("?");
                    }
                    try {
                        builder.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return builder.toString();
            }
        } else if (method == Request.Method.POST) {
            if (params == null) {
                params = new HashMap<String, String>();
            }
            if (url.contains("?")) {
                Uri uri = Uri.parse(url);
                Set<String> names = uri.getQueryParameterNames();
                if (names != null && !names.isEmpty()) {
                    for (String s : names) {
                        params.put(s, uri.getQueryParameter(s));
                    }
                }
                url = url.substring(0, url.indexOf("?"));
            }
        }
        return url;
    }


    public int getMethod() {
        return method;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void addParam(String key, String value) {
        if (method == Request.Method.GET) {
            if (!TextUtils.isEmpty(key) && value != null) {
                Uri uri = Uri.parse(getUrl());
                Set<String> names = uri.getQueryParameterNames();
                if (names != null && !names.isEmpty()) {
                    if (!names.contains(key)) {
                        if (url.contains("?")) {
                            if (url.endsWith("?") || url.endsWith("&")) {
                                url += key + "=" + value;
                            } else {
                                url += "&" + key + "=" + value;
                            }
                        } else {
                            url += "?" + key + "=" + value;
                        }
                    }
                }
            }
        } else if (method == Request.Method.POST) {
            if (params == null) {
                params = new HashMap<String, String>();
            }
            params.put(key, value);
        }
    }
}
