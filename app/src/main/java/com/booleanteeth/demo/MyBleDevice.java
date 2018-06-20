package com.booleanteeth.demo;

import android.bluetooth.BluetoothDevice;

/**
 * 创建时间： 2018/6/20.
 * 开发者：黄海
 * 邮箱：1165441461@qq.com
 * Github：https://github.com/FlyMantou
 * 类说明：
 */
public class MyBleDevice {
    private String name;
    private BluetoothDevice device;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
