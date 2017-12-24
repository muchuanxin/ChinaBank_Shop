package com.xd.aselab.chinabank_shop.activity.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.AccountTypeVo;
import com.xd.aselab.chinabank_shop.Vos.MyWorkersInfoVo;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.SecureQuestionActivity;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.Encode;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopRegisterActivity extends AppCompatActivity {

    private Button btn_back;
    private TextView txt_shopkeeper_acccount;
    private EditText et_shopkeeper_psw;
    private EditText et_shop_name;
    private TextView txt_manager_jobID;
    private EditText et_shopkeeper_tel;
    private EditText et_shop_addr;
    private TextView tv_type_select;
    private String typeSelected;
    private String province;
    private String city;
    private String area;
    private String street;
    private Spinner spinner_shop_type;
    private List<String> dataList;
    private ArrayAdapter<String> arr_adapter;
    private Button btnToLogin;
    private Button btnRegister;

    private String shopkeeper_acccount;
    private String password;
    private String shopName;
    private String managerJobID;
    private String shopkeeperTel;
    private String shopAddr;
    private String shopType;
    private Dialog mDialog=null;
    private AccountTypeVo vo;
    private SharePreferenceUtil sp;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissRequestDialog();
            String reCode="",message="";
            switch (msg.what){
                case 1:{
                    JSONObject result=(JSONObject)msg.obj;
                    try {
                        reCode=result.getString("status");
                        message=result.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(reCode.equals("true")){
                        ToastCustom.makeToastCenter(getApplicationContext(), "注册成功,等待审核");
                      //  sp=new SharePreferenceUtil(ShopRegisterActivity.this,"user");
                        sp.setAccount(shopkeeper_acccount);
                        sp.setPassword(password);
                        sp.setShopName(shopName);
                        sp.setShopMoblie(shopkeeperTel);
                        sp.setTelAccount(shopkeeperTel);
                       // sp.setShopProvince(shopAddr);
                        sp.setShopProvince(province);
                        sp.setShopCity(city);
                        sp.setShopCounty(area);
                        sp.setShopStreet(street);
                        Log.e("www","----"+sp.getShopProvince()+ sp.getShopCity()+ sp.getShopCounty()+ sp.getShopStreet());
//                        setResult(Activity.RESULT_OK);
//                        ShopRegisterActivity.this.finish();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), SecureQuestionActivity.class);
                        startActivity(intent);
                    }else if (reCode.equals("false")){
                        if(!message.equals("")){
                            ToastCustom.makeToastCenter(getApplicationContext(),message);
                        }else
                            ToastCustom.makeToastCenter(getApplicationContext(),"您已注册过，请直接登录");
                    }else {
                        ToastCustom.makeToastCenter(getApplicationContext(),"注册失败，请重新尝试");
                    }
                }
                break;

                case 0:{
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到服务器");
                    break;
                }

                case -1:{
                    ToastCustom.makeToastCenter(getApplicationContext(),"无法连接到网络");
                    break;
                }

                default:
                    break;

            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initViews();
        sp=new SharePreferenceUtil( this,"user");

        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为单次定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(false);
        // 设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (null != aMapLocation) {
                    //et_shop_addr.setText(aMapLocation.getAddress());
                    province = aMapLocation.getProvince();
                    city = aMapLocation.getCity();
                    area = aMapLocation.getDistrict();
                    street = aMapLocation.getStreet();
                    et_shop_addr.setText(aMapLocation.getStreet());
                }
            }
        });
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private void initViews(){

        btn_back=(Button)findViewById(R.id.s_back_to_login);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister=(Button)findViewById(R.id.btnShopRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!judge())
                    return;
                else
                    submit();
            }
        });

        vo=(AccountTypeVo)getIntent().getSerializableExtra("vo");
        shopkeeper_acccount=vo.getNew_account();
        managerJobID=vo.getAccount();
