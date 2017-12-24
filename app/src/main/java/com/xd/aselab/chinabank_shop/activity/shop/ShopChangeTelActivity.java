package com.xd.aselab.chinabank_shop.activity.shop;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.worker.WorkerMyInfoActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopChangeTelActivity extends AppCompatActivity {

    private RelativeLayout back;
    private Button btn_submit;
    private TextView txt_shopAccount;
    private EditText et_psw;
    private EditText et_tel;
    private String shopPsw;
    private String shopTel;
    private SharePreferenceUtil sp;
    private Dialog mDialog=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dismissRequestDialog();
            String message="",reCode="";
            switch (msg.what){
                case 1:
                    JSONObject result=(JSONObject) msg.obj;
                    try {
                        reCode=result.getString("status");
                        message=result.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(reCode.equals("true")){
                        ToastCustom.makeToastCenter(getApplicationContext(), "修改成功");
                        sp.setShopMoblie(shopTel);
                        sp.setTelAccount(shopTel);
                        Intent intent=new Intent(ShopChangeTelActivity.this,WorkerMyInfoActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ShopChangeTelActivity.this.finish();
                    }
                    else if(reCode.equals("false")){
                        ToastCustom.makeToastCenter(getApplicationContext(),message);
                    }
                    break;
                case 0:
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到服务器");
                    break;
                case -1:
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到网络");
                    break;
                default:
                    break;

            }
            dismissRequestDialog();

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_change_tel);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initDatas();
        initViews();

        back=(RelativeLayout)findViewById(R.id.shop_changeTel_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit=(Button)findViewById(R.id.btn_shop_changeTel_tijiao);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!judge())
                    return;
                else
                    submit();
            }
        });

    }

    private void  initDatas(){
        sp=new SharePreferenceUtil(this,"user");
    }

    private void initViews(){
        txt_shopAccount=(TextView)findViewById(R.id.shop_changeTel_shopAccont);
        txt_shopAccount.setText(sp.getAccount());
        et_psw=(EditText)findViewById(R.id.shop_changeTel_psw);
        et_tel=(EditText)findViewById(R.id.shop_changeTel_tel);
    }

    private boolean judge(){
        shopPsw=et_psw.getText().toString().trim();
        shopTel=et_tel.getText().toString().trim();
//        if(shopPsw.equals(""))
//        {
//            ToastCustom.makeToastCenter(getApplicationContext(),"请输入密码");
//            return false;
//        }
        if(shopTel.equals("")){
            ToastCustom.makeToastCenter(getApplicationContext(),"请输入手机号");
            return false;
        }
        return  true;
    }

    private void submit(){
        final String url= ConnectUtil.SHOP_CHANGE_TEL;
        final Message msg=new Message();
        final PostParameter[] postParameters=new PostParameter[4];
        postParameters[0]=new PostParameter("shop_account",sp.getAccount());
      //  postParameters[1]=new PostParameter("password", Encode.getEncode("MD5", shopPsw));
        postParameters[1]=new PostParameter("password", Encode.getEncode("MD5", sp.getPassword()));
        postParameters[2]=new PostParameter("new_tel",shopTel);
        postParameters[3]=new PostParameter("cookie",sp.getCookie());
        showRequestDialog();
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(ConnectUtil.isNetworkAvailable(getApplicationContext())){
                    String jsonStr=ConnectUtil.httpRequest(url,postParameters,ConnectUtil.POST);
                    if(jsonStr==null||jsonStr.equals("")){
                        msg.what=0;
                        msg.obj="fail";
                    }else {
                        try {
                            JSONObject result=new JSONObject(jsonStr);
                            msg.what=1;
                            msg.obj=result;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    msg.what=-1;
                    msg.obj="网络不可用";
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(ShopChangeTelActivity.this, "请稍等...");
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
