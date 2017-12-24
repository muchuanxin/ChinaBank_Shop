package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.PercentView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test);
      PercentView p= (PercentView) findViewById(R.id.haha);
        p.setAngel(70);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
String str="评估时间："+df.format(new Date());
        p.setRankText("70",str);

    }
}
