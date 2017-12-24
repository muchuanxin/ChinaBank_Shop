package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.fragment.ChatFragment_Right;
import com.xd.aselab.chinabank_shop.photo.Compat;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.SlidingDrawerGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class Group_info extends AppCompatActivity {

    private ImageView return_button;
    private CircleImageView owner_headimage;
    private CircleImageView group_image;
    private TextView submit;
    private TextView owner_name;
    private TextView group_tag;
    private int member_number;
    private List list;
    private String[][] member_info;
    private SharePreferenceUtil sp;
    private GridLayout grid_layout;
    private SlidingDrawerGridView grid_view;
    private boolean flag;
    private String head_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(Group_info.this, "user");

        grid_view = (SlidingDrawerGridView) findViewById(R.id.grid_view);
        submit = (TextView) findViewById(R.id.submit);
        group_image = (CircleImageView) findViewById(R.id.group_image);
        Intent intent = getIntent();
        String temp = intent.getStringExtra("manage_info");
        owner_name = (TextView) findViewById(R.id.owner_name);
//        grid_layout = (GridLayout) findViewById(R.id.grid_layout);
        owner_headimage = (CircleImageView) findViewById(R.id.owner_headimage);
        group_tag = (TextView) findViewById(R.id.group_tag);
        list = new ArrayList();
        flag = false;

        //网格用户处  设置监听  如果成功踢人则  网格重新设置adapter
/*        grid_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        PostParameter[] post = new PostParameter[3];
                        post[0] = new PostParameter("member_account", member_info[0][grid_layout.getId()]);
                        post[1] = new PostParameter("owner_account", sp.getAccount());
                        post[2] = new PostParameter("group_id", getIntent().getIntExtra("group_id", 0) + "");
                        String jsonStr = ConnectUtil.httpRequest(ConnectUtil.ExpelMemberFromGroup, post, "POST");
                        Message msg = handler.obtainMessage();
                        if (("").equals(jsonStr) || null == jsonStr) {
                            msg.what = 0;
                            msg.obj = "服务器连接失败";
                        } else {
                            msg.what = 3;
                            msg.obj = jsonStr;
                        }
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });*/
        //right1  是我加入的群  不可踢人
        if (getIntent().getStringExtra("manage_info").equals("right1")) {
            grid_view.setEnabled(false);
        } else if (getIntent().getStringExtra("manage_info").equals("right0")) {
            grid_view.setClickable(true);
            grid_view.setEnabled(true);
        }
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == list.size() - 1) {

                    Intent intent = new Intent(Group_info.this, NewGroup.class);
                    intent.putExtra("jump", "add");
                    intent.putExtra("group_id", getIntent().getStringExtra("group_id"));
                    intent.putExtra("group_name", getIntent().getStringExtra("group_name"));
                    startActivity(intent);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Group_info.this);
                    builder.setTitle("温馨提示");
//                    builder.setMessage("是否将  " + member_info[1][position] + "  逐出该群？");

                    Map map = new HashMap();
                    map = (Map) list.get(position);
                    builder.setMessage("是否将  " + map.get("name") + "  逐出该群？");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    PostParameter[] post = new PostParameter[5];
