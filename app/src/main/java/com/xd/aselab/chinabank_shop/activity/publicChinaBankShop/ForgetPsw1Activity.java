package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;

import org.json.JSONObject;

public class ForgetPsw1Activity extends AppCompatActivity {

    private RelativeLayout back;
    private EditText account_edit;
    private Button submit;

    private String account;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_forget_psw1_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        account_edit = (EditText) findViewById(R.id.act_forget_psw1_account);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ForgetPsw1：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ForgetPsw1Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    //Toast.makeText(ForgetPsw1Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(ForgetPsw1Activity.this, ForgetPsw2Activity.class);
                                    intent.putExtra("account", json.getString("account"));
                                    intent.putExtra("question", json.getString("question"));
                                    intent.putExtra("answer", json.getString("answer"));
                                    intent.putExtra("type", json.getString("type"));
                                    finish();
                                    startActivity(intent);
                                } else {
                                    Log.e("ForgetPsw1Activity", ForgetPsw1Activity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ForgetPsw1Activity.this, ForgetPsw1Activity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ForgetPsw1Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ForgetPsw1Activity", ForgetPsw1Activity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        submit = (Button) findViewById(R.id.act_forget_psw1_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = account_edit.getText().toString().trim();
                if (account==null || "".equals(account)){
                    Toast.makeText(ForgetPsw1Activity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[1];
                            params[0] = new PostParameter("telephone", account);
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.GetShopSecureQuestion, params, ConnectUtil.POST);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
    }
}
