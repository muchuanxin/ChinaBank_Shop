package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDivScoreShopActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class VirtualExchangeScoreActivity extends AppCompatActivity {
    private TextView tv_total_score;
    private TextView tv_unexchange_score;
    private TextView tv_exchanged_score;

    private RelativeLayout back;
    private Button exchange;

    private int not_exchange_score = 0;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_exchange_score);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        tv_total_score = (TextView) findViewById(R.id.all_score);
        tv_unexchange_score = (TextView) findViewById(R.id.unexchange_score);
        tv_exchanged_score = (TextView) findViewById(R.id.exchanged_score);

        back = (RelativeLayout) findViewById(R.id.my_info_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        exchange = (Button) findViewById(R.id.exchange);
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VirtualExchangeScoreActivity.this, CardDivScoreShopActivity.class);
                intent.putExtra("not_exchange_score", not_exchange_score);
                startActivity(intent);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Log.e("tag", "case0");
                        String recode = (String) msg.obj;
                        try {
                            Log.e("tag", "try");
                            JSONObject jsonObject = new JSONObject(recode);
                            String status = jsonObject.getString("status");

                            if ("false".equals(status) || status == "false") {
                                Log.e("tag", "false");
                                Toast.makeText(VirtualExchangeScoreActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if ("true".equals(status) || status == "true") {
                                Log.e("tag", "true");
                                int total_score = jsonObject.getInt("total_score");
                                int exchange_score = jsonObject.getInt("exchange_score");
                                not_exchange_score = jsonObject.getInt("not_exchange_score");
                                tv_total_score.setText(total_score + "");
                                tv_unexchange_score.setText(not_exchange_score + "");
                                tv_exchanged_score.setText(exchange_score + "");
                            } else {
                                Log.e("tag", "error");
                            }
                        } catch (JSONException e) {
                            Log.e("tag", "catch");
                            e.printStackTrace();

                        }
                        break;

                    default:
                        Log.e("tag", "default");
                        Toast.makeText(VirtualExchangeScoreActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(VirtualExchangeScoreActivity.this)) {
                    SharePreferenceUtil sp = new SharePreferenceUtil(VirtualExchangeScoreActivity.this, "user");
                    PostParameter[] param = new PostParameter[1];
                    param[0] = new PostParameter("account", sp.getAccount());
                    String recode = ConnectUtil.httpRequest(ConnectUtil.GetNotExchangeScore, param, ConnectUtil.POST);
                    if (null == recode || "".equals(recode)) {
                        msg.what = 1;
                        msg.obj = "获取未兑换积分失败";
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
