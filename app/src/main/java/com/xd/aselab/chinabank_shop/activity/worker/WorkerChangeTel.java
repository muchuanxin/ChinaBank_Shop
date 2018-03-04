package com.xd.aselab.chinabank_shop.activity.worker;

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
import com.xd.aselab.chinabank_shop.activity.shop.MyInfoActivity;
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

public class WorkerChangeTel extends AppCompatActivity {

    private RelativeLayout back;
    private Button btn_submit;
    private TextView txt_workerAccount;
    private EditText et_psw;
    private EditText et_tel;
    private String workerPsw;
    private String workerTel;
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
                        //sp.setShopManagerTel(workerTel);
                        sp.setWorkerTel(workerTel);
                        sp.setTelAccount(workerTel);
                        Intent intent=new Intent(WorkerChangeTel.this,WorkerMyInfoActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        WorkerChangeTel.this.finish();
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
        setContentView(R.layout.activity_worker_change_tel);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initDatas();
        initViews();

        back=(RelativeLayout)findViewById(R.id.worker_changeTel_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit=(Button)findViewById(R.id.btn_worker_changeTel_tijiao);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!judge())
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
        txt_workerAccount=(TextView)findViewById(R.id.worker_changeTel_shopAccont);
        txt_workerAccount.setText(sp.getAccount());
        et_psw=(EditText)findViewById(R.id.worker_changeTel_psw);
        et_tel=(EditText)findViewById(R.id.worker_changeTel_tel);
    }

    private boolean judge(){
        workerPsw=et_psw.getText().toString().trim();
        workerTel=et_tel.getText().toString().trim();
//        if(workerPsw.equals(""))
//        {
//            ToastCustom.makeToastCenter(getApplicationContext(), "请输入密码");
//            return false;
//        }
        if(workerTel.equals("")){
            ToastCustom.makeToastCenter(getApplicationContext(),"请输入电话号码");
            return false;
        }

        if(!isMobileNO( workerTel)){
            ToastCustom.makeToastCenter(WorkerChangeTel.this,"您输入的手机号有误");
            return false;
        }
        return  true;
    }

    private void submit(){
        final String url= ConnectUtil.WORKER_CHANGE_TEL;
        final Message msg=new Message();
        final PostParameter[] postParameters=new PostParameter[3];
        postParameters[0]=new PostParameter("worker_account",sp.getAccount());
        //postParameters[1]=new PostParameter("password", Encode.getEncode("MD5", workerPsw));
        postParameters[1]=new PostParameter("password", Encode.getEncode("MD5", sp.getPassword()));
        postParameters[2]=new PostParameter("new_tel",workerTel);
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
        mDialog = DialogFactory.creatRequestDialog(WorkerChangeTel.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    //判断手机号是否合法
    public static boolean isMobileNO(String mobiles) {
        // Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        //Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7,8])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
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
