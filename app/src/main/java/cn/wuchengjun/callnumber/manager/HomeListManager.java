package cn.wuchengjun.callnumber.manager;

import android.content.Context;

import cn.wuchengjun.callnumber.module.BusinessInfo;
import cn.wuchengjun.callnumber.resource.URLResource;
import cn.wuchengjun.callnumber.utils.JsonHelper;
import cn.wuchengjun.callnumber.utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Project: CallQueue
 * Author: GaoYang
 * Date: 2017/9/14 0014
 */

public class HomeListManager extends BaseManager<HomeListManager.HomeListListener> {

    private static final String APP_KEY = "z9p#5f02xcmw!drVxQ^uL6ojB&";
    private static HomeListManager mInstance;

    private HomeListManager() {
        super();
    }

    synchronized public static HomeListManager getInstance() {
        if (mInstance == null) {
            return new HomeListManager();
        }
        return mInstance;
    }

    /**
     * 获取首页业务List
     */
    public void getHomeList() {
        RequestBody body = new FormBody.Builder()
                .add("IMEI", "123")
                .build();

        Request request = new Request.Builder()
                .url(URLResource.getHomeListUrl())
                .post(body).build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                System.out.println("doRequestOnFailure" + call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<BusinessInfo> businessInfos;
                if (response.isSuccessful()) { // 请求成功
                    String str = response.body().string();
                    try {
                        JSONObject json = new JSONObject(str);
                        if (JsonHelper.getInt(json, "e") == 0) {
                            json = JsonHelper.getJsonObject(json, "d");
                            if (json != null) {
                                JSONArray businessList = JsonHelper.getJsonArray(json, "businessList");
                                if (businessList != null && businessList.length() > 0) {
                                    businessInfos = new ArrayList<BusinessInfo>();
                                    BusinessInfo businessInfo;
                                    for (int i = 0; i < businessList.length(); i++) {
                                        json = businessList.optJSONObject(i);
                                        businessInfo = new BusinessInfo();
                                        businessInfo.setId(JsonHelper.getString(json, "id"));
                                        businessInfo.setName(JsonHelper.getString(json, "name"));
                                        businessInfo.setRequire(JsonHelper.getString(json, "require"));
                                        businessInfo.setType(JsonHelper.getString(json, "type"));
                                        businessInfo.setOffice_id(JsonHelper.getString(json, "office_id"));
                                        businessInfo.setCollege_id(JsonHelper.getString(json, "college_id"));
                                        businessInfo.setHall_id(JsonHelper.getString(json, "hall_id"));
                                        businessInfo.setCreatetime(JsonHelper.getString(json, "createtime"));
                                        businessInfo.setQueueCount(JsonHelper.getString(json, "queueCount"));
                                        businessInfos.add(businessInfo);
                                    }

                                    fireGotBusiness(businessInfos, true);
                                }else {
                                    fireGotBusiness(null, false);
                                }
                            }else {
                                fireGotBusiness(null, false);
                            }
                        }else {
                            fireGotBusiness(null, false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else { // 请求失败
                    System.out.println("doRequest请求失败");
                }
            }
        });
    }

    /**
     * 遍历业务
     *
     * @param businessInfos
     * @param result
     */
    private void fireGotBusiness(List<BusinessInfo> businessInfos, boolean result) {
        synchronized (mListeners) {
            for (HomeListListener l : mListeners) {
                l.onTraverseBusiness(businessInfos, result);
            }
        }
    }

    /**
     * 绑定设备接口
     *
     * @param context
     */
    public void bindDevice(Context context) {
        // 将参数按ASCII码从小到大排序并拼串(大写字母ASCII值小于小写字母 A-Z=65-90 a-z=97-122)
        String str = "IMEI=" + "123" + "&key=" + APP_KEY;
        MD5Util getMD5 = new MD5Util();
        // 对拼串进行MD5处理
        String md5 = MD5Util.GetMD5Code(str);
        // 将MD5结果大写 赋值给signature
        String upMD5 = md5.toUpperCase();
        String signature = upMD5;

        RequestBody body = new FormBody.Builder()
                .add("IMEI", "123")
                .add("signature", signature)
                .build();

        Request request = new Request.Builder()
                .url(URLResource.getBindDeviceUrl())
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call arg0, Response response) throws IOException {
                if (response.isSuccessful()) { // 请求成功
                    String str = response.body().string();
                    System.out.println("bindDevice请求成功" + str);
                } else { // 请求失败
                    System.out.println("bindDevice请求失败");
                }
            }

            public void onFailure(Call arg0, IOException arg1) {
                // 请求失败
                System.out.println("bindDeviceOnFailure" + arg0);
            }
        });
    }

    public interface HomeListListener {
        void onTraverseBusiness(List<BusinessInfo> businessInfos, boolean result);
    }
}
