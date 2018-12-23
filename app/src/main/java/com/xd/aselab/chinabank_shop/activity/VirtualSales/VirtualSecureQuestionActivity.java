package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class VirtualSecureQuestionActivity extends AppCompatActivity {

    private EditText secure_question;
    private EditText secure_answer;
    private Button secure_submit;
    private RelativeLayout back;

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

        initMembers();
        initEvents();
        handleData();
    }

    private void initMembers() {
        back = (RelativeLayout) findViewById(R.id.act_secure_question_back_btn);
        secure_question = (EditText) findViewById(R.id.act_secure_question_question);
        secure_answer = (EditText) findViewById(R.id.act_secure_question_answer);
        secure_submit = (Button) findViewById(R.id.act_secure_question_submit_btn);
    }

    private void initEvents() {
        // 返回按钮点击
        // 注意输入法键盘的隐藏
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(secure_question);
                hideKeyboard(secure_answer);
                finish();
            }
        });

        // 安全提问的提交点击
        // 也有键盘的隐藏
        secure_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(secure_question);
                hideKeyboard(secure_answer);

                question = secure_question.getText().toString().trim();
                answer = secure_answer.getText().toString().trim();

                if ("".equals(question) || question == null) {
                    Toast.makeText(VirtualSecureQuestionActivity.this, "问题不能为空！", Toast.LENGTH_SHORT).show();
                } else if ("".equals(answer) || answer == null) {
                    Toast.makeText(VirtualSecureQuestionActivity.this, "答案不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            if (ConnectUtil.isNetworkAvailable(VirtualSecureQuestionActivity.this)) {
                                SharePreferenceUtil sp = new SharePreferenceUtil(VirtualSecureQuestionActivity.this, "user");
                                PostParameter[] post = new PostParameter[3];
                                post[0] = new PostParameter("account", sp.getAccount());
                                post[1] = new PostParameter("question", question);
                                post[2] = new PostParameter("answer", answer);

                                String recode = ConnectUtil.httpRequest(ConnectUtil.SetVirtualSecureQuestion, post, ConnectUtil.POST);
                                if (null == recode || "".equals(recode)) {
                                    msg.what = 1;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = recode;
                                }
                            } else {
                                msg.what = 2;
                                msg.obj = "网络连接错误";
                            }
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
    }

    private void handleData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0:
                        try {
                            String recode = (String) msg.obj;
                            if (recode != null) {
                                JSONObject jsonObject = new JSONObject(recode);
                                String status = jsonObject.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(VirtualSecureQuestionActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    Toast.makeText(VirtualSecureQuestionActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent();
                                    intent.setClass(VirtualSecureQuestionActivity.this, VirtualChangePasswordActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e("VirtualSecureQuestion", VirtualSecureQuestionActivity.this.getResources().getString(R.string.handler_what_exception));
                                }
                            } else {
                                Toast.makeText(VirtualSecureQuestionActivity.this, VirtualSecureQuestionActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Log.e("VirtualSecureQuestion", VirtualSecureQuestionActivity.this.getResources().getString(R.string.handler_what_exception));
                }
            }
        };
    }

    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

}
