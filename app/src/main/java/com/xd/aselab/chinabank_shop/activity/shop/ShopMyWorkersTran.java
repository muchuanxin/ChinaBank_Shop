package com.xd.aselab.chinabank_shop.activity.shop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

public class ShopMyWorkersTran extends AppCompatActivity {

    private TextView contact;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_my_workers_tran);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActionBar().hide();

            contact = (TextView) findViewById(R.id.shop_my_workers_info_trans_contact);
            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = getIntent();
                    Intent intent2 = new Intent();
                    intent2.putExtra("position",intent1.getIntExtra("position", 0));
                   // intent2.putExtra("action","contact");
                    setResult(143, intent2);
                    finish();
                }
            });

            cancel = (TextView) findViewById(R.id.shop_my_workers_info_trans_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            finish();
            return super.onTouchEvent(event);
        }
    }

