package com.xd.aselab.chinabank_shop.activity.shop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;

public class ShopMyManagement extends AppCompatActivity {
    private RelativeLayout back;
    private LinearLayout my_performance;
    private LinearLayout my_shop_info;
    private LinearLayout my_shop_terminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_my_management);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back=(RelativeLayout)findViewById(R.id.shop_my_management_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        my_performance=(LinearLayout)findViewById(R.id.shop_my_performance_row);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
              //  intent.setClass(ShopMyManagement.this,ShopkeeperPerformanceActivity.class);
                intent.setClass(ShopMyManagement.this,ShopMyWorkers.class);
                startActivity(intent);
            }
        });

        my_shop_info=(LinearLayout)findViewById(R.id.shop_my_shop_info_row);
        my_shop_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMyManagement.this,ShopInfoActivity.class);
                startActivity(intent);
            }
        });

        my_shop_terminate=(LinearLayout)findViewById(R.id.my_shop_terminate_row);
        my_shop_terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMyManagement.this,ApplyTerminateActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
