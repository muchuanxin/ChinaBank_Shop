package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.CBNetwork;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.activity.shop.ShopMainPageActivity;
import com.xd.aselab.chinabank_shop.fragment.ImageCycleView;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class CardDivMainPage extends AppCompatActivity {


    private LinearLayout my_performance;
    private LinearLayout hotpoint;
    private LinearLayout my_network;
    private LinearLayout bank_benefit;
    private ImageView myinfo;
    private ImageCycleView cycleView;
    private ImageCycleView.ImageCycleViewListener cycleViewListener;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div_main_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        my_performance= (LinearLayout)findViewById(R.id.my_performance);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CardDivMainPage.this,CardDiv_My_Performance.class);
                startActivity(intent);
            }
        });

        SharePreferenceUtil sp = new SharePreferenceUtil(CardDivMainPage.this, "user");
        JPushInterface.setAlias(CardDivMainPage.this,0,sp.getAccount());

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            Log.e("reCode","reCode------"+reCode);
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(CardDivMainPage.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    ArrayList<String> imageUrls = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        String picture_url = temp.getString("picture_url");
                                        imageUrls.add(picture_url);
                                    }
                                    if (imageUrls.size()>0)
                                        cycleView.setImageResources(imageUrls, cycleViewListener);
                                    else {
                                        imageUrls.add("no_picture");
                                        cycleView.setImageResources(imageUrls, cycleViewListener);
                                    }
                                } else {
                                    Log.e("MyContact_Activity", CardDivMainPage.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(CardDivMainPage.this, CardDivMainPage.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", CardDivMainPage.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };


        cycleView= (ImageCycleView) findViewById(R.id.act_main_ImageCycleView);
        cycleViewListener= new ImageCycleView.ImageCycleViewListener(){

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadBitmap(CardDivMainPage.this, imageURL, imageView, 0);
            }

            @Override
            public void onImageClick(int postion, View imageView) {

            }
        };



        hotpoint= (LinearLayout)findViewById(R.id.my_recommend);
        hotpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CardDivMainPage.this,CardDiv_My_Recommend.class);

                startActivity(intent);
            }
        });
        my_network= (LinearLayout)findViewById(R.id.my_network);
        my_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CardDivMainPage.this,CBNetwork.class);
                startActivity(intent);
            }
        });
        bank_benefit= (LinearLayout)findViewById(R.id.bank_benefit);
        bank_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CardDivMainPage.this,ChinaBankBenefit.class);
                startActivity(intent);
            }
        });
        myinfo= (ImageView)findViewById(R.id.btn_main_clerk_info);
        myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CardDivMainPage.this,CardDiv_My_Info.class);
                startActivity(intent);
            }
        });
        //    LinearLayout = (LinearLayout) findViewById(R.id.);
//    LinearLayout = (LinearLayout) findViewById(R.id.);
//    LinearLayout = (LinearLayout) findViewById(R.id.bank_benefit);
//    ImageView = (ImageView) findViewById(R.id.btn_main_clerk_info);
//
//        myinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(CardDivMainPage.this,CardDiv_My_Info.class);
//                startActivity(intent);
//            }
//        });
//        hotpoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(CardDivMainPage.this,CardDiv_My_Recommend.class);
//                startActivity(intent);
//            }
//        });
//
//
//        my_network.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(CardDivMainPage.this, CBNetwork.class);
//                startActivity(intent);
//            }
//        });
//
//
//        bank_benefit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setClass(CardDivMainPage.this,ChinaBankBenefit.class);
//                startActivity(intent);
//
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //onResume是在每次Activity正式运行之前执行  Activity正式运行前开启一个线程  运行前向后台传递参数
        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("rolling", "picture");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetRollingPicture, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
        cycleView.startImageCycle();
    }
}
