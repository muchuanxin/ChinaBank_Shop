package com.xd.aselab.chinabank_shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDivMainPage;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Info;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Performance;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Recommend;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.CBNetwork;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/12/4.
 */

public class MainFragment extends Fragment {

    private ImageCycleView imageCycleView;
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener;
    private LinearLayout my_performance;
    private LinearLayout hotpoint;
    private LinearLayout my_network;
    private LinearLayout bank_benefit;
    private ImageView myinfo;
    private ImageCycleView cycleView;
    private ImageCycleView.ImageCycleViewListener cycleViewListener;
    private Handler handler;
    private TextView show_user_name;
    private View root;
    private SharePreferenceUtil sp;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_card_div_main_page, container, false);


        super.onCreate(savedInstanceState);
        sp = new SharePreferenceUtil(getActivity(), "user");
        show_user_name = (TextView) root.findViewById(R.id.show_user_name);
        show_user_name.setText(sp.getWorkerName());
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        my_performance = (LinearLayout) root.findViewById(R.id.my_performance);
        my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_My_Performance.class);
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


        hotpoint = (LinearLayout) root.findViewById(R.id.hotpoint);
        hotpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_My_Recommend.class);
                startActivity(intent);
            }
        });
        my_network = (LinearLayout) root.findViewById(R.id.my_network);
        my_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "银行网点，正在开发中", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CBNetwork.class);
                startActivity(intent);
            }
        });
        bank_benefit = (LinearLayout) root.findViewById(R.id.bank_benefit);
        bank_benefit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChinaBankBenefit.class);
                startActivity(intent);
            }
        });
/*        myinfo = (ImageView) root.findViewById(R.id.btn_main_clerk_info);
        myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_My_Info.class);
                startActivity(intent);
            }
        });*/
        //    LinearLayout = (LinearLayout) root.findViewById(R.id.);
//    LinearLayout = (LinearLayout) root.findViewById(R.id.);
//    LinearLayout = (LinearLayout) root.findViewById(R.id.bank_benefit);
//    ImageView = (ImageView) root.findViewById(R.id.btn_main_clerk_info);
//
//        myinfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(getActivity(),CardDiv_My_Info.class);
//                startActivity(intent);
//            }
//        });
//        hotpoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(getActivity(),CardDiv_My_Recommend.class);
//                startActivity(intent);
//            }
//        });
//
//
//        my_network.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), CBNetwork.class);
//                startActivity(intent);
//            }
//        });
//
//
//        bank_benefit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setClass(getActivity(),ChinaBankBenefit.class);
//                startActivity(intent);
//
//            }
//        });


        return root;
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
