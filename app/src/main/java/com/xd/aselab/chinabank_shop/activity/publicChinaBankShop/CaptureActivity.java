package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class CaptureActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    //扫码成功回调
    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", result);
        this.setResult(RESULT_OK, resultIntent);
        finish();
    }

    //打开相机失败回调
    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机失败", Toast.LENGTH_SHORT).show();
    }

    //手机振动
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

}
