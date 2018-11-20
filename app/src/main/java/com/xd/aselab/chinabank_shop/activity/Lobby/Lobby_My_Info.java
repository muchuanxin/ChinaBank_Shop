package com.xd.aselab.chinabank_shop.activity.Lobby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.Lobby;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Info_Info;
import com.xd.aselab.chinabank_shop.activity.CardDiv.ChangePhotoActivity;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CircleImageView;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import java.io.FileNotFoundException;

import cn.jpush.android.api.JPushInterface;

public class Lobby_My_Info extends AppCompatActivity {

    private TextView update;
    private RelativeLayout back;
    private LinearLayout change_psw;
    private LinearLayout change_tel;
    private TextView userName;
    private TextView account_label;
    private TextView account_text;
    private TextView psw_label;
    private TextView psw_text;
    private TextView name_label;
    private TextView name_text;
    private TextView tel_label;
    private TextView tel_text;
    private TextView erji_label;
    private TextView erji_text;
    private TextView siji_label;
    private TextView siji_text;
    private CircleImageView image;
    private ImageLoader imageLoader;
    private Uri imageUri;
    private Button exit;
    private boolean state = true;

    private SharePreferenceUtil sp;

    @Override
    protected void onResume() {
        super.onResume();
        sp = new SharePreferenceUtil(Lobby_My_Info.this, "user");
        userName = (TextView) findViewById(R.id.user_name);
        psw_label = (TextView) findViewById(R.id.label_shop_psw);
        psw_text = (TextView) findViewById(R.id.my_psw_text);
        tel_label = (TextView) findViewById(R.id.my_tel_label);
        tel_text = (TextView) findViewById(R.id.my_tel_text);
        account_label = (TextView) findViewById(R.id.label_shop_account);
        account_text = (TextView) findViewById(R.id.my_account_text);
        name_label = (TextView) findViewById(R.id.my_name_label);
        name_text = (TextView) findViewById(R.id.my_name_text);
        erji_label = (TextView) findViewById(R.id.erji_name_label);
        erji_text = (TextView) findViewById(R.id.erji_name_text);
        siji_label = (TextView) findViewById(R.id.siji_name_label);
        siji_text = (TextView) findViewById(R.id.siji_name_text);
        image = (CircleImageView) findViewById(R.id.image);
        imageLoader = ImageLoader.getInstance();

        userName.setText(sp.getWorkerName());
        name_text.setText(sp.getWorkerName());
        account_text.setText(sp.getAccount());
        tel_text.setText(sp.getWorkerTel());
        psw_text.setText(sp.getPassword());
        erji_text.setText(sp.getErji_Name());
        siji_text.setText(sp.getSiji_Name());

        //---------------------------------------
        Log.d("Dorise",sp.getPhotoUrl()+"");
        imageLoader.loadBitmap(Lobby_My_Info.this, sp.getHead_image(), image, R.drawable.final_head);

        /*
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击更换头像
                Intent intent = new Intent(Lobby_My_Info.this, ChangePhotoActivity.class);
                intent.putExtra("jump","personal_info");
                startActivityForResult(intent,3);

            }
        });
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_my_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.my_info_back_btn) ;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        update = (TextView) findViewById(R.id.update);
        change_psw = (LinearLayout) findViewById(R.id.my_psw_row);
        change_tel = (LinearLayout) findViewById(R.id.tel_row);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state) {
                    update.setText("  完成");
                    changeTextColor(state);
                    state = false;
                    change_psw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            //如果用户的密码是默认初始密码，则说明是第一次修改密码
                            sp = new SharePreferenceUtil(Lobby_My_Info.this, "user");
                            if("123456".equals(sp.getPassword())) {
                                intent.setClass(Lobby_My_Info.this, SecureQustionActivity.class);
                            } else {
                                intent.setClass(Lobby_My_Info.this, Lobby_ChangePsw.class);
                            }
                            startActivity(intent);
                        }
                    });
                    change_tel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(Lobby_My_Info.this, Lobby_ChangeTel.class);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    update.setText("  修改");
                    changeTextColor(state);
                    state = true;
                    change_psw.setOnClickListener(null);
                    change_tel.setOnClickListener(null);
                }
            }
        });

        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.setIsLogin(false);
                JPushInterface.deleteAlias(Lobby_My_Info.this, 0);
                JPushInterface.cleanTags(Lobby_My_Info.this, 1);
                Intent intent = new Intent(Lobby_My_Info.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
        int black = getResources().getColor(R.color.black);
        int grey = getResources().getColor(R.color.grey);
        psw_label = (TextView) findViewById(R.id.label_shop_psw);
        psw_text = (TextView) findViewById(R.id.my_psw_text);
        tel_label = (TextView) findViewById(R.id.my_tel_label);
        tel_text = (TextView) findViewById(R.id.my_tel_text);
        account_label = (TextView) findViewById(R.id.label_shop_account);
        account_text = (TextView) findViewById(R.id.my_account_text);
        name_label = (TextView) findViewById(R.id.my_name_label);
        name_text = (TextView) findViewById(R.id.my_name_text);
        erji_label = (TextView) findViewById(R.id.erji_name_label);
        erji_text = (TextView) findViewById(R.id.erji_name_text);
        siji_label = (TextView) findViewById(R.id.siji_name_label);
        siji_text = (TextView) findViewById(R.id.siji_name_text);

        //用户正在修改个人信息
        if(state) {
            psw_label.setTextColor(black);
            psw_text.setTextColor(black);
            tel_label.setTextColor(black);
            tel_text.setTextColor(black);

            account_label.setTextColor(grey);
            account_text.setTextColor(grey);
            name_label.setTextColor(grey);
            name_text.setTextColor(grey);
            erji_label.setTextColor(grey);
            erji_text.setTextColor(grey);
            siji_label.setTextColor(grey);
            siji_text.setTextColor(grey);
        } else {
            account_label.setTextColor(black);
            psw_label.setTextColor(black);
            name_label.setTextColor(black);
            tel_label.setTextColor(black);
            erji_label.setTextColor(black);
            siji_label.setTextColor(black);

            account_text.setTextColor(grey);
            name_text.setTextColor(grey);
            psw_text.setTextColor(grey);
            tel_text.setTextColor(grey);
            erji_text.setTextColor(grey);
            siji_text.setTextColor(grey);
        }
    }
}
