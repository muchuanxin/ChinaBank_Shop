package com.xd.aselab.chinabank_shop.activity.worker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.activity.shop.MyInfoActivity;
import com.xd.aselab.chinabank_shop.activity.shop.ShopChangePswActivity;
import com.xd.aselab.chinabank_shop.activity.shop.ShopChangeTelActivity;
import com.xd.aselab.chinabank_shop.activity.shop.ShopMyDimerCode;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

public class WorkerMyInfoActivity extends AppCompatActivity {

    private LinearLayout btn_toChangePsw;
    private LinearLayout btn_toChangeTel;
    private TextView label_workerAccount,label_workerPsw,label_shopName,label_shopType,label_shop_addr;
    private TextView label_workerTel,lable_shopTel,label_workerName;
    private TextView update;
    private TextView txt_workerAccount;
    private TextView txt_workerPsw,txt_workerName;
    private TextView txt_shopName;
    private TextView txt_shopType;
    private TextView txt_shop_addr;
    private TextView txt_workerTel;
    private TextView txt_shopTel;
    private LinearLayout shopTel;
    private SharePreferenceUtil sp;
    private Button btn_exit;
    private RelativeLayout btn_back;
    private boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_my_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initDatas();
        initViews();

        btn_toChangePsw=(LinearLayout)findViewById(R.id.worker_my_psw_row);
//        btn_toChangePsw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(WorkerMyInfoActivity.this, WorkerChangePsw.class);
//                startActivity(intent);
//            }
//        });

        btn_toChangeTel=(LinearLayout)findViewById(R.id.worker_my_tel_row);
//        btn_toChangeTel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(WorkerMyInfoActivity.this, WorkerChangeTel.class);
//                startActivity(intent);
//            }
//        });

        shopTel=(LinearLayout)findViewById(R.id.worker_my_shop_tel_row);
//        shopTel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sp.getShopMoblie())));
//                call();
//
//            }
//        });

        btn_back=(RelativeLayout)findViewById(R.id.worker_my_info_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_exit=(Button)findViewById(R.id.worker_tuichu_my_info);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(WorkerMyInfoActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                MobclickAgent.onProfileSignOff();
                finish();

            }
        });

        update = (TextView) findViewById(R.id.act_worker_my_info_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state){
                    update.setText("完成");
                    changeTextColor(state);
                    state = false;
                    btn_toChangePsw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(WorkerMyInfoActivity.this, WorkerChangePsw.class);
                            startActivity(intent);
                        }
                    });
                    btn_toChangeTel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(WorkerMyInfoActivity.this, WorkerChangeTel.class);
                            startActivity(intent);
                        }
                    });
                    shopTel.setOnClickListener(null);
                }
                else {
                    update.setText("修改");
                    changeTextColor(state);
                    state = true;
                    btn_toChangePsw.setOnClickListener(null);
                    btn_toChangeTel.setOnClickListener(null);
                    shopTel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sp.getShopMoblie())));
                            call();

                        }
                    });
                }
            }
        });

    }
    private void call(){
        AlertDialog.Builder builder=new AlertDialog.Builder(WorkerMyInfoActivity.this);
        builder.setTitle("商家电话");
        builder.setMessage(sp.getShopMoblie());
        builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(WorkerMyInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WorkerMyInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sp.getShopMoblie())));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void initDatas(){
        sp=new  SharePreferenceUtil(this,"user");
    }

    private void initViews(){
        label_workerAccount = (TextView)findViewById(R.id.label_worker_my_account);
        label_workerName = (TextView) findViewById(R.id.label_worker_my_name);
        label_workerPsw = (TextView)findViewById(R.id.label_worker_my_psw);
        label_shopName = (TextView)findViewById(R.id.label_worker_my_shop_name);
        label_shopType = (TextView)findViewById(R.id.label_worker_my_shop_type);
        label_shop_addr = (TextView)findViewById(R.id.label_worker_my_shop_addr);
        label_workerTel = (TextView)findViewById(R.id.label_worker_my_tel);
        lable_shopTel = (TextView)findViewById(R.id.label_worker_my_shop_managerTel);

        txt_workerAccount=(TextView)findViewById(R.id.worker_my_account_text);
        txt_workerAccount.setText(sp.getAccount());

        txt_workerName = (TextView)findViewById(R.id.worker_my_name_text);
        txt_workerName.setText(sp.getWorkerName());

        txt_workerPsw=(TextView)findViewById(R.id.worker_my_psw_text);
        txt_workerPsw.setText(sp.getPassword());
        txt_shopName=(TextView)findViewById(R.id.worker_my_shop_name_text);
        txt_shopName.setText(sp.getShopName());

        txt_shopType=(TextView)findViewById(R.id.worker_my_shop_type_text);
        txt_shopType.setText(sp.getShopIndustryType());

        txt_shop_addr=(TextView)findViewById(R.id.worker_my_shop_addr_text);
        txt_shop_addr.setMovementMethod(ScrollingMovementMethod.getInstance());
        txt_shop_addr.setOnClickListener(new View.OnClickListener() {
            Boolean flag = true;
            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    txt_shop_addr.setEllipsize(null);
                } else {
                    flag = true;
                    txt_shop_addr.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
        String province=sp.getShopProvince();
        String city=sp.getShopCity();
        String county=sp.getShopCounty();
        String street=sp.getShopStreet();
        txt_shop_addr.setText(province + city + county + street);

        txt_workerTel=(TextView)findViewById(R.id.worker_my_tel_text);
        txt_workerTel.setText(sp.getWorkerTel());

        txt_shopTel=(TextView)findViewById(R.id.worker_my_shop_managerTel_text);
        txt_shopTel.setText(sp.getShopMoblie());
    }

    private void changeTextColor(boolean state){
        if (state){
            int grey = getResources().getColor(R.color.grey);
            label_workerAccount.setTextColor(grey);
            txt_workerAccount.setTextColor(grey);
            label_workerName.setTextColor(grey);
            txt_workerName.setTextColor(grey);
            label_shopName.setTextColor(grey);
            txt_shopName.setTextColor(grey);
            label_shopType.setTextColor(grey);
            txt_shopType.setTextColor(grey);
            label_shop_addr.setTextColor(grey);
            txt_shop_addr.setTextColor(grey);
            lable_shopTel.setTextColor(grey);
            txt_shopTel.setTextColor(grey);
            // my.setTextColor(grey);
        }
        else {
            int black = getResources().getColor(R.color.black);
            label_workerAccount.setTextColor(black);
            txt_workerAccount.setTextColor(black);
            label_workerName.setTextColor(black);
            txt_workerName.setTextColor(black);
            label_shopName.setTextColor(black);
            txt_shopName.setTextColor(black);
            label_shopType.setTextColor(black);
            txt_shopType.setTextColor(black);
            label_shop_addr.setTextColor(black);
            txt_shop_addr.setTextColor(black);
            lable_shopTel.setTextColor(black);
            txt_shopTel.setTextColor(black);
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
