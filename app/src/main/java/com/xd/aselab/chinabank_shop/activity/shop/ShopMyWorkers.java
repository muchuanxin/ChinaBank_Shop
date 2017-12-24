package com.xd.aselab.chinabank_shop.activity.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.MyWorkersInfoVo;
import com.xd.aselab.chinabank_shop.activity.shop.worksPerformanceList.WorkersPerAdapter;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
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

public class ShopMyWorkers extends AppCompatActivity {
    private RelativeLayout btnBack;
    private Spinner spinner;
    private TextView tv_time_select;
    private TextView tv_sucess;
    private TextView tv_sum;
    private ListView listView;
    private Dialog mDialog=null;
    private SharePreferenceUtil sp;
    private ArrayList<MyWorkersInfoVo> datas;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private String selectedTime;
    private String beginTime;
    private String endTime;
    private int success_sum;
    private int saomiao_sum;
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            dismissRequestDialog();
            if(msg.what==0){
                ArrayList<MyWorkersInfoVo> items=(ArrayList<MyWorkersInfoVo>)msg.obj;
                //updateUI(items);
                for(MyWorkersInfoVo vo:items)
                    datas.add(vo);
                if(datas.size()==0){
                    ToastCustom.makeToastCenter(getApplicationContext(),"当前没有业绩数据");
                }
                listView.setAdapter(new WorkersPerAdapter(datas, ShopMyWorkers.this));
                tv_sucess.setText("该时间段内共有" + success_sum + "人成功办卡");
                tv_sum.setText("该时间段内办卡扫描量共为"+saomiao_sum+"次");

            }
            else if (msg.what==1){
                ToastCustom.makeToastCenter(getApplicationContext(),(String)msg.obj);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_my_workers);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(this, "user");
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        initView();

        showRequestDialog();
        new Thread() {
            @Override
            public void run() {
                datas = new ArrayList<MyWorkersInfoVo>();
                success_sum = 0;
                saomiao_sum = 0;
                PostParameter[] postParameters = new PostParameter[4];
                calendar.setTime(new Date());
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                postParameters[0] = new PostParameter("account", sp.getAccount());
                postParameters[1] = new PostParameter("begin", sdf.format(calendar.getTime()));
                postParameters[2] = new PostParameter("end",  sdf.format(new Date()));
                postParameters[3] = new PostParameter("cookie", sp.getCookie());
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_MY_WORKER, postParameters, "POST");
                Message msg = handler.obtainMessage();
                if (jsonStr != null && !"".equals(jsonStr)) {
                    try {
                        JSONObject json = new JSONObject(jsonStr);
                        String status = json.getString("status");
                        if (status.equals("true")) {
                            JSONArray jsons = json.getJSONArray("list");

                            ArrayList<MyWorkersInfoVo> temp = new ArrayList<MyWorkersInfoVo>();

                            for (int i = 0; i < jsons.length(); i++) {
                                JSONObject elem = (JSONObject) jsons.get(i);
                                MyWorkersInfoVo vo = new MyWorkersInfoVo();
                                vo.setWorkerName(elem.getString("name"));
                                vo.setCardsNumber(elem.getString("success_sum"));
                                vo.setTel(elem.getString("teleNumber"));
                                success_sum += elem.getInt("success_sum");
                                saomiao_sum += elem.getInt("sum");
                                temp.add(vo);
                            }

                            msg.what = 0;
                            msg.obj = temp;

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        msg.what = 1;
                        msg.obj = "出现错误";
                    }
                } else {
                    msg.what = 1;
                    msg.obj = "无法连接到服务器";
                }
                handler.sendMessage(msg);
            }
        }.start();


    }

    private void initView(){

        tv_sucess=(TextView)findViewById(R.id.my_workers_total_cards);
        tv_sum=(TextView)findViewById(R.id.my_workers_total_scan_number);
        btnBack = (RelativeLayout) findViewById(R.id.shopkeeper_performance_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView=(ListView)findViewById(R.id.my_workers_ls_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
              //  Log.e("lskjdklfji", datas.get(position).getTel());
               // startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + datas.get(position).getTel())));
                final AlertDialog.Builder builder=new AlertDialog.Builder(ShopMyWorkers.this);
                builder.setTitle(datas.get(position).getTel());
                builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActivityCompat.checkSelfPermission(ShopMyWorkers.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ShopMyWorkers.this, new String[]{Manifest.permission.CALL_PHONE},
                                    Constants.ActivityCompatRequestPermissionsCode);
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + datas.get(position).getTel())));

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
        });

        tv_time_select=(TextView)findViewById(R.id.shop_time_select);
        tv_time_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopMyWorkers.this);
                //  builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("选择时间范围");
                //    指定下拉列表的显示数据
                final String[] timeScope = {"最近一周", "最近一个月", "最近三个月", "最近一年", "从加盟以来"};
                //    设置一个下拉的列表选择项
                builder.setItems(timeScope, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedTime = timeScope[which];
                        tv_time_select.setText(selectedTime);
                        System.out.println("选择的时间段：" + selectedTime);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                        ca.setTime(new Date());//设置时间为当前时间
                        endTime = sdf.format(ca.getTime());
                        System.out.println("0当前时间endTime：" + endTime);
                        if (selectedTime.equals("最近一周")) {
                            ca.add(Calendar.DAY_OF_MONTH, -7);
                        } else if (selectedTime.equals("最近一个月")) {
                            ca.add(Calendar.MONTH, -1);
                        } else if (selectedTime.equals("最近三个月")) {
                            ca.add(Calendar.MONTH, -3);
                        } else if (selectedTime.equals("最近一年")) {
                            ca.add(Calendar.YEAR, -1);
                        } else {
                            ca.set(2000, 01, 01);
                        }

                        beginTime = sdf.format(ca.getTime());
                        System.out.println("0开始时间beginTime:" + beginTime);
                        showRequestDialog();
                        new Thread() {
                            @Override
                            public void run() {
                                datas = new ArrayList<MyWorkersInfoVo>();
                                success_sum = 0;
                                saomiao_sum = 0;
                                PostParameter[] postParameters = new PostParameter[4];
                                postParameters[0] = new PostParameter("account", sp.getAccount());
                                postParameters[1] = new PostParameter("begin", beginTime);
                                postParameters[2] = new PostParameter("end", endTime);
                                postParameters[3] = new PostParameter("cookie", sp.getCookie());
                                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_MY_WORKER, postParameters, "POST");
                                Message msg = handler.obtainMessage();
                                if (jsonStr != null && !"".equals(jsonStr)) {
                                    try {
                                        JSONObject json = new JSONObject(jsonStr);
                                        String status = json.getString("status");
                                        if (status.equals("true")) {
                                            JSONArray jsons = json.getJSONArray("list");

                                            ArrayList<MyWorkersInfoVo> temp = new ArrayList<MyWorkersInfoVo>();

                                            for (int i = 0; i < jsons.length(); i++) {
                                                JSONObject elem = (JSONObject) jsons.get(i);
                                                MyWorkersInfoVo vo = new MyWorkersInfoVo();
                                                vo.setWorkerName(elem.getString("name"));
                                                vo.setCardsNumber(elem.getString("success_sum"));
                                                vo.setTel(elem.getString("teleNumber"));
                                                success_sum += elem.getInt("success_sum");
                                                saomiao_sum += elem.getInt("sum");
                                                temp.add(vo);
                                            }

                                            msg.what = 0;
                                            msg.obj = temp;

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        msg.what = 1;
                                        msg.obj = "出现错误";
                                    }
                                } else {
                                    msg.what = 1;
                                    msg.obj = "无法连接到服务器";
                                }
                                handler.sendMessage(msg);
                            }
                        }.start();

                    }
                });
                builder.show();

            }
        });
    }


