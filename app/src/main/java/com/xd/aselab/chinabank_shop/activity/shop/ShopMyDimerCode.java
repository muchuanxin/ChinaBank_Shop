package com.xd.aselab.chinabank_shop.activity.shop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ShopMyDimerCode extends AppCompatActivity {
    private RelativeLayout back;
    private ImageView im_code;
    private String reCode="";//要生成二维码的字符串
    private SharePreferenceUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_my_dimer_code);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp=new SharePreferenceUtil(this,"user");
        reCode=sp.getAccount();
        initView();
    }

    void initView(){
        im_code=(ImageView)findViewById(R.id.im_code);
        back=(RelativeLayout)findViewById(R.id.shop_dimerCode_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createCode(reCode);
    }

    void createCode(String recode){

        Bitmap logoBitmap = BitmapFactory.decodeResource(ShopMyDimerCode.this.getResources(), R.drawable.busniess80);
        Bitmap qrCodeBitmap = QRCodeEncoder.syncEncodeQRCode(recode,
                BGAQRCodeUtil.dp2px(ShopMyDimerCode.this, 150), Color.BLACK, Color.WHITE, logoBitmap);

        im_code.setImageBitmap(qrCodeBitmap);
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
