package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.xd.aselab.chinabank_shop.R;

import java.util.Random;

public class BenDiYouHuiActivity extends AppCompatActivity {

    private RelativeLayout back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_di_you_hui);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.activity_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webview);

        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setSaveFormData(true);
//        webSettings.setGeolocationEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//
//        webView.requestFocus();
//        webView.setScrollBarStyle(0);

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setDomStorageEnabled(true);

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //解决HTTPS图片不显示的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

//        noString = generate6RandomNumber();

        try {
            String url = "http://cbank.orzc.cn/index.php/Index/indexs";
            webView.loadUrl(url);
        }catch (Exception e){
            e.printStackTrace();
        }

        //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        webView.setWebViewClient(new WebViewClient(){
//            //该方法此处不必重写
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//
//            }

            //解决HTTPS图片不显示的问题
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed(); // 接受网站证书
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setTitle(String.valueOf(view.getTitle()));
            }

        });
    }

//    @Override
//    public void onBackPressed() {
//        if (webView.canGoBack())
//            webView.goBack();
//        else
//            super.onBackPressed();
//    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            Log.e("tag", "canGoBack");
            return true;
        } else if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private String generate6RandomNumber(){
        Random rand = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0; i<6; i++){
            stringBuffer.append(rand.nextInt(10));
        }
        return stringBuffer.toString();
    }

}
