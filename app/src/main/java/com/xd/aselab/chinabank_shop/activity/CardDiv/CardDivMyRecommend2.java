package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CardDivMyRecommend2 extends AppCompatActivity {

    private TextView name;
    private TextView tel;
    private TextView money;
    private TextView mun;
    private Button calculate;
    private boolean tag;
    private String name_text;
    private String Tel_text;
    private String money_text;
    private String div_num_text;
    private SharePreferenceUtil sp;
    private ImageView return_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div_my_recommend2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        name = (TextView) findViewById(R.id.name);
        tel = (TextView) findViewById(R.id.tel);
        money = (TextView) findViewById(R.id.money);
        mun = (TextView) findViewById(R.id.mun);
        calculate = (Button) findViewById(R.id.calculate);
        return_button = (ImageView) findViewById(R.id.return_button);

        sp = new SharePreferenceUtil(CardDivMyRecommend2.this, "user");

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = true;

                name_text = name.getText().toString().trim();
                Tel_text = tel.getText().toString().trim();
                money_text = money.getText().toString().trim();
                div_num_text = mun.getText().toString().trim();
                if (name_text == null || name_text.equals("")) {
                    Toast.makeText(CardDivMyRecommend2.this, "请输入申请人姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Tel_text == null || Tel_text.equals("")) {
                    Toast.makeText(CardDivMyRecommend2.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isMobileNo.isMobileNO(Tel_text)) {
                    Toast.makeText(CardDivMyRecommend2.this, "请输入正确的联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else if (money_text == null || money_text.equals("")) {
                    Toast.makeText(CardDivMyRecommend2.this, "请输入分期金额", Toast.LENGTH_SHORT).show();
                    return;
                } else if (div_num_text == null || div_num_text.equals("")) {
                    Toast.makeText(CardDivMyRecommend2.this, "请输入分期数", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                Message msg = handler.obtainMessage();

                                PostParameter post[] = new PostParameter[5];

                                post[0] = new PostParameter("account", sp.getAccount());
                                post[1] = new PostParameter("applicant", name_text);
                                post[2] = new PostParameter("telephone", Tel_text);
                                post[3] = new PostParameter("money", money_text);
                                post[4] = new PostParameter("installment_num", div_num_text);
                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.RecommendInstallmentNew, post, "POST");
                                if ("" == jsonStr || jsonStr == null) {
                                    msg.what = 0;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 1;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);
                            }
                        }.start();



                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    Toast.makeText(CardDivMyRecommend2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());

                        if (obj.getString("status").equals("true")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(CardDivMyRecommend2.this);
                            final View dialog_content = LayoutInflater.from(CardDivMyRecommend2.this).inflate(R.layout.activity_card_div__my__recommend__toast, null);
                            TextView recommend_num = (TextView) dialog_content.findViewById(R.id.recommend_num);
                            TextView recommend_score = (TextView) dialog_content.findViewById(R.id.recommend_score);
                            TextView exchange_score = (TextView) dialog_content.findViewById(R.id.exchange_score);
                            TextView maybe_score = (TextView) dialog_content.findViewById(R.id.maybe_score);


                            String number = obj.getString("number");


                            recommend_num.setText("您本月累计推荐" + number + "笔业务");
                            String score = obj.getString("score");

                            recommend_score.setText("累计获得" + score + "积分");

                            exchange_score.setText("已兑换" + obj.getString("exchange_score") + "积分");

                            maybe_score.setText("本次预计获得" + obj.getString("this_score") + "积分");

                            builder.setTitle("提交成功");
                            builder.setView(dialog_content);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            builder.show();
                        } else if (obj.getString("status").equals(false)) {
                            Toast.makeText(CardDivMyRecommend2.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        break;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };

}
