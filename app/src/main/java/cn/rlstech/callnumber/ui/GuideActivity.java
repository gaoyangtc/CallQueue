package cn.rlstech.callnumber.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;

import java.io.File;
import java.util.List;

import cn.rlstech.callnumber.R;
import cn.rlstech.callnumber.application.GlobalApp;
import cn.rlstech.callnumber.dialog.AppUpdateDialog;
import cn.rlstech.callnumber.dialog.ConfirmDialog;
import cn.rlstech.callnumber.manager.FileLoadManager;
import cn.rlstech.callnumber.net.NormalRequest;
import cn.rlstech.callnumber.net.RequestError;
import cn.rlstech.callnumber.net.RequestInfo;
import cn.rlstech.callnumber.net.RequestManager;
import cn.rlstech.callnumber.utils.AndroidUtil;
import cn.rlstech.callnumber.utils.BluetoothUtil;
import cn.rlstech.callnumber.utils.LogUtil;
import cn.rlstech.callnumber.utils.ToastUtil;

/**
 * 点赞Pad首页
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener, FileLoadManager.FileLoadListener {

    private TextView mBluetoothSwitchView;
    private ListView mBluetoothListView;
    private TextView mBluetoothComplete;
    private TextView mBluetoothTip;
    private TextView mBluetoothSwitchTip;

    private int mSelectedPosition = -1;

    private DeviceListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.act_guide_layout);

        GlobalApp.getContext().onActivityCreate(this);
        getAppUpdateInfo();
        FileLoadManager.getInstance().init(getApplicationContext());
        FileLoadManager.getInstance().addListener(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AndroidUtil.getNetworkType() == AndroidUtil.NetType.WIFI) {
            fillAdapter();
        } else if (AndroidUtil.getNetworkType() == AndroidUtil.NetType.NO_NET) {
            ConfirmDialog confirmDialog = ConfirmDialog.newInstance(netDialogListener, getString(R.string.finish_app),
                    getString(R.string.switch_wifi), getString(R.string.mobile_no_net));
            confirmDialog.show(this);
        } else {
            ConfirmDialog confirmDialog = ConfirmDialog.newInstance(netDialogListener, getString(R.string.finish_app),
                    getString(R.string.switch_wifi), getString(R.string.mobile_net_watch));
            confirmDialog.show(this);
        }
    }

    private void initView() {
        mBluetoothSwitchView = (TextView) findViewById(R.id.act_guide_bluetooth_switch);
        mBluetoothListView = (ListView) findViewById(R.id.act_guide_bluetooth_list);
        mBluetoothComplete = (TextView) findViewById(R.id.act_guide_bluetooth_jump);
        mBluetoothTip = (TextView) findViewById(R.id.act_guide_bluetooth_tip);
        mBluetoothSwitchTip = (TextView) findViewById(R.id.act_guide_bluetooth_switch_tip);

        mBluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mAdapter.notifyDataSetChanged();
            }
        });

        mBluetoothSwitchView.setOnClickListener(this);
        mBluetoothComplete.setOnClickListener(this);

        mAdapter = new DeviceListAdapter(this);
        mBluetoothListView.setAdapter(mAdapter);
    }

    /**
     * 从所有已配对设备中找出打印设备并显示
     */
    private void fillAdapter() {
        //推荐使用 BluetoothUtil.getPairedPrinterDevices()
        List<BluetoothDevice> printerDevices = BluetoothUtil.getPairedDevices();
        mAdapter.clear();
        mAdapter.addAll(printerDevices);
        refreshButtonText(printerDevices);
    }

    private void refreshButtonText(List<BluetoothDevice> printerDevices) {
        if (printerDevices.size() > 0) {
            mBluetoothSwitchView.setText("配对更多设备");
            mBluetoothTip.setVisibility(View.VISIBLE);
            mBluetoothSwitchTip.setVisibility(View.GONE);
            mBluetoothListView.setVisibility(View.VISIBLE);
        } else {
            mBluetoothSwitchView.setText("点击开启蓝牙");
            mBluetoothTip.setVisibility(View.GONE);
            mBluetoothSwitchTip.setVisibility(View.VISIBLE);
            mBluetoothListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnected(BluetoothSocket socket, int taskType) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_guide_bluetooth_switch:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.act_guide_bluetooth_jump:
                if (mSelectedPosition >= 0) {
                    if (mAdapter.getItem(mSelectedPosition) != null) {
                        Intent intent = new Intent();
                        intent.putExtra("BluetoothInfo", mAdapter.getItem(mSelectedPosition));
                        intent.setClass(this, CallNumberActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.show("蓝牙设备异常");
                    }
                } else {
                    ToastUtil.show("还未选择打印设备");
                }
                break;
        }
    }

    /**
     * 获取app更新信息
     */
    private void getAppUpdateInfo() {
        if (AndroidUtil.getNetworkType() == AndroidUtil.NetType.NO_NET) { // 沒有网络
            return;
        }
        AppUpdateDialog.newInstance(new AppUpdateDialog.AppUpdateDialogListener() {
            @Override
            public void onClicked(AppUpdateDialog.AppUpdateDialogButton btn) {
                if (btn == AppUpdateDialog.AppUpdateDialogButton.POSITIVE) { // 立即更新
                    FileLoadManager.getInstance().commit("http://www.wencyv.com/CallNumber_1.0_release6.apk");
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onDismiss() {
            }
        }, "有新版本更新", "立即更新", "取消", "更新内容").show(this);

//        String url = Resource.getAppUpdate(); // 获取更新接口
//        RequestInfo requestInfo = new RequestInfo(Request.Method.POST, "");
//        NormalRequest request = new NormalRequest(requestInfo, new RequestManager.RequestListener() {
//            @Override
//            public void onResponse(String data, RequestError error) {
//                if (error != null && !TextUtils.isEmpty(error.getMsg())) {
//                    return;
//                } else if (!TextUtils.isEmpty(data)) {
//                }
//            }
//        });
//        RequestManager.instance().request(this, request);
    }

    @Override
    public void onFileLoadListener() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                upGradeApk();
            }
        });
    }

    /**
     * 升級app
     */
    private void upGradeApk() {
        StringBuilder hint = new StringBuilder();
        hint.append("为我点赞(").append("1.0.0").append(")将替换当前版本，会保存所有以前的用户数据。").append("\n").append("确定要安装吗？");
        AppUpdateDialog.newInstance(new AppUpdateDialog.AppUpdateDialogListener() {
            @Override
            public void onClicked(AppUpdateDialog.AppUpdateDialogButton btn) {
                if (btn == AppUpdateDialog.AppUpdateDialogButton.POSITIVE) { //安装
                    String name = "CallNumber_1.0_release6.apk";
                    FileLoadManager.getInstance().installAPK(GuideActivity.this, new File(FileLoadManager.getPath(), name));
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onDismiss() {
            }
        }, "提示", "安装", "取消", hint).show(this);
    }

    private class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

        DeviceListAdapter(Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            BluetoothDevice device = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            }

            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            CheckBox cbDevice = (CheckBox) convertView.findViewById(R.id.cb_device);

            if (device != null && !TextUtils.isEmpty(device.getName())) {
                tvDeviceName.setText(device.getName());
            }

            cbDevice.setChecked(position == mSelectedPosition);

            return convertView;
        }
    }

    private ConfirmDialog.ConfirmDialogListener netDialogListener = new ConfirmDialog.ConfirmDialogListener() {
        @Override
        public void onClicked(ConfirmDialog.ConfirmDialogButton btn) {
            if (btn.toString().equals(ConfirmDialog.ConfirmDialogButton.NEGATIVE.toString())) {
                AndroidUtil.toWIFISetting(GuideActivity.this);
            } else {
                finish();
            }
        }

        @Override
        public void onCancel() {
            finish();
        }

        @Override
        public void onDismiss() {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GlobalApp.getContext().onActivityDestroy(this);
        FileLoadManager.getInstance().removeListener(this);

        mAdapter.clear();
    }
}
