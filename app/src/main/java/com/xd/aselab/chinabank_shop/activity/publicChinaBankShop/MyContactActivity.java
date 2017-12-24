package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

public class MyContactActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView manager_txt;
    private TextView tel_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back=(RelativeLayout) findViewById(R.id.my_cont_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        manager_txt = (TextView) findViewById(R.id.my_cont_manager_txt);
        manager_txt.setText("刘昊贤");

        tel_txt = (TextView) findViewById(R.id.my_cont_tel_txt);
        tel_txt.setText("12345678912");
    }
}
