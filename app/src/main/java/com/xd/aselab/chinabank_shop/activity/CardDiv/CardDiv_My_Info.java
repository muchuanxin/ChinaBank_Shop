package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

public class CardDiv_My_Info extends AppCompatActivity {

    private ImageView head_photo;
    private ImageLoader imageLoader;
    private SharePreferenceUtil sp;
    private TextView user_name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(CardDiv_My_Info.this,"user");

        user_name= (TextView) findViewById(R.id.user_name);
        user_name.setText(sp.getWorkerName());
        Log.d("Dorise",sp.getWorkerName());
        Log.d("Dorise","=============");

        head_photo = (ImageView)findViewById(R.id.dafault_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(CardDiv_My_Info.this,sp.getHead_image(),head_photo,R.drawable.portrait);

        LinearLayout my_performance_row= (LinearLayout) findViewById(R.id.my_performance_row);
        my_performance_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CardDiv_My_Info.this,CardDiv_My_Info_Info.class);
                startActivity(intent);
            }
        });
        LinearLayout my_info_row= (LinearLayout) findViewById(R.id.my_info_row);
        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CardDiv_My_Info.this,CardDiv_Check_Public.class);
                startActivity(intent);
            }
        });
        LinearLayout terminate_row= (LinearLayout) findViewById(R.id.terminate_row);
        terminate_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CardDiv_My_Info.this,CardDiv_Cancel_League.class);
                startActivity(intent);
            }
        });

/*        Button exit= (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CardDiv_My_Info.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
        ImageView return_button= (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.loadBitmap(CardDiv_My_Info.this,sp.getHead_image(),head_photo,R.drawable.portrait);
    }
}
