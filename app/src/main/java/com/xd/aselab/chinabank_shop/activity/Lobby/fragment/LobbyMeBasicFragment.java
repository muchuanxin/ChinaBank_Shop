package com.xd.aselab.chinabank_shop.activity.Lobby.fragment;


import android.content.Intent;
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
import com.xd.aselab.chinabank_shop.activity.Lobby.Lobby_My_ExchangeScore;
import com.xd.aselab.chinabank_shop.activity.Lobby.Lobby_My_Info;
import com.xd.aselab.chinabank_shop.activity.Lobby.Lobby_My_Page;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LobbyMeBasicFragment extends Fragment {

    private SharePreferenceUtil sp;
    private ImageView dafault_head;
    private ImageLoader imageLoader;
    private ImageView headImage;
    private TextView userName;
    private LinearLayout my_info_row;
    private LinearLayout check_announcement_row;
    private LinearLayout terminate_row;
    private LinearLayout exchange_score_row;
    private RelativeLayout back;
    private View root;

    public LobbyMeBasicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_lobby_my_page, container, false);

        dafault_head = (ImageView) root.findViewById(R.id.dafault_head);
        sp = new SharePreferenceUtil(getActivity(), "user");
        userName = (TextView) root.findViewById(R.id.user_name);
        exchange_score_row=(LinearLayout) root.findViewById(R.id.exchange_score_line);
        userName.setText(sp.getWorkerName());
        headImage = (ImageView) root.findViewById(R.id.dafault_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(getActivity(), sp.getHead_image(), headImage, R.drawable.final_head);

        my_info_row = (LinearLayout) root.findViewById(R.id.myhead);
        exchange_score_row = (LinearLayout) root.findViewById(R.id.exchange_score_line);
        //check_announcement_row = (LinearLayout) root.findViewById(R.id.check_announcement_row);
        //terminate_row = (LinearLayout) root.findViewById(R.id.terminate_row);

        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Lobby_My_Info.class);
                startActivity(intent);
            }
        });

        exchange_score_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Lobby_My_ExchangeScore.class);
                startActivity(intent);
            }
        });
        //check_announcement_row.setOnClickListener(null);
        //terminate_row.setOnClickListener(null);

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
