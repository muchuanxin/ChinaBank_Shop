package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.EncryptUtils;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import java.util.Random;

public class CardDivScoreShopActivity extends AppCompatActivity {

    private ImageView back_btn;
    private WebView webView;

    private String account;
    private int score;
    private String noString;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div_score_shop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webview);

        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

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

        SharePreferenceUtil sp = new SharePreferenceUtil(CardDivScoreShopActivity.this, "user");
        account = sp.getAccount();
        score = getIntent().getIntExtra("not_exchange_score", 0);
        noString = generate6RandomNumber();
        String sign_content = "appsecret="+ EncryptUtils.AppSecret+"&nostring="+noString+"&score="+score+"&uid="+account;
        sign = Encode.getEncode("MD5", sign_content);

        try {
            String account_encrypt = EncryptUtils.encrypt(EncryptUtils.AppKey, account.getBytes("UTF-8"));
            String score_encrypt = EncryptUtils.encrypt(EncryptUtils.AppKey, (""+score).getBytes("UTF-8"));

            //测试url
            //String url = "http://jifen.koudaiqifu.cn/1000424?uid="+account_encrypt+"&score="+score_encrypt+"&nostring="+noString+"&sign="+sign;

            //正式url
            String url = "https://jifen.fxqifu.com/1000229?uid="+account_encrypt+"&score="+score_encrypt+"&nostring="+noString+"&sign="+sign;
            webView.loadUrl(url);

            Log.e("account", account);
            Log.e("score", score+"");
            Log.e("noString", noString);
            Log.e("sign_content", sign_content);
            Log.e("sign", sign);
            Log.e("account_encrypt", account_encrypt);
            Log.e("score_encrypt", score_encrypt);
            Log.e("url", url);

        }catch (Exception e){
            e.printStackTrace();
        }

        //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //解决HTTPS图片不显示的问题
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed(); // 接受网站证书
            }

        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
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
