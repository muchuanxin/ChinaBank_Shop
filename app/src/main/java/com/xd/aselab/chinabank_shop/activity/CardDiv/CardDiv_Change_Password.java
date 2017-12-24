
package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.media.Image;
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
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CardDiv_Change_Password extends AppCompatActivity {

    private ImageView return_button;
    private SharePreferenceUtil sp;
    private EditText shop_changePsw_oldPsw;
    private EditText shop_changePsw_newPsw;
    private EditText shop_changePsw_newPsw_again;

    private String old_password;
    private String new_password;
    private String new_password_again;
    private JSONObject obj;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;

            try {
                obj = new JSONObject(str);

                if (msg.what == 0) {

                    Toast.makeText(CardDiv_Change_Password.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    if (obj.getString("status").equals("true")) {
                        sp.setPassword(new_password);
                        Intent intent = new Intent(CardDiv_Change_Password.this, CardDiv_My_Info_Info.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(CardDiv_Change_Password.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__change__password);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(this, "user");
        TextView shop_changePsw_id = (TextView) findViewById(R.id.shop_changePsw_id);
        shop_changePsw_id.setText(sp.getAccount());

        shop_changePsw_oldPsw = (EditText) findViewById(R.id.shop_changePsw_oldPsw);
        shop_changePsw_newPsw = (EditText) findViewById(R.id.shop_changePsw_newPsw);
        shop_changePsw_newPsw_again = (EditText) findViewById(R.id.shop_changePsw_newPsw_again);

        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button btn_shop_changePsw = (Button) findViewById(R.id.btn_shop_changePsw);
        btn_shop_changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                old_password = shop_changePsw_oldPsw.getText().toString().trim();
                new_password = shop_changePsw_newPsw.getText().toString().trim();
                new_password_again = shop_changePsw_newPsw_again.getText().toString().trim();
                if (old_password == null || old_password.equals("")) {
                    Toast.makeText(CardDiv_Change_Password.this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new_password == null || new_password.equals("")) {
                    Toast.makeText(CardDiv_Change_Password.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new_password.length() < 6) {
                    Toast.makeText(CardDiv_Change_Password.this, "密码长度须大于6位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new_password_again == null || new_password_again.equals("")) {
                    Toast.makeText(CardDiv_Change_Password.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!old_password.equals(sp.getPassword())) {
                    Toast.makeText(CardDiv_Change_Password.this, "请输入正确的旧密码", Toast.LENGTH_SHORT).show();
                } else if (!new_password.equals(new_password_again)) {
                    Toast.makeText(CardDiv_Change_Password.this, "您输入的新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread() {
                        @Override
                        public void run() {

                            Message msg = handler.obtainMessage();
                            if (ConnectUtil.isNetworkAvailable(CardDiv_Change_Password.this)) {
                                super.run();
                                PostParameter[] params = new PostParameter[4];
                                params[0] = new PostParameter("worker_account", sp.getAccount());
                                params[2] = new PostParameter("new_password", Encode.getEncode("MD5", new_password));
                                params[1] = new PostParameter("old_password", Encode.getEncode("MD5", old_password));
                                params[3] = new PostParameter("type", "2");
                                Log.d("旧密码：", old_password);
                                Log.d("新密码：", new_password);
                                Log.d("确认信密码：", new_password_again);
                                Log.d("账号：", sp.getAccount());
                                Log.d("旧密码：", Encode.getEncode("MD5", old_password));
                                Log.d("类型：", sp.getUserType());


                                String url = ConnectUtil.WorkerChangerPsw;
                                String jsonStr = ConnectUtil.httpRequest(url, params, "POST");

                                if (!jsonStr.equals("") || jsonStr != null) {
                                    msg.what = 0;
                                    msg.obj = jsonStr;

                                }
                            } else {
                                msg.what = 1;
                                msg.obj = "当前网络不可用";

                            }
                            handler.sendMessage(msg);
                        }
                    }.start();
                }

            }
        });

    }
}
