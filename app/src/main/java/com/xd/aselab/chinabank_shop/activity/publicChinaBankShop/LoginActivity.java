
package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.AccountTypeVo;
import com.xd.aselab.chinabank_shop.Vos.CardDivInfo;
import com.xd.aselab.chinabank_shop.Vos.Personal_Loan;
import com.xd.aselab.chinabank_shop.Vos.Worker;
import com.xd.aselab.chinabank_shop.activity.CardDiv.*;
import com.xd.aselab.chinabank_shop.activity.personalLoan.Personal_My_Main_Page;
import com.xd.aselab.chinabank_shop.activity.worker.RegisterClerkActivity;
import com.xd.aselab.chinabank_shop.activity.shop.ShopMainPageActivity;
import com.xd.aselab.chinabank_shop.activity.shop.ShopRegisterActivity;
import com.xd.aselab.chinabank_shop.activity.worker.ClerkMainPageActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.NetWorkUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.Vos.Shopkeeper;
import com.xd.aselab.chinabank_shop.util.ToastCustom;
import com.xd.aselab.chinabank_shop.util.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends AppCompatActivity {

    private EditText et_account;
    private EditText et_password;
    private String account = "";
    private String password = "";
    private Button btnLogin;
    private TextView btnForgetPsw;
    private TextView btnShopRegister;
    private Dialog mDialog = null;
    private NetWorkUtil netWorkUtil;
    private SharePreferenceUtil sp;
    private SharePreferenceUtil spu;
    private Shopkeeper shopkeeper;
    private Worker worker;
    private CardDivInfo carddiv;
    private ImageView account_delete;
    private ImageView psw_delete;
    private Personal_Loan loan;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            dismissRequestDialog();
            String message = "", reCode = "";
            switch (msg.what) {
                case 1: {
                    JSONObject result = (JSONObject) msg.obj;
                    try {
                        reCode = result.getString("status");
                        if (result.has("message")){
                            message = result.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (reCode.equalsIgnoreCase("true")) {
                        sp.setAccount(account);
                        sp.setPassword(password);
                        sp.setIsLogin(true);
                        JPushInterface.setAlias(LoginActivity.this,0,account);
                        try {

                            String userType = result.getString("type");
                            MobclickAgent.onProfileSignIn(account);
                            //MobclickAgent.setDebugMode( true );
                            if (userType.equals("shop")) {
                                shopkeeper = new Shopkeeper();
                                shopkeeper.setShopkeeperAccount(result.getString("account"));
                                shopkeeper.setShopkeeperPassword(password);
                                shopkeeper.setShopkeeperTel(result.getString("teleNumber"));
                                shopkeeper.setSshopName(result.getString("shopName"));
                                shopkeeper.setSserviceDescribe(result.getString("serviceDescribe"));
                                shopkeeper.setSlocDescribe(result.getString("locationDescribe"));
                                shopkeeper.setsOwnerName(result.getString("ownerName"));
                                shopkeeper.setSprovince(result.getString("province"));
                                shopkeeper.setScity(result.getString("city"));
                                shopkeeper.setScounty(result.getString("county"));
                                shopkeeper.setSstreet(result.getString("street"));
                                shopkeeper.setSindustryType(result.getString("industryType"));
                                shopkeeper.setSmanagerAccount(result.getString("manager_account"));
                                shopkeeper.setSmanagerTel(result.getString("managerTele"));
                                shopkeeper.setScookie(result.getString("cookie"));
                                initShopkeeperInfo();
                                gotoShopMainActivity();
                            } else if (userType.equals("worker_1")) {

                                //worker_type=1代表信用卡

                                worker = new Worker();
                                worker.setWorkerAccount(result.getString("account"));
                                worker.setWorkerPassword(password);
                                worker.setWorkerName(result.getString("workerName"));
                                worker.setWorkerTel(result.getString("workerTele"));
                                worker.setwShopName(result.getString("shopName"));
                                worker.setwShopTel(result.getString("shopTele"));
                                worker.setwShopAccount(result.getString("shopAccount"));
                                worker.setwOwnerName(result.getString("ownerName"));
                                worker.setwShopProvince(result.getString("province"));
                                worker.setwShopCity(result.getString("city"));
                                worker.setwShopCounty(result.getString("county"));
                                worker.setwShopStreet(result.getString("street"));
                                worker.setwShopIndutryType(result.getString("industryType"));
                                worker.setHead_image(result.getString("head_image"));
                                worker.setwCookie(result.getString("cookie"));
                                initWorkerInfo();
                                gotoWorkerMainActivity();
                            } else if (userType.equals("worker_2")) {
                                //worker_type=2代表卡分期
                                carddiv = new CardDivInfo();
                                carddiv.setCarddivAccount(result.getString("account"));
                                carddiv.setCarddivWorkerName(result.getString("name"));
                                carddiv.setCarddivWorkerTel(result.getString("telephone"));
                                carddiv.setCarddivType(result.getString("type"));
                                carddiv.setCarddivCompany(result.getString("company"));
                                carddiv.setCarddivManagerAccount(result.getString("manager_account"));
                                carddiv.setCarddivManegerName(result.getString("manager_name"));
                                carddiv.setCarddivManagerTel(result.getString("manager_tele"));
                                carddiv.setHead_image(result.getString("head_image"));
                                carddiv.setCarddivManagerHeadImage(result.getString("manager_head_image"));
                                initWorker_2Info();
                                gotoWorker_2MainActivity();

                            } else if (userType.equals("worker_3")) {
                                loan = new Personal_Loan();
                                loan.setPersonalAccount(result.getString("account"));
                                loan.setPersonalWorkerName(result.getString("name"));
                                loan.setPersonalWorkerTel(result.getString("telephone"));
                                loan.setPersonalType(result.getString("type"));
                                loan.setPersonalCompany(result.getString("company"));
                                loan.setPersonalManager_account(result.getString("account"));
                                loan.setPersonalManager_name(result.getString("manager_name"));
                                loan.setPersonalManager_tele(result.getString("manager_tele"));
                                loan.setPersonalManager_name(result.getString("head_image"));
                                initWorker_3Info();
                                gotoWorker_3MainActivity();
                                //worker_type=3代表个人消贷
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("ww", "e is" + e.toString());
                            ToastCustom.makeToastCenter(getApplicationContext(), "登录失败");
                        }

                    } else if (reCode.equalsIgnoreCase("false")) {

                        if (!message.equals("")) {
                            ToastCustom.makeToastCenter(getApplicationContext(), message);
                        } else
                            ToastCustom.makeToastCenter(getApplicationContext(), "用户名或密码错误，请重新输入");

                    } else {
                        ToastCustom.makeToastCenter(getApplicationContext(),
                                "未能登录成功，请重新登录");
                    }
                }

                break;
                case 0: {
                    ToastCustom.makeToastCenter(getApplicationContext(), msg.obj.toString());
                    break;
                }

                case -1:
                    ToastCustom.makeToastCenter(getApplicationContext(), "网络连接失败");
                    break;
                case 5:
                    try {
                        reCode = (String)msg.obj;
                        if (reCode != null && !"{}".equals(reCode)) {
                            JSONObject json = new JSONObject(reCode);
                            String status = json.getString("status");
                            if ("false".equals(status)) {
                                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if ("true".equals(status)) {
                                String new_version = json.getString("version");
                                if (new_version!=null && !"".equals(new_version)){
                                    UpdateManager updateManager = UpdateManager.getUpdateManager();
                                    updateManager.judgeAppUpdate(new_version,LoginActivity.this);
                                }
                                else {
                                    Log.e("new_version","版本号为空串");
                                }
                            }
                        }
                        else if (reCode != null && "{}".equals(reCode)){
                            Log.e("new_version","版本号为空null");
                        }
                        else {
                            Log.e("connect","连接失败，未获取版本号");
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }
            if (msg.what == 2) {
                // Toast.makeText(LoginActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                ToastCustom.makeToastCenter(getApplicationContext(), (String) msg.obj);
            } else if (msg.what == 3) {
                AccountTypeVo vo = (AccountTypeVo) msg.obj;
                Log.i("liuhaoxian", "new_account=" + vo.getNew_account());
                if (vo.getType().equals("shop")) {
                    //传给服务器的账号是商铺，是信用卡推广员注册
                    Intent intent = new Intent();
                    intent.putExtra("vo", vo);
                    intent.setClass(LoginActivity.this, RegisterClerkActivity.class);
                    startActivity(intent);

                } else if(vo.getType().equals("manager"))//传给服务器的账号是经理，是(1)商家或(2)卡分期推广员或(3)个人消贷推广员注册
                {
                    Intent intent = new Intent();
                    intent.putExtra("vo", vo);
                    switch (vo.getWorker_type()){
                        case "1":
                            intent.setClass(LoginActivity.this, ShopRegisterActivity.class);
                            break;
                        case "2":
                            intent.setClass(LoginActivity.this, CardDiv_My_Register.class);
                            break;
                        case "3":
                            //intent.setClass(LoginActivity.this, ShopRegisterActivity.class);
                            break;
                        default:
                            break;
                    }
                    startActivity(intent);
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        et_account = (EditText) findViewById(R.id.login_accountText);
        et_password = (EditText) findViewById(R.id.login_passwdText);

        account_delete = (ImageView) findViewById(R.id.act_login_account_delete);
        psw_delete = (ImageView) findViewById(R.id.act_login_password_delete);
        spu = new SharePreferenceUtil(LoginActivity.this, "user");
        // Log.e("www",spu.getTelAccount()+"--------");
        if (!"".equals(spu.getTelAccount())) {
            //   Log.e("www",spu.getTelAccount());
            et_account.setText(spu.getTelAccount());
            et_password.setText(spu.getPassword());
        }
        if (spu.getAccount().length() > 0) {
            account_delete.setVisibility(View.VISIBLE);
            psw_delete.setVisibility(View.VISIBLE);
        }

        account_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_account.setText("");
            }
        });
        psw_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_password.setText("");
            }
        });


        et_account.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    account_delete.setVisibility(View.GONE);
                } else {
                    account_delete.setVisibility(View.VISIBLE);
                }
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    psw_delete.setVisibility(View.GONE);
                } else {
                    psw_delete.setVisibility(View.VISIBLE);
                }
            }
        });

        System.out.println("sHA1" + sHA1(LoginActivity.this));

        // initDatas();

        btnForgetPsw = (TextView) findViewById(R.id.btn_forget_psw);
        btnForgetPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetPsw1Activity.class);
                startActivity(intent);
            }
        });

        btnShopRegister = (TextView) findViewById(R.id.btn_to_shop_register);
        btnShopRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //判断有没有权限
                    if (PermissionChecker.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions((Activity) LoginActivity.this, new String[]{Manifest.permission.CAMERA }, 10012);
                    }
                    else {
                        Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, 0);
                    }
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 0);
                }

            }
        });

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                account = et_account.getText().toString();
                password = et_password.getText().toString();


                submit();

            }
        });

    }


    //定位权限获取回调
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 10012:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                    startActivity(intent);
                } else{
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(LoginActivity.this, "请开启摄像头权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String scanResult = data.getStringExtra("result");
            Log.i("liuhaoxian", "result=" + scanResult);
            getAccountType(scanResult);
        }

    }

    private void gotoShopMainActivity() {
        Intent intent1 = new Intent();
        intent1.setClass(LoginActivity.this, ShopMainPageActivity.class);
        startActivity(intent1);

    }

    private void gotoWorker_3MainActivity() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, Personal_My_Main_Page.class);
        startActivity(intent);
    }

    private void gotoWorker_2MainActivity() {
        Intent intent2 = new Intent();
//        intent2.setClass(LoginActivity.this, CardDivMainPage.class);
        intent2.setClass(LoginActivity.this, com.xd.aselab.chinabank_shop.activity.CardDiv.MainActivity.class);
        startActivity(intent2);
    }

    private void gotoWorkerMainActivity() {
        Intent intent2 = new Intent();
        intent2.setClass(LoginActivity.this, ClerkMainPageActivity.class);
        startActivity(intent2);
    }

    void getAccountType(final String str) {
        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("account", str);
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_ACCOUNT_TYPE, params, "POST");
                Message msg = handler.obtainMessage();
                //3为OK，2为异常
                Log.e("jsonStr","-----"+jsonStr);
                if (jsonStr != null && !"".equals(jsonStr)) {
                    try {
                        JSONObject json = new JSONObject(jsonStr);
                        String status = json.getString("status");
                        if (status.equals("true")) {
                            if (!json.has("message")) {
                                AccountTypeVo vo = new AccountTypeVo();
                                vo.setNew_account(json.getString("new_account"));
                                vo.setType(json.getString("type"));
                                if ("manager".equals(vo.getType())){
                                    vo.setWorker_type(json.getString("worker_type"));
                                    vo.setAccount(str.split("_")[0]);
                                }
                                else {
                                    vo.setAccount(str);
                                }
                                msg.what = 3;
                                msg.obj = vo;
                            }

                        } else {
                            msg.what = 5;
                            msg.obj = "服务器异常，请重试";
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 4;
                        msg.obj = "出现错误，请重试";
                    }

                } else {
                    msg.what = 2;
                    msg.obj = "网络连接异常，请检查您的网络设置";
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    protected void initShopkeeperInfo() {
        sp.setAccount(shopkeeper.getShopkeeperAccount());
        System.out.println("Login Sp" + shopkeeper.getShopkeeperAccount());
        //  sp.setPassword(shopkeeper.getShopkeeperPassword());
        sp.setShopMoblie(shopkeeper.getShopkeeperTel());
        sp.setTelAccount(shopkeeper.getShopkeeperTel());
        sp.setShopName(shopkeeper.getSshopName());
        sp.setShopLocationDescribe(shopkeeper.getSlocDescribe());
        sp.setShopSerDescribe(shopkeeper.getSserviceDescribe());
        sp.setShopOwnerName(shopkeeper.getsOwnerName());
        sp.setShopProvince(shopkeeper.getSprovince());
        sp.setShopCity(shopkeeper.getScity());
        sp.setShopCounty(shopkeeper.getScounty());
        sp.setShopStreet(shopkeeper.getSstreet());
        sp.setShopIndustryType(shopkeeper.getSindustryType());
        sp.setShopManagerAccount(shopkeeper.getSmanagerAccount());
        sp.setShopManagerTel(shopkeeper.getSmanagerTel());
        sp.setCookie(shopkeeper.getScookie());
        sp.setUserType("shop");
    }

    protected void initWorker_2Info() {
        sp.setAccount(carddiv.getCarddivAccount());
        sp.setWorkerName(carddiv.getCarddivWorkerName());
        sp.setWorkerTel(carddiv.getCarddivWorkerTel());
        sp.setUserType(carddiv.getCarddivType());
        sp.setTelAccount(carddiv.getCarddivWorkerTel());
        sp.setCompany(carddiv.getCarddivCompany());
        sp.setShopManagerAccount(carddiv.getCarddivManagerAccount());
        sp.setManagerName(carddiv.getCarddivManegerName());
        sp.setShopManagerTel(carddiv.getCarddivManagerTel());
        sp.setHead_image(carddiv.getHead_image());
        sp.setUserType("worker_2");
        sp.setCardDivManagerImage(carddiv.getCarddivManagerHeadImage());
    }

    protected void initWorker_3Info() {
        sp.setAccount(loan.getPersonalAccount());
        sp.setWorkerName(loan.getPersonalWorkerName());
        sp.setWorkerTel(loan.getPersonalWorkerTel());
        sp.setUserType(loan.getPersonalType());
        sp.setTelAccount(loan.getPersonalWorkerTel());
        sp.setCompany(loan.getPersonalCompany());
        sp.setShopManagerAccount(loan.getPersonalManager_account());
        sp.setManagerName(loan.getPersonalManager_name());
        sp.setShopManagerTel(loan.getPersonalManager_tele());
        sp.setHead_image(loan.getPersonalHead_image());
        sp.setUserType("worker_3");
    }

    protected void initWorkerInfo() {
        sp.setAccount(worker.getWorkerAccount());
        sp.setHead_image(worker.getHead_image());
        sp.setWorkerName(worker.getWorkerName());
        sp.setWorkerTel(worker.getWorkerTel());
        sp.setTelAccount(worker.getWorkerTel());
        sp.setShopMoblie(worker.getwShopTel());
        sp.setShopName(worker.getwShopName());
        sp.setShopAccount(worker.getwShopAccount());
        sp.setShopProvince(worker.getwShopProvince());
        sp.setShopCity(worker.getwShopCity());
        sp.setShopCounty(worker.getwShopCounty());
        sp.setShopStreet(worker.getwShopStreet());
        sp.setShopOwnerName(worker.getwOwnerName());
        sp.setShopIndustryType(worker.getwShopIndutryType());

        sp.setCookie(worker.getwCookie());
        sp.setUserType("worker_1");


    }


    private void initDatas() {
        netWorkUtil = new NetWorkUtil(LoginActivity.this);
        sp = new SharePreferenceUtil(getApplicationContext(), "user");
/*
        if(!sp.getAccount().equals("")&&!(sp.getAccount()==null))
        {
            System.out.println("initDatas--sp.getAccount---"+sp.getAccount());
            et_account.setText(""+sp.getAccount());
            et_password.setText(""+sp.getPassword());
        }*/

        if (!sp.getTelAccount().equals("") && !(sp.getTelAccount() == null)) {
            System.out.println("initDatas--sp.getgettelAccount---" + sp.getTelAccount());
            et_account.setText("" + sp.getTelAccount());
            et_password.setText("" + sp.getPassword());
        }

    }

    private void submit() {
        if (account.equals("") || password.equals("")) {
            //DialogFactory.ToastDialog(LoginActivity.this,"温馨提示","请输入账号和密码");
            ToastCustom.makeToastCenter(getApplicationContext(), "请输入账号和密码");
        } else {
            if (!netWorkUtil.isNetworkAvailable()) {
                //  DialogFactory.ToastDialog(LoginActivity.this,"温馨提示","当前网络不可用");
                ToastCustom.makeToastCenter(getApplicationContext(), "当前网络不可用");
            } else {
                showRequestDialog();
                new Thread() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        String url = "";
                        if (ConnectUtil.isNetworkAvailable(getApplicationContext())) {
                            PostParameter[] postParameters=new PostParameter[2];

                            url = ConnectUtil.USER_LOGIN;
                            //postParameters = ;
                            postParameters[0] = new PostParameter("account", account);
                            postParameters[1] = new PostParameter("password", Encode.getEncode("MD5", password));
                            String jsonStr = ConnectUtil.httpRequest(url, postParameters, ConnectUtil.POST);
                            Log.e("Dorise-----", "" + jsonStr);
                            if (jsonStr == null || jsonStr.equals("")) {
                                message.what = 0;
                                message.obj = "服务器连接失败";
                                handler.sendMessage(message);
                            } else {
                                try {
                                    JSONObject result = new JSONObject(jsonStr);
                                    message.what = 1;
                                    message.obj = result;
                                    Log.d("Dorise", message.obj.toString());
                                    handler.sendMessage(message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            message.what = -1;
                            message.obj = "网络不可用";
                            handler.sendMessage(message);
                        }
                    }
                }.start();

            }
        }

    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(LoginActivity.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void onResume() {
        super.onResume();
        initDatas();
        MobclickAgent.onResume(this);

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("send", "version");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetShopClientVersion, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 5;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
