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
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONObject;

public class SecureQuestionActivity extends AppCompatActivity {

    private RelativeLayout back;
    private EditText question_edit;
    private EditText answer_edit;
    private Button submit;

    private String question;
    private String answer;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_question);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_secure_question_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        question_edit = (EditText) findViewById(R.id.act_secure_question_question);
        answer_edit = (EditText) findViewById(R.id.act_secure_question_answer);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode!=null){
                                Log.e("SecureQuestion：reCode", reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(SecureQuestionActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(SecureQuestionActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent();
                                    intent.setClass(SecureQuestionActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e("SecureQuestionActivity", SecureQuestionActivity.this.getResources().getString(R.string.status_exception));
                                }
                            }
                            else {
                                Toast.makeText(SecureQuestionActivity.this, SecureQuestionActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                Log.e("SecureQuestionActivity", "reCode为空");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("SecureQuestionActivity", SecureQuestionActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };

        submit = (Button) findViewById(R.id.act_secure_question_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = question_edit.getText().toString().trim();
                answer = answer_edit.getText().toString().trim();
                if (question==null || "".equals(question))
                    Toast.makeText(SecureQuestionActivity.this, "问题不能为空！", Toast.LENGTH_SHORT).show();
                else if (answer==null || "".equals(answer)){
                    Toast.makeText(SecureQuestionActivity.this, "答案不能为空！", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            SharePreferenceUtil spu = new SharePreferenceUtil(SecureQuestionActivity.this, "user");
                            PostParameter[] params = new PostParameter[3];
                            params[0] = new PostParameter("account", spu.getAccount());
                            params[1] = new PostParameter("question", question);
                            params[2] = new PostParameter("answer", answer);
                            String type = spu.getUserType();
                            String reCode = "";
                            switch (type){
                                case "shop":
                                    reCode = ConnectUtil.httpRequest(ConnectUtil.SetShopSecureQuestion, params, ConnectUtil.POST);
                                    break;
                                case "shop_worker":
                                    reCode = ConnectUtil.httpRequest(ConnectUtil.SetWorkerSecureQuestion, params, ConnectUtil.POST);
                                    break;
                                case "4s_worker":
                                    reCode = ConnectUtil.httpRequest(ConnectUtil.SetInstallmentWorkerSecureQuestion, params, ConnectUtil.POST);
                                    break;
                            }

                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });

        Toast.makeText(SecureQuestionActivity.this, "注册成功，请填写安全验证问题", Toast.LENGTH_LONG).show();
    }
}
