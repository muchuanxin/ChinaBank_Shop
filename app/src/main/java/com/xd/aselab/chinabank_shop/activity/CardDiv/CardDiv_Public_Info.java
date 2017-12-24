package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.fragment.ImageCycleView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HandshakeCompletedEvent;

public class CardDiv_Public_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__public__info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        HashMap<String,Object> infoMap = (HashMap<String, Object>)getIntent().getSerializableExtra("map_info");


        TextView titleTextview = (TextView)findViewById(R.id.InformationTitle);
        TextView contentTextview = (TextView)findViewById(R.id.InformationContent) ;
        TextView senderTextview = (TextView)findViewById(R.id.InformationSender);
        TextView timeTextview = (TextView)findViewById(R.id.InformationTime) ;
        ImageView return_btn= (ImageView) findViewById(R.id.return_button);

        String title = (String) infoMap.get("title");
        String content = "        "+(String) infoMap.get("content");
        String time = (String) infoMap.get("time");
        String sender = (String) infoMap.get("sender");

        titleTextview.setText(title);
        contentTextview.setText(content);
        senderTextview.setText(sender);
        timeTextview.setText(time);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
