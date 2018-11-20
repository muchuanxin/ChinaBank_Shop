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
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_Change_Tel;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONException;
import org.json.JSONObject;

public class Lobby_ChangeTel extends AppCompatActivity {
    private TextView change_tel_account;
    private EditText change_tel_new_tel;
    private RelativeLayout back;
    private Button change_tel_submit;

    private String account;
    private String new_tel;

    private SharePreferenceUtil sp;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_change_tel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.shop_changeTel_back_btn) ;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(change_tel_new_tel);
                finish();
            }
        });

        change_tel_account = (TextView) findViewById(R.id.shop_changeTel_shopAccont);
        sp = new SharePreferenceUtil(Lobby_ChangeTel.this, "user");
        change_tel_account.setText(sp.getAccount());

        change_tel_new_tel = (EditText) findViewById(R.id.shop_changeTel_tel);
        change_tel_submit = (Button) findViewById(R.id.btn_shop_changeTel_tijiao);

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
                                    Toast.makeText(Lobby_ChangeTel.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if("true".equals(status) || status == "true") {
                                    Toast.makeText(Lobby_ChangeTel.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_ChangeTel.this, "user");
                                    sp.setWorkerTel(new_tel);
                                    finish();
                                } else {
                                    Log.e("Lobby_ChangeTel", Lobby_ChangeTel.this.getResources().getString(R.string.handler_what_exception));
                                }
                                break;

                            default:
                                Toast.makeText(Lobby_ChangeTel.this, msg.obj.toString().trim(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Lobby_ChangeTel.this, Lobby_ChangeTel.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                }
            }
        };

        change_tel_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(change_tel_new_tel);

                account = change_tel_account.getText().toString().trim();
                new_tel = change_tel_new_tel.getText().toString().trim();

                if("".equals(account) || null == account) {
                    Toast.makeText(Lobby_ChangeTel.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if("".equals(new_tel) || null == new_tel) {
                    Toast.makeText(Lobby_ChangeTel.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if(!isMobileNo.isMobileNO(new_tel)){
                    Toast.makeText(Lobby_ChangeTel.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            if(ConnectUtil.isNetworkAvailable(Lobby_ChangeTel.this)) {
                                SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_ChangeTel.this, "user");
                                PostParameter[] params = new PostParameter[4];
                                params[0] = new PostParameter("worker_account", account);
                                params[1] = new PostParameter("password", Encode.getEncode("MD5",sp.getPassword()));
                                params[2] = new PostParameter("new_tel", new_tel);
                                params[3] = new PostParameter("type", "7");
                                String recode = ConnectUtil.httpRequest(ConnectUtil.WorkerChangeTel, params, ConnectUtil.POST);
                                if (recode == null || recode.equals("")) {
                                    msg.what = 2;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = recode;
                                }
                            } else {
                                msg.what = 1;
                                msg.obj = Lobby_ChangeTel.this.getResources().getString(R.string.network_connect_exception);
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