//        spinner = (Spinner) findViewById(R.id.shop_timeHorizonSpinner);
//        final String[] selectTimeScope = {"最近一周", "最近一个月", "最近三个月", "最近一年", "从加盟以来"};
//        SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, selectTimeScope);
//        spinner.setAdapter(adapter);
//        //设置spinner事件监听
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                //  selectedTime=selectTimeScope[position];
//                selectedTime = (String) spinner.getSelectedItem();
//                System.out.println("选择的时间段：" + selectedTime);
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
//                ca.setTime(new Date());//设置时间为当前时间
//                endTime = sdf.format(ca.getTime());
//                System.out.println("0当前时间endTime：" + endTime);
//                if (selectedTime.equals("最近一周")) {
//                    ca.add(Calendar.DAY_OF_MONTH, -7);
//                } else if (selectedTime.equals("最近一个月")) {
//                    ca.add(Calendar.MONTH, -1);
//                } else if (selectedTime.equals("最近三个月")) {
//                    ca.add(Calendar.MONTH, -3);
//                } else if (selectedTime.equals("最近一年")) {
//                    ca.add(Calendar.YEAR, -1);
//                } else {
//                    ca.set(2000, 01, 01);
//                }
//                beginTime = sdf.format(ca.getTime());
//                System.out.println("0开始时间beginTime:" + beginTime);
//                showRequestDialog();
//                new Thread() {
//                    @Override
//                    public void run() {
//                        datas = new ArrayList<MyWorkersInfoVo>();
//                        success_sum = 0;
//                        saomiao_sum = 0;
//                        PostParameter[] postParameters = new PostParameter[4];
//                        postParameters[0] = new PostParameter("account", sp.getAccount());
//                        postParameters[1] = new PostParameter("begin", beginTime);
//                        postParameters[2] = new PostParameter("end", endTime);
//                        postParameters[3] = new PostParameter("cookie", sp.getCookie());
//                        String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_MY_WORKER, postParameters, "POST");
//                        Message msg = handler.obtainMessage();
//                        if (jsonStr != null && !"".equals(jsonStr)) {
//                            try {
//                                JSONObject json = new JSONObject(jsonStr);
//                                String status = json.getString("status");
//                                if (status.equals("true")) {
//                                    JSONArray jsons = json.getJSONArray("list");
//
//                                    ArrayList<MyWorkersInfoVo> temp = new ArrayList<MyWorkersInfoVo>();
//
//                                    for (int i = 0; i < jsons.length(); i++) {
//                                        JSONObject elem = (JSONObject) jsons.get(i);
//                                        MyWorkersInfoVo vo = new MyWorkersInfoVo();
//                                        vo.setWorkerName(elem.getString("name"));
//                                        vo.setCardsNumber(elem.getString("success_sum"));
//                                        vo.setTel(elem.getString("teleNumber"));
//                                        success_sum += elem.getInt("success_sum");
//                                        saomiao_sum += elem.getInt("sum");
//                                        temp.add(vo);
//                                    }
//
//                                    msg.what = 0;
//                                    msg.obj = temp;
//
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                msg.what = 1;
//                                msg.obj = "出现错误";
//                            }
//                        } else {
//                            msg.what = 1;
//                            msg.obj = "无法连接到服务器";
//                        }
//                        handler.sendMessage(msg);r
//                    }
//                }.start();


//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//    }
//


//    private void initData(){
//
//        sp=new SharePreferenceUtil(this,"user");
//       // getWorkersList();
//
//    }



    public void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(this, "请稍等...");
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