package com.xd.aselab.chinabank_shop.activity.Lobby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Lobby_Recommend_ApplyCard extends AppCompatActivity {

    private EditText application_id;
    private ImageView submit;
    private ImageView return_button;

    private String application_id_text;
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_card_recommend);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        submit = (ImageView) findViewById(R.id.submit);
        application_id = (EditText) findViewById(R.id.application_id);
        return_button = (ImageView) findViewById(R.id.return_button);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(application_id);
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(application_id);

                application_id_text = application_id.getText().toString().trim();
                if(application_id_text.equals("")) {
                    Toast.makeText(Lobby_Recommend_ApplyCard.this, "请输入申请件编号！", Toast.LENGTH_SHORT).show();
                }

                else {
                    //向后台传送数据
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (ConnectUtil.isNetworkAvailable(Lobby_Recommend_ApplyCard.this)) {

                                Message msg = handler.obtainMessage();

                                SharePreferenceUtil sp = new SharePreferenceUtil(Lobby_Recommend_ApplyCard.this, "user");
                                PostParameter postParameter[] = new PostParameter[2];
                                postParameter[0] = new PostParameter("account", sp.getAccount());
                                postParameter[1] = new PostParameter("application_id", application_id_text);

                                jsonStr = ConnectUtil.httpRequest(ConnectUtil.LobbyRecommendApplyCard, postParameter, "POST");
                                if("".equals(jsonStr) || jsonStr == null){
                                    msg.what = 0;
                                    msg.obj = "提交失败";
                                } else {
                                    msg.what = 1;
                                    msg.obj = jsonStr;
                                }
                                handler.sendMessage(msg);

                            } else {
                                Toast.makeText(Lobby_Recommend_ApplyCard.this, "网络连接异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                            }
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            jsonStr = (String) msg.obj;

            switch (msg.what) {
                case 0:
                    Toast.makeText(Lobby_Recommend_ApplyCard.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        if (obj.getString("status").equals("true")) {
                            String number = obj.getString("number");
                            String score = obj.getString("score");
                            String exchange_score = obj.getString("exchange_score");

                            AlertDialog.Builder builder = new AlertDialog.Builder(Lobby_Recommend_ApplyCard.this);
                            final View dialog_content = LayoutInflater.from(Lobby_Recommend_ApplyCard.this).inflate(R.layout.lobby_recommend_applycard_toast, null);
                            TextView recommend_num = (TextView) dialog_content.findViewById(R.id.number);
                            TextView recommend_score = (TextView) dialog_content.findViewById(R.id.score);
                            TextView recommend_exchangeScore = (TextView) dialog_content.findViewById(R.id.exchange_score);

                            recommend_num.setText("您本月累计推荐" + number + "笔业务");
                            recommend_score.setText("累计获得" + score + "积分");
                            recommend_exchangeScore.setText("已兑换" + exchange_score + "积分");

                            builder.setTitle(obj.getString("message"));
                            builder.setView(dialog_content);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            builder.show();
                        } else if(obj.getString("status").equals(false)){
                            Toast.makeText(Lobby_Recommend_ApplyCard.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
    };
}
