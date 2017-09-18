package com.gaoyang.bluetoothprinter.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaoyang.bluetoothprinter.R;
import com.gaoyang.bluetoothprinter.adapter.BaseListAdapter;
import com.gaoyang.bluetoothprinter.adapter.BusinessAdapter;
import com.gaoyang.bluetoothprinter.module.BusinessInfo;
import com.gaoyang.bluetoothprinter.resource.URLResource;
import com.gaoyang.bluetoothprinter.utils.AndroidUtil;
import com.gaoyang.bluetoothprinter.utils.JsonHelper;
import com.gaoyang.bluetoothprinter.utils.MD5Util;
import com.gaoyang.bluetoothprinter.utils.PrintUtil;
import com.gaoyang.bluetoothprinter.utils.ToastUtil;

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

public class CallNumberActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final static int TASK_TYPE_PRINT = 2;

    private BluetoothDevice mBluetoothDevice;

    private ViewPager mCallNumberPager;
    private BusinessAdapter mBusinessAdapter;
    private List<GridAdapter> mBusinessInfos;

    private LinearLayout mPointContainer;

    private TextView mBluetoothName;
    private int mTotalPage;
    private ImageView[] mPointImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_call_number);

        initView();
    }

    private void initView() {
        mCallNumberPager = (ViewPager) findViewById(R.id.act_call_number_pager);
        mBusinessAdapter = new BusinessAdapter();
        mCallNumberPager.setAdapter(mBusinessAdapter);
        mBusinessInfos = new ArrayList<>();

        mBluetoothName = (TextView) findViewById(R.id.act_call_number_device_name);
        mBluetoothName.setOnClickListener(this);

        mCallNumberPager = (ViewPager) findViewById(R.id.act_call_number_pager);
        mCallNumberPager.setOnPageChangeListener(this);
        mPointContainer = (LinearLayout) findViewById(R.id.act_call_number_point_layout);

        mBluetoothDevice = getIntent().getParcelableExtra("BluetoothInfo");
        if (mBluetoothDevice != null) {
            mBluetoothName.setText(mBluetoothDevice.getName());
        }

//        HomeListManager.getInstance().bindDevice(this);
        getHomeList();
    }

    @Override
    public void onConnected(BluetoothSocket socket, int taskType) {
        switch (taskType) {
            case TASK_TYPE_PRINT:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_image);
                PrintUtil.printTest(socket, bitmap);
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
            lp.rightMargin = AndroidUtil.dip2px(this, 2);
            lp.leftMargin = AndroidUtil.dip2px(this, 2);
            for (int i = 0; i < mTotalPage; i++) {
                mPointImg[i] = new ImageView(this);
                if (i == 0) {
                    mPointImg[i].setImageResource(R.mipmap.icon_pager_checked);
                } else {
                    mPointImg[i].setImageResource(R.mipmap.icon_pager);
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
                mPointImg[i].setImageResource(R.mipmap.icon_pager_checked);
            } else {
                mPointImg[i].setImageResource(R.mipmap.icon_pager);
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
            }

            holder.mCallNumberView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOrder(info.id);
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
        String IMEI = "123";
        // 加密用的key
        String key = "z9p#5f02xcmw!drVxQ^uL6ojB&";

        // 将参数按ASCII码从小到大排序并拼串(大写字母ASCII值小于小写字母 A-Z=65-90 a-z=97-122)
        String str = "IMEI=" + IMEI + "&businessId=" + businessId + "&key=" + key;
        MD5Util getMD5 = new MD5Util();
        // 对拼串进行MD5处理
        String md5 = getMD5.GetMD5Code(str);
        // 将MD5结果大写 赋值给signature
        String upMD5 = md5.toUpperCase();
        String signature = upMD5;
        System.out.println("signature=" + signature);

        //  请求参数为IMEI, businessId, signature
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
                    System.out.println("doCallNum请求成功" + str);

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
}