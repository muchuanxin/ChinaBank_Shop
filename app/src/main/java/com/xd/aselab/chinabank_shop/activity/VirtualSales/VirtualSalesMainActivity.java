package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.VirtualSales.fragment.VirtualMainFragment;
import com.xd.aselab.chinabank_shop.activity.VirtualSales.fragment.VirtualMeBasicFragment;
import com.xd.aselab.chinabank_shop.fragment.ChatFragment;
import com.xd.aselab.chinabank_shop.fragment.RecommendFragment;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

import static com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualSalesMainActivity.GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION;
import static com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualSalesMainActivity.KEY_EXTRAS;
import static com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualSalesMainActivity.KEY_MESSAGE;
import static com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualSalesMainActivity.MESSAGE_RECEIVED_ACTION;
import static com.xd.aselab.chinabank_shop.activity.VirtualSales.VirtualSalesMainActivity.MESSAGE_UPDATE_MESSAGCHAT_ACTION;
import static com.xd.aselab.chinabank_shop.util.Constants.FROM_MAIN_ME_TO_LOGIN;
import static com.xd.aselab.chinabank_shop.util.Constants.LOGIN_TO_MAIN_ME;

public class VirtualSalesMainActivity extends AppCompatActivity {

    private VirtualMainFragment fra_main;
    private ChatFragment fra_chat;
    private RecommendFragment fra_recommend;
    private VirtualMeBasicFragment fra_me_basic;

    private LinearLayout main_home_btn;
    private LinearLayout chat_btn;
    private LinearLayout recommend_btn;
    private LinearLayout me_btn;

    private View.OnClickListener footer_onclicklistener;

    private SharePreferenceUtil spu;
    private long mExitTime;
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(VirtualSalesMainActivity.this, "user");
        initViews();

