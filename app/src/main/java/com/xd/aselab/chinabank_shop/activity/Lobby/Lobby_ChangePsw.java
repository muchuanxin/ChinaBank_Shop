package com.xd.aselab.chinabank_shop.activity.Lobby;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Lobby_ChangePsw extends AppCompatActivity {

    private TextView changePsw_account;
    private EditText changePsw_old_psw;
    private EditText changePsw_new_psw;
    private EditText changePsw_confrim_psw;
    private Button changePsw_submit;
    private RelativeLayout back;

    private String account;
    private String old_psw;
    private String new_psw;
    private String new_psw_again;

    private SharePreferenceUtil sp;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_change_psw);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.shop_changePsw_back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(changePsw_old_psw);
                hideKeyboard(changePsw_new_psw);
                hideKeyboard(changePsw_confrim_psw);
                finish();
            }
        });

        changePsw_account = (TextView) findViewById(R.id.shop_changePsw_id);
        sp = new SharePreferenceUtil(Lobby_ChangePsw.this, "user");
        changePsw_account.setText(sp.getAccount());

        changePsw_old_psw = (EditText) findViewById(R.id.shop_changePsw_oldPsw);
        changePsw_new_psw = (EditText) findViewById(R.id.shop_changePsw_newPsw);
        changePsw_confrim_psw = (EditText) findViewById(R.id.shop_changePsw_newPsw_again);

        changePsw_submit = (Button) findViewById(R.id.btn_shop_changePsw);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String recode = (String) msg.obj;
                if(recode != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(recode);
                        String status = jsonObject.getString("status");
                        switch (msg.what) {
                            case 0:
                                if("false".equals(status) || status == "false") {
                                    Toast.makeText(Lobby_ChangePsw.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if("true".equals(status) || status == "true") {
                                    //SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_ChangePsw.this, "uer");
                                    Toast.makeText(Lobby_ChangePsw.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_ChangePsw.this, "user");
                                    sp.setPassword(new_psw);
                                    finish();
                                } else {
                                    Log.e("Lobby_ChangePsw", Lobby_ChangePsw.this.getResources().getString(R.string.handler_what_exception));
                                }
                                break;

                            default:
                                Toast.makeText(Lobby_ChangePsw.this, msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Lobby_ChangePsw.this, Lobby_ChangePsw.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                }
            }
        };

        changePsw_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(changePsw_old_psw);
                hideKeyboard(changePsw_new_psw);
                hideKeyboard(changePsw_confrim_psw);

                account = changePsw_account.getText().toString().trim();
                old_psw = changePsw_old_psw.getText().toString().trim();
                new_psw = changePsw_new_psw.getText().toString().trim();
                new_psw_again = changePsw_confrim_psw.getText().toString().trim();

                if("".equals(account) || null == account) {
                    Toast.makeText(Lobby_ChangePsw.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if("".equals(old_psw) || null == old_psw) {
                    Toast.makeText(Lobby_ChangePsw.this, "原密码不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if("".equals(new_psw) || null == new_psw) {
                    Toast.makeText(Lobby_ChangePsw.this, "新密码不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if (new_psw.length()<6){
                    Toast.makeText(Lobby_ChangePsw.this, "新密码至少6位", Toast.LENGTH_SHORT).show();
                }
                else if ("123456".equals(new_psw)){
                    Toast.makeText(Lobby_ChangePsw.this, "新密码不能为默认密码", Toast.LENGTH_SHORT).show();
                }
                else if (!new_psw.equals(new_psw_again)){
                    Toast.makeText(Lobby_ChangePsw.this, "确认密码须与新密码一致", Toast.LENGTH_SHORT).show();
                }
                else if ( old_psw.equals(new_psw)){
                    Toast.makeText(Lobby_ChangePsw.this, "新密码不能与原密码相同", Toast.LENGTH_SHORT).show();
                }
                else if (!old_psw.equals(sp.getPassword())) {
                    Toast.makeText(Lobby_ChangePsw.this, "请输入正确的旧密码", Toast.LENGTH_SHORT).show();
                }
                else if(!account.equals(sp.getAccount())) {
                    Toast.makeText(Lobby_ChangePsw.this, "账号输入错误！", Toast.LENGTH_SHORT).show();
                }
                else {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            if(ConnectUtil.isNetworkAvailable(Lobby_ChangePsw.this)) {
                                //SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_ChangePsw.this, "user");
                                PostParameter[] params = new PostParameter[4];
                                params[0] = new PostParameter("worker_account", account);
                                params[1] = new PostParameter("old_password", Encode.getEncode("MD5", old_psw));
                                params[2] = new PostParameter("new_password", Encode.getEncode("MD5", new_psw));
                                params[3] = new PostParameter("type", "7");
                                String recode = ConnectUtil.httpRequest(ConnectUtil.WorkerChangerPsw, params, ConnectUtil.POST);
                                if("".equals(recode) || recode == null) {
                                    msg.what = 2;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = recode;
                                }
                            } else {
                                msg.what = 1;
                                msg.obj = Lobby_ChangePsw.this.getResources().getString(R.string.handler_what_exception);
                            }
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
    }

    //用于返回界面隐藏软键盘
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
