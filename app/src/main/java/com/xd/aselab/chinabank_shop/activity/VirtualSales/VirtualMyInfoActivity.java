package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CircleImageView;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import java.io.FileNotFoundException;

import cn.jpush.android.api.JPushInterface;

public class VirtualMyInfoActivity extends AppCompatActivity {

    private TextView update;
    private LinearLayout change_psw;
    private LinearLayout change_tel;
    private TextView userName;
    private TextView account_text;
    private TextView psw_text;
    private TextView name_text;
    private TextView tel_text;
    private CircleImageView image;
    private ImageView back;
    private ImageLoader imageLoader;
    private Uri imageUri;
    private Button exit;
    private boolean state;

    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_my_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initEvent();
    }

    private void initView() {
        // 个人信息修改/完成的flag
        state = true;

        sp = new SharePreferenceUtil(VirtualMyInfoActivity.this, "user");
        userName = (TextView) findViewById(R.id.user_name);
        account_text = (TextView) findViewById(R.id.my_account);
        change_psw = (LinearLayout)findViewById(R.id.change_password);
        psw_text = (TextView) findViewById(R.id.my_password);
        name_text = (TextView) findViewById(R.id.my_name);
        tel_text = (TextView) findViewById(R.id.my_tel);
        change_tel = (LinearLayout)findViewById(R.id.change_tel);
        image = (CircleImageView) findViewById(R.id.image);
        imageLoader = ImageLoader.getInstance();
        update = (TextView) findViewById(R.id.update);
        back = (ImageView) findViewById(R.id.back);
        exit = (Button) findViewById(R.id.exit);

        userName.setText(sp.getWorkerName());
        name_text.setText(sp.getWorkerName());
        account_text.setText(sp.getAccount());
        tel_text.setText(sp.getWorkerTel());
        psw_text.setText(sp.getPassword());

        imageLoader.loadBitmap(VirtualMyInfoActivity.this, sp.getHead_image(), image, R.drawable.final_head);
    }

    private void initEvent() {
        // 返回事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 修改信息事件
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state) {
                    update.setText("  完成");
                    // 修改时改变文本颜色
                    changeTextColor(state);
                    state = false;
                    change_psw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            //如果用户的密码是默认初始密码，则说明是第一次修改密码
                            //此时需要去设置安全问题
                            if ("123456".equals(sp.getPassword())) {
                                intent.setClass(VirtualMyInfoActivity.this, VirtualSecureQuestionActivity.class);
                            } else {
                                intent.setClass(VirtualMyInfoActivity.this, VirtualChangePasswordActivity.class);
                            }
                            startActivity(intent);
                        }
                    });
                    change_tel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(VirtualMyInfoActivity.this, VirtualChangeTelActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    update.setText("  修改");
                    changeTextColor(state);
                    state = true;
                    change_psw.setOnClickListener(null);
                    change_tel.setOnClickListener(null);
                }
            }
        });

        // 退出登录
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.setIsLogin(false);
                JPushInterface.deleteAlias(VirtualMyInfoActivity.this, 0);
                JPushInterface.cleanTags(VirtualMyInfoActivity.this, 1);
                Intent intent = new Intent(VirtualMyInfoActivity.this, LoginActivity.class);
                setResult(Constants.EXIT_TO_LOGIN, intent);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                imageUri = Uri.parse(sp.getHead_image());
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

        //用户正在修改个人信息
        if (state) {
            psw_text.setTextColor(black);
            tel_text.setTextColor(black);
            account_text.setTextColor(grey);
            name_text.setTextColor(grey);
        } else {

            account_text.setTextColor(grey);
            name_text.setTextColor(grey);
            psw_text.setTextColor(grey);
            tel_text.setTextColor(grey);
        }
    }
}
