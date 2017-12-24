package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CardDiv_Cancel_League extends AppCompatActivity implements View.OnClickListener {
private EditText reson_of_terminate;
    private String reason;
    private Button tijiao_reson;
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__cancel__league);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp=new SharePreferenceUtil(this,"user");
        ImageView exit= (ImageView) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reson_of_terminate= (EditText) findViewById(R.id.reson_of_terminate);
        tijiao_reson= (Button) findViewById(R.id.tijiao_reson);
        tijiao_reson.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        reason=reson_of_terminate.getText().toString().trim();
        if(("").equals(reason)||reason==null){
            Toast.makeText(this,"请输入解约理由",Toast.LENGTH_SHORT).show();
            return ;
        }

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] param=new PostParameter[3];
                param[0]=new PostParameter("account",sp.getAccount());
                param[2]=new PostParameter("reason",reason);
                param[1]=new PostParameter("type","2");
                Log.d("account",sp.getAccount());
                Log.d("reason",reason);

                Message msg=handler.obtainMessage();
                if(ConnectUtil.isNetworkAvailable(CardDiv_Cancel_League.this)){

                String jsonStr=ConnectUtil.httpRequest(ConnectUtil.RelieveWorker,param,"POST");
                if(("").equals(jsonStr)||jsonStr==null){
                    msg.what=2;
                    msg.obj="服务器连接失败";
                }else{
                    msg.what=0;
                    msg.obj=jsonStr;
                }
                }else{
                    msg.what=1;
                    msg.obj="请检查您的网络连接设置";
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {


            if(msg.what==0){
                String str= (String) msg.obj;
                JSONObject obj=new JSONObject(str);

                Toast.makeText(CardDiv_Cancel_League.this,obj.getString("message"),Toast.LENGTH_SHORT).show();
                if(obj.getString("status").equals("true")){
                Intent intent=new Intent(CardDiv_Cancel_League.this, LoginActivity.class);
                startActivity(intent);}
            }else{
                Toast.makeText(CardDiv_Cancel_League.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
