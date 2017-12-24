package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

public class Chat_Activity extends AppCompatActivity {

    private ImageView return_button;
    private TextView manage;
    private String temp;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp=new SharePreferenceUtil(Chat_Activity.this,"user");
        manage= (TextView) findViewById(R.id.manage);
        //如果是 群聊的话  manage设置为可见并且可点击
        //如果是单聊的话   manage设置为不可见  不可点击

        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
         temp=intent.getStringExtra("jump");
        //left是单聊   right0是我创建的群   right是我加入的群
        if(temp.equals("left")){
            intent.putExtra("receiver",sp.getAccount());
            intent.putExtra("receiver_head",sp.getHead_image());
            intent.putExtra("receiver_name",sp.getWorkerName());
            manage.setVisibility(View.GONE);
        }else if(temp.equals("right0")||temp.equals("right1")){
            manage.setVisibility(View.VISIBLE);
        }
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Chat_Activity.this,Group_info.class);

                intent.putExtra("group_id",getIntent().getStringExtra("group_id"));
                intent.putExtra("group_name", getIntent().getStringExtra("group_name"));
                intent.putExtra("group_head", getIntent().getStringExtra("head_image"));
                intent.putExtra("manage_info",temp);
                startActivity(intent);
            }
        });
    }
}
