package com.xd.aselab.chinabank_shop.activity.VirtualSales.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualExchangeScoreActivity;
import com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualMyInfoActivity;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import java.io.FileNotFoundException;

public class VirtualMeBasicFragment extends Fragment {

    private SharePreferenceUtil sp;
    private ImageLoader imageLoader;
    private ImageView headImage;
    private TextView userName;
    private LinearLayout my_info_row;
    private LinearLayout exchange_score_row;
    private RelativeLayout back;
    private View root;

    public VirtualMeBasicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_lobby_my_page, container, false);

        sp = new SharePreferenceUtil(getActivity(), "user");
        userName = (TextView) root.findViewById(R.id.user_name);
        exchange_score_row = (LinearLayout) root.findViewById(R.id.exchange_score_line);
        // 设定用户名和头像
        userName.setText(sp.getWorkerName());
        headImage = (ImageView) root.findViewById(R.id.dafault_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(getActivity(), sp.getHead_image(), headImage, R.drawable.final_head);
        my_info_row = (LinearLayout) root.findViewById(R.id.myhead);

        // 个人信息详情页
        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), VirtualMyInfoActivity.class);
                startActivityForResult(intent, 10);
            }
        });

        // 积分兑换相关设置
        exchange_score_row = (LinearLayout) root.findViewById(R.id.exchange_score_line);
        exchange_score_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), VirtualExchangeScoreActivity.class);
                startActivity(intent);
            }
        });

        //返回按钮设置
        back = (RelativeLayout) root.findViewById(R.id.shop_my_management_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return root;
    }
}
