
package com.xd.aselab.chinabank_shop.activity.shop;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShopkeeperPerformanceActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView addCard;
    private TextView selectDate1;
    private TextView selectDate2;
    private Calendar calendar=Calendar.getInstance();
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkeeper_performance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        selectDate1=(TextView)findViewById(R.id.shopkeeper_Performance_selectDate1);
        selectDate2=(TextView)findViewById(R.id.shopkeeper_Performance_selectDate2);
        SimpleDateFormat format_for_date = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        selectDate1.setText(format_for_date.format(date));
        selectDate2.setText(format_for_date.format(date));
        this.date = format_for_date.format(date);
        selectDate1.setOnClickListener(new DateListener1());
        selectDate2.setOnClickListener(new DateListener2());

        back=(RelativeLayout)findViewById(R.id.shopkeeper_performance_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addCard=(TextView)findViewById(R.id.shopkeeper_addCard);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopkeeperPerformanceActivity.this,AddCardActivity.class);
                startActivity(intent);
            }
        });

    }


    private class DateListener1 implements View.OnClickListener{
        @Override
        public void onClick(View v){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker=new DatePickerDialog(ShopkeeperPerformanceActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                            Calendar cal=Calendar.getInstance();
                            cal.set(Calendar.YEAR,year);
                            cal.set(Calendar.MONTH,monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            DecimalFormat mFormat= new DecimalFormat("00");
                            date = year + "-" + mFormat.format(monthOfYear + 1) + "-" + mFormat.format(dayOfMonth);
                            selectDate1.setText(date);
                            System.out.println("set is " + date);
                        }
                    }
                    ,year,month,day);
            datePicker.show();
        }
    }

    private class DateListener2 implements View.OnClickListener{
        @Override
        public void onClick(View v){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker=new DatePickerDialog(ShopkeeperPerformanceActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                            Calendar cal=Calendar.getInstance();
                            cal.set(Calendar.YEAR,year);
                            cal.set(Calendar.MONTH,monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            DecimalFormat mFormat= new DecimalFormat("00");
                            date = year + "-" + mFormat.format(monthOfYear + 1) + "-" + mFormat.format(dayOfMonth);
                            selectDate2.setText(date);
                            System.out.println("set is " + date);
                        }
                    }
                    ,year,month,day);
            datePicker.show();
        }
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
