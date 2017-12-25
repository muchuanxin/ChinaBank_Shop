package com.xd.aselab.chinabank_shop.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;


import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.Chat_Activity;
import com.xd.aselab.chinabank_shop.chat.ui.ChatActivity;
import com.xd.aselab.chinabank_shop.util.MyExtendableAdapter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

/**
 * Created by Administrator on 2017/12/7.
 */

public class ChatFragment_Left extends Fragment {
    private View root;
    private MyExtendableAdapter adapter;
    private ExpandableListView extendable_listview;
    private SharePreferenceUtil sp;
    private String[][] manager_head_image;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.chat_fragment_left, container, false);
        extendable_listview = (ExpandableListView) root.findViewById(R.id.expandable_listview);
        sp = new SharePreferenceUtil(mContext, "user");
        String[] parent = new String[]{"我的上级"};
        String[][] child = {{sp.getManagerName()}};
        manager_head_image = new String[][]{{sp.getCardDivManagerImage()}};
//false表示不显示人名字后面的  复选框
        adapter = new MyExtendableAdapter(parent, child, inflater.getContext(), false, manager_head_image);
        adapter.setCheckBoxInvisible();
        extendable_listview.setAdapter(adapter);
        extendable_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("receiver",sp.getShopManagerAccount());
                intent.putExtra("receiver_name",sp.getManagerName());
                intent.putExtra("receiver_head",sp.getCardDivManagerImage());
                intent.putExtra("jump", "left");
                startActivity(intent);
                return false;
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = getActivity();
        super.onAttach(context);
    }

}


