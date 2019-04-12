package com.xd.aselab.chinabank_shop.activity.Lobby.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.Lobby.Lobby_My_ExchangeScore;
import com.xd.aselab.chinabank_shop.activity.Lobby.Lobby_Recommend_ApplyCard;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.BenDiYouHuiActivity;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.CBNetwork;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.activity.Lobby.LobbyPerformance;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.JinRongFuWuActivity;
import com.xd.aselab.chinabank_shop.fragment.ImageCycleView;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class LobbyMainFragment extends Fragment {
    private LinearLayout myPerformance;
    private LinearLayout myRecommend;
    private LinearLayout myNetwork;
    private LinearLayout bankBenefit;
    private LinearLayout exchangeScore;
    private LinearLayout economyService;

    private View root;
    private SharePreferenceUtil sp;
    private TextView show_user_name;
    private Handler handler;
    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private ImageCycleView.ImageCycleViewListener cycleViewListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.activity_lobby_main_page, container, false);

        sp = new SharePreferenceUtil(getActivity(), "user");
        show_user_name = (TextView) root.findViewById(R.id.show_user_name);
        show_user_name.setText(sp.getWorkerName());

        myPerformance = (LinearLayout) root.findViewById(R.id.my_performance);
        myRecommend = (LinearLayout) root.findViewById(R.id.my_recommend);
        myNetwork = (LinearLayout) root.findViewById(R.id.my_network);
        bankBenefit = (LinearLayout) root.findViewById(R.id.bank_benefit);
        exchangeScore = (LinearLayout) root.findViewById(R.id.exchange_score);
        economyService = (LinearLayout) root.findViewById(R.id.economy_service);

        myPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LobbyPerformance.class);
                startActivity(intent);
            }
        });

        myRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Lobby_Recommend_ApplyCard.class);
                startActivity(intent);
            }
        });

        myNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "银行网点，正在开发中", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 23) {

                    //判断有没有定位权限
                    if (PermissionChecker.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || PermissionChecker.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //请求定位权限
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 10012);
                    }
                    else {
                        Intent intent = new Intent(getActivity(), CBNetwork.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(getActivity(), CBNetwork.class);
                    startActivity(intent);
                }
            }
        });

        bankBenefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BenDiYouHuiActivity.class);
                startActivity(intent);
            }
        });

        exchangeScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Lobby_My_ExchangeScore.class);
                startActivity(intent);
            }
        });

        economyService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JinRongFuWuActivity.class);
                startActivity(intent);
            }
        });

        SharePreferenceUtil sp = new SharePreferenceUtil(getActivity(), "user");
        JPushInterface.setAlias(getActivity(), 0, sp.getAccount());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            String reCode = (String) msg.obj;
                            Log.e("reCode", "reCode------" + reCode);
                            if (reCode != null) {
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    ArrayList<String> imageUrls = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        String picture_url = temp.getString("picture_url");
                                        imageUrls.add(picture_url);
                                    }
                                    if (imageUrls.size() > 0)
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    else {
                                        imageUrls.add("no_picture");
                                        imageCycleView.setImageResources(imageUrls, mAdCycleViewListener);
                                    }
                                } else {
                                    Log.e("MyContact_Activity", getActivity().getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("MyContact_Activity", "reCode为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("MyContact_Activity", getActivity().getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        imageCycleView = (ImageCycleView)root.findViewById(R.id.act_main_ImageCycleView);
        mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                if ("no_picture".equals(imageURL)){
                    imageView.setImageResource(R.drawable.placeholder2);
                }
                else {
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.loadBitmap(getActivity(), imageURL, imageView, 0);
                }
            }
        };

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 10012:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    Intent intent = new Intent(getActivity(), CBNetwork.class);
                    startActivity(intent);
                } else{
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getActivity(), "请允许定位", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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

    }

    @Override
    public void onPause() {
        super.onPause();
        imageCycleView.pushImageCycle();
    }


}
