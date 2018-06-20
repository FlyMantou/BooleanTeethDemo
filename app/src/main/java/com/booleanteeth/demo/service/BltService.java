package com.booleanteeth.demo.service;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.booleanteeth.demo.appliaction.BltAppliaction;
import com.booleanteeth.demo.contants.BltContant;
import com.booleanteeth.demo.manager.BltManager;

import java.io.IOException;

/**
 * Created by LuHao on 2016/9/26.
 * 蓝牙服务
 */
public class BltService {

    /**
     * 设置成单例模式
     */
    private BltService() {
        createBltService();
    }

    private static class BlueToothServices {
        private static BltService bltService = new BltService();
    }

    public static BltService getInstance() {
        return BlueToothServices.bltService;
    }

    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket socket;

    public BluetoothSocket getSocket() {
        return socket;
    }

    public BluetoothServerSocket getBluetoothServerSocket() {
        return bluetoothServerSocket;
    }

    /**
     * 从蓝牙适配器中创建一个蓝牙服务作为服务端，在获得蓝牙适配器后创建服务器端
     */
    private void createBltService() {
        try {
            if (BltManager.getInstance().getmBluetoothAdapter() != null && BltManager.getInstance().getmBluetoothAdapter().isEnabled()) {
                bluetoothServerSocket = BltManager.getInstance().getmBluetoothAdapter().listenUsingRfcommWithServiceRecord("com.bluetooth.demo", BltContant.SPP_UUID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这个操作应该放在子线程中，因为存在线程阻塞的问题
     */
    public void run(Handler handler) {
        //服务器端的bltsocket需要传入uuid和一个独立存在的字符串，以便验证，通常使用包名的形式
        while (true) {
            try {
                //注意，当accept()返回BluetoothSocket时，socket已经连接了，因此不应该调用connect方法。
                //这里会线程阻塞，直到有蓝牙设备链接进来才会往下走
                socket = getBluetoothServerSocket().accept();
                if (socket != null) {
                    BltAppliaction.bluetoothSocket = socket;
                    //回调结果通知
                    Message message = new Message();
                    message.what = 3;
                    message.obj = socket.getRemoteDevice();
                    handler.sendMessage(message);
                    //如果你的蓝牙设备只是一对一的连接，则执行以下代码
                    getBluetoothServerSocket().close();
                    //如果你的蓝牙设备是一对多的，则应该调用break；跳出循环
                    //break;
                }
            } catch (IOException e) {
                try {
                    getBluetoothServerSocket().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            getBluetoothServerSocket().close();
        } catch (IOException e) {
            Log.e("blueTooth", "关闭服务器socket失败");
        }
    }
}
