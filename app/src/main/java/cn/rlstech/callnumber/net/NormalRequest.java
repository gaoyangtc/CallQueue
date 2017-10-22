package cn.rlstech.callnumber.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import cn.rlstech.callnumber.R;
import cn.rlstech.callnumber.application.GlobalApp;
import cn.rlstech.callnumber.utils.LogUtil;


/**
 * Project: trunk
 * Author: GaoYang
 */
public class NormalRequest extends StringRequest {
    private static final String TAG = NormalRequest.class.getSimpleName();
    private RequestInfo mRequest;

    public NormalRequest(final RequestInfo request, final RequestManager.RequestListener listener) {

        super(request.getMethod(), request.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String o) {
                LogUtil.i(TAG, "request:" + request.getUrl());
                LogUtil.d(TAG, "response:" + o);
                if (listener != null) {
                    listener.onResponse(o, null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (listener != null) {
                    if (error instanceof com.android.volley.TimeoutError) {
                        listener.onResponse(null, new RequestError(RequestError.TIME_OUT, getString(R.string.net_time_out)));
                    } else if (error instanceof com.android.volley.NoConnectionError) {
                        listener.onResponse(null, new RequestError(RequestError.NO_NET, getString(R.string.no_net)));
                    } else if (error instanceof com.android.volley.NetworkError) {
                        listener.onResponse(null, new RequestError(RequestError.NET_ERROR, getString(R.string.no_net)));
                    } else if (error instanceof com.android.volley.ServerError) {
                        listener.onResponse(null, new RequestError(RequestError.SERVER_ERROR, getString(R.string.net_server_error)));
                    } else {
                        listener.onResponse(null, new RequestError(RequestError.UNKNOWN, getString(R.string.unknow_error)));
                    }
                }
            }
        });
        this.mRequest = request;
    }

    private static String getString(int resId) {
        return GlobalApp.getContext().getString(resId);
    }

    /**
     * 不要用
     *
     * @param request       RequestInfo
     * @param listener      Response.Listener
     * @param errorListener Response.ErrorListener
     */
    public NormalRequest(RequestInfo request, Response.Listener listener, Response.ErrorListener errorListener) {
        super(request.getMethod(), request.getUrl(), listener, errorListener);
        this.mRequest = request;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mRequest.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // TODO: 2017/5/16 封装Map，存储埋点上报通用参数
        return super.getHeaders();
    }
}
