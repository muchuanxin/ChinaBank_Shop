package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.CardDiv_Recommend_List_Item;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.ChinaBankBenefit;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.activity.shop.worksPerformanceList.WorkersPerAdapter;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CardDiv_My_Recommend_List extends AppCompatActivity {
    private SharePreferenceUtil sp;
    private String jsonStr;
    private Dialog mDialog = null;
    private List final_list = new ArrayList();
    private ListView listView;
    private final int REQUEST_CODE = 0x1001;
    private String phone;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissRequestDialog();
            super.handleMessage(msg);

            jsonStr = (String) msg.obj;
            if (msg.what == 0) {
                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    if (obj.getString("status").equals("false")) {
                        Toast.makeText(CardDiv_My_Recommend_List.this, obj.getString("message"), Toast.LENGTH_SHORT);
                    } else {

                        JSONArray arr = obj.getJSONArray("recommend");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            List<Map<String, String>> temp_list = new ArrayList<>();
                            Map<String, String> map = new HashMap<>();
                            map.put("id", temp.getString("id"));
                            map.put("applicant", temp.getString("applicant"));
                            map.put("telephone", temp.getString("telephone"));
                            map.put("time", temp.getString("time"));
                            map.put("money", temp.getString("money"));
                            map.put("installment_num", temp.getString("installment_num"));
//                            map.put("car_type", temp.getString("car_type"));
//                            map.put("evaluation", temp.getString("evaluation"));
                            map.put("confirm", temp.getString("confirm"));

                            temp_list.add(map);
                            final_list.add(map);
                            if (temp_list.size() == 0) {
                                Toast.makeText(CardDiv_My_Recommend_List.this, "当前暂无用户信息", Toast.LENGTH_SHORT).show();
                            } else {
                                myAdapter adapter = new myAdapter(CardDiv_My_Recommend_List.this, final_list);
                                listView.setAdapter(adapter);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(CardDiv_My_Recommend_List.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__recommend__div);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(CardDiv_My_Recommend_List.this, "user");
        ImageView return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(CardDiv_My_Recommend_List.this)) {
                    PostParameter[] postParameters = new PostParameter[2];
                    postParameters[0] = new PostParameter("account", sp.getAccount());
                    postParameters[1] = new PostParameter("range", getIntent().getStringExtra("scope"));

                    jsonStr = ConnectUtil.httpRequest(ConnectUtil.InstallmentWorkerMyRecommendList, postParameters, "POST");
                    if (!("").equals(jsonStr) && jsonStr != null) {
                        msg.obj = jsonStr;
                        msg.what = 0;
                    }
                } else {
                    msg.what = 1;
                    msg.obj = getString(R.string.network_connect_exception);

                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public class myAdapter extends BaseAdapter {
        private Context context;
        private List<CardDiv_Recommend_List_Item> list;
        private Map mymap;

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
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview;
            ViewHolder viewHolder;
//            CardDiv_Recommend_List_Item myitem= (CardDiv_Recommend_List_Item) getItem(i);
            if (view == null) {
                myview = LayoutInflater.from(context).inflate(R.layout.carddiv_recommend_list, null);
                viewHolder = new ViewHolder();
                viewHolder.name_viewholder = (TextView) myview.findViewById(R.id.name);
                viewHolder.time_viewholder = (TextView) myview.findViewById(R.id.time);
                viewHolder.tel = (TextView) myview.findViewById(R.id.tel);
                viewHolder.div_money = (TextView) myview.findViewById(R.id.div_money);
                viewHolder.div_mun = (TextView) myview.findViewById(R.id.div_num);
                viewHolder.purchase = (TextView) myview.findViewById(R.id.purchase);
//                viewHolder.evaluation = (TextView) myview.findViewById(R.id.evaluation);
                viewHolder.confirm = (TextView) myview.findViewById(R.id.confirm);
                viewHolder.image = (ImageView) myview.findViewById(R.id.head);
                myview.setTag(viewHolder);

            } else {
                myview = view;
                viewHolder = (ViewHolder) myview.getTag();
            }
            mymap = (Map) final_list.get(i);
            viewHolder.name_viewholder.setText(mymap.get("applicant") + "");
            viewHolder.time_viewholder.setText(mymap.get("time") + "");
            viewHolder.tel.setText("联系电话：" + mymap.get("telephone") + "");
            DecimalFormat df = new DecimalFormat("#0.0000");
            viewHolder.div_money.setText("分期金额：" + df.format(Double.valueOf(String.valueOf(mymap.get("money")))) +"万元");
            viewHolder.div_mun.setText("分期数：" + mymap.get("installment_num") +"月");
//            viewHolder.purchase.setText("汽车品牌：" + mymap.get("car_type") + "");
//            viewHolder.evaluation.setText("预授信评分：" + mymap.get("evaluation") + "");
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mymap.get("telephone")));

                    phone = (String) mymap.get("telephone");
/*                    if (ActivityCompat.checkSelfPermission(CardDiv_My_Recommend_List.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                    }
                    startActivity(intent);*/
//                        callPhone();

                    if (Build.VERSION.SDK_INT >= 23) {

                                //判断有没有拨打电话权限
                                if (PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                        //请求拨打电话权限
                                        ActivityCompat.requestPermissions(CardDiv_My_Recommend_List.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);

                                    } else {
                                    startActivity(intent);
                                    }

                            } else {
                        startActivity(intent);
                            }


            }


            });
            if (mymap.get("confirm").equals("CHECK")) {
                viewHolder.confirm.setText("未确认");
            } else if (mymap.get("confirm").equals("YES")) {
                viewHolder.confirm.setText("已确认");
            } else if (mymap.get("confirm").equals("NO")) {
                viewHolder.confirm.setText("已拒绝");
            } else if (mymap.get("confirm").equals("SUCCESS")) {
                viewHolder.confirm.setText("已放款");

            }

            return myview;
        }

/*        private void callPhone() {
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }*/

        private class ViewHolder {
            public TextView name_viewholder;
            public TextView time_viewholder;
            public TextView tel;
            public TextView div_money;
            public TextView div_mun;
            public TextView purchase;
//            public TextView evaluation;
            public TextView confirm;
            public ImageView image;
        }
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(CardDiv_My_Recommend_List.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    Constants.ActivityCompatRequestPermissionsCode);
            return;
        }
        Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
        startActivity(intent);
    }

}