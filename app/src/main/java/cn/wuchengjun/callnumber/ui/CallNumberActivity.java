package cn.wuchengjun.callnumber.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wuchengjun.callnumber.R;
import cn.wuchengjun.callnumber.adapter.BaseListAdapter;
import cn.wuchengjun.callnumber.adapter.BusinessAdapter;
import cn.wuchengjun.callnumber.module.BusinessInfo;
import cn.wuchengjun.callnumber.resource.URLResource;
import cn.wuchengjun.callnumber.utils.AndroidUtil;
import cn.wuchengjun.callnumber.utils.JsonHelper;
import cn.wuchengjun.callnumber.utils.MD5Util;
import cn.wuchengjun.callnumber.utils.PrintUtil;
import cn.wuchengjun.callnumber.utils.TextToSpeechUtils;
import cn.wuchengjun.callnumber.utils.ToastUtil;
import cn.wuchengjun.callnumber.utils.ZXingUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.wuchengjun.callnumber.application.GlobalApp;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallNumberActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String APP_KEY = "z9p#5f02xcmw!drVxQ^uL6ojB&";
    private final static int TASK_TYPE_PRINT = 2;

    private BluetoothDevice mBluetoothDevice;

    private ViewPager mCallNumberPager;
    private BusinessAdapter mBusinessAdapter;
    private List<GridAdapter> mBusinessInfos;

    private LinearLayout mPointContainer;

    private Socket mSocket;

    private TextView mIMEIName;
    private com.dalong.marqueeview.MarqueeView mNotice;
    private int mTotalPage;
    private ImageView[] mPointImg;
    private Bitmap mBitmap;
    private String mDeviceID;

    private SharedPreferences mSharedPreferences;

    private String mIMEI;
    private String login;
    private ArrayList mPrintList;
    private static ProgressDialog mProgressDialogCall;
    private TextToSpeechUtils mTTSUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_call_number);

