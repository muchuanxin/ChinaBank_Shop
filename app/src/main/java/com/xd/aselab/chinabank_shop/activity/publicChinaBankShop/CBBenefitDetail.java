package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;

public class CBBenefitDetail extends AppCompatActivity {

    private RelativeLayout back;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbbenefit_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back = (RelativeLayout) findViewById(R.id.act_cb_benefit_detail_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        web = (WebView) findViewById(R.id.act_cb_benefit_detail_web);
        web.loadUrl(getIntent().getStringExtra("content"));
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
