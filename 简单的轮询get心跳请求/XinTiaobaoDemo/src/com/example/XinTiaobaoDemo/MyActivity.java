package com.example.XinTiaobaoDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {
    public static boolean isTrue = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void startXT(View view){
        isTrue = true;
        Intent intent = new Intent(this,HeartbeatService.class);
        startService(intent);
    }

    public void stopXT(View view){
        isTrue = false;

    }


}