//           shopkeeper_acccount="11223344";
//           managerJobID="test";
/*********************/
        txt_shopkeeper_acccount=(TextView)findViewById(R.id.shopkeeperId);
        txt_shopkeeper_acccount.setText(shopkeeper_acccount);
        et_shopkeeper_psw=(EditText)findViewById(R.id.userpsw);
        et_shop_name=(EditText)findViewById(R.id.shopname);
        txt_manager_jobID=(TextView)findViewById(R.id.managerJobID);
        txt_manager_jobID.setText(managerJobID);
        et_shopkeeper_tel=(EditText)findViewById(R.id.shoptel);
        et_shop_addr=(EditText)findViewById(R.id.shopAddr);

        password=et_shopkeeper_psw.getText().toString().trim();
        shopName=et_shop_name.getText().toString().trim();
        shopkeeperTel=et_shopkeeper_tel.getText().toString().trim();
        shopAddr=et_shop_addr.getText().toString().trim();

            tv_type_select = (TextView) findViewById(R.id.shopTypeSelect);
            tv_type_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopRegisterActivity.this);
                    //  builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("选择店铺类型");
                    //    指定下拉列表的显示数据
                    final String[] types = {"餐饮", "美容", "服务", "酒店", "电商", "超市", "个人代理"};
                    //    设置一个下拉的列表选择项
                    builder.setItems(types, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            typeSelected = types[which];
                            tv_type_select.setText(typeSelected);
                            System.out.println("选择的店铺类型：" + typeSelected);
                        }
                    });
                    builder.show();
                }
            });
    }

    protected boolean judge(){

        password=et_shopkeeper_psw.getText().toString().trim();
        shopName=et_shop_name.getText().toString().trim();
        shopkeeperTel=et_shopkeeper_tel.getText().toString().trim();
        shopAddr=et_shop_addr.getText().toString().trim();
        shopType=tv_type_select.getText().toString().trim();

        if(password.equals("")||shopName.equals("")||shopkeeperTel.equals("")||shopAddr.equals("")){
            ToastCustom.makeToastCenter(ShopRegisterActivity.this,"请补全店铺信息");
            return false;
        }
        if(password.length()<6){
            ToastCustom.makeToastCenter(ShopRegisterActivity.this,"密码至少为六位");
            return false;
        }
        if(shopType.equals("")){
            ToastCustom.makeToastCenter(ShopRegisterActivity.this,"请选择店铺类型");
            return false;
        }

        if(!isMobileNO( shopkeeperTel)){
            ToastCustom.makeToastCenter(ShopRegisterActivity.this,"输入的手机号有误");
            return false;
        }

        return true;
    }

    protected void submit( ){

        showRequestDialog();
        new Thread(){
            @Override
            public void run() {
                Message message=new Message();
                if(ConnectUtil.isNetworkAvailable(getApplicationContext())){
                    PostParameter[] postParameters=new PostParameter[13];
                    postParameters[0]=new PostParameter("account",shopkeeper_acccount);
                    postParameters[1]=new PostParameter("password", Encode.getEncode("MD5", password));
                    postParameters[2]=new PostParameter("managerAccount",managerJobID);
                    Log.i("wyy","1:"+managerJobID);
                    postParameters[3]=new PostParameter("shopName",shopName);
                    postParameters[4]=new PostParameter("teleNumber",shopkeeperTel);
                    postParameters[5]=new PostParameter("province",province);
                    postParameters[6]=new PostParameter("city",city);
                    postParameters[7]=new PostParameter("county",area);
                    postParameters[8]=new PostParameter("street",shopAddr);
                    postParameters[9]=new PostParameter("longitude","");
                    postParameters[10]=new PostParameter("latitude","");
                    postParameters[11]=new PostParameter("industry",shopType);
                    postParameters[12]=new PostParameter("typeId","");

                    String jsonStr=ConnectUtil.httpRequest(ConnectUtil.REGISTER_SHOP,postParameters,ConnectUtil.POST);
                    if(jsonStr==null||jsonStr.equals("")){
                        Log.i("xxx", 1 + "");
                        message.what=0;
                        message.obj="fail";
                        handler.sendMessage(message);
                    }else {
                        try {
                            JSONObject result=new JSONObject(jsonStr);
                            message.what=1;
                            message.obj=result;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    message.what=-1;
                    message.obj="网络不可用";
                    handler.sendMessage(message);
                }
            }
        }.start();

    }

    //判断手机号是否合法
    public static boolean isMobileNO(String mobiles) {
       // Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7,8])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(ShopRegisterActivity.this, "请稍等...");
        mDialog.show();
    }

    public void dismissRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
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
