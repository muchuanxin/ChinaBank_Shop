package com.xd.aselab.chinabank_shop.activity.shop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

public class ShopInfoActivity extends AppCompatActivity {

    private RelativeLayout btnBack;
    private TextView txt_shop_name;
    private TextView txt_shop_type;
    private TextView txt_shop_addr;
    private TextView txt_shop_tel;
    private TextView txt_shop_manager_tel;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp=new SharePreferenceUtil(this,"user");

        btnBack=(RelativeLayout) findViewById(R.id.shop_manage_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_shop_name=(TextView)findViewById(R.id.shop_name_text);
        txt_shop_name.setText(sp.getShopName());

        txt_shop_type=(TextView)findViewById(R.id.shop_type_text);
        txt_shop_type.setText(sp.getShopIndustryType());

        txt_shop_addr=(TextView)findViewById(R.id.shop_addr_text);
        txt_shop_addr.setMovementMethod(ScrollingMovementMethod.getInstance());
        txt_shop_addr.setOnClickListener(new View.OnClickListener() {
            Boolean flag=true;
            @Override
            public void onClick(View v) {
                if(flag){
                    flag=false;
                    txt_shop_addr.setEllipsize(null);
                }else {
                    flag=true;
                    txt_shop_addr.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
        String province=sp.getShopProvince();
        String city=sp.getShopCity();
        String county=sp.getShopCounty();
        String street=sp.getShopStreet();
        txt_shop_addr.setText(province+city+county+street);

        txt_shop_tel=(TextView)findViewById(R.id.shop_tel_text);
        txt_shop_tel.setText(sp.getShopMoblie());

        txt_shop_manager_tel=(TextView)findViewById(R.id.manager_tel_text);
        txt_shop_manager_tel.setText(sp.getShopManagerTel());

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
