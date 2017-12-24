package com.xd.aselab.chinabank_shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.Chat_Activity;
import com.xd.aselab.chinabank_shop.chat.ui.ChatActivity;
import com.xd.aselab.chinabank_shop.chat.ui.GroupChatActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.MyExtendableAdapter;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/7.
 */

public class ChatFragment_Right extends Fragment {
    private View root;
    private MyExtendableAdapter adapter;
    private ExpandableListView extendable_listview;
    private SharePreferenceUtil sp;
    private String[][] child_head;
    private int group1_number;
    private int group2_number;
    private JSONArray group1;
    private JSONArray group2;
    private int[][] group_id;
    private String[][] group_name;

    String[] parent = new String[]{"我创建的群", "我加入的群"};


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.chat_fragment_right, container, false);


        extendable_listview = (ExpandableListView) root.findViewById(R.id.extendable_listview);
        sp = new SharePreferenceUtil(getActivity(), "user");


        extendable_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getContext(), GroupChatActivity.class);
                if (groupPosition != 0) {
                    //right11111   =说明是群聊   我加入的群
                    intent.putExtra("group_info", "right1");
                    intent.putExtra("group_id", ""+group_id[1][childPosition]);
                    intent.putExtra("group_head", child_head[1][childPosition]);
                    Log.d("Dorise 20177 128 139", child_head[1][childPosition] + "");
                    intent.putExtra("group_name", group_name[1][childPosition]);
                } else {
                    //right00000  =说明是群聊   我创建的群
                    //如果是群聊  读取当前群的id  保存在sp中  在查看或者管理群信息的时候  要传群id号
                    intent.putExtra("group_info", "right0");
//                    Bundle bundle=new Bundle();

//                    intent.putExtra("group_info", (Parcelable) list);
                    intent.putExtra("group_id", ""+group_id[0][childPosition]);
                    intent.putExtra("group_head", child_head[0][childPosition]);
                    Log.d("Dorise 我创建的群 ", child_head[0][childPosition] + "");
                    intent.putExtra("group_name", group_name[0][childPosition]);
                }
                startActivity(intent);
                return false;
            }
        });

        return root;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                        Map map = (Map) msg.obj;
                        String jsonStr = map.get("jsonStr") + "";
                        JSONObject obj = new JSONObject(jsonStr);
                        if (obj.getString("status").equals("false")) {
                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            break;
                        } else if (obj.getString("status").equals("true")) {
                            group1 = obj.getJSONArray("group");
                            group1_number = group1.length();

                        }
                        String IjoinStr = map.get("IjoinStr") + "";
                        JSONObject Ijoinobj = new JSONObject(IjoinStr);
                        if (Ijoinobj.getString("status").equals("false")) {
                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            break;
                        } else if (Ijoinobj.getString("status").equals("true")) {
                            group2 = Ijoinobj.getJSONArray("group");
                            group2_number = group2.length();

                        }

                        int group_number = (group1_number > group2_number) ? group1_number : group2_number;
                        group_name = new String[2][group_number];
                        child_head = new String[2][group_number];
                        group_id = new int[2][group_number];

                        //group_head  是  000我创建的群和111我加入的群  两个分组的头像
                        //group——id 是  000我创建的群和111我加入的群  每个组的id
                        for (int i = 0; i < group1_number; i++) {
                            Log.d("Dorise_group_info", group1.get(i) + "");
                            JSONObject one_group_info = (JSONObject) group1.get(i);
                            group_name[0][i] = one_group_info.getString("group_name");
                            child_head[0][i] = one_group_info.getString("head_image");
                            group_id[0][i] = one_group_info.getInt("group_id");
                        }
                        for (int i = 0; i < group2_number; i++) {
                            Log.d("Dorise_group_info", group2.get(i) + "");
                            JSONObject one_group_info = (JSONObject) group2.get(i);
                            group_name[1][i] = one_group_info.getString("group_name");
                            child_head[1][i] = one_group_info.getString("head_image");
                            group_id[1][i] = one_group_info.getInt("group_id");

                        }

                        adapter = new MyExtendableAdapter(parent, group_name, getContext(), false, child_head);
                        extendable_listview.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        final PostParameter[] post = new PostParameter[1];

        final Message msg = handler.obtainMessage();
        post[0] = new PostParameter("account", sp.getAccount());
        new Thread() {
            @Override
            public void run() {
                super.run();
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetMyCreateGroup, post, "POST");
                String IjoinStr = ConnectUtil.httpRequest(ConnectUtil.GetMyJoinGroup, post, "POST");
 Log.d("我加入的群",IjoinStr);
                //我创建的群和我加入的群都不能为空
                if (null == jsonStr || null == IjoinStr) {
                    msg.what = 0;
                    msg.obj = "连接服务器失败";
                } else {
                    msg.what = 1;
                    Map map = new HashMap();
                    map.put("jsonStr", jsonStr);

                    map.put("IjoinStr", IjoinStr);
                    msg.obj = map;
                }
                handler.sendMessage(msg);
            }

        }.start();

    }

}
