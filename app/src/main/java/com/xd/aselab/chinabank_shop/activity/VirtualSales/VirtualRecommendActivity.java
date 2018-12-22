package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastUtil;
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class VirtualRecommendActivity extends AppCompatActivity {

    private EditText name;
    private EditText tel;
    private Spinner secondBank;
    private Spinner typeChoose;
    private Button submit_btn;
    private String name_text;
    private String Tel_text;
    private String type_text;
    private String erji_bank;
    private SharePreferenceUtil sp;
    private ImageView back_btn;
    Map<String, String> erji_map = new HashMap<>();
    Map<String, String> product_types = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_recommend);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        name = (EditText) findViewById(R.id.name);
        tel = (EditText) findViewById(R.id.tel);
        typeChoose = (Spinner) findViewById(R.id.spinner);
        secondBank = (Spinner) findViewById(R.id.secondBank);
        submit_btn = (Button) findViewById(R.id.submit);
        back_btn = (ImageView) findViewById(R.id.return_button);
        sp = new SharePreferenceUtil(VirtualRecommendActivity.this, "user");

        product_types.put("汽车分期","car");
        product_types.put("车位分期","parking");
        product_types.put("家装分期","decoration");
        product_types.put("旅游分期","tour");
        product_types.put("优客业务","youke");

        // 返回按钮处理
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取二级行
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                PostParameter post[] = new PostParameter[0];
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetAllSecondaryBank, post, "POST");
                if ("" == jsonStr || jsonStr == null) {
                    msg.what = 0;
                    msg.obj = "提交失败";
                } else {
                    msg.what = 2;
                    msg.obj = jsonStr;
                }
                handler.sendMessage(msg);
            }
        }.start();

        // 类型下拉框的处理
        final List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("汽车分期");
        spinnerItems.add("车位分期");
        spinnerItems.add("家装分期");
        spinnerItems.add("旅游分期");
        spinnerItems.add("优客分期");
        //简单的string数组适配器：样式res，数组
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(VirtualRecommendActivity.this,
                android.R.layout.simple_spinner_item, spinnerItems);
        //下拉的样式res
        spinnerAdapter.setDropDownViewResource(R.layout.virtual_recommend_spinner_item);
        //绑定 Adapter到控件
        typeChoose.setAdapter(spinnerAdapter);
        typeChoose.setSelection(0);
        //选择监听
        typeChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //parent就是父控件spinner
            //view就是spinner内填充的textview,id=@android:id/text1
            //position是值所在数组的位置
            //id是值所在行的位置，一般来说与positin一致
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                type_text = product_types.get(spinnerItems.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // 提交按钮处理
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(name);
                hideKeyboard(tel);
                name_text = name.getText().toString().trim();
                Tel_text = tel.getText().toString().trim();
                if (name_text == null || name_text.equals("")) {
                    Toast.makeText(VirtualRecommendActivity.this, "请输入申请人姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Tel_text == null || Tel_text.equals("")) {
                    Toast.makeText(VirtualRecommendActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isMobileNo.isMobileNO(Tel_text)) {
                    Toast.makeText(VirtualRecommendActivity.this, "请输入正确的联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = new Message();
                            PostParameter post[] = new PostParameter[5];
                            post[0] = new PostParameter("account", sp.getAccount());
                            post[1] = new PostParameter("applicant", name_text);
                            post[2] = new PostParameter("telephone", Tel_text);
                            post[3] = new PostParameter("product_type", type_text);
                            post[4] = new PostParameter("belong_erji_num", erji_bank);
                            String jsonStr = ConnectUtil.httpRequest(ConnectUtil.RecommendVirtualInstallment, post, "POST");
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
                    Toast.makeText(VirtualRecommendActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());

                        if (obj.getString("status").equals("true")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(VirtualRecommendActivity.this);
                            final View dialog_content = LayoutInflater.from(VirtualRecommendActivity.this).inflate(R.layout.activity_virtual_recommend_result, null);
                            TextView recommend_num = (TextView) dialog_content.findViewById(R.id.amount);
                            TextView recommend_score = (TextView) dialog_content.findViewById(R.id.total_score);
                            TextView exchange_score = (TextView) dialog_content.findViewById(R.id.exchanged_score);

                            String number = obj.getString("number");
                            recommend_num.setText("您本月累计推荐" + number + "笔业务");
                            String score = obj.getString("score");
                            recommend_score.setText("累计获得" + score + "积分");
                            exchange_score.setText("已兑换" + obj.getString("exchange_score") + "积分");
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
                            Toast.makeText(VirtualRecommendActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                case 2:
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        if (obj.getString("status").equals("false")) {
                            Toast.makeText(VirtualRecommendActivity.this, obj.getString("message"), Toast.LENGTH_SHORT);
                        } else {
                            JSONArray arr = obj.getJSONArray("list");
                            final List<String> erjiNames = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject temp = (JSONObject) arr.get(i);
                                erji_map.put(temp.getString("erji_name"), temp.getString("erji_num"));
                                erjiNames.add(temp.getString("erji_name"));
                                //简单的string数组适配器：样式res，数组
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(VirtualRecommendActivity.this,
                                        android.R.layout.simple_spinner_item, erjiNames);
                                //下拉的样式res
                                spinnerAdapter.setDropDownViewResource(R.layout.virtual_recommend_spinner_item);
                                //绑定 Adapter到控件
                                secondBank.setAdapter(spinnerAdapter);
                                secondBank.setSelection(0);
                                //选择监听
                                secondBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    //parent就是父控件spinner
                                    //view就是spinner内填充的textview,id=@android:id/text1
                                    //position是值所在数组的位置
                                    //id是值所在行的位置，一般来说与positin一致
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view,
                                                               int pos, long id) {
                                        erji_bank = erji_map.get(erjiNames.get(pos));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Another interface callback
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //用于返回界面隐藏软键盘
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
