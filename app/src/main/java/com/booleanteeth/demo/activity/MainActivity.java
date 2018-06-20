package com.booleanteeth.demo.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.booleanteeth.demo.MyBleDevice;
import com.booleanteeth.demo.R;
import com.booleanteeth.demo.contants.BltContant;
import com.booleanteeth.demo.manager.BltManager;
import com.booleanteeth.demo.service.BltService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button searth_switch, searth_my_switch, create_service;
    private Switch blue_switch;
    private ListView blue_list;
    private List<MyBleDevice> bltList;
    private MyAdapter myAdapter;
    private ProgressBar btl_bar;
    private TextView blt_status_text;
    private LinearLayout content_ly;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://搜索蓝牙
                    break;
                case 2://蓝牙可以被搜索
                    break;
                case 3://设备已经接入
                    btl_bar.setVisibility(View.GONE);
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    blt_status_text.setText("设备" + device.getName() + "已经接入");
                    Toast.makeText(MainActivity.this, "设备" + device.getName() + "已经接入", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, BltSocketAcivity.class);
                    startActivity(intent);
                    break;
                case 4://已连接某个设备
                    btl_bar.setVisibility(View.GONE);
                    BluetoothDevice device1 = (BluetoothDevice) msg.obj;
                    blt_status_text.setText("已连接" + device1.getName() + "设备");
                    Toast.makeText(MainActivity.this, "已连接" + device1.getName() + "设备", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(MainActivity.this, BltSocketAcivity.class);
                    startActivity(intent1);
                    break;
                case 5:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BltManager.getInstance().initBltManager(this);
        initView();
        initData();
    }

    private void initView() {
        searth_switch = (Button) findViewById(R.id.searth_switch);
        searth_my_switch = (Button) findViewById(R.id.searth_my_switch);
        create_service = (Button) findViewById(R.id.create_service);
        blue_switch = (Switch) findViewById(R.id.blue_switch);
        blue_list = (ListView) findViewById(R.id.blue_list);
        btl_bar = (ProgressBar) findViewById(R.id.btl_bar);
        blt_status_text = (TextView) findViewById(R.id.blt_status_text);
        content_ly = (LinearLayout) findViewById(R.id.content_ly);
        searth_switch.setOnClickListener(this);
        searth_my_switch.setOnClickListener(this);
        create_service.setOnClickListener(this);
    }

    private void initData() {
        btl_bar.setVisibility(View.GONE);
        bltList = new ArrayList<>();
        myAdapter = new MyAdapter();
        blue_list.setOnItemClickListener(this);
        blue_list.setAdapter(myAdapter);
        //检查蓝牙是否开启
        BltManager.getInstance().checkBleDevice(this);
        //注册蓝牙扫描广播
        blueToothRegister();
        //更新蓝牙开关状态
        checkBlueTooth();
        //第一次进来搜索设备
        BltManager.getInstance().clickBlt(this, BltContant.BLUE_TOOTH_SEARTH);
    }

    /**
     * 注册蓝牙回调广播
     */
    private void blueToothRegister() {
        BltManager.getInstance().registerBltReceiver(this, new BltManager.OnRegisterBltReceiver() {

            /**搜索到新设备
             * @param device
             */
            @Override
            public void onBluetoothDevice(String name,BluetoothDevice device) {

                if (bltList!=null){
                    for (MyBleDevice myBleDevice1:bltList){
                        if (myBleDevice1.getDevice()==device){
                            return;
                        }
                    }
                    MyBleDevice myBleDevice = new MyBleDevice();
                    myBleDevice.setName(name);
                    myBleDevice.setDevice(device);
                    bltList.add(myBleDevice);
                    Log.i("huanghai","发现新设备-->"+myBleDevice.getName());
                }

                if (myAdapter != null)
                    myAdapter.notifyDataSetChanged();
            }

            /**连接中
             * @param device
             */
            @Override
            public void onBltIng(BluetoothDevice device) {
                btl_bar.setVisibility(View.VISIBLE);
                blt_status_text.setText("连接" + device.getName() + "中……");
            }

            /**连接完成
             * @param device
             */
            @Override
            public void onBltEnd(BluetoothDevice device) {
                btl_bar.setVisibility(View.GONE);
                blt_status_text.setText("连接" + device.getName() + "完成");
            }

            /**取消链接
             * @param device
             */
            @Override
            public void onBltNone(BluetoothDevice device) {
                btl_bar.setVisibility(View.GONE);
                blt_status_text.setText("取消了连接" + device.getName());
            }
        });
    }

    /**
     * 检查蓝牙的开关状态
     */
    private void checkBlueTooth() {
        if (BltManager.getInstance().getmBluetoothAdapter() == null || !BltManager.getInstance().getmBluetoothAdapter().isEnabled()) {
            blue_switch.setChecked(false);
        } else
            blue_switch.setChecked(true);
        blue_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    //启用蓝牙
                    BltManager.getInstance().clickBlt(MainActivity.this, BltContant.BLUE_TOOTH_OPEN);
                else
                    //禁用蓝牙
                    BltManager.getInstance().clickBlt(MainActivity.this, BltContant.BLUE_TOOTH_CLOSE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searth_switch://搜索设备
                btl_bar.setVisibility(View.VISIBLE);
                blt_status_text.setText("正在搜索设备");
                BltManager.getInstance().clickBlt(this, BltContant.BLUE_TOOTH_SEARTH);
                break;
            case R.id.searth_my_switch://检查蓝牙是否可用，并打开
                btl_bar.setVisibility(View.GONE);
                blt_status_text.setText("设备可以被搜索（300s）");
                //让本机设备能够被其他人搜索到
                BltManager.getInstance().clickBlt(this, BltContant.BLUE_TOOTH_CLEAR);
                break;
            case R.id.create_service://创建服务端
                btl_bar.setVisibility(View.VISIBLE);
                blt_status_text.setText("正在等待设备加入");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BltService.getInstance().run(handler);
                    }
                }).start();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MyBleDevice bluetoothDevice = bltList.get(position);
        btl_bar.setVisibility(View.VISIBLE);
        blt_status_text.setText("正在连接" + bluetoothDevice.getName());
        //链接的操作应该在子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                BltManager.getInstance().createBond(bluetoothDevice.getDevice(), handler);
            }
        }).start();

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return bltList.size();
        }

        @Override
        public Object getItem(int position) {
            return bltList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder vh;
            MyBleDevice device = bltList.get(position);// 从集合中获取当前行的数据
            if (convertView == null) {
                // 说明当前这一行不是重用的
                // 加载行布局文件，产生具体的一行
                v = getLayoutInflater().inflate(R.layout.item_blt, null);
                // 创建存储一行控件的对象
                vh = new ViewHolder();
                // 将该行的控件全部存储到vh中
                vh.blt_name = (TextView) v.findViewById(R.id.blt_name);
                vh.blt_address = (TextView) v.findViewById(R.id.blt_address);
                vh.blt_type = (TextView) v.findViewById(R.id.blt_type);
                vh.blt_bond_state = (TextView) v.findViewById(R.id.blt_bond_state);
                v.setTag(vh);// 将vh存储到行的Tag中
            } else {
                v = convertView;
                // 取出隐藏在行中的Tag--取出隐藏在这一行中的vh控件缓存对象
                vh = (ViewHolder) convertView.getTag();
            }

            // 从ViewHolder缓存的控件中改变控件的值
            // 这里主要是避免多次强制转化目标对象而造成的资源浪费
            vh.blt_name.setText("蓝牙名称：" + device.getName());
            vh.blt_address.setText("蓝牙地址:" + device.getDevice().getAddress());
            vh.blt_type.setText("蓝牙类型:" + device.getDevice().getType());
            vh.blt_bond_state.setText("蓝牙状态:" + BltManager.getInstance().bltStatus(device.getDevice().getBondState()));
            return v;
        }

        private class ViewHolder {
            TextView blt_name, blt_address, blt_type, blt_bond_state;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面关闭的时候要断开蓝牙
        BltManager.getInstance().unregisterReceiver(this);
    }
}
