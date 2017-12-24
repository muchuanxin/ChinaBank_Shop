package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.NetWorkUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.PublicItemAdapter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogRecord;
import org.json.JSONObject;

public class CardDiv_Check_Public extends AppCompatActivity {

    private SimpleAdapter mSimpleAdapter;
    private Button bt;
    private ListView lv;
    private ProgressBar pg;
    private ArrayList<HashMap<String,Object>> list;
    private ArrayList<HashMap<String,Object>> sublist;
    private View moreview;
    private View ItemView;
    private Handler handler;
    private Handler btnhandler = new Handler();
    private int MaxDataNum;
    private  int lastVisibleIndex;
    private  int imageid = R.drawable.icon;
    private ImageView exit;
    List<Public_Information> listpublicinfo = new ArrayList<Public_Information>() ;
    private NetWorkUtil netWorkUtil;
    private Dialog mDialog=null;
private SharePreferenceUtil sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__check__public);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        exit= (ImageView) findViewById(R.id.exit);
sp=new SharePreferenceUtil(CardDiv_Check_Public.this,"user");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv = (ListView)findViewById(R.id.listView1) ;
        moreview = getLayoutInflater().inflate(R.layout.load_more_data,null);
        ItemView = getLayoutInflater().inflate(R.layout.carddiv_public_item,null);
        bt = (Button)moreview.findViewById(R.id.bt_load);
        pg = (ProgressBar)moreview.findViewById(R.id.pg);
        list = new ArrayList<HashMap<String, Object>>();
        sublist =  new ArrayList<HashMap<String, Object>>();

        findAllPublicInfo();

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        String key = "";
                        String value = "";
                        try{
                            String reCode = (String)msg.obj;
                            System.out.println(reCode+"+++++++++");
                            JSONObject jsonObject = new JSONObject(reCode);
                            JSONArray jsonArray = jsonObject.getJSONArray("announcement");
                            if(jsonArray.length()==0){
                                Toast.makeText(CardDiv_Check_Public.this,"当前暂无公告",Toast.LENGTH_SHORT).show();
                            }else
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                Public_Information tempinfo = new Public_Information();
                                JSONObject temp = jsonArray.getJSONObject(i);
                                Iterator iterator = temp.keys();
                                while (iterator.hasNext())
                                {
                                    key =(String)iterator.next();
                                    value = (String)temp.get(key);
                                    if (key.equals("id"))
                                    {
                                        tempinfo.setId(value);
                                    }
                                    else
                                    {
                                        if (key.equals("sender"))
                                        {
                                            tempinfo.setTitle(value);
                                        }
                                        else
                                        {
                                            if (key.equals("time"))
                                            {
                                                tempinfo.setTime(value);
                                            }
                                            else
                                            {
                                                if (key.equals("title"))
                                                {
                                                    tempinfo.setTitle(value);
                                                }
                                                else
                                                {
                                                    if (key.equals("content"))
                                                    {
                                                        tempinfo.setContent(value);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                                listpublicinfo.add(tempinfo);
                            }
                            MaxDataNum = listpublicinfo.size();
                            for (Public_Information public_information:listpublicinfo)
                            {
                                HashMap<String,Object> map = new HashMap<String,Object>();
                                map.put("images",imageid);
                                map.put("title",public_information.getTitle());
                                map.put("time",public_information.getTime());
                                map.put("id",public_information.getId());
                                map.put("content",public_information.getContent());
                                map.put("sender",public_information.getSender());
                                list.add(map);
                            }
                            //这个地方，新建一个临时的templist,先存一下list的前10个，然后在bt_load的点击事件中从count开始继续+5
                            if (list.size()>=10)
                            {
                                sublist.addAll(list.subList(0,9));
                            }
                            else
                            {
                                sublist.addAll(list.subList(0,list.size()-1));
                            }
                            mSimpleAdapter = new SimpleAdapter(CardDiv_Check_Public.this,sublist,R.layout.carddiv_public_item,new String[]{"images","title","time"},new int[]{R.id.image,R.id.text,R.id.text2});
                            lv.addFooterView(moreview);
                            lv.setAdapter(mSimpleAdapter);
                            //绑定监听器，响应点击事件,跳转到另一个界面
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                                    ListView listView = (ListView)parent;
                                    HashMap<String,Object> map = (HashMap<String, Object>)listView.getItemAtPosition(i);
                                    Intent intent=new Intent(CardDiv_Check_Public.this,CardDiv_Public_Info.class);
                                    intent.putExtra("map_info",map);
                                    startActivity(intent);

                                }
                            });


                            lv.setOnScrollListener(new AbsListView.OnScrollListener(){

                                @Override
                                public void onScrollStateChanged(AbsListView absListView, int i) {
                                    //滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
                                    if(i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE&&lastVisibleIndex==mSimpleAdapter.getCount())
                                    {
                                        pg.setVisibility(View.VISIBLE);
                                        bt.setVisibility(View.GONE);
                                        btnhandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadMoreData();
                                                bt.setVisibility(View.VISIBLE);
                                                pg.setVisibility(View.GONE);
                                                mSimpleAdapter.notifyDataSetChanged();
                                            }

                                        }, 2000);
                                    }
                                }

                                @Override
                                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;

                                    // 所有的条目已经和最大条数相等，则移除底部的View
                                    if (totalItemCount == MaxDataNum + 1) {
                                        bt.setText("数据全部加载完成，没有更多数据！");
                                    }

                                }
                            });
                            bt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mSimpleAdapter.getCount()==MaxDataNum)
                                    {
                                        pg.setVisibility(View.GONE);
                                        bt.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        pg.setVisibility(View.VISIBLE);
                                        bt.setVisibility(View.GONE);
                                        loadMoreData();
                                        bt.setVisibility(View.VISIBLE);
                                        pg.setVisibility(View.GONE);
                                        mSimpleAdapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                        catch (Exception e)
                        {
                            System.out.println("case0 转换出错");
                            e.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("huoqushibai");
                }
            }
        };




    }

    public  void  loadMoreData()
    {
        int count =mSimpleAdapter.getCount();
        try{
            if(count+5<MaxDataNum)
            {
                for (int i=count;i<count+5;i++)
                {
                    sublist.add(list.get(i));
                }
            }
            else
            {
                for (int i =count;i<MaxDataNum;i++)
                {
                    sublist.add(list.get(i));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public  void findAllPublicInfo()
    {
        final String url = "GetAnnouncement";
        final String user_account = sp.getAccount();

        //不可访问的时候是Null
        if(!ConnectUtil.isNetworkAvailable(this))
        {
            ToastCustom.makeToastCenter(getApplicationContext(),"当前网络不可用");
        }
        else
        {

            new Thread(){
                @Override
                public void run() {

                    Message message = new Message();
                    //
                    if(ConnectUtil.isNetworkAvailable(getApplicationContext())) {
                        PostParameter[] postParameters;
                        postParameters=new PostParameter[1];
                        postParameters[0]=new PostParameter("account",user_account);
                        String jsonStr=ConnectUtil.httpRequest(ConnectUtil.API_HOST+url,postParameters,ConnectUtil.POST);
                        message.what = 0;
                        message.obj = jsonStr;
                        System.out.println(jsonStr+"____");
                        handler.sendMessage(message);
                    }
                    else
                    {
                        message.what=-1;
                        message.obj="网络不可用";
                        handler.sendMessage(message);
                    }
                }
            }.start();

        }

    }
    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(CardDiv_Check_Public.this, "请稍等...");
        mDialog.show();
    }
    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
