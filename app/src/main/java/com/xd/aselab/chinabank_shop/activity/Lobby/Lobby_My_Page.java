package com.xd.aselab.chinabank_shop.activity.Lobby;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

public class Lobby_My_Page extends AppCompatActivity {
    private ImageView headImage;
    private TextView userName;
    private LinearLayout my_info_row;
    private LinearLayout check_announcement_row;
    private LinearLayout terminate_row;
    private LinearLayout exchange_score_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_my_page);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        my_info_row = (LinearLayout) findViewById(R.id.myhead);
        exchange_score_line = (LinearLayout) findViewById(R.id.exchange_score_line);

        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Lobby_My_Page.this, Lobby_My_Info.class);
                startActivity(intent);
            }
        });

        exchange_score_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Lobby_My_Page.this, Lobby_My_ExchangeScore.class);
                startActivity(intent);
            }
        });
    }
}
