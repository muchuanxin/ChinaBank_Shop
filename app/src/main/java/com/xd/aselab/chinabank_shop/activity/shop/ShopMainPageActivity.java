package com.xd.aselab.chinabank_shop.activity.shop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotrecommnder.MainHotRecommenderActivity;
import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.CBNetwork;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.MyContactActivity;
import com.xd.aselab.chinabank_shop.activity.worker.ClerkMainPageActivity;
import com.xd.aselab.chinabank_shop.fragment.ImageCycleView;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopMainPageActivity extends AppCompatActivity {
    private TextView myContact;
    private TextView welcomeInfo;
    private ImageView myInfo;
    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private LinearLayout hotRecommend;
    private LinearLayout myManagement;
    private LinearLayout bankNetwork;
    private LinearLayout bankBenefit;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        myContact=(TextView) findViewById(R.id.btn_shop_main_my_contact);
        myContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ShopMainPageActivity.this, MyContactActivity.class);
                startActivity(intent);
            }
        });

        welcomeInfo=(TextView)findViewById(R.id.act_main_shop_toast_i);
        welcomeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastCustom.makeToastCenter(getApplicationContext(),"欢迎您");
            }
        });

        myInfo=(ImageView) findViewById(R.id.btn_main_shop_info);
        myInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMainPageActivity.this,MyInfoActivity.class);
                startActivity(intent);
            }
        });

        imageCycleView = (ImageCycleView) findViewById(R.id.act_main_ImageCycleView);

        mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
              //  imageView.setImageResource(Integer.valueOf(imageURL).intValue());
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadBitmap(ShopMainPageActivity.this, imageURL, imageView, 0);
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
                                    Toast.makeText(ShopMainPageActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                                    Log.e("MyContact_Activity", ShopMainPageActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ShopMainPageActivity.this, ShopMainPageActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", ShopMainPageActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        /*ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add("images/app/top1.jpg");
        imageUrls.add("images/app/top2.jpg");
        imageUrls.add("images/app/top3.jpg");
        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);*/

        hotRecommend=(LinearLayout)findViewById(R.id.shop_main_hot_recommend);
        hotRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMainPageActivity.this,MainHotRecommenderActivity.class);
               // intent.setClass(ShopMainPageActivity.this,ShopInfoActivity.class);
                startActivity(intent);
            }
        });

        myManagement=(LinearLayout)findViewById(R.id.shop_main_my_management);
        myManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMainPageActivity.this,ShopMyManagement.class);
                startActivity(intent);
            }
        });

        bankNetwork=(LinearLayout)findViewById(R.id.shop_main_my_network);
        bankNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {

                    //判断有没有定位权限
                    if (PermissionChecker.checkSelfPermission(ShopMainPageActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || PermissionChecker.checkSelfPermission(ShopMainPageActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //请求定位权限
                        ActivityCompat.requestPermissions(ShopMainPageActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 10012);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setClass(ShopMainPageActivity.this, CBNetwork.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(ShopMainPageActivity.this, CBNetwork.class);
                    startActivity(intent);
                }
            }
        });

        bankBenefit=(LinearLayout)findViewById(R.id.shop_main_china_bank_benefit);
        bankBenefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShopMainPageActivity.this,ChinaBankBenefit.class);
                startActivity(intent);

            }
        });
    }

    //定位权限获取回调
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 10012:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    Intent intent = new Intent(ShopMainPageActivity.this, CBNetwork.class);
                    startActivity(intent);
                } else{
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(ShopMainPageActivity.this, "请允许定位", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
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
