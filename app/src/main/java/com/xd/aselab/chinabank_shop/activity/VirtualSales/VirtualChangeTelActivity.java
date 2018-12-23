package com.xd.aselab.chinabank_shop.activity.VirtualSales;

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
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONException;
import org.json.JSONObject;

public class VirtualChangeTelActivity extends AppCompatActivity {
    private ImageView iv_back;
    private Button bt_submit;
    private EditText et_tel;
    private TextView tv_account;

    private SharePreferenceUtil spu;

    private String new_tel;
    private JSONObject obj;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_change_tel);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initMembers();
        initEvents();
        handleData();
    }

    // 变量初始化
    private void initMembers() {
        spu = new SharePreferenceUtil(VirtualChangeTelActivity.this, "user");
        iv_back = (ImageView) findViewById(R.id.back_btn);
        tv_account = (TextView) findViewById(R.id.account);
        et_tel = (EditText) findViewById(R.id.tel);
        bt_submit = (Button) findViewById(R.id.submit);

        tv_account.setText(spu.getAccount());
    }

    // 提交后的处理
    // 返回上一页面
    private void handleData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    String message = (String) msg.obj;
                    try {
                        obj = new JSONObject(message);

                        Toast.makeText(VirtualChangeTelActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        if (obj.getString("status").equals("true")) {
                            // 修改后同步到spu
                            spu.setWorkerTel(new_tel);
                            spu.setTelAccount(new_tel);
                            Intent intent = new Intent(VirtualChangeTelActivity.this, VirtualMyInfoActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(VirtualChangeTelActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // 点击事件
    private void initEvents() {
        // 返回键
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 提交键
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_tel= et_tel.getText().toString().trim();
                if (new_tel== null || new_tel.equals("")) {
                    Toast.makeText(VirtualChangeTelActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                } else if (!isMobileNo.isMobileNO(new_tel)) {
                    Toast.makeText(VirtualChangeTelActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = handler.obtainMessage();
                            if (ConnectUtil.isNetworkAvailable(VirtualChangeTelActivity.this)) {
                                PostParameter[] param = new PostParameter[4];
                                param[0] = new PostParameter("worker_account", spu.getAccount());
                                param[1] = new PostParameter("password", Encode.getEncode("MD5", spu.getPassword()));
                                param[2] = new PostParameter("new_tel", new_tel);
                                param[3] = new PostParameter("type", "9");
                                Log.d("account", spu.getAccount());
                                Log.d("new_tel", new_tel);
                                Log.d("password", spu.getPassword());

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
