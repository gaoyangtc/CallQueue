package com.gaoyang.bluetoothprinter.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gaoyang.bluetoothprinter.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BluetoothService {

    private Context mContext;

    private List<BluetoothDevice> mUnpairedList = null; // 用于存放未配对蓝牙设备
    private List<BluetoothDevice> mPairedList = null; // 用于存放已配对蓝牙设备

    private Button mSwitchBTButton = null;
    private Button mSearchBTButton = null;

    private ListView mUnpairedListView = null;
    private ListView mPairedListView = null;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * 添加已绑定蓝牙设备到ListView
     */
    private void addBondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = mPairedList.size();
        System.out.println("已绑定设备数量：" + count);
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", mPairedList.get(i).getName());
            data.add(map);// 把item项的数据加到data中    
        }
        String[] from = {"deviceName"};
        int[] to = {R.id.device_name};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.mContext, data,
                R.layout.bonddevice_item, from, to);
        // 把适配器装载到listView中    
        mPairedListView.setAdapter(simpleAdapter);

        mPairedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                BluetoothDevice device = mPairedList.get(arg2);
                Intent intent = new Intent();
                intent.setClassName(mContext,
                        "com.jerry.bluetoothprinter.view.PrintDataActivity");
                intent.putExtra("deviceAddress", device.getAddress());
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * 添加未绑定蓝牙设备到ListView
     */
    private void addUnbondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = mUnpairedList.size();
        System.out.println("未绑定设备数量：" + count);
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", mUnpairedList.get(i).getName());
            data.add(map);// 把item项的数据加到data中    
        }
        String[] from = {"deviceName"};
        int[] to = {R.id.undevice_name};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.mContext, data,
                R.layout.unbonddevice_item, from, to);

        // 把适配器装载到listView中    
        this.mUnpairedListView.setAdapter(simpleAdapter);

        // 为每个item绑定监听，用于设备间的配对    
        this.mUnpairedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                try {
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    createBondMethod
                            .invoke(mUnpairedList.get(arg2));
                    // 将绑定好的设备添加的已绑定list集合
                    mPairedList.add(mUnpairedList.get(arg2));
                    // 将绑定好的设备从未绑定list集合中移除
                    mUnpairedList.remove(arg2);
                    addBondDevicesToListView();
                    addUnbondDevicesToListView();
                } catch (Exception e) {
                    Toast.makeText(mContext, "配对失败！", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
    }

    public BluetoothService(Context context, ListView unpairedListView, ListView pairedListView, Button switchBTButton, Button searchBTButton) {
        mContext = context;
        mUnpairedListView = unpairedListView;
        mPairedListView = pairedListView;

        mUnpairedList = new ArrayList<BluetoothDevice>();
        mPairedList = new ArrayList<BluetoothDevice>();

        mSwitchBTButton = switchBTButton;
        mSearchBTButton = searchBTButton;

        initIntentFilter();
    }

    private void initIntentFilter() {
        // 设置广播信息过滤    
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果    
        mContext.registerReceiver(receiver, intentFilter);
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        mBluetoothAdapter.disable();
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return boolean
     */
    public boolean isOpen() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 搜索蓝牙设备
     */
    public void searchDevices() {
        mPairedList.clear();
        mUnpairedList.clear();
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去    
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 添加未绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void addUnbondDevices(BluetoothDevice device) {
        System.out.println("未绑定设备名称：" + device.getName());
        if (!mUnpairedList.contains(device)) {
            mUnpairedList.add(device);
        }
    }

    /**
     * 添加已绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void addBandDevices(BluetoothDevice device) {
        System.out.println("已绑定设备名称：" + device.getName());
        if (!mPairedList.contains(device)) {
            mPairedList.add(device);
        }
    }

    /**
     * 蓝牙广播接收器
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        ProgressDialog progressDialog = null;

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        addBandDevices(device);
                    } else {
                        addUnbondDevices(device);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    progressDialog = ProgressDialog.show(context, mContext.getString(R.string.dialog_loading),
                            mContext.getString(R.string.bluetooth_loading), true);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    progressDialog.dismiss();

                    addUnbondDevicesToListView();
                    addBondDevicesToListView();
                    // bluetoothAdapter.cancelDiscovery();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                        mSwitchBTButton.setText(R.string.switch_bluetooth_close);
                        mSearchBTButton.setEnabled(true);
                        mPairedListView.setEnabled(true);
                        mUnpairedListView.setEnabled(true);
                    } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                        mSwitchBTButton.setText(R.string.switch_bluetooth_open);
                        mSearchBTButton.setEnabled(false);
                        mPairedListView.setEnabled(false);
                        mUnpairedListView.setEnabled(false);
                    }
                    break;
            }
        }
    };
} 