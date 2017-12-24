package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChinaBankBenefit extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;

    private Handler handler;
    private List<Map<String, String >> list = new ArrayList<>();
    private CBBenefitAdapter adapter;
    private boolean addOnce = true;
    private int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_china_bank_benefit);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back = (RelativeLayout) findViewById(R.id.act_china_bank_benefit_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_china_bank_benefit_list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ChinaBankBenefit.this, CBBenefitDetail.class);
                intent.putExtra("content", list.get(position).get("content"));
                startActivity(intent);
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ChinaBankBenefit：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ChinaBankBenefit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    JSONArray jsonArray = json.getJSONArray("list");
                                    List<Map<String, String >> temp_list = new ArrayList<>();
                                    for (int i=0; i<jsonArray.length(); i++){
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        Map<String, String> map = new HashMap<>();
                                                   map.put("title", temp.getString("title"));
                                        map.put("content", temp.getString("content"));
                                        map.put("ima_url", temp.getString("ima_url"));
                                        temp_list.add(map);
                                        list.add(map);
                                    }
                                    if(page==1 && temp_list.size()==0) {
                                        Toast.makeText(ChinaBankBenefit.this, "当前没有银行福利", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(page!=1 && temp_list.size()==0){
                                        Toast.makeText(ChinaBankBenefit.this, "当前没有更多福利", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        page++;
                                        adapter = new CBBenefitAdapter(ChinaBankBenefit.this, list);
                                        list_view.setAdapter(adapter);
                                        if (list.size()!=0 && addOnce){
                                            View view = getLayoutInflater().inflate(R.layout.load_more_item,null);
                                            LinearLayout ll=(LinearLayout)view.findViewById(R.id.ll_loadmore);
                                            ll.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    getCBBenefit();
                                                }
                                            });
                                            list_view.addFooterView(view);
                                            addOnce=false;
                                        }
                                    }
                                } else {
                                    Log.e("CBBenefit_Activity", ChinaBankBenefit.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ChinaBankBenefit.this, ChinaBankBenefit.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("CBBenefit_Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("CBBenefit_Activity", ChinaBankBenefit.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        getCBBenefit();

    }

    private void getCBBenefit(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                SharePreferenceUtil spu = new SharePreferenceUtil(ChinaBankBenefit.this, "user");
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("page", ""+page);
                params[1] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.CHINA_BANK_BENEFIT, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private class CBBenefitAdapter extends BaseAdapter{

        private Context context=null;
        private List<Map<String,String>> list=null;

        public CBBenefitAdapter (Context context, List<Map<String,String>> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Map<String,String> getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder=null;
            if (convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_cb_benefit, null);
                viewHolder=new ViewHolder();
                viewHolder.img = (ImageView)convertView.findViewById(R.id.list_view_cb_benefit_img);
                viewHolder.txt = (TextView)convertView.findViewById(R.id.list_view_cb_benefit_txt);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadBitmap(context, list.get(position).get("ima_url"), viewHolder.img, 0);
            viewHolder.txt.setText(list.get(position).get("title"));
            viewHolder.txt.setAlpha(0.6f);
            return convertView;
        }

        private class ViewHolder{
            public ImageView img;
            public TextView txt;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