        setTabSelection(0);
        registerMessageReceiver();
    }

    void initViews() {
        main_home_btn = (LinearLayout) findViewById(R.id.mcx_footer_home);
        chat_btn = (LinearLayout) findViewById(R.id.mcx_footer_chat);
        recommend_btn = (LinearLayout) findViewById(R.id.mcx_footer_tuijian);
        me_btn = (LinearLayout) findViewById(R.id.mcx_footer_me);

        // 只考虑首页和我的点击，群聊和推荐不搞
        footer_onclicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mcx_footer_home:
                        setTabSelection(0);
                        break;
                    case R.id.mcx_footer_me:
                        setTabSelection(1);
                        break;
                    default:
                        break;
                }
            }
        };
        main_home_btn.setOnClickListener(footer_onclicklistener);
        me_btn.setOnClickListener(footer_onclicklistener);
    }

    // 底部导航点击事件设置
    private void setTabSelection(int index) {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                //首页
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageButton) main_home_btn.findViewById(R.id.mcx_footer_home_btn))
                        .setImageResource(R.drawable.main_black);
                if (fra_main == null) {
                    fra_main = new VirtualMainFragment();
                    transaction.add(R.id.mcx_main_content, fra_main, "main_home");
                }
                break;
            case 1:
                // 我的
                // 当点击了设置tab时，改变控件的图片和文字颜色
                ((ImageButton) me_btn.findViewById(R.id.mcx_footer_me_btn))
                        .setImageResource(R.drawable.me_black);
                if (fra_me_basic == null) {
                    fra_me_basic = new VirtualMeBasicFragment();
                    transaction.add(R.id.mcx_main_content, fra_me_basic, "main_basic_me");
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    //清除掉所有的选中状态
    private void resetBtn() {
        ((ImageButton) main_home_btn.findViewById(R.id.mcx_footer_home_btn))
                .setImageResource(R.drawable.main_hui);
        ((ImageButton) chat_btn.findViewById(R.id.mcx_footer_chat_btn))
                .setImageResource(R.drawable.chat_hui);
        ((ImageButton) recommend_btn.findViewById(R.id.mcx_footer_tuijian_btn))
                .setImageResource(R.drawable.tuijian_hui);
        ((ImageButton) me_btn.findViewById(R.id.mcx_footer_me_btn))
                .setImageResource(R.drawable.me_hui);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (fra_main != null) {
            transaction.remove(fra_main);
            fra_main = null;
        }

        if (fra_chat != null) {
            transaction.remove(fra_chat);
            fra_chat = null;
        }

        if (fra_recommend != null) {
            transaction.remove(fra_recommend);
            fra_recommend = null;
        }

        if (fra_me_basic != null) {
            transaction.remove(fra_me_basic);
            fra_me_basic = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_MAIN_ME_TO_LOGIN && resultCode == LOGIN_TO_MAIN_ME) {
            setTabSelection(3);
        }
        if (resultCode == Constants.INFO_TO_MAIN) {
            Log.e("www", "INFO_TO_MAIN");
            setTabSelection(0);
        }
        if(resultCode==Constants.EXIT_TO_LOGIN){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
//        getMyCreateGroup();
//        parseData();
        JPushInterface.setAlias(VirtualSalesMainActivity.this, new Random().nextInt(), spu.getAccount());
    }

    // 微聊相关，虚拟4S用不上
//    void getMyCreateGroup() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                final PostParameter[] params = new PostParameter[1];
//                params[0] = new PostParameter("account", spu.getAccount());
//                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetMyCreateGroup, params, ConnectUtil.POST);
//                Log.e("reCode", "" + reCode);
//                Message msg = new Message();
//                msg.what = 0;
//                msg.obj = reCode;
//                handler.sendMessage(msg);
//            }
//        }.start();
//    }
//
//    void getMyJoinGroup() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                final PostParameter[] params = new PostParameter[1];
//                params[0] = new PostParameter("account", spu.getAccount());
//                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetMyJoinGroup, params, ConnectUtil.POST);
//                Log.e("reCode", "" + reCode);
//                Message msg = new Message();
//                msg.what = 1;
//                msg.obj = reCode;
//                handler.sendMessage(msg);
//            }
//        }.start();
//    }
//
//    void parseData() {
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case 0:
//                        try {
//                            String reCode = (String) msg.obj;
//                            if (reCode != null) {
//                                JSONObject json = new JSONObject(reCode);
//                                String status = json.getString("status");
//                                if ("false".equals(status)) {
//                                    Toast.makeText(VirtualSalesMainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//                                } else if ("true".equals(status)) {
//                                    JSONArray recommendJA = json.getJSONArray("group");
//                                    for (int i = 0; i < recommendJA.length(); i++) {
//                                        JSONObject temp = recommendJA.getJSONObject(i);
//                                        groupSet.add(temp.getString("group_id"));
//                                    }
//                                }
//                            }
//                            getMyJoinGroup();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case 1:
//                        try {
//                            String reCode = (String) msg.obj;
//                            if (reCode != null) {
//                                JSONObject json = new JSONObject(reCode);
//                                String status = json.getString("status");
//                                if ("false".equals(status)) {
//                                    Toast.makeText(VirtualSalesMainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//                                } else if ("true".equals(status)) {
//                                    JSONArray recommendJA = json.getJSONArray("group");
//                                    for (int i = 0; i < recommendJA.length(); i++) {
//                                        JSONObject temp = recommendJA.getJSONObject(i);
//                                        groupSet.add(temp.getString("group_id"));
//                                    }
//                                }
//                            }
//                            JPushInterface.setTags(VirtualSalesMainActivity.this, new Random().nextInt(), groupSet);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//            }
//        };
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(VirtualSalesMainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //for receive customer msg from jpush server
    private VirtualSalesMainActivity.MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.xd.aselab.chinabankmanager.MESSAGE_RECEIVED_ACTION";
    public static final String MESSAGE_UPDATE_MESSAGCHAT_ACTION = "com.example.jpushdemo.MESSAGE_UPDATE_MESSAGCHAT_ACTION";
    public static final String GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION = "com.example.jpushdemo.GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        filter.addAction(MESSAGE_UPDATE_MESSAGCHAT_ACTION);
        filter.addAction(GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    SharePreferenceUtil spu = new SharePreferenceUtil(VirtualSalesMainActivity.this, "user");
                    //spu.setExtras(extras);
                    Log.d("www", "MessageReceiver.extras-Main---" + extras.replace("\\", ""));
                    if (extras != null) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    // setCostomMsg(showMsg.toString());
                } else if (MESSAGE_UPDATE_MESSAGCHAT_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    SharePreferenceUtil spu = new SharePreferenceUtil(VirtualSalesMainActivity.this, "user");
                    spu.setChatExtra(extras);
                    Log.e("www", "单聊MessageReceiver.extras-Main---" + extras.replace("\\", ""));
                    if (extras != null) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                } else if (GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    SharePreferenceUtil spu = new SharePreferenceUtil(VirtualSalesMainActivity.this, "user");
                    //spu.setGroupChatExtra(extras);
                    Log.e("www", "群聊MessageReceiver.extras-Main---" + extras.replace("\\", ""));
                    if (extras != null) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                } else {
                    Log.d("www", "MessageReceiver-!!!!!!!!!!!!!");
                }
            } catch (Exception e) {
            }
        }
    }
}