package com.mashell.simpleeventbus.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mashell.simpleeventbus.EventBus;
import com.mashell.simpleeventbus.R;
import com.mashell.simpleeventbus.ThreadMode;
import com.mashell.simpleeventbus.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new String("发送一个 EventBus"));
                    }
                }).start();
            }
        });
    }

    @Subscribe( threadMode = ThreadMode.MAIN_THREAD)
    public void onMainEvent(String name){
        mTextView.setText(name+"\n"+Thread.currentThread());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
