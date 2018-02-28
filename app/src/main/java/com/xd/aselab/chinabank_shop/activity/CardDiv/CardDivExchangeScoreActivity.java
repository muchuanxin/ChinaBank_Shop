package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class CardDivExchangeScoreActivity extends AppCompatActivity {

    private TextView all_score;
    private TextView unexchange_score;
    private TextView exchange_score;
    private SharePreferenceUtil sp;
    private ImageView back_btn;
    private Button exchange;

    private int not_exchange_score = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.d("Dorise", "当前未兑换积分00000");
                    Toast.makeText(CardDivExchangeScoreActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Log.d("Dorise", "当前未兑换积分111110");
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        if ("true".equals(obj.getString("status"))) {


                            all_score.setText(obj.getString("total_score"));
                            unexchange_score.setText(obj.getString("not_exchange_score"));
                            exchange_score.setText(obj.getString("exchange_score"));

                            not_exchange_score = obj.getInt("not_exchange_score");

                        } else {
                            Toast.makeText(CardDivExchangeScoreActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div_exchange_score);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(CardDivExchangeScoreActivity.this, "user");

        all_score = (TextView) findViewById(R.id.all_score);
        unexchange_score = (TextView) findViewById(R.id.unexchange_score);
        exchange_score = (TextView) findViewById(R.id.exchange_score);

        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        exchange = (Button) findViewById(R.id.exchange);
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDivExchangeScoreActivity.this, CardDivScoreShopActivity.class);
                intent.putExtra("not_exchange_score", not_exchange_score);
                startActivity(intent);
            }
        });

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                PostParameter post[] = new PostParameter[1];


                post[0] = new PostParameter("account", sp.getAccount());
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetNotExchangeScore, post, "POST");
                if (("").equals(jsonStr) || jsonStr == null) {
                    msg.what = 0;
                    msg.obj = "获取未兑换积分失败";
                } else {
                    msg.what = 1;
                    msg.obj = jsonStr;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }
}
