
package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsSatellite;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.app.WindowDecorActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PercentView;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CardDiv_My_Recommend_Result extends AppCompatActivity {

    String my_div_money;
    String my_div_num;
    String my_purchase;
    private PostParameter[] postParameter;
    private EditText div_money;
    private EditText div_num;
    private EditText purchase;
    private SharePreferenceUtil sp;
    private Intent intent;
    private String applicant;
    private String telephone;
    private String evaluation_detail;
    private float low;
    private float high;
    private float getScore;
    private PercentView percent;
    private TextView text;
    private TextView evaluate_time;
    private JSONObject final_obj;
    private TableLayout tableLayout;
    private Button submit;
    private Button before_submit;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        String str = (String) msg.obj;
                        final_obj = new JSONObject(str);


                        if (final_obj.getString("status").equals("true")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(CardDiv_My_Recommend_Result.this);
                            final View dialog_content = LayoutInflater.from(CardDiv_My_Recommend_Result.this).inflate(R.layout.activity_card_div__my__recommend__toast, null);
                            TextView recommend_num = (TextView) dialog_content.findViewById(R.id.recommend_num);
                            TextView recommend_score = (TextView) dialog_content.findViewById(R.id.recommend_score);
                            TextView exchange_score = (TextView) dialog_content.findViewById(R.id.exchange_score);
                            TextView maybe_score = (TextView) dialog_content.findViewById(R.id.maybe_score);
                            try {

                                String number = final_obj.getString("number");


                                recommend_num.setText("您本月累计推荐" + number + "笔业务");
                                String score = final_obj.getString("score");

                                recommend_score.setText("累计获得" + score + "积分");

                                exchange_score.setText("累计兑换" + final_obj.getString("exchange_score") + "积分");

                                maybe_score.setText("本次预计获得" + final_obj.getString("this_score") + "积分");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            builder.setTitle("提交成功");
                            builder.setView(dialog_content);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //==
                                    //                              Intent intent = new Intent(CardDiv_My_Recommend_Result.this, CardDiv_My_Recommend.class);
//                                intent.putExtra("clear", "clear");
//                                startActivity(intent);

                                    setResult(1, intent);
                                    finish();

                                }
                            });
                            builder.show();


                        } else {
                            Toast.makeText(CardDiv_My_Recommend_Result.this, final_obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Toast.makeText(CardDiv_My_Recommend_Result.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 6:

                    try {
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            low = Float.parseFloat(obj.getString("do_not_recommend"));
                            high = Float.parseFloat(obj.getString("suggest_recommend"));

                            //写入handler里
                            if (getScore < low) {
                                text.setVisibility(View.VISIBLE);
                                text.setText("授信评分太低，不建议推荐");
                                tableLayout.setVisibility(View.GONE);
                                submit.setVisibility(View.GONE);
                                before_submit.setVisibility(View.VISIBLE);
                                before_submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tableLayout.setVisibility(View.VISIBLE);
                                        submit.setVisibility(View.VISIBLE);
                                        before_submit.setVisibility(View.GONE);

                                    }
                                });
                            } else if (getScore >= high) {
                                tableLayout.setVisibility(View.VISIBLE);
                                text.setVisibility(View.VISIBLE);
                                text.setText("预授信评分较高，建议推荐");
                                before_submit.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                            } else {

                                tableLayout.setVisibility(View.VISIBLE);
                                submit.setVisibility(View.VISIBLE);
                                before_submit.setVisibility(View.GONE);
                                text.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(CardDiv_My_Recommend_Result.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            //如果提交成功  则跳出对话框展示当前可能获取积分情况
            //如果提交失败则Toast显示提交失败
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__recommend__result);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(this, "user");
        percent= (PercentView) findViewById(R.id.percent);








        evaluate_time = (TextView) findViewById(R.id.evaluate_time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        evaluate_time.setText("评估时间："+df.format(new Date()));

        TextView sum = (TextView) findViewById(R.id.sum);
        InputMethodManager imm = (InputMethodManager) getSystemService(CardDiv_My_Recommend_Result.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sum.getWindowToken(), 0);
        getScore = getIntent().getFloatExtra("evaluation", 0);
        percent.setAngel(getScore);
        String str="评估时间："+df.format(new Date());
        percent.setRankText(getScore+"",str);
        sum.setText(getScore + "");
        text = (TextView) findViewById(R.id.text);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);
        submit = (Button) findViewById(R.id.submit);
        before_submit = (Button) findViewById(R.id.before_submit);

        new Thread() {

            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                String getCreditScoreLine = ConnectUtil.httpRequest(ConnectUtil.GetCreditScoreLine, null, "POST");

//                String getCreditScoreLine = "{" + "\"status\":\"true\"," + "\"do_not_recommend\":60," + "\"suggest_recommend\":80" + "}";


                if (("").equals(getCreditScoreLine) || getCreditScoreLine == null) {
                    msg.what = 5;
                    msg.obj = "获取预授信分数线失败";
                } else {
                    msg.what = 6;
                    msg.obj = getCreditScoreLine;
                }
                handler.sendMessage(msg);
            }
        }.start();


        ImageView return_btn = (ImageView) findViewById(R.id.return_button);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                setResult(2, intent);
            }
        });

        final EditText money = (EditText) findViewById(R.id.money);
        money.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        money.setText(s);
                        money.setSelection(s.length());
                    }
                } else {
                    money.setMaxWidth(8);
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    money.setText(s);
                    money.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        money.setText(s.subSequence(0, 1));
                        money.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }


        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(CardDiv_My_Recommend_Result.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(submit.getWindowToken(), 0);
                my_div_money = div_money.getText().toString().trim();
                my_div_num = div_num.getText().toString().trim();
                my_purchase = purchase.getText().toString().trim();

                intent = getIntent();
                applicant = intent.getStringExtra("applicant");
                telephone = intent.getStringExtra("telephone");
                getScore = intent.getFloatExtra("evaluation", 0);
                evaluation_detail = intent.getStringExtra("evaluation_detail");
                Log.d("Dorise=================", getScore + "");

                if (my_div_money.equals("") || my_div_num.equals("") || my_purchase.equals("") || my_div_money == null || my_div_num == null || my_purchase == null) {
                    Toast.makeText(CardDiv_My_Recommend_Result.this, "请输入完整的用户信息", Toast.LENGTH_SHORT).show();
                } else {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = handler.obtainMessage();
                            String url = ConnectUtil.RecommendInstallment;
                            if (ConnectUtil.isNetworkAvailable(CardDiv_My_Recommend_Result.this)) {
                                postParameter = new PostParameter[8];
                                postParameter[0] = new PostParameter("account", sp.getAccount());
                                Log.d("Dorise--1", sp.getAccount());
                                postParameter[1] = new PostParameter("applicant", applicant);
                                Log.d("Dorise--2", applicant);
                                postParameter[2] = new PostParameter("telephone", telephone);
                                Log.d("Dorise--3", telephone);
                                postParameter[3] = new PostParameter("money", my_div_money);
                                Log.d("Dorise--4", my_div_money);
                                postParameter[4] = new PostParameter("installment_num", my_div_num);
                                Log.d("Dorise--5", my_div_num);
                                postParameter[5] = new PostParameter("car_type", my_purchase);
                                Log.d("Dorise--6", my_purchase);
                                postParameter[6] = new PostParameter("evaluation_detail", intent.getStringExtra("evaluation_detail"));
                                Log.d("Dorise--7", intent.getStringExtra("evaluation_detail"));
                                String score = getScore + "";
                                postParameter[7] = new PostParameter("evaluation", score);
                                Log.d("Dorise--8", score);

                                Log.d("Dorise+++++++", score + "提交的score信息");
                                String jsonStr = ConnectUtil.httpRequest(url, postParameter, ConnectUtil.POST);
                                Log.d("Dorise==获取到的jsonstr", jsonStr);
                                if (jsonStr.equals("") || jsonStr == null) {
                                    msg.what = 2;
                                    msg.obj = "连接服务器失败";
                                } else {

                                    msg.what = 0;
                                    msg.obj = jsonStr;


                                }
                            } else {
                                msg.what = 1;
                                msg.obj = getString(R.string.network_connect_exception);

                            }
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });

        div_money = (EditText) findViewById(R.id.money);
        div_num = (EditText) findViewById(R.id.mun);
        purchase = (EditText) findViewById(R.id.purchase_id);


    }
}

