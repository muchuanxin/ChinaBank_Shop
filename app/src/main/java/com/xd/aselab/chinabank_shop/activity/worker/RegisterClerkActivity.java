package com.xd.aselab.chinabank_shop.activity.worker;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.AccountTypeVo;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.SecureQuestionActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterClerkActivity extends AppCompatActivity {

    private AccountTypeVo vo;
    private TextView txt_workerID;
    private EditText et_psw;
    private EditText et_wName;
    private EditText et_wTel;
    private TextView txt_shop_account;
    private Button btn_worker_register;
    private String workerID;
    private String password;
    private String wName;
    private String wTel;
    //private SharePreferenceUtil sp;
    private String shop_account;
    private Dialog mDialog=null;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissRequestDialog();
            String reCode="",message="";
            switch (msg.what){
                case 1:{
                    JSONObject result=(JSONObject)msg.obj;
                    try {
                        reCode=result.getString("status");
                        message=result.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(reCode.equals("true")){
                     //   ToastCustom.makeToastCenter(getApplicationContext(), "注册成功");
                        SharePreferenceUtil sp=new SharePreferenceUtil(getApplicationContext(),"user");
                        System.out.println("register--sp "+sp);
                        sp.setAccount(workerID);
                        sp.setPassword(password);
                        sp.setWorkerName(wName);
                        sp.setWorkerTel(wTel);
                        sp.setTelAccount(wTel);
                        sp.setShopAccount(shop_account);
                        sp.setUserType("shop_worker");
                        System.out.println("register--sp.setAccount"+sp.getAccount());
                        //setResult(Activity.RESULT_OK);
                        //RegisterClerkActivity.this.finish();
                        Intent intent = new Intent();
                        intent.setClass(RegisterClerkActivity.this, SecureQuestionActivity.class);
                        startActivity(intent);
                    }else if (reCode.equals("false")){
                        if(!message.equals("")){
                            ToastCustom.makeToastCenter(getApplicationContext(),message);
                        }else
                            ToastCustom.makeToastCenter(getApplicationContext(),"您已注册过，请直接登录");
                    }else {
                        ToastCustom.makeToastCenter(getApplicationContext(),"注册失败，请重新尝试");
                    }
                }
                break;

                case 0:{
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到服务器");
                    break;
                }

                case -1:{
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到网络");
                    break;
                }

                default:
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_clerk);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Button btnToLogin=(Button)findViewById(R.id.c_back_to_login);
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_worker_register=(Button)findViewById(R.id.btnClerkregister);
        btn_worker_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!judge())
                    return;
                else
                    submit();
            }
        });

        initViews();


    }

    private void initViews(){
        vo=(AccountTypeVo)getIntent().getSerializableExtra("vo");
        workerID=vo.getNew_account();
        shop_account=vo.getAccount();

        txt_workerID=(TextView)findViewById(R.id.clerkId);
        txt_workerID.setText(workerID);
        txt_shop_account=(TextView) findViewById(R.id.shop_account);
        txt_shop_account.setText(shop_account);

        et_psw=(EditText) findViewById(R.id.clerk_psw);
        et_wName=(EditText ) findViewById(R.id.clerk_name);
        et_wTel=(EditText)findViewById(R.id.clerk_Tel);
        btn_worker_register=(Button)findViewById(R.id.btnClerkregister);

    }

    protected void submit(){

        showRequestDialog();
        new Thread(){
            @Override
            public void run() {
                Message message=new Message();
                if(ConnectUtil.isNetworkAvailable(getApplicationContext())){
                    PostParameter[] postParameters=new PostParameter[5];
                    postParameters[0]=new PostParameter("account",workerID);
                    postParameters[1]=new PostParameter("password", Encode.getEncode("MD5",password));
                    postParameters[2]=new PostParameter("shopAccount",shop_account);
                    postParameters[3]=new PostParameter("teleNumber",wTel);
                    postParameters[4]=new PostParameter("name",wName);
                    String jsonStr=ConnectUtil.httpRequest(ConnectUtil.REGISTER_WORKER,postParameters,ConnectUtil.POST);
                    if(jsonStr==null||jsonStr.equals("")){
                        Log.i("xxx",1+"");
                        message.what=0;
                        message.obj="fail";
                        handler.sendMessage(message);
                    }else {
                        try {
                            JSONObject result=new JSONObject(jsonStr);
                            message.what=1;
                            message.obj=result;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    message.what=-1;
                    message.obj="网络不可用";
                    handler.sendMessage(message);
                }
            }
        }.start();

    }

    protected boolean judge(){

        password=et_psw.getText().toString().trim();
        wName=et_wName.getText().toString().trim();
        wTel=et_wTel.getText().toString().trim();

        if(password.equals("")||wName.equals("")||wTel.equals(""))
        {
            ToastCustom.makeToastCenter(RegisterClerkActivity.this,"请补全信息");
            return false;
        }
        if(password.length()<6){
            ToastCustom.makeToastCenter(RegisterClerkActivity.this,"密码至少为六位");
            return false;
        }

        if(!isMobileNO( wTel)){
            ToastCustom.makeToastCenter(RegisterClerkActivity.this,"您输入的手机号有误");
            return false;
        }

        return true;

    }

    //判断手机号是否合法
    public static boolean isMobileNO(String mobiles) {
        //Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        //Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7,8])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(RegisterClerkActivity.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
