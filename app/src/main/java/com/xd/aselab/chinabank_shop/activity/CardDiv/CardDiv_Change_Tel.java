
package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONException;
import org.json.JSONObject;

import static com.xd.aselab.chinabank_shop.util.ConnectUtil.WorkerChangerPsw;

public class CardDiv_Change_Tel extends AppCompatActivity {
    private ImageView return_button;
    private SharePreferenceUtil sp;
    private EditText shop_changeTel_tel;
    private String new_Tel;
    private JSONObject obj;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                String message = (String) msg.obj;
                try {
                    obj = new JSONObject(message);

                    Toast.makeText(CardDiv_Change_Tel.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                    if (obj.getString("status").equals("true")) {
                        sp.setWorkerTel(new_Tel);
                        sp.setTelAccount(new_Tel);
                        Intent intent = new Intent(CardDiv_Change_Tel.this, CardDiv_My_Info_Info.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CardDiv_Change_Tel.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__change__tel);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(this, "user");
        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView shop_changeTel_shopAccont = (TextView) findViewById(R.id.shop_changeTel_shopAccont);
        shop_changeTel_shopAccont.setText(sp.getAccount());

        shop_changeTel_tel = (EditText) findViewById(R.id.shop_changeTel_tel);
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_Tel = shop_changeTel_tel.getText().toString().trim();
                if (new_Tel == null || new_Tel.equals("")) {
                    Toast.makeText(CardDiv_Change_Tel.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                } else if (!isMobileNo.isMobileNO(new_Tel)) {
                    Toast.makeText(CardDiv_Change_Tel.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                } else {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = handler.obtainMessage();
                            if (ConnectUtil.isNetworkAvailable(CardDiv_Change_Tel.this)) {
                                String url = "WorkerChangeTel";
                                PostParameter[] param = new PostParameter[4];
                                param[1] = new PostParameter("worker_account", sp.getAccount());
                                param[2] = new PostParameter("new_tel", new_Tel);
                                param[0] = new PostParameter("password", Encode.getEncode("MD5", sp.getPassword()));
                                param[3] = new PostParameter("type", "2");
                                Log.d("account", sp.getAccount());
                                Log.d("new_tel", new_Tel);
                                Log.d("password", sp.getPassword());

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.WorkerChangeTel, param, "POST");
                                if (jsonStr == null || jsonStr.equals("")) {
                                    msg.what = 2;
                                    msg.obj = "服务器连接失败";
                                } else {
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
