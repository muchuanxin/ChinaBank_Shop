package com.xd.aselab.chinabank_shop.activity.Lobby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.Lobby_Recommend_List_Item;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Recommend_List;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lobby_Recommend_List extends AppCompatActivity {

    private String jsonStr;
    private List final_list = new ArrayList();
    private ListView listView;
    private Dialog mDialog;
    private SharePreferenceUtil sp;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissRequestDialog();
            super.handleMessage(msg);

            jsonStr = (String) msg.obj;
            if(msg.what == 0) {
                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    if(obj.getString("status").equals(false)) {
                        Toast.makeText(Lobby_Recommend_List.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray arr = obj.getJSONArray("recommend");
                        for(int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            List<Map<String, String>> temp_List = new ArrayList<>();
                            Map<String, String> map = new HashMap<>();
                            map.put("id", temp.getString("id"));
                            map.put("application_id", temp.getString("application_id"));
                            map.put("time", temp.getString("time"));
                            map.put("confirm", temp.getString("confirm"));

                            temp_List.add(map);
                            final_list.add(map);
                            if(temp_List.size() == 0) {
                                Toast.makeText(Lobby_Recommend_List.this, "当前暂无用户信息", Toast.LENGTH_SHORT).show();
                            } else {
                                myAdapter adapter = new myAdapter(Lobby_Recommend_List.this, final_list);
                                listView.setAdapter(adapter);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Lobby_Recommend_List.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_recommend_div);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        sp = new SharePreferenceUtil(Lobby_Recommend_List.this, "user");
        ImageView return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        showRequestDialog();

        new Thread(){
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if(ConnectUtil.isNetworkAvailable(Lobby_Recommend_List.this)){
                    PostParameter[] postParameters = new PostParameter[2];
                    postParameters[0] = new PostParameter("account", sp.getAccount());
                    postParameters[1] = new PostParameter("range", getIntent().getStringExtra("scope"));

                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.LobbyMyRecommendList, postParameters, "POST");
                    if (null == jsonStr || "".equals(jsonStr)) {
                        msg.what = 1;
                        msg.obj = "提交失败";
                    } else {
                        msg.obj = jsonStr;
                        msg.what = 0;
                    }
                } else {
                    msg.what = 2;
                    msg.obj = getString(R.string.network_connect_exception);
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public class myAdapter extends BaseAdapter {
        private Context context;
        private List<Lobby_Recommend_List_Item> list;
        private Map myMap;

        public myAdapter(Context context, List list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //用来获取用户点击的是哪个list_item
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View myView;
            ViewHolder viewHolder;

            if (view == null) {
                myView = LayoutInflater.from(context).inflate(R.layout.lobby_recommend_list, null);
                viewHolder = new ViewHolder();
                viewHolder.application_id = (TextView) myView.findViewById(R.id.application_id);
                viewHolder.time_viewHolder = (TextView) myView.findViewById(R.id.time);
                viewHolder.confirm_viewHolder = (TextView) myView.findViewById(R.id.confirm);
                viewHolder.confirm_image = (ImageView) myView.findViewById(R.id.confirm_image);
                myView.setTag(viewHolder);
            } else {
                myView = view;
                viewHolder = (ViewHolder) myView.getTag();
            }
            myMap = (Map) final_list.get(position);
            viewHolder.application_id.setText(myMap.get("application_id") + "");
            viewHolder.time_viewHolder.setText(myMap.get("time") + "");
            viewHolder.confirm_viewHolder.setText( myMap.get("confirm") + "");
            DecimalFormat df = new DecimalFormat("#0.0000");

            int black = getResources().getColor(R.color.black);
            int grey = getResources().getColor(R.color.grey);
            int red = getResources().getColor(R.color.china_bank_red);

            if (myMap.get("confirm").equals("CHECK")) {
                viewHolder.confirm_viewHolder.setTextColor(red);
                viewHolder.confirm_viewHolder.setText("审核中");
                viewHolder.confirm_image.setImageResource(R.drawable.card_check);
            } else if (myMap.get("confirm").equals("SUCCESS")) {
                viewHolder.confirm_viewHolder.setTextColor(grey);
                viewHolder.confirm_viewHolder.setText("办卡成功");
                viewHolder.confirm_image.setImageResource(R.drawable.card_success);
            } else if (myMap.get("confirm").equals("FAIL")) {
                viewHolder.confirm_viewHolder.setTextColor(red);
                viewHolder.confirm_viewHolder.setText("办卡失败");
                viewHolder.confirm_image.setImageResource(R.drawable.card_fail);
            }

            return myView;
        }
    }

    private class ViewHolder {
        public TextView id_viewHolder;
        public TextView application_id;
        public TextView time_viewHolder;
        public TextView confirm_viewHolder;
        public ImageView confirm_image;

    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(Lobby_Recommend_List.this, "请稍等...");
        mDialog.show();
    }
    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
