package com.booleanteeth.demo.appliaction;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by Luhao on 2016/9/28.
 *
 */
public class BltAppliaction extends Application {

    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static BluetoothSocket bluetoothSocket;
}
