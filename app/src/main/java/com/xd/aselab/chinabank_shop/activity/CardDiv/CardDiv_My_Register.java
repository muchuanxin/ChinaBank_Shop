package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.AccountTypeVo;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.isMobileNo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDiv_My_Register extends AppCompatActivity {

    private TextView account;
    private TextView manager;
    private EditText password;
    private EditText name;
    private EditText tel;
    private AutoCompleteTextView work_place;
    private Button register;
    private String password_result;
    private String name_result;
    private String tel_result;
    private String work_place_result;
    private String account_result;
    private String manager_account;
    private ImageView return_button;
    private SharePreferenceUtil sp;
    private String[] shop_id;
    //    private String[] shop_name;
    private List shop_name;
    private String[] shop_addr;
    private JSONArray shop_info_jsonArray;
    private String postparameter_shop_id;
    private TextView work_address;
    private boolean flag;
    private String[] shop_name_array;
    private Map map;
    private Map shop_id_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        map = new HashMap();
        shop_id_map = new HashMap();
        flag = false;
        sp = new SharePreferenceUtil(this, "user");
        account = (TextView) findViewById(R.id.account);
        manager = (TextView) findViewById(R.id.manager);
        return_button = (ImageView) findViewById(R.id.return_button);
        work_address = (TextView) findViewById(R.id.work_address);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        tel = (EditText) findViewById(R.id.tel);
        work_place = (AutoCompleteTextView) findViewById(R.id.work_place);
        register = (Button) findViewById(R.id.register);

        //新建一个线程  oncreate的时候从后台获取640家单位信息  在work_place中进行内容输入限定
        new Thread() {
            @Override
            public void run() {

                Message msg = handler.obtainMessage();
                super.run();
                String work_place_set = ConnectUtil.httpRequest(ConnectUtil.GetAll4SShop, null, "POST");

                Log.d("Dorise--获取到的json工作集", work_place_set);
                if (("").equals(work_place_set) || work_place_set == null) {
                    msg.what = 3;
                    msg.obj = "获取4S店信息失败";
                } else {
                    msg.what = 4;
                    msg.obj = work_place_set;
                }
                handler.sendMessage(msg);
            }
        }.start();

        AccountTypeVo vo = (AccountTypeVo) getIntent().getSerializableExtra("vo");
        account_result = vo.getNew_account();
        manager_account = vo.getAccount();
        account.setText(account_result);
        manager.setText(manager_account);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_result = password.getText().toString().trim();
                name_result = name.getText().toString().trim();
                tel_result = tel.getText().toString().trim();
                work_place_result = work_place.getText().toString().trim();

                if (("").equals(password_result) || null == password_result) {
                    Toast.makeText(CardDiv_My_Register.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password_result.length() < 6) {
                    Toast.makeText(CardDiv_My_Register.this, "密码长度须大于6位", Toast.LENGTH_SHORT).show();
                    return;
                } else if (("").equals(name_result) || null == name_result) {
                    Toast.makeText(CardDiv_My_Register.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (("").equals(tel_result) || null == tel_result) {
                    Toast.makeText(CardDiv_My_Register.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isMobileNo.isMobileNO(tel_result)) {
                    Toast.makeText(CardDiv_My_Register.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;

                } else if (("").equals(work_place_result) || null == work_place_result) {
                    Toast.makeText(CardDiv_My_Register.this, "请输入工作单位", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!flag) {

                    //如果flag为true  说明已经点击过了  这里可以跳过
                    //如果flag为false  说明要从640数据中读取
                    //如果数据是点击选择的  那么可以省略掉640家挨个比较的过程
                    //如果数据是用户手动输入的   那么640家数据必须挨个比较
                    Log.d("Dorise--flag挨个查找", flag + "");


                    //map里有work_place_result  说明店铺名称输入是正确的
                    if (!("").equals(map.get(work_place_result)) && null != map.get(work_place_result)) {
                        postparameter_shop_id = (String) shop_id_map.get(work_place_result);

                        work_address.setText((String) map.get(work_place_result));
                        flag = true;
//                        return;
                    } else {
                        Toast.makeText(CardDiv_My_Register.this, "请输入正确的工作单位", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                if (flag) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Message msg = handler.obtainMessage();
                            if (ConnectUtil.isNetworkAvailable(CardDiv_My_Register.this)) {


                                PostParameter[] param = new PostParameter[6];
                                param[0] = new PostParameter("account", account_result);
                                param[1] = new PostParameter("password", Encode.getEncode("MD5", password_result));
                                param[2] = new PostParameter("name", name_result);
                                param[3] = new PostParameter("telephone", tel_result);
                                param[4] = new PostParameter("4sshop_id", postparameter_shop_id);
                                Log.d("Dorise--postparam", postparameter_shop_id);
                                param[5] = new PostParameter("manager_account", manager_account);

                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.InstallmentWorkerRegister, param, "POST");
                                if (("").equals(jsonStr) || null == jsonStr) {
                                    msg.what = 2;
                                    msg.obj = "服务器连接失败";
                                } else {
                                    msg.what = 0;
                                    msg.obj = jsonStr;
                                }
                            } else {
                                msg.what = 1;
                                msg.obj = "网络连接错误，请检查您的网络连接";
                            }
                            handler.sendMessage(msg);
                        }
                    }.start();


                } else {
                    Toast.makeText(CardDiv_My_Register.this, "请输入正确的工作单位", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                //case=0: 提交注册信息  后台返回字符串非空
                case 0:
                    try {
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            sp.setAccount(account_result);
                            sp.setPassword(password_result);
                            sp.setUserName(name_result);
                            sp.setTelAccount(tel_result);
                            sp.setWorkerTel(tel_result);
                            sp.setWork_place(work_place_result);
                            sp.setShopManagerAccount(manager_account);
                            Toast.makeText(CardDiv_My_Register.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CardDiv_My_Register.this, LoginActivity.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(CardDiv_My_Register.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //case1:网络连接错误  case2:服务器连接失败  case3：获取4S店信息失败
                case 1:
                case 2:
                case 3:
                    Toast.makeText(CardDiv_My_Register.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                //case4  oncreate的时候从服务器获取数据并解析 将其存入数组便于setadapter时进行适配
                case 4:
                    try {
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (obj.getString("status").equals("true")) {
                            shop_info_jsonArray = new JSONArray(obj.getString("4sshop_list"));
//                            shop_id = new String[obj.getString("4sshop_list").length()];
                            shop_name = new ArrayList(("4sshop_list").length());
//                            shop_name = new ArrayList();
                            shop_addr = new String[obj.getString("4sshop_list").length()];
                            for (int i = 0; i < shop_info_jsonArray.length(); i++) {
                                JSONObject final_obj = (JSONObject) shop_info_jsonArray.get(i);
//                                shop_id[i] = final_obj.getString("4sshop_id");
                                shop_name.add(final_obj.getString("4sshop_name"));
//                                shop_addr[i] = final_obj.getString("4sshop_addr");

                                String temp = final_obj.getString("4sshop_name");
                                map.put(temp, final_obj.getString("4sshop_addr"));
                                shop_id_map.put(temp, final_obj.getString("4sshop_id"));
                            }


                            shop_name_array = new String[shop_name.size()];
                            for (int i = 0; i < shop_name.size(); i++) {

                                Log.d("Dorise--shop_info", shop_name.get(i).toString());

                                shop_name_array[i] = (String) shop_name.get(i);
                            }

//                            work_place.setAdapter(new ArrayAdapter<String>(CardDiv_My_Register.this, R.layout.auto_complete_item_layout, shop_name));

                            ArrayAdapter adapter = new ArrayAdapter(CardDiv_My_Register.this, android.R.layout.simple_list_item_1, shop_name_array);
                            work_place.setAdapter(adapter);
//                            work_place.addTextChangedListener(new );
                            work_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    //给每一个item设置一个下标  点击内容的时候需要在

                                    work_address.setText(map.get(work_place.getText().toString().trim()) + "");
                                    postparameter_shop_id = (String) shop_id_map.get(work_place.getText().toString().trim());
                                    flag = true;

                                }
                            });

                            work_place.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    flag = false;
                                    String work_place_result = work_place.getText().toString().trim();
                                    //如果work_place框子里输入的不是制定640家店铺名称  店铺地址置空
                                    if (!isExist(work_place.getText().toString().trim())) {
                                        work_address.setText("");
                                        flag = false;
                                        Log.d("Dorise--onTextChanged", "不存在");
                                    } else {
                                        flag = true;
                                        postparameter_shop_id = (String) shop_id_map.get(work_place_result);
                                        work_address.setText((String) map.get(work_place_result));
                                        Log.d("Dorise--onTextChanged", "存在" + map.get(work_place_result));
                                    }

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });
                            Log.d("Dorise--Adapter设置成功", "Okok");
                        }

                        //获取到店铺信息要匹配到EditText里
//                            JSONObject final_jsonObject=JSONArray
                        else {
                            Toast.makeText(CardDiv_My_Register.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }

        }
    };


    private boolean isExist(String place) {

        if (!("").equals(map.get(place)) && null != map.get(place)) {
            postparameter_shop_id = (String) shop_id_map.get(work_place_result);
            work_address.setText(map.get(work_place_result) + "");
            //flag = true;
            return true;
        } else {
            return false;
        }
    }
}
