package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.CustonPopWindow;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.activity.shop.MyInfoActivity;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;


public class CardDiv_My_Info_Info extends AppCompatActivity {


    private boolean change = false;
    private TextView act_my_info_update;
    private LinearLayout my_account_row;
    private LinearLayout name_row;
    private TextView name_text;
    private TextView label_shop_account;
    private TextView my_account_text;
    private TextView manager_tel;
    private LinearLayout my_psw_row;
    private LinearLayout type_row;
    private Uri imageUri;
    private ImageView image;
    private TextView exit;
    private TextView user_name;
    private ImageLoader imageLoader;
    private SharePreferenceUtil sp;
    private TextView my_psw_text;
    private TextView name;
    private TextView label_shop_psw;
    private TextView type_text;
    private TextView label_shop_type;
    private TextView manager_tel_text;
    private CustonPopWindow popwindow;

    @Override
    protected void onResume() {
        super.onResume();
        manager_tel = (TextView) findViewById(R.id.manager_tel);
        manager_tel_text.setText(sp.getShopManagerTel());

        my_account_text.setText(sp.getAccount());


        my_psw_text.setText(sp.getPassword());

        type_text = (TextView) findViewById(R.id.type_text);
        type_text.setText(sp.getWorkerTel());
        name_text = (TextView) findViewById(R.id.name_text);
        name = (TextView) findViewById(R.id.name);
        name_text.setText(sp.getWorkerName());
        label_shop_psw= (TextView) findViewById(R.id.label_shop_psw);
        label_shop_type= (TextView) findViewById(R.id.label_shop_type);
        sp = new SharePreferenceUtil(CardDiv_My_Info_Info.this, "user");
        image = (ImageView) findViewById(R.id.image);
        imageLoader = ImageLoader.getInstance();

        imageLoader.loadBitmap(CardDiv_My_Info_Info.this, sp.getHead_image(), image, R.drawable.portrait);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__info__info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        sp = new SharePreferenceUtil(CardDiv_My_Info_Info.this, "user");
        label_shop_account = (TextView) findViewById(R.id.label_shop_account);

        user_name = (TextView) findViewById(R.id.user_name);
        user_name.setText(sp.getWorkerName());
        exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //退出登录清空Alias和Tags
                sp.setIsLogin(false);
                JPushInterface.deleteAlias(CardDiv_My_Info_Info.this, 0);
                JPushInterface.cleanTags(CardDiv_My_Info_Info.this, 1);

                Intent intent = new Intent(CardDiv_My_Info_Info.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        my_account_text = (TextView) findViewById(R.id.my_account_text);


        my_psw_text = (TextView) findViewById(R.id.my_psw_text);

        manager_tel_text = (TextView) findViewById(R.id.manager_tel_text);

        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayout addr_row = (LinearLayout) findViewById(R.id.addr_row);
        addr_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //经理电话  点击跳出拨号对话框
                popwindow = new CustonPopWindow(CardDiv_My_Info_Info.this);
                popwindow.showAtLocation(CardDiv_My_Info_Info.this.findViewById(R.id.manager_tel), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                //给popwindow设置取消时的监听   popwindow消失时设置透明背景
                popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1; //0.0-1.0
                        getWindow().setAttributes(lp);
                    }
                });

//                TextView cancel = popwindow.getView().findViewById(R.id.cancel);
//                cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        popwindow.dismiss();
//                    }
//                });


                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.5f; //0.0-1.0
                getWindow().setAttributes(lp);
            }
        });


        name_row = (LinearLayout) findViewById(R.id.name_row);


        act_my_info_update = (TextView) findViewById(R.id.act_my_info_update);
        my_psw_row = (LinearLayout) findViewById(R.id.my_psw_row);
        type_row = (LinearLayout) findViewById(R.id.type_row);
        act_my_info_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (change) {
                    //密码 手机号可修改
                    //帐号姓名经理电话不可修改
                    act_my_info_update.setText("修改");
                    change = false;
                    changeTextColor(change);


                    my_psw_row.setOnClickListener(null);
                    type_row.setOnClickListener(null);
                    image.setOnClickListener(null);

                } else {
                    act_my_info_update.setText("完成");
                    change = true;
                    changeTextColor(change);


                    my_psw_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CardDiv_My_Info_Info.this, CardDiv_Change_Password.class);
                            startActivity(intent);
                        }
                    });

                    type_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent1 = new Intent(CardDiv_My_Info_Info.this, CardDiv_Change_Tel.class);
                            startActivity(intent1);
                        }
                    });


                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//点击更换头像

                            Intent intent = new Intent(CardDiv_My_Info_Info.this, ChangePhotoActivity.class);
                            intent.putExtra("jump","my_info");
                            startActivityForResult(intent,3);

                        }
                    });

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {

                imageUri= Uri.parse(sp.getHead_image());
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            image.setImageBitmap(bitmap); // 将裁剪后的照片显示出来

        }

    }


    private void changeTextColor(boolean state) {
        if (state) {
            int grey = getResources().getColor(R.color.grey);
            name_text.setTextColor(grey);
            name.setTextColor(grey);

            my_account_text.setTextColor(grey);
            label_shop_account.setTextColor(grey);

            label_shop_psw.setTextColor(getResources().getColor(R.color.black));
            my_psw_text.setTextColor(getResources().getColor(R.color.black));
            label_shop_type.setTextColor(getResources().getColor(R.color.black));
            type_text.setTextColor(getResources().getColor(R.color.black));

            manager_tel_text.setTextColor(grey);
            manager_tel.setTextColor(grey);

            // my.setTextColor(grey);
        } else {
            int black = getResources().getColor(R.color.black);
            name_text.setTextColor(Color.parseColor("#9e9e9e"));
            name.setTextColor(Color.parseColor("#565656"));
            my_account_text.setTextColor(Color.parseColor("#9e9e9e"));
            label_shop_account.setTextColor(Color.parseColor("#565656"));

            manager_tel_text.setTextColor(Color.parseColor("#9e9e9e"));
            manager_tel.setTextColor(Color.parseColor("#565656"));

            label_shop_psw.setTextColor(Color.parseColor("#565656"));
            my_psw_text.setTextColor(Color.parseColor("#9e9e9e"));
            label_shop_type.setTextColor(Color.parseColor("#565656"));
            type_text.setTextColor(Color.parseColor("#9e9e9e"));

        }
    }


}
