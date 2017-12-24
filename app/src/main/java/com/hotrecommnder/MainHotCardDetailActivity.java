package com.hotrecommnder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.HotCardVo;
import com.xd.aselab.chinabank_shop.util.ImageLoader;

public class MainHotCardDetailActivity extends AppCompatActivity {

    RelativeLayout ll_back;
    WebView web;
    TextView tv_name,tv_content,tv_code;
    ImageView im_pic;
    ImageLoader imageLoader;
    HotCardVo vo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hot_card_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
        initData();
    }
    void initView(){
        ll_back=(RelativeLayout)findViewById(R.id.act_hot_card_detail_back_btn);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        web = (WebView) findViewById(R.id.act_hot_card_detail_web);
        /*tv_name=(TextView)findViewById(R.id.tv_name);
        tv_content=(TextView)findViewById(R.id.tv_content);*/
        /*tv_code=(TextView)findViewById(R.id.tv_code);
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("liuhaoxian","goto the view");
                //跳转到生成二维码页面
            }
        });*/
        //im_pic=(ImageView)findViewById(R.id.im_pic);
    }
    void initData(){
        web.loadUrl(getIntent().getStringExtra("content"));
        /*imageLoader=ImageLoader.getInstance();
        vo=(HotCardVo)getIntent().getSerializableExtra("cardVo");
        tv_name.setText(vo.getCardName());
        String content=vo.getDesc()+"\n";
        int i=1;
        for(String str:vo.getItems())
        {
            content+=""+i+"、"+str+"\n";

            i++;
        }
        tv_content.setText(content);
        //图片
        imageLoader.loadBitmap(MainHotCardDetailActivity.this, ConnectUtil.IP+vo.getImageUrl(), im_pic, 0);*/
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
