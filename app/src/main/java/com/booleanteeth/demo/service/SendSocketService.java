package com.booleanteeth.demo.service;

import android.text.TextUtils;
import android.util.Log;

import com.booleanteeth.demo.appliaction.BltAppliaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Luhao on 2016/9/28.
 * 发送消息的服务
 */
public class SendSocketService {

    /**
     * 发送文本消息
     *
     * @param message
     */
    public static void sendMessage(String message) {
        if (BltAppliaction.bluetoothSocket == null || TextUtils.isEmpty(message)) return;
        try {
            message += "\n";
            OutputStream outputStream = BltAppliaction.bluetoothSocket.getOutputStream();
            outputStream.write(message.getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件
     */
    public static void sendMessageByFile(String filePath) {
        if (BltAppliaction.bluetoothSocket == null || TextUtils.isEmpty(filePath)) return;
        try {
            OutputStream outputStream = BltAppliaction.bluetoothSocket.getOutputStream();
            //要传输的文件路径
            File file = new File(filePath);
            //说明不存在该文件
            if (!file.exists()) return;
            //说明该文件是一个文件夹
            if (file.isDirectory()) return;
            //1、发送文件信息实体类
            outputStream.write("file".getBytes("utf-8"));
            //将文件写入流
            FileInputStream fis = new FileInputStream(file);
            //每次上传1M的内容
            byte[] b = new byte[1024];
            int length;
            int fileSize = 0;//实时监测上传进度
            while ((length = fis.read(b)) != -1) {
                fileSize += length;
                Log.i("socketChat", "文件上传进度：" + (fileSize / file.length() * 100) + "%");
                //2、把文件写入socket输出流
                outputStream.write(b, 0, length);
            }
            //关闭文件流
            fis.close();
            //该方法无效
            //outputStream.write("\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
