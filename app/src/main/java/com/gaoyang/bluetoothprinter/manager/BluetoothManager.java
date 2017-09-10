package com.gaoyang.bluetoothprinter.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.gaoyang.bluetoothprinter.R;
import com.gaoyang.bluetoothprinter.service.BluetoothService;

/**
 * Project: BluetoothPrinter
 * Author: GaoYang
 * Date: 2017/9/10
 */

public class BluetoothManager implements View.OnClickListener {

    private Button mSwitchBTButton = null;
    private Button mSearchBTButton = null;
    private Activity mActivity = null;

    private ListView mUnpairedList = null;
    private ListView mPairedList = null;
    private Context mContext = null;

    private BluetoothService mBluetoothService = null;

    public BluetoothManager(Context context, ListView unpairedList, ListView pairedList, Button switchBTButton, Button searchBTButton,
                            Activity activity) {
        super();
        this.mContext = context;
        this.mUnpairedList = unpairedList;
        this.mPairedList = pairedList;
        this.mSwitchBTButton = switchBTButton;
        this.mSearchBTButton = searchBTButton;
        this.mActivity = activity;
        this.mBluetoothService = new BluetoothService(mContext, mUnpairedList, mPairedList, mSwitchBTButton, mSearchBTButton);
    }

    public void setSwitchBTButton(Button switchBTButton) {
        mSwitchBTButton = switchBTButton;
    }

    public void setSearchDevices(Button searchBTButton) {
        mSearchBTButton = searchBTButton;
    }

    public void setUnbondDevices(ListView unpairedList) {
        mUnpairedList = unpairedList;
    }

    /**
     * 初始化界面
     */
    public void initView() {
        if (this.mBluetoothService.isOpen()) { // 若蓝牙未打开则显示关闭文案
            mSwitchBTButton.setText(R.string.switch_bluetooth_close);
        }
        if (!this.mBluetoothService.isOpen()) { // 若蓝牙未打开，则禁用搜索功能
            this.mSearchBTButton.setEnabled(false);
        }
    }

    /**
     * 搜索附近的蓝牙设备
     */
    private void searchDevices() {
        mBluetoothService.searchDevices();
    }

    /**
     * 各种按钮的监听
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_main_bluetooth_search:
                searchDevices();
                break;
            case R.id.act_main_bluetooth_open:
                if (!this.mBluetoothService.isOpen()) { // 蓝牙关闭的情况
                    this.mBluetoothService.openBluetooth(mActivity);
                } else { // 蓝牙打开的情况
                    this.mBluetoothService.closeBluetooth();
                }
                break;
        }
    }
}
