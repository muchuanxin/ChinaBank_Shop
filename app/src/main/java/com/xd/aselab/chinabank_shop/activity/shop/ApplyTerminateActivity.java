package com.xd.aselab.chinabank_shop.activity.shop;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplyTerminateActivity extends AppCompatActivity {

    private RelativeLayout btnBack;
    private EditText et_terminate_reason;
    private String terminateReason;
    private Button submitBtn;
    private Dialog mDialog=null;
    private SharePreferenceUtil sp;
    private Handler handler=new Handler(){
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
                        ToastCustom.makeToastCenter(getApplicationContext(), "提交成功");
                        setResult(Activity.RESULT_OK);
                        ApplyTerminateActivity.this.finish();
                    }else {
                        ToastCustom.makeToastCenter(getApplicationContext(),"提交失败，请重新尝试");
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
        setContentView(R.layout.activity_apply_terminate);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp=new SharePreferenceUtil(this,"user");

        et_terminate_reason=(EditText)findViewById(R.id.reson_of_terminate);

        btnBack=(RelativeLayout) findViewById(R.id.apply_terminate_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitBtn=(Button) findViewById(R.id.tijiao_reson);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!judge()){
                    return;
                }else {
                    submit();
                }

            }
        });
    }

    private boolean judge(){
        terminateReason=et_terminate_reason.getText().toString().trim();
        if(terminateReason.equals("")){
            ToastCustom.makeToastCenter(getApplicationContext(),"请输入解约理由");
            return false;
        }
        return true;
    }

    private void submit(){
        showRequestDialog();
        final String url= ConnectUtil.RELIEVE_SHOP;
        new Thread(){
           @Override
           public void run() {
               Message message=new Message();
               if(ConnectUtil.isNetworkAvailable(getApplicationContext())) {
                   PostParameter[] postParameters = new PostParameter[4];
                   postParameters[0] = new PostParameter("shop_account", sp.getAccount());
                   postParameters[1] = new PostParameter("manager_account", sp.getShopManagerAccount());
                   postParameters[2] = new PostParameter("reason", et_terminate_reason.getText().toString().trim());
                   postParameters[3] = new PostParameter("cookie",sp.getCookie());
                   String jsonStr = ConnectUtil.httpRequest(url, postParameters, ConnectUtil.POST);
                   if (jsonStr == null || jsonStr.equals("")) {
                       message.what = 0;
                       message.obj = "fail";
                       handler.sendMessage(message);
                   } else {
                       try {
                           JSONObject result = new JSONObject(jsonStr);
                           message.what = 1;
                           message.obj = result;
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

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(ApplyTerminateActivity.this, "请稍等...");
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
