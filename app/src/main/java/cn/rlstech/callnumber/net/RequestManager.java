package cn.rlstech.callnumber.net;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 网络请求管理类
 * Created by huangYx on 2016/11/3.
 */
public class RequestManager {

    private static RequestManager sInstance;

    synchronized public static RequestManager instance() {
        if (sInstance == null) {
            sInstance = new RequestManager();
        }
        return sInstance;
    }

    private final Map<String, RequestQueue> mRequests;

    private RequestManager() {
        mRequests = new HashMap<String, RequestQueue>();
    }

    public void request(Context context, Request request) {
        getRequestQueue(context).add(request);
    }

    public void request(RequestQueue queue, Request request){
        queue.add(request);
    }

    public void clear(Context context) {
        synchronized (mRequests) {
            String key = String.valueOf(context.hashCode());
            if (mRequests.containsKey(key)) {
                RequestQueue queue = mRequests.remove(key);
                queue.stop();
            }
        }
    }

    public void clear(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        synchronized (mRequests) {
            Set<String> keys = mRequests.keySet();
            RequestQueue queue;
            for (String key : keys) {
                if (mRequests.containsKey(key)) {
                    queue = mRequests.get(key);
                    queue.cancelAll(tag);
                }
            }
        }
    }

    private RequestQueue getRequestQueue(Context context) {
        synchronized (mRequests) {
            RequestQueue queue;
            String key = String.valueOf(context.hashCode());
            if (mRequests.containsKey(key)) {
                queue = mRequests.get(key);
            } else {
                queue = Volley.newRequestQueue(context);
                mRequests.put(key, queue);
            }
            return queue;
        }
    }

    public interface RequestListener {
        void onResponse(String data, RequestError error);
    }
}