//                                    post[0] = new PostParameter("member_account", member_info[0][position]);
                                    Map map = new HashMap();
                                    map = (Map) list.get(position);
                                    post[0] = new PostParameter("member_account", map.get("account") + "");
                                    post[1] = new PostParameter("owner_account", sp.getAccount());
                                    post[2] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                                    post[3] = new PostParameter("owner_name",sp.getWorkerName());
                                    post[4] = new PostParameter("group_name", getIntent().getStringExtra("group_name") );
                                    Log.d("Dorise 12 16 gp owner",sp.getWorkerName());
                                    Log.d("Dorise 12 16 gp name",getIntent().getStringExtra("group_name"));


                                    String jsonStr = ConnectUtil.httpRequest(ConnectUtil.ExpelMemberFromGroup, post, "POST");
                                    Log.d("Dorise jsonStr踢人", jsonStr);
                                    Message msg = handler.obtainMessage();
                                    if (("").equals(jsonStr) || null == jsonStr) {
                                        msg.what = 0;
                                        msg.obj = "服务器连接失败";
                                    } else {
                                        msg.what = 3;
                                        msg.arg1 = position;
                                        msg.obj = jsonStr;
                                    }
                                    handler.sendMessage(msg);
                                }
                            }.start();

                        }
                    });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

        head_image = getIntent().getStringExtra("head_image");
        ImageLoader loader = ImageLoader.getInstance();
        loader.loadBitmap(Group_info.this, head_image, group_image, R.drawable.default_pic);
        group_tag.setText(getIntent().getStringExtra("group_name"));
        if (temp.equals("right0")) {
            //是群主的话还应该具备  添加群成员的功能
            submit.setText("解散该群");

            //群主可以更换群头像
            group_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Group_info.this, ChangePhotoActivity.class);
                    intent.putExtra("jump", "group_head");
                    intent.putExtra("group_id", getIntent().getStringExtra("group_id"));
                    intent.putExtra("head_image", getIntent().getStringExtra("head_image"));
                    startActivityForResult(intent, Constants.LOCAL_PHOTO);
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnclick("owner");
                }
            });

        } else if (temp.equals("right1")) {
//当前是我加入的群    群成员身份
            group_tag.setText(getIntent().getStringExtra("group_name"));
            submit.setText("退出该群");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnclick("member");
                }
            });

        }

        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            JSONObject obj = null;
            try {
                obj = new JSONObject((String) msg.obj);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //获取群成员信息
                        if (obj.getString("status").equals("false")) {
                            Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else if (obj.getString("status").equals("true")) {
                            JSONObject owner = obj.getJSONObject("owner");
                            owner_name.setText(owner.getString("name"));
                            //设置群主头像

                            ImageLoader loader = ImageLoader.getInstance();
                            loader.loadBitmap(Group_info.this, owner.getString("head_image"), owner_headimage, R.drawable.default_head);

                            JSONArray member = obj.getJSONArray("member");

                            member_number = member.length();
//                            member_info = new String[3][member_number + 1];

                            for (int i = 0; i < member_number; i++) {
                                Map map = new HashMap();
                                JSONObject info = (JSONObject) member.get(i);
                                map.put("account", info.getString("account"));
                                map.put("name", info.getString("name"));
                                map.put("head_image", info.getString("head_image"));
                                list.add(map);
//                                member_info[0][i] = info.getString("account");
//                                member_info[1][i] = info.getString("name");
//                                member_info[2][i] = info.getString("head_image");
                            }


//                            member_info[0][member_number] = "";
//                            member_info[1][member_number] = "";
//                            member_info[2][member_number] = "";
                            if (getIntent().getStringExtra("manage_info").equals("right0")) {
                                Map map = new HashMap();

                                map.put("account", "");
                                map.put("name", "");
                                map.put("head_image", "");
                                list.add(map);
                            }
                            MyAdapter adapter;
//                            = new MyAdapter();
//                            grid_view.setAdapter(adapter);

                            if (null == grid_view.getAdapter()) {
                                adapter = new MyAdapter(list);
                                Log.d("Dorise 逐出", "获取到空的adapter");
                            } else {
                                Log.d("Dorise 逐出", "获取到非空adapter");
                                adapter = (MyAdapter) grid_view.getAdapter();
                                adapter.notifyDataSetChanged();
                            }
                            grid_view.setAdapter(adapter);


                        }


                        break;
                    case 2:

                        if (obj.getString("status").equals("true")) {
//解散成功  或者退群成功的话  就返回原来  主activity
                            //退群清除Tag
                            HashSet<String> group_id_set = new HashSet<>();
                            group_id_set.add(getIntent().getStringExtra("group_id"));
                            JPushInterface.deleteTags(Group_info.this, 2, group_id_set);

                            Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Group_info.this, MainActivity.class);
                            startActivity(intent);
                        } else if (obj.getString("status").equals("false")) {
                            Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case 3:
                        //群主踢人
                        Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        if (obj.getString("status").equals("true")) {
//                            info_map.remove(msg.arg1);
                            list.remove(msg.arg1);
//                            member_number -= 1;
                            //成功的话gridlayout 重新加载
//                            MyAdapter adapter = new MyAdapter(list);
//                            grid_view.setAdapter(adapter);
                            MyAdapter adapter = (MyAdapter) grid_view.getAdapter();
                            adapter.notifyDataSetChanged();
                        } else if (obj.getString("status").equals("false")) {
//                            Toast.makeText(Group_info.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }
/*                        MyAdapter adapter;
                        if (null == grid_view.getAdapter()) {
                            adapter = new MyAdapter();
                        } else {
                            Log.d("Dorise 逐出", "获取到非空adapter");
                            adapter = (MyAdapter) grid_view.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                        grid_view.setAdapter(adapter);*/

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };

    void setOnclick(final String user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Group_info.this);
        builder.setTitle("温馨提示");
        if (user.equals("owner")) {
            builder.setMessage("是否解散该群？");
        } else if (user.equals("member")) {
            builder.setMessage("是否退出该群？");
        }


        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();


                        String jsonStr = null;
                        if (user.equals("owner")) {
                            PostParameter[] post = new PostParameter[3];
                            post[0] = new PostParameter("account", sp.getAccount());
                            post[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                            post[2] = new PostParameter("group_name", getIntent().getStringExtra("group_name") + "");
                            Log.d("Dorise 12 16 gp name", getIntent().getStringExtra("group_name") + "");
                            jsonStr = ConnectUtil.httpRequest(ConnectUtil.DissolveChatGroup, post, "POST");

                        } else if (user.equals("member")) {
                            PostParameter[] post = new PostParameter[2];
                            post[0] = new PostParameter("account", sp.getAccount());
                            post[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                            jsonStr = ConnectUtil.httpRequest(ConnectUtil.LeaveChatGroup, post, "POST");

                        }
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
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ImageLoader loader = ImageLoader.getInstance();
            loader.loadBitmap(Group_info.this, sp.getGroup_Head_image(), group_image, R.drawable.default_head);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//
//        ImageLoader loader=ImageLoader.getInstance();
//        loader.loadBitmap(Group_info.this,sp.getGroup_Head_image(),group_image,R.drawable.default_head);

//获取群成员信息

        new Thread() {
            @Override
            public void run() {
                super.run();
                list.clear();
                PostParameter[] post = new PostParameter[1];
                post[0] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");


                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetGroupMember, post, "POST");

                Log.d("Dorise--jsonStr", jsonStr + "111111");
                Message msg = handler.obtainMessage();
                if (("").equals(jsonStr) || null == jsonStr) {
                    msg.what = 0;
                    msg.obj = "连接服务器失败";
                } else {
                    msg.what = 1;
                    msg.obj = jsonStr;
                }
                handler.sendMessage(msg);
            }
        }.start();


    }

    class MyAdapter extends BaseAdapter {
        ViewHolder holder;
        View myview;
        List list;

        MyAdapter(List list) {
            this.list = new ArrayList();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                myview = getLayoutInflater().inflate(R.layout.chardiv_chat_person_info, parent, false);
                holder.name = (TextView) myview.findViewById(R.id.name_text);
                holder.image = (ImageView) myview.findViewById(R.id.head_image);
                myview.setTag(holder);
            } else {
                myview = convertView;
                holder = (ViewHolder) myview.getTag();
            }
            ImageLoader loader = ImageLoader.getInstance();
            Map temp = new HashMap();
            temp = (Map) list.get(position);

            if (temp.get("name").equals("")) {
                holder.image.setImageResource(R.drawable.add_group);
//                loader.loadBitmap(Group_info.this, member_info[2][position], holder.image, R.drawable.add_group);
                holder.name.setText("");

            } else {
//                holder.name.setText(member_info[1][position]);
                holder.name.setText(temp.get("name") + "");
                loader.loadBitmap(Group_info.this, temp.get("head_image") + "", holder.image, R.drawable.default_head);
            }
            return myview;
        }

    }

    class ViewHolder {
        TextView name;
        ImageView image;
    }


}
