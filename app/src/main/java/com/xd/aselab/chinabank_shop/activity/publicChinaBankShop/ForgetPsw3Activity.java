package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

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
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONObject;

public class ForgetPsw3Activity extends AppCompatActivity {

    private RelativeLayout back;
    private EditText new_psw_edit;
    private EditText new_psw_again_edit;
    private Button submit;
    private String type;

    private String new_psw;
    private String new_psw_again;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        type = getIntent().getStringExtra("type");
        back = (RelativeLayout) findViewById(R.id.act_forget_psw3_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new_psw_edit = (EditText) findViewById(R.id.act_forget_psw3_psw);
        new_psw_again_edit = (EditText) findViewById(R.id.act_forget_psw3_psw_again);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("ForgetPsw3：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ForgetPsw3Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(ForgetPsw3Activity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharePreferenceUtil spu = new SharePreferenceUtil(ForgetPsw3Activity.this, "user");
                                    spu.setAccount(getIntent().getStringExtra("account"));
                                    spu.setPassword(new_psw);
                                    finish();
                                } else {
                                    Log.e("ForgetPsw3Activity", ForgetPsw3Activity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(ForgetPsw3Activity.this, ForgetPsw3Activity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("ForgetPsw3Activity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("ForgetPsw3Activity", ForgetPsw3Activity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        submit = (Button) findViewById(R.id.act_forget_psw3_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_psw = new_psw_edit.getText().toString().trim();
                new_psw_again = new_psw_again_edit.getText().toString().trim();
                if ( new_psw==null || "".equals(new_psw) || new_psw_again==null || "".equals(new_psw_again) ){
                    Toast.makeText(ForgetPsw3Activity.this, "请输入新密码，并确认密码", Toast.LENGTH_SHORT).show();
                }
                else if (new_psw.length()<6){
                    Toast.makeText(ForgetPsw3Activity.this, "新密码至少6位", Toast.LENGTH_SHORT).show();
                }
                else if (!new_psw.equals(new_psw_again)){
                    Toast.makeText(ForgetPsw3Activity.this, "确认密码须与新密码一致", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[4];
                            String reCode="";
                            if(type.equals("shop")){
                                params[0] = new PostParameter("shop_account", getIntent().getStringExtra("account"));
                                params[1] = new PostParameter("old_password", "NuLL");
                                params[2] = new PostParameter("new_password", Encode.getEncode("MD5", new_psw));
                                reCode = ConnectUtil.httpRequest(ConnectUtil.SHOP_CHANGE_PSW, params, ConnectUtil.POST);
                            }else if(type.equals("worker")) {
                                params[0] = new PostParameter("worker_account", getIntent().getStringExtra("account"));
                                params[1] = new PostParameter("old_password", "NuLL");
                                params[2] = new PostParameter("new_password", Encode.getEncode("MD5", new_psw));
                                reCode = ConnectUtil.httpRequest(ConnectUtil.WORKER_CHANGE_PSW, params, ConnectUtil.POST);
                            }else if(type.equals("installment_worker")){
                                params[0] = new PostParameter("worker_account", getIntent().getStringExtra("account"));
                                params[1] = new PostParameter("old_password", "NuLL");
                                params[2] = new PostParameter("new_password", Encode.getEncode("MD5", new_psw));
                                params[3] = new PostParameter("type", "2");
                                reCode = ConnectUtil.httpRequest(ConnectUtil.WORKER_CHANGE_PSW, params, ConnectUtil.POST);
                            }
//                            params[0] = new PostParameter("account", getIntent().getStringExtra("account"));
//                            params[1] = new PostParameter("password", "NuLL");
//                            params[2] = new PostParameter("newPassword", Encode.getEncode("MD5", new_psw));
//                            String reCode = ConnectUtil.httpRequest(ConnectUtil.SHOP_CHANGE_PSW, params, ConnectUtil.POST);
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
