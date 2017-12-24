package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.MyExtendableAdapter;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NewGroup extends AppCompatActivity {

    private ImageView return_button;
    private MyExtendableAdapter adapter;
    private ExpandableListView extendable_listview;
    private Button submit;
    private SharePreferenceUtil sp;
    private String group_name;
    private String member_account;
    private String[][] head_image;
    private EditText group_name_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp = new SharePreferenceUtil(NewGroup.this, "user");
        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        group_name_id = (EditText) findViewById(R.id.group_name);

        String[] parent = new String[]{"我的上级"};
        String[][] child = new String[][]{{sp.getManagerName()}};
        head_image = new String[1][1];
        head_image[0][0] = sp.getCardDivManagerImage();
        extendable_listview = (ExpandableListView) findViewById(R.id.extendable_listview);
        adapter = new MyExtendableAdapter(parent, child, NewGroup.this, true, head_image);
        View foot=getLayoutInflater().inflate(R.layout.foot,extendable_listview,false);
        extendable_listview.addFooterView(foot);
        submit = (Button) foot. findViewById(R.id.submit);
        extendable_listview.setAdapter(adapter);


        if (getIntent().getStringExtra("jump").equals("add")) {
            TextView text = (TextView) findViewById(R.id.title);
            text.setText("添加成员");
            findViewById(R.id.linear).setVisibility(View.GONE);
            findViewById(R.id.add_member).setVisibility(View.GONE);
        } else if (getIntent().getStringExtra("jump").equals("new")) {
            TextView text = (TextView) findViewById(R.id.title);
            text.setText("新建群");
            findViewById(R.id.linear).setVisibility(View.VISIBLE);
            findViewById(R.id.add_member).setVisibility(View.VISIBLE);
        }


//        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("jump").equals("new")) {
                    group_name = group_name_id.getText().toString().trim();
                    if (("").equals(group_name) || null == group_name) {
                        Toast.makeText(NewGroup.this, "请输入群名称", Toast.LENGTH_SHORT).show();
                        return;
                    }//如果没有选择群成员
                    else if (null == adapter.getList() || 0 == adapter.getList().size()) {
                        if (null == adapter.getList()) {
                            Log.d("Dorise_adapterlist", "adapterlist空");
                        }
                        Toast.makeText(NewGroup.this, "请选择群成员", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Thread() {

                            @Override
                            public void run() {
                                super.run();

                                String member_account = listToString(adapter.getList(), ',');
                                PostParameter[] post = new PostParameter[4];
                                post[0] = new PostParameter("group_name", group_name);
                                post[1] = new PostParameter("creator_account", sp.getAccount());
                                post[2] = new PostParameter("member_account", member_account);

                                post[3] = new PostParameter("creator_name", sp.getWorkerName());

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.CreateChatGroup, post, "POST");

                                Log.d("Dorise", group_name);
                                Log.d("Dorise creator_account", sp.getAccount());
                                Log.d("Dorise member_account", member_account);


                                Log.d("Dorise新建群后台返回", jsonStr);
                                Log.d("Dorise_PostParameter", post[2].getValue());
                                Message msg = handler.obtainMessage();

                                if (("").equals(jsonStr) || null == jsonStr) {
                                    msg.what = 0;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 1;
                                    msg.obj = jsonStr;

                                }
                                handler.sendMessage(msg);
                            }
                        }.start();


                    }
                } else if (getIntent().getStringExtra("jump").equals("add")) {
                    //add是加人
                    if (null == adapter.getList() || 0 == adapter.getList().size()) {
                        if (null == adapter.getList()) {
                            Log.d("Dorise_adapterlist", "adapterlist空");
                        }
                        Toast.makeText(NewGroup.this, "请选择群成员", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                String member_account = listToString(adapter.getList(), ',');
                                PostParameter[] post = new PostParameter[5];
                                post[0] = new PostParameter("owner_account", sp.getAccount());
                                post[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                                post[2] = new PostParameter("member_account", member_account);
                                post[3] = new PostParameter("owner_name", sp.getWorkerName());
                                post[4] = new PostParameter("group_name", getIntent().getStringExtra("group_name"));

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.InviteMemberToGroup, post, "POST");
                                Message msg = handler.obtainMessage();
                                if (("").equals(jsonStr) || null == jsonStr) {
                                    msg.what = 0;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 2;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);
                            }
                        }.start();
                    }

                }
            }
        });


    }

    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(NewGroup.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                            Log.d("Dorise建群", "成功");

                        } else if (obj.getString("status").equals("false")) {
                            Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            Log.d("Dorise建群", "失败");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:

                    try {
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                           finish();
                        } else if (obj.getString("status").equals("false")) {
                            Toast.makeText(NewGroup.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.d("Dorise建群", "失败");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };


}
