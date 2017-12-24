package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.xd.aselab.chinabank_shop.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private List<String> dataList;
    private ArrayAdapter<String> arr_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_login);
        //setContentView(R.layout.activity_register_clerk);
        setContentView(R.layout.activity_shop_register);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        spinner=(Spinner) findViewById(R.id.shopTypeSpinner);
        dataList=new ArrayList<String>();
        dataList.add("店铺类型一");
        dataList.add("店铺类型二");
        dataList.add("店铺类型三");
        dataList.add("店铺类型四");
        dataList.add("店铺类型五");

        arr_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataList);
        //arr_adapter=new ArrayAdapter<String>(this,R.layout.myspinner,dataList);

        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(arr_adapter);
    }


}
