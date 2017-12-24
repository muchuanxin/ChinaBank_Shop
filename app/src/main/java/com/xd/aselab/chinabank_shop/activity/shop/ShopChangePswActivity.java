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
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopChangePswActivity extends AppCompatActivity {

    private RelativeLayout back;
    private TextView txt_shopAccount;
    private EditText et_oldPsw;
    private EditText et_newPsw;
    private EditText et_newPswAgain;
    private Button btn_submit;
    private String shopAccount;
    private String oldpsw;
    private String newpsw;
    private String newPsw_again;
    private SharePreferenceUtil sp;
    private Dialog mDialog=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
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
                        sp.setPassword(newpsw);
                        Intent intent=new Intent(ShopChangePswActivity.this,LoginActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ShopChangePswActivity.this.finish();
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
        setContentView(R.layout.activity_shop_change_psw);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initDatas();
        initViews();

        back=(RelativeLayout)findViewById(R.id.shop_changePsw_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit=(Button)findViewById(R.id.btn_shop_changePsw);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!judge())
                    return;
                else
                    submit();
//                Intent intent1=new Intent();
//                intent1.setClass(ShopChangePswActivity.this, ShopMainPageActivity.class);
//                startActivity(intent1);
            }
        });
    }

    private void initDatas(){
        sp=new SharePreferenceUtil(this,"user");
    }

    private void initViews(){

        txt_shopAccount=(TextView)findViewById(R.id.shop_changePsw_id);
        txt_shopAccount.setText(sp.getAccount());

        et_newPsw=(EditText)findViewById(R.id.shop_changePsw_newPsw);
        et_oldPsw=(EditText)findViewById(R.id.shop_changePsw_oldPsw);
        et_newPswAgain=(EditText)findViewById(R.id.shop_changePsw_newPsw_again);

    }

    private void submit(){

        final String url=ConnectUtil.SHOP_CHANGE_PSW;
        final Message msg=new Message();

        final PostParameter[] postParameters=new PostParameter[4];
        postParameters[0]=new PostParameter("shop_account",sp.getAccount());
        postParameters[1]=new PostParameter("old_password", Encode.getEncode("MD5", oldpsw));
        postParameters[2]=new PostParameter("new_password",Encode.getEncode("MD5",newpsw));
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
    private boolean judge(){

        oldpsw=et_oldPsw.getText().toString().trim();
        newpsw=et_newPsw.getText().toString().trim();
        newPsw_again=et_newPswAgain.getText().toString().trim();

        if(oldpsw.equals(""))
        {
            ToastCustom.makeToastCenter(getApplicationContext(),"请输入旧密码");
            return false;
        }
        if(newpsw.equals(""))
        {
            ToastCustom.makeToastCenter(getApplicationContext(),"请输入新密码");
            return false;
        }
        if(newpsw.length()<6||newPsw_again.length()<6)
        {
            ToastCustom.makeToastCenter(getApplicationContext(),"密码至少为六位");
            return false;
        }
        if(!newpsw.equals(newPsw_again))
        {
            ToastCustom.makeToastCenter(getApplicationContext(),"新密码和确认密码不一致");
            return false;
        }

        return true;
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(ShopChangePswActivity.this, "请稍等...");
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
