package com.booleanteeth.demo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.booleanteeth.demo.R;
import com.booleanteeth.demo.service.ReceiveSocketService;
import com.booleanteeth.demo.service.SendSocketService;

/**
 * Created by Luhao on 2016/9/28.
 */
public class BltSocketAcivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout content_ly;
    private EditText go_edit_text;
    private Button go_text_btn, go_file_btn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blt_socket);
        content_ly = (LinearLayout) findViewById(R.id.content_ly);
        go_edit_text = (EditText) findViewById(R.id.go_edit_text);
        go_text_btn = (Button) findViewById(R.id.go_text_btn);
        go_file_btn = (Button) findViewById(R.id.go_file_btn);
        go_text_btn.setOnClickListener(this);
        go_file_btn.setOnClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReceiveSocketService.receiveMessage(handler);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_file_btn://发送文件
                SendSocketService.sendMessageByFile(Environment.getExternalStorageDirectory()+"/test.gif");
                break;
            case R.id.go_text_btn://发送文本消息
                if (TextUtils.isEmpty(go_edit_text.getText().toString().trim())) return;
                SendSocketService.sendMessage(go_edit_text.getText().toString().trim());
                content_ly.addView(getRightTextView(go_edit_text.getText().toString().trim()));
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://文本消息
                    if (TextUtils.isEmpty(msg.obj.toString())) return;
                    content_ly.addView(getLeftTextView(msg.obj.toString()));
                    break;
                case 2://文件消息
                    if (TextUtils.isEmpty(msg.obj.toString())) return;
                    content_ly.addView(getLeftTextView(msg.obj.toString()));
                    break;
                case 3://
                    break;
                case 4://
                    break;
                case 5:
                    break;
            }
        }
    };

    private TextView getLeftTextView(String message) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        textView.setGravity(View.FOCUS_LEFT);
        textView.setBackgroundResource(android.R.color.darker_gray);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setLayoutParams(layoutParams);
        textView.setText(message);
        return textView;
    }

    private TextView getRightTextView(String message) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        textView.setGravity(View.FOCUS_RIGHT);
        textView.setBackgroundResource(android.R.color.white);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setLayoutParams(layoutParams);
        textView.setText(message);
        return textView;
    }
}
