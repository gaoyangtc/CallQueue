package com.gaoyang.bluetoothprinter.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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

import com.gaoyang.bluetoothprinter.R;
import com.gaoyang.bluetoothprinter.utils.BluetoothUtil;
import com.gaoyang.bluetoothprinter.utils.ToastUtil;

import java.util.List;

/**
 * 点赞Pad首页
 * Created by gaoyang.
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBluetoothSwitchView;
    private ListView mBluetoothListView;
    private TextView mBluetoothComplete;

    private int mSelectedPosition = -1;

    private DeviceListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.act_guide_layout);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillAdapter();
    }

    private void initView() {
        mBluetoothSwitchView = (TextView) findViewById(R.id.act_guide_bluetooth_switch);
        mBluetoothListView = (ListView) findViewById(R.id.act_guide_bluetooth_list);
        mBluetoothComplete = (TextView) findViewById(R.id.act_guide_bluetooth_jump);

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
        } else {
            mBluetoothSwitchView.setText("点击开启蓝牙");
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
}
