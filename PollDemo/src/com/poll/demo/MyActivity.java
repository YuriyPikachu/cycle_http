package com.poll.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.poll.demo.sys.MyApplication;
import com.poll.demo.utils.Constant;

public class MyActivity extends Activity implements View.OnClickListener {
    private Button btn_start,btn_stop;//启动和结束
    private Myreciver2 myreciver2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        myreciver2 = new Myreciver2();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ReceiverAction.TONGZHI_ACTION);
        registerReceiver(myreciver2,intentFilter);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.btn_start:
                 MyApplication.isGetMsg = true;
                 break;
             case R.id.btn_stop:
                 MyApplication.isGetMsg = false;
                 break;
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(myreciver2);
    }

    class Myreciver2 extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = MyApplication.getInstance().getSharedPreferencesValue("yig");
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
        }
    }
}
