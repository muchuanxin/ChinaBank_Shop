package com.hotrecommnder;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hotrecommnder.adapter.HotCardAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.HotCardVo;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainHotRecommenderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RelativeLayout ll_back;
    private ListView listview;
    private Dialog mDialog=null;
    private SharePreferenceUtil util;
    private int page=1;
    private boolean addOnce=true;
    private ArrayList<HotCardVo> datas=new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
              dismissRequestDialog();
              if(msg.what==0){
                  ArrayList<HotCardVo> items=(ArrayList<HotCardVo>)msg.obj;
                  Log.i("liuhaoxian","items size="+items.size());
                  updateUI(items, page);
              }
              else if (msg.what==1)
              {
                  Toast.makeText(MainHotRecommenderActivity.this, (String) msg.obj, Toast.LENGTH_SHORT);
              }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hot_recommender);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
        intData();
    }

    void initView(){
        ll_back=(RelativeLayout)findViewById(R.id.act_hot_recommmend_back_btn);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview=(ListView)findViewById(R.id.ls_view);
        listview.setOnItemClickListener(this);

    }

    void intData(){
        util=new SharePreferenceUtil(this,"user");
        page=1;
        getHotCardList(page);
    }

    void updateUI(ArrayList<HotCardVo> items,int page){
        if(page==1&&items.size()==0) {
            Toast.makeText(MainHotRecommenderActivity.this, "当前没有热卡推荐", Toast.LENGTH_SHORT).show();
        }
        else if(page!=1&&items.size()==0){
            Toast.makeText(MainHotRecommenderActivity.this, "当前没有更多卡片", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.i("liuhaoxian","size="+items.size());
            this.page++;
            for(HotCardVo vo:items)
                datas.add(vo);
            if (datas.size()!=0&&addOnce){
                View view=(View)getLayoutInflater().inflate(R.layout.load_more_item,null);
                LinearLayout ll=(LinearLayout)view.findViewById(R.id.ll_loadmore);
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getHotCardList(MainHotRecommenderActivity.this.page);
                    }
                });
                listview.addFooterView(view);
                addOnce=false;

            }
            listview.setAdapter(new HotCardAdapter(datas,this));

        }
    }
    void getHotCardList(final int page){
        showRequestDialog();
        new Thread(){
            @Override
            public void run() {

                PostParameter[] params=new PostParameter[3];

                params[0]=new PostParameter("account",util.getAccount());
                params[1]=new PostParameter("cookie",util.getCookie());
                params[2]=new PostParameter("page",""+page);
                String jsonStr= ConnectUtil.httpRequest(ConnectUtil.SERVER+"Common/GetHotCards",params,"POST");
                Message msg=handler.obtainMessage();
                //0为OK，1为异常
                if(jsonStr!=null){
                    try {
                        JSONObject json=new JSONObject(jsonStr);
                        String status=json.getString("status");
                        if(status.equals("true")){
                            JSONArray jsons=json.getJSONArray("list");

                            ArrayList<HotCardVo> temp=new ArrayList<>();
                            for(int i=0;i<jsons.length();i++){
                                JSONObject elem=(JSONObject)jsons.get(i);
                                HotCardVo vo=new HotCardVo();
                                vo.setCardName(elem.getString("title"));
                                vo.setDesc(elem.getString("des"));
                                vo.setImageUrl(elem.getString("ima_url"));
                                vo.setContent(elem.getString("content"));
                                /*JSONArray items=elem.getJSONArray("imageItems");
                                for(int j=0;j<items.length();j++)
                                {
                                    JSONObject str=(JSONObject)items.get(j);
                                    vo.getItems().add(str.getString("item"));
                                }*/
                                temp.add(vo);
                            }
                            //提交给handler
                            msg.what=0;
                            msg.obj=temp;

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what=1;
                        msg.obj="出现未知错误";
                    }

                }
                else
                {
                    msg.what=1;
                    msg.obj="无法连接到服务器";
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         Log.i("liuhaoxian","click item="+position);
         HotCardVo vo=datas.get(position);
         Intent intent=new Intent(this,MainHotCardDetailActivity.class);
         intent.putExtra("content",vo.getContent());
         startActivity(intent);
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
