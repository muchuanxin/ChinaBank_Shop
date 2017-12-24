package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;


import java.util.Timer;
import java.util.TimerTask;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        final Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
//        intent.setClass(this, MyTest.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 1000 * 2);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(StartActivity.this);
        CustomPushNotificationBuilder builder2 = new CustomPushNotificationBuilder(StartActivity.this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
        builder2.layoutIconDrawable = R.mipmap.ic_launcher;
        builder2.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder2);

        //清除极光推送的Alias和Tags
        SharePreferenceUtil spu = new SharePreferenceUtil(StartActivity.this, "user");
        if (!spu.getisLogin()) {
            JPushInterface.deleteAlias(StartActivity.this, 0);
            JPushInterface.cleanTags(StartActivity.this, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(StartActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(StartActivity.this);
    }

}