//        mIMEI = AndroidUtil.getIMEI();
        mIMEI = "123";
        login = "123";
        mPrintList = new ArrayList();
        mTTSUtils = TextToSpeechUtils.getInstance(CallNumberActivity.this);
        initView();


    }

    private void initSocket() {
        if (TextUtils.isEmpty(mDeviceID)) {
            return;
        }
        try {
            mSocket = IO.socket("http://call-number.wuchengjun.cn:2120");
            mSocket.on("connect", onConnect);
            mSocket.on("new_msg", onNewMessage);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mIMEIName = (TextView) findViewById(R.id.act_call_number_device_name);
        mNotice = (com.dalong.marqueeview.MarqueeView) findViewById(R.id.act_call_number_notice);
//        mNotice.setText("叫号通知");
        mNotice.startScroll();

        mSharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        mDeviceID = mSharedPreferences.getString("deviceId", "");
        if (TextUtils.isEmpty(mDeviceID)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear();
            editor.commit();
            bindDevice();
        } else {
            mIMEIName.setText(mIMEI);
            initSocket();
        }

        mCallNumberPager = (ViewPager) findViewById(R.id.act_call_number_pager);
        mBusinessAdapter = new BusinessAdapter();
        mCallNumberPager.setAdapter(mBusinessAdapter);
        mBusinessInfos = new ArrayList<>();

        mCallNumberPager = (ViewPager) findViewById(R.id.act_call_number_pager);
        mCallNumberPager.setOnPageChangeListener(this);
        mPointContainer = (LinearLayout) findViewById(R.id.act_call_number_point_layout);

        mBluetoothDevice = getIntent().getParcelableExtra("BluetoothInfo");

        getHomeList();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show("socket连接成功, 当前设备ID="+mDeviceID+", IMEI="+mIMEI);
//                    mSocket.emit("login", mDeviceID);
                    mSocket.emit("login", login);
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    JSONObject json = null;
                    try {
                        json = new JSONObject(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String voice = JsonHelper.getString(json, "voice");
                    mNotice.setText(voice);
                    mTTSUtils.speakText(voice);
                }
            });
        }
    };

    @Override
    public void onConnected(BluetoothSocket socket, int taskType) {
        switch (taskType) {
            case TASK_TYPE_PRINT:
                PrintUtil.printTest(socket, mBitmap, mPrintList);
                break;

        }
    }

    private void connectDevice(int taskType) {
        if (mBluetoothDevice != null) {
            super.connectDevice(mBluetoothDevice, taskType);
        } else {
            ToastUtil.show("蓝牙设备异常");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_call_number_device_name:
                connectDevice(TASK_TYPE_PRINT);
                break;
        }
    }

    private void notifyCommonUI(List<BusinessInfo> businessInfos, boolean result) {
        System.out.println("businessInfos="+businessInfos);
        if (businessInfos == null) {
            ToastUtil.show("数据异常, 请检查网络");
            System.out.println("数据异常, 请检查网络 businessInfos="+businessInfos;
            return;
        }
        // 计算礼物分页页数
        if (businessInfos.size() % 8 == 0) {
            mTotalPage = businessInfos.size() / 8;
        } else {
            mTotalPage = businessInfos.size() / 8 + 1;
        }

        mPointContainer.removeAllViews();
        if (mTotalPage > 1) {
            mPointImg = new ImageView[mTotalPage];
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(AndroidUtil.dip2px(this, 10),
                    AndroidUtil.dip2px(this, 10));
            lp.rightMargin = AndroidUtil.dip2px(this, 5);
            lp.leftMargin = AndroidUtil.dip2px(this, 5);
            for (int i = 0; i < mTotalPage; i++) {
                mPointImg[i] = new ImageView(this);
                if (i == 0) {
                    mPointImg[i].setImageResource(R.mipmap.point_select);
                } else {
                    mPointImg[i].setImageResource(R.mipmap.point_normal);
                }
                mPointImg[i].setScaleType(ImageView.ScaleType.FIT_XY);
                mPointContainer.addView(mPointImg[i], lp);
            }
            mPointContainer.setVisibility(View.VISIBLE);
        } else {
            mPointContainer.setVisibility(View.GONE);
        }

        mBusinessAdapter.clear();

        GridAdapter adapter;
        for (int i = 0; i < mTotalPage; i++) {
            final GridView gridView = (GridView) View.inflate(this, R.layout.item_viewpager_layout, null);
            gridView.setNumColumns(4);
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            adapter = new GridAdapter(this);
            gridView.setAdapter(adapter);
            int end = (i + 1) * 8;
            if (end > businessInfos.size()) {
                end = businessInfos.size();
            }
            adapter.setDatas(businessInfos.subList(i * 8, end));
            mBusinessInfos.add(adapter);
            mBusinessAdapter.addItem(gridView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mTotalPage; i++) {
            if (i == position) {
                mPointImg[i].setImageResource(R.mipmap.point_select);
            } else {
                mPointImg[i].setImageResource(R.mipmap.point_normal);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GridAdapter extends BaseListAdapter<BusinessInfo> {

        public GridAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_gridview_layout, null);
                holder = new ViewHolder();
                holder.mTitleView = (TextView) convertView.findViewById(R.id.item_grideview_title);
                holder.mContentView = (TextView) convertView.findViewById(R.id.item_grideview_content);
                holder.mCallNumberView = (TextView) convertView.findViewById(R.id.item_grideview_call_number);
                holder.mNumberView = (TextView) convertView.findViewById(R.id.item_grideview_number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BusinessInfo info = getItem(position);
            if (!TextUtils.isEmpty(info.getName())) {
                holder.mTitleView.setText(info.getName());
            }

            if (!TextUtils.isEmpty(info.getRequire())) {
                holder.mContentView.setText(info.getRequire());
            }

            if (!TextUtils.isEmpty(info.getQueueCount())) {
                holder.mNumberView.setText(info.getQueueCount());
                int num = Integer.parseInt(info.getQueueCount());
                if (num < 3) {
                    Resources resources = GlobalApp.getContext().getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.bg_gridview_number_green);
                    holder.mNumberView.setBackgroundDrawable(btnDrawable);
                }else if (num < 11) {
                    Resources resources = GlobalApp.getContext().getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.bg_gridview_number_yellow);
                    holder.mNumberView.setBackgroundDrawable(btnDrawable);
                }else {
                    Resources resources = GlobalApp.getContext().getResources();
                    Drawable btnDrawable = resources.getDrawable(R.drawable.bg_gridview_number_red);
                    holder.mNumberView.setBackgroundDrawable(btnDrawable);
                }
            }

            holder.mCallNumberView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOrder(info.id);
                    showProgressDialogCall("取号中, 请稍候...");
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mTitleView;
        public TextView mContentView;
        public TextView mCallNumberView;
        public TextView mNumberView;
    }

    protected void showProgressDialogCall(String message) {
        if (mProgressDialogCall == null) {
            mProgressDialogCall = new ProgressDialog(this);
            mProgressDialogCall.setCanceledOnTouchOutside(false);
            mProgressDialogCall.setCancelable(false);
        }
        mProgressDialogCall.setMessage(message);
        if (!mProgressDialogCall.isShowing()) {
            mProgressDialogCall.show();
        }
    }

    public static void printSuccess() {
        System.out.println("printSuccess");
        CallNumberActivity.mProgressDialogCall.dismiss();
    }
    /**
     * 获取首页业务List
     */
    public void getHomeList() {
        RequestBody body = new FormBody.Builder()
                .add("IMEI", mIMEI)
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
                final List<BusinessInfo> businessInfos;
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyCommonUI(businessInfos, true);
                                        }
                                    });
                                } else {
                                    notifyCommonUI(null, false);
                                }
                            } else {
                                notifyCommonUI(null, false);
                            }
                        } else {
                            notifyCommonUI(null, false);
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

    private void getOrder(String businessId) {
        // 设备串号
        String IMEI = mIMEI;
        // 将参数按ASCII码从小到大排序并拼串(大写字母ASCII值小于小写字母 A-Z=65-90 a-z=97-122)
        String str = "IMEI=" + IMEI + "&businessId=" + businessId + "&key=" + APP_KEY;
        MD5Util getMD5 = new MD5Util();
        // 对拼串进行MD5处理
        String md5 = getMD5.GetMD5Code(str);
        // 将MD5结果大写 赋值给signature
        String upMD5 = md5.toUpperCase();
        String signature = upMD5;

        // 请求参数为IMEI, businessId, signature
        RequestBody body = new FormBody.Builder()
                .add("IMEI", IMEI)
                .add("businessId", businessId)
                .add("signature", signature)
                .build();

        Request request = new Request.Builder()
                .url(URLResource.getOrderUrl())
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            public void onResponse(Call arg0, Response response) throws IOException {
                // TODO Auto-generated method stub
                if (response.isSuccessful()) {
                    // 请求成功
                    String str = response.body().string();
                    try {
                        JSONObject json = new JSONObject(str);
                        if (JsonHelper.getInt(json, "e") == 0) {
                            json = JsonHelper.getJsonObject(json, "d");
                            if (json != null) {
                                JSONArray siteList = JsonHelper.getJsonArray(json, "site");
                                if (siteList != null && siteList.length() > 0) {
                                    mPrintList.clear();
                                    for (int i = 0; i < siteList.length(); i++) {
                                        json = siteList.optJSONObject(i);
                                        System.out.println(i);
                                        String type = JsonHelper.getString(json, "type");
                                        String text = JsonHelper.getString(json, "text");
                                        if (type.equals("1")) {
                                            mPrintList.add(text);
                                        }else {
                                            String url = JsonHelper.getString(json, "text");
                                            String width = JsonHelper.getString(json, "width");
                                            String height = JsonHelper.getString(json, "height");
                                            mBitmap = ZXingUtils.createQRImage(url, Integer.valueOf(width), Integer.valueOf(height));
                                        }
                                    }
                                    System.out.println(mPrintList);
                                    connectDevice(TASK_TYPE_PRINT);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 请求失败
                    System.out.println("doCallNum请求失败");
                }
            }

            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
                // 请求失败
                System.out.println("doCallNumOnFailure" + arg0);
            }
        });
    }

    /**
     * 绑定设备接口
     */
    public void bindDevice() {
        // 将参数按ASCII码从小到大排序并拼串(大写字母ASCII值小于小写字母 A-Z=65-90 a-z=97-122)
        String IMEI = mIMEI;
        String str = "IMEI=" + IMEI + "&key=" + APP_KEY;
        MD5Util getMD5 = new MD5Util();
        // 对拼串进行MD5处理
        String md5 = getMD5.GetMD5Code(str);
        // 将MD5结果大写 赋值给signature
        String upMD5 = md5.toUpperCase();
        String signature = upMD5

        RequestBody body = new FormBody.Builder()
                .add("IMEI", IMEI)
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
                    // 请求成功
                    String str = response.body().string();
                    try {
                        JSONObject json = new JSONObject(str);
                        if (JsonHelper.getInt(json, "e") == 0) {
                            json = JsonHelper.getJsonObject(json, "d");
                            if (json != null) {
                                mDeviceID = JsonHelper.getString(json, "deviceId");
                                initSocket();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIMEIName.setText(mIMEI);
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putString("deviceId", mDeviceID);
                                        editor.commit();
                                    }
                                });
                            }
                        } else if (JsonHelper.getInt(json, "e") == 10002) {
                            json = JsonHelper.getJsonObject(json, "d");
                            if (json != null) {
                                mDeviceID = JsonHelper.getString(json, "deviceId");
                                initSocket();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIMEIName.setText(mIMEI);
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();
                                        editor.putString("deviceId", mDeviceID);
                                        editor.commit();
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
}
