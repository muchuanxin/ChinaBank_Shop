package com.xd.aselab.chinabank_shop.activity.worker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.MyWorkersInfoVo;
import com.xd.aselab.chinabank_shop.activity.shop.worksPerformanceList.WorkersPerAdapter;
import com.xd.aselab.chinabank_shop.fragment.HistogramView;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.SpinnerAdapter;
import com.xd.aselab.chinabank_shop.util.ToastCustom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WorkerMyPerformance extends AppCompatActivity {

    private RelativeLayout btnBack;
    private Spinner spinner;
    private TextView tv_time_select;
    private TextView tv_success_count;
    private TextView tv_saomiao_count;
    private String selectedTime;
    private String beginTime;
    private String endTime;
    private int success_sum;
    private int saomiao_sum;
    private Calendar calendar;
    private SimpleDateFormat sdf,sdfy;
    private TextView gray_bar;
    private Dialog mDialog=null;
    private SharePreferenceUtil sp;
    private RelativeLayout chart;
    private HistogramView histogramView;
    private String[] names=new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
   // private float[] numbers=new float[]{120,2,3,4,5,6,7,8,9,10,11,120};
    private float[] numbers;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissRequestDialog();
            if(msg.what==0){
               // numbers=(float[])msg.obj;
                Log.i("www","success_sum"+success_sum);
                tv_success_count.setText("共成功办卡" + success_sum + "张");
                tv_saomiao_count.setText("办卡扫描量共为" + saomiao_sum + "次");
                histogramView=new HistogramView(WorkerMyPerformance.this,names,numbers);
                chart.addView(histogramView);
                gray_bar.setVisibility(View.VISIBLE);

            }
            else if (msg.what==1){
                ToastCustom.makeToastCenter(getApplicationContext(), (String) msg.obj);
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_my_performance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        sp = new SharePreferenceUtil(this, "user");
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdfy = new SimpleDateFormat("yyyy");
        calendar = Calendar.getInstance();

        initViews();
        initDatas();

        /**************/
        showRequestDialog();
        new Thread(){
            @Override
            public void run() {
                super.run();
                success_sum = 0;
                saomiao_sum = 0;
                numbers=new float[]{0,0,0,0,0,0,0,0,0,0,0,0};
                PostParameter[] postParameters = new PostParameter[5];
                calendar.setTime(new Date());
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
                postParameters[0] = new PostParameter("account", sp.getAccount());
                postParameters[1] = new PostParameter("begin", sdf.format(calendar.getTime()));
                postParameters[2] = new PostParameter("end", sdf.format(new Date()));
                postParameters[3] = new PostParameter("type", "worker");
                postParameters[4] = new PostParameter("cookie", sp.getCookie());
                String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_CARD_STATISTICS, postParameters, "POST");
                Message msg = handler.obtainMessage();
                if (jsonStr != null && !"".equals(jsonStr)) {
                    try {
                        JSONObject json = new JSONObject(jsonStr);
                        String status = json.getString("status");
                        success_sum=json.getInt("success_sum");
                        saomiao_sum=json.getInt("sum");
                        numbers=new float[]{0,0,0,0,0,0,0,0,0,0,0,0};
                        if (status.equals("true")) {
                            String yearResult=json.getString("this_year");
                            JSONObject jsonYearResult=new JSONObject(yearResult);
                            numbers[0]=jsonYearResult.getInt("1月");
                            numbers[1]=jsonYearResult.getInt("2月");
                            numbers[2]=jsonYearResult.getInt("3月");
                            numbers[3]=jsonYearResult.getInt("4月");
                            numbers[4]=jsonYearResult.getInt("5月");
                            numbers[5]=jsonYearResult.getInt("6月");
                            numbers[6]=jsonYearResult.getInt("7月");
                            numbers[7]=jsonYearResult.getInt("8月");
                            numbers[8]=jsonYearResult.getInt("9月");
                            numbers[9]=jsonYearResult.getInt("10月");
                            numbers[10]=jsonYearResult.getInt("11月");
                            numbers[11]=jsonYearResult.getInt("12月");

                            msg.what = 0;
                            //msg.obj=numbers;

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

        /*************/

    }


    private void initViews(){
      /*  spinner = (Spinner) findViewById(R.id.worker_timeHorizonSpinner);
        String[] timeSelected = {"最近一周", "最近一个月", "最近三个月", "最近一年", "从加盟以来"};
        SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, timeSelected);
        spinner.setAdapter(adapter);
        */

        tv_time_select=(TextView)findViewById(R.id.worker_timeSelect);
        tv_success_count=(TextView)findViewById(R.id.worker_my_success_total_cards);
        tv_saomiao_count=(TextView)findViewById(R.id.worker_my_total_scan_number);
        gray_bar = (TextView) findViewById(R.id.act_my_perf_gray_bar);
        btnBack=(RelativeLayout)findViewById(R.id.worker_performance_back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chart=(RelativeLayout)findViewById(R.id.worker_my_performance_chart);
//        histogramView=new HistogramView(WorkerMyPerformance.this,names,numbers);
//        chart.addView(histogramView);
    }

 private void initDatas() {
     //sp = new SharePreferenceUtil(this, "user");
     tv_time_select.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             AlertDialog.Builder builder = new AlertDialog.Builder(WorkerMyPerformance.this);
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
                     //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                     Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                     ca.setTime(new Date());//设置时间为当前时间
                     endTime = sdf.format(ca.getTime());
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
                             //datas = new ArrayList<MyWorkersInfoVo>();
                             success_sum = 0;
                             saomiao_sum = 0;
                             numbers = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                             PostParameter[] postParameters = new PostParameter[5];
                             postParameters[0] = new PostParameter("account", sp.getAccount());
                             postParameters[1] = new PostParameter("begin", beginTime);
                             postParameters[2] = new PostParameter("end", endTime);
                             postParameters[3] = new PostParameter("type", "worker");
                             postParameters[4] = new PostParameter("cookie", sp.getCookie());
                             String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GET_CARD_STATISTICS, postParameters, "POST");
                             Message msg = handler.obtainMessage();
                             if (jsonStr != null && !"".equals(jsonStr)) {
                                 try {
                                     JSONObject json = new JSONObject(jsonStr);
                                     String status = json.getString("status");
                                     success_sum = json.getInt("success_sum");
                                     Log.i("www","2success_sum"+success_sum);
                                     saomiao_sum = json.getInt("sum");
                                     if (status.equals("true")) {
                                         String yearResult = json.getString("this_year");
                                         JSONObject jsonYearResult = new JSONObject(yearResult);
                                         numbers[0] = jsonYearResult.getInt("1月");
                                         numbers[1] = jsonYearResult.getInt("2月");
                                         numbers[2] = jsonYearResult.getInt("3月");
                                         numbers[3] = jsonYearResult.getInt("4月");
                                         numbers[4] = jsonYearResult.getInt("5月");
                                         numbers[5] = jsonYearResult.getInt("6月");
                                         numbers[6] = jsonYearResult.getInt("7月");
                                         numbers[7] = jsonYearResult.getInt("8月");
                                         numbers[8] = jsonYearResult.getInt("9月");
                                         numbers[9] = jsonYearResult.getInt("10月");
                                         numbers[10] = jsonYearResult.getInt("11月");
                                         numbers[11] = jsonYearResult.getInt("12月");

                                         msg.what = 0;
                                         //msg.obj=numbers;

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

     gray_bar.setText(sdfy.format(new Date())+"年各月办卡成功情况分析");

 }

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
