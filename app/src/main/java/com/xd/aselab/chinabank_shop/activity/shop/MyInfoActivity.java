package com.xd.aselab.chinabank_shop.activity.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

//店主个人信息
public class MyInfoActivity extends AppCompatActivity {

    private Button tuichu;
    private RelativeLayout back;
    private LinearLayout changePsw;
    private LinearLayout changeTel;
    private LinearLayout baseManagerTel;
    private LinearLayout my_dimerCode;
    private TextView update;
    private TextView lable_shop_account,lable_shop_psw,lable_shop_name,lable_shop_type,lable_shop_addr;
    private TextView lable_shop_tel,lable_shop_mingpian,lable_shop_dimer;
    private TextView txt_shopkeeper_account;
    private TextView txt_shopkeeper_psw;
    private TextView txt_shop_name;
    private TextView txt_shop_type;
    private TextView txt_shop_addr;
    private TextView txt_my_tel;
    private TextView txt_manage_tel;
    private SharePreferenceUtil sp;
    private boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initDatas();
        initViews();

        back=(RelativeLayout) findViewById(R.id.my_info_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tuichu=(Button) findViewById(R.id.tuichu_my_info);
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(
                        MyInfoActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                MobclickAgent.onProfileSignOff();
                finish();
            }
        });

        changePsw=(LinearLayout)findViewById(R.id.my_psw_row);
//        changePsw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setClass(MyInfoActivity.this, ShopChangePswActivity.class);
//                startActivity(intent);
//            }
//        });
//
        changeTel=(LinearLayout) findViewById(R.id.my_shop_tel_row);
//        changeTel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setClass(MyInfoActivity.this,ShopChangeTelActivity.class);
//                startActivity(intent);
//            }
//        });

        baseManagerTel=(LinearLayout)findViewById(R.id.my_shop_managerTel_row);
        baseManagerTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sp.getShopManagerTel())));
                call2();
            }
        });

        my_dimerCode=(LinearLayout)findViewById(R.id.my_shop_erweima_row);
        my_dimerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MyInfoActivity.this,ShopMyDimerCode.class);
                startActivity(intent);
            }
        });

        update = (TextView) findViewById(R.id.act_my_info_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state){
                    update.setText("完成");
                    changeTextColor(state);
                    state = false;
                    changePsw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MyInfoActivity.this, ShopChangePswActivity.class);
                            startActivity(intent);
                        }
                    });
                    changeTel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(MyInfoActivity.this, ShopChangeTelActivity.class);
                            startActivity(intent);
                        }
                    });
                    my_dimerCode.setOnClickListener(null);
                }
                else {
                    update.setText("修改");
                    changeTextColor(state);
                    state = true;
                    changePsw.setOnClickListener(null);
                    changeTel.setOnClickListener(null);
                    my_dimerCode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent();
                            intent.setClass(MyInfoActivity.this,ShopMyDimerCode.class);
                            startActivity(intent);
                        }
                    });

    }
}
        });

    }

    private void call2(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MyInfoActivity.this);
        builder.setTitle("联系人电话");
        builder.setMessage(sp.getShopManagerTel());
        builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sp.getShopManagerTel())));
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

    }

    private void initViews(){
        sp=new SharePreferenceUtil(this,"user");
        lable_shop_account=(TextView)findViewById(R.id.label_shop_account);
        lable_shop_psw=(TextView)findViewById(R.id.label_shop_psw);
        lable_shop_name=(TextView)findViewById(R.id.label_shop_name);
        lable_shop_type=(TextView)findViewById(R.id.label_shop_type);
        lable_shop_addr=(TextView)findViewById(R.id.label_shop_address);
        lable_shop_tel=(TextView)findViewById(R.id.label_shop_tel);
        lable_shop_mingpian=(TextView)findViewById(R.id.label_shop_mingpian);
        lable_shop_dimer=(TextView)findViewById(R.id.my_erweima_text);
        txt_shopkeeper_account=(TextView)findViewById(R.id.my_account_text);
        txt_shopkeeper_account.setText(sp.getAccount());
        txt_shopkeeper_psw=(TextView)findViewById(R.id.my_psw_text);
        txt_shopkeeper_psw.setText(sp.getPassword());
        txt_shop_name=(TextView)findViewById(R.id.my_shop_name_text);
        txt_shop_name.setText(sp.getShopName());
        txt_shop_type=(TextView)findViewById(R.id.my_shop_type_text);
        txt_shop_type.setText(sp.getShopIndustryType());
        txt_shop_addr=(TextView)findViewById(R.id.my_shop_addr_text);
        txt_shop_addr.setMovementMethod(ScrollingMovementMethod.getInstance());
        txt_shop_addr.setOnClickListener(new View.OnClickListener() {
            Boolean flag=true;
            @Override
            public void onClick(View v) {
                if(flag){
                    flag=false;
                    txt_shop_addr.setEllipsize(null);
                }else {
                    flag=true;
                    txt_shop_addr.setEllipsize(TextUtils.TruncateAt.END);
                }

            }
        });
        String province=sp.getShopProvince();
        String city=sp.getShopCity();
        String county=sp.getShopCounty();
        String street=sp.getShopStreet();
        txt_shop_addr.setText(province+city+county+street);
        txt_my_tel=(TextView) findViewById(R.id.my_shop_tel_text);
        txt_my_tel.setText(sp.getShopMoblie());
        txt_manage_tel=(TextView)findViewById(R.id.my_shop_managerTel_text);
        txt_manage_tel.setText(sp.getShopManagerTel());

    }

    private void changeTextColor(boolean state){
        if (state){
            int grey = getResources().getColor(R.color.grey);
            lable_shop_account.setTextColor(grey);
            txt_shopkeeper_account.setTextColor(grey);
            lable_shop_name.setTextColor(grey);
            txt_shop_name.setTextColor(grey);
            lable_shop_type.setTextColor(grey);
            txt_shop_type.setTextColor(grey);
            lable_shop_addr.setTextColor(grey);
            txt_shop_addr.setTextColor(grey);
            lable_shop_mingpian.setTextColor(grey);
            lable_shop_dimer.setTextColor(grey);
           // my.setTextColor(grey);
        }
        else {
            int black = getResources().getColor(R.color.black);
            lable_shop_account.setTextColor(black);
            txt_shopkeeper_account.setTextColor(black);
            lable_shop_name.setTextColor(black);
            txt_shop_name.setTextColor(black);
            lable_shop_type.setTextColor(black);
            txt_shop_type.setTextColor(black);
            lable_shop_addr.setTextColor(black);
            txt_shop_addr.setTextColor(black);
            lable_shop_mingpian.setTextColor(black);
            lable_shop_dimer.setTextColor(black);
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
