package com.gaoyang.bluetoothprinter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.gaoyang.bluetoothprinter.manager.BluetoothManager;

public class MainActivity extends AppCompatActivity {

    private Button mOpenBluetooth;
    private Button mSearchBluetooth;
    private ListView mUnpairedList;
    private ListView mPairedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mOpenBluetooth = (Button) findViewById(R.id.act_main_bluetooth_open);
        mSearchBluetooth = (Button) findViewById(R.id.act_main_bluetooth_search);
        mUnpairedList = (ListView) findViewById(R.id.act_main_bluetooth_list_unpaired);
        mPairedList = (ListView) findViewById(R.id.act_main_bluetooth_list_paired);

        BluetoothManager bluetoothManager = new BluetoothManager(this, mUnpairedList, mPairedList, mOpenBluetooth, mSearchBluetooth, this);

        bluetoothManager.setSearchDevices(mSearchBluetooth);
        bluetoothManager.initView();

        mOpenBluetooth.setOnClickListener(bluetoothManager);
        mSearchBluetooth.setOnClickListener(bluetoothManager);
    }
}
