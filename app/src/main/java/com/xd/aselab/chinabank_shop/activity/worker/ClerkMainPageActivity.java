package com.xd.aselab.chinabank_shop.activity.worker;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotrecommnder.MainHotRecommenderActivity;
import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.CBNetwork;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.activity.shop.ShopMainPageActivity;
import com.xd.aselab.chinabank_shop.fragment.ImageCycleView;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClerkMainPageActivity extends AppCompatActivity {
    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private TextView welcomInfo;
    private ImageView btn_info;
    private LinearLayout hotRecommend;
    private LinearLayout myPerformance;
    private LinearLayout cbNetwork;
    private LinearLayout clerkBankBenefit;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clerk_main_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        btn_info=(ImageView)findViewById(R.id.btn_main_clerk_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClerkMainPageActivity.this, WorkerMyInfoActivity.class);
                startActivity(intent);
            }
        });

        welcomInfo=(TextView)findViewById(R.id.act_main_clerk_toast_i);
        welcomInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastCustom.makeToastCenter(getApplicationContext(),"欢迎您，业务扩展员！");
            }
        });

        imageCycleView = (ImageCycleView) findViewById(R.id.act_main_ImageCycleView);

        mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadBitmap(ClerkMainPageActivity.this, imageURL, imageView, 0);
            }
        };
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ClerkMainPageActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    ArrayList<String> imageUrls = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        String picture_url = temp.getString("picture_url");
                                        imageUrls.add(picture_url);
                                    }
                                    if (imageUrls.size()>0)
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    else {
                                        imageUrls.add("no_picture");
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    }
                                } else {
                                    Log.e("MyContact_Activity", ClerkMainPageActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ClerkMainPageActivity.this, ClerkMainPageActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", ClerkMainPageActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        /*ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add("images/app/top1.jpg");
        imageUrls.add("images/app/top2.jpg");
        imageUrls.add("images/app/top3.jpg");
        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);*/

        myPerformance=(LinearLayout)findViewById(R.id.clerk_main_my_performance);
        myPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClerkMainPageActivity.this, WorkerMyPerformance.class);
                startActivity(intent);
            }
        });

        hotRecommend=(LinearLayout)findViewById(R.id.clerk_main_hotpoint);
        hotRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ClerkMainPageActivity.this,MainHotRecommenderActivity.class);
                startActivity(intent);
            }
        });

        cbNetwork=(LinearLayout)findViewById(R.id.clerk_main_my_network);
        cbNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClerkMainPageActivity.this, CBNetwork.class);
                startActivity(intent);
            }
        });

       clerkBankBenefit=(LinearLayout)findViewById(R.id.clerk_main_bank_benefit);
        clerkBankBenefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ClerkMainPageActivity.this, ChinaBankBenefit.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
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
        imageCycleView.startImageCycle();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageCycleView.pushImageCycle();
        MobclickAgent.onPause(this);
    }

}
