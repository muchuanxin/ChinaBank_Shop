package com.xd.aselab.chinabank_shop.activity.VirtualSales;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CustomPopupWindow;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.MyMarkerView;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VirtualPerformanceActivity extends AppCompatActivity  implements OnChartValueSelectedListener{

    private TextView virtual_timeSelect;
    private TextView recommend_num;
    private TextView success_num;
    private TextView success_money;
    private TextView score;
    private TextView exchanged_score;
    private TextView not_exchanged_score;
    private TextView name;
    private ImageView return_button;

    private BarChart mChart;

    private SharePreferenceUtil sp;
    private SimpleDateFormat sdf;
    private TextView gray_bar;
    private LinearLayout click;
    private JSONObject obj;
    private JSONObject myObj;

    private ImageView no_data_img;
    private ImageView forward;
    private TextView no_data_txt;
    private LinearLayout timeSelect;
    private CustomPopupWindow mPop;

    private float recommend_number[] = new float[12];
    private float success_number[] = new float[12];
    protected Typeface mTfLight;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_performance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mChart = (BarChart) findViewById(R.id.chart1);
        sp = new SharePreferenceUtil(this,"user");
        sdf = new SimpleDateFormat("yyyy-mm-dd");
        name = (TextView) findViewById(R.id.name);
        name.setText("销售" + sp.getWorkerName());
        gray_bar = (TextView) findViewById(R.id.act_my_perf_gray_bar);
        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR) + "年各月分期业务情况分析");
        recommend_num = (TextView) findViewById(R.id.recommend_num);
        success_num = (TextView) findViewById(R.id.success_num);
        success_money = (TextView) findViewById(R.id.success_money);
        // 查看推荐业务详情列表
        click = (LinearLayout) findViewById(R.id.click);
        no_data_txt = (TextView) findViewById(R.id.act_kafenqi_my_perf_no_data_txt);
        not_exchanged_score = (TextView) findViewById(R.id.score_unchanged);
        no_data_img = (ImageView) findViewById(R.id.act_kafenqi_my_perf_no_data_img);
        score = (TextView) findViewById(R.id.score_inall);
        exchanged_score = (TextView) findViewById(R.id.score_exchanged);
        return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        virtual_timeSelect = (TextView) findViewById(R.id.virtual_timeSelect);
        forward = (ImageView) findViewById(R.id.forward);
        timeSelect = (LinearLayout) findViewById(R.id.timeSelect);
        mPop = new CustomPopupWindow(this);

        timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linear = (LinearLayout) findViewById(R.id.virtual_dataSelect);
                mPop.showAsDropDown(linear);

                mPop.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {
                    @Override
                    public void setOnItemClick(View v) {
                        Calendar ca = Calendar.getInstance();
                        ca.setTime(new Date());
                        switch (v.getId()){
                            case R.id.week:
                                virtual_timeSelect.setText("最近一周");
                                ca.add(Calendar.DAY_OF_MONTH, -7);
                                setText("one_week");
                                mPop.dismiss();
                                break;

                            case R.id.month:
                                virtual_timeSelect.setText("最近一个月");
                                ca.add(Calendar.MONTH, -1);
                                setText("one_month");
                                mPop.dismiss();
                                break;

                            case R.id.three_month:
                                virtual_timeSelect.setText("最近三个月");
                                ca.add(Calendar.MONTH, -3);
                                setText("three_month");
                                mPop.dismiss();
                                break;

                            case R.id.year:
                                virtual_timeSelect.setText("最近一年");
                                ca.add(Calendar.YEAR, -1);
                                setText("one_year");
                                mPop.dismiss();
                                break;

                            default:
                                mPop.dismiss();
                                break;
                        }
                    }
                });
            }
        });

        new Thread(){
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if(ConnectUtil.isNetworkAvailable(VirtualPerformanceActivity.this))
                {
                    super.run();
                    //要向后台传送的参数“account”
                    PostParameter[] param = new PostParameter[1];
                    param[0] = new PostParameter("account", sp.getAccount());
                    //获取向后台发送消息后返回的数据集！！！
                    String jsonStr = ConnectUtil.httpRequest(ConnectUtil.VirtualInstallmentWorkerMyPerformance, param, "POST");

                    if(("").equals(jsonStr) || jsonStr == null)
                    {
                        msg.what = 2;
                        msg.obj = "网络连接失败";
                    } else {
                        msg.what = 0;
                        msg.obj = jsonStr;
                    }
                } else {
                    msg.what = 1;
                    msg.obj = "网络连接错误，请检查您的网络连接";
                }
                handler.sendMessage(msg);

            }
        }.start();
        configChart();
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                String str = (String)msg.obj;
                try{
                    obj = new JSONObject(str);
                    if(obj.getString("status").equals("true"))
                    {
                        setText("one_week");
                        flag = false;
                        //该jsonObject用于呈现图表中各月的业务情况
                        JSONObject myobj = obj.getJSONObject("this_year");
//                        new AlertDialog.Builder(VirtualPerformanceActivity.this)
//                                .setTitle("dayu0")
//                                .setMessage(myobj.toString())
//                                .setIcon(R.mipmap.ic_launcher)
//                                .create().show();
                        for(int i = 0; i < 12; i++)
                        {
                            recommend_number[i] = (float)myobj.getJSONObject("" + (i+1)).getDouble("recommend_num");
                            success_number[i] = (float)myobj.getJSONObject("" + (i+1)).getDouble("success_num");

                            if(recommend_number[i] > 0)
                            {
                                flag = true;
                            }
                        }
                        if(!flag)
                        {
                            mChart.setVisibility(View.GONE);
                            no_data_img.setVisibility(View.VISIBLE);
                            no_data_txt.setVisibility(View.VISIBLE);
                            Toast.makeText(VirtualPerformanceActivity.this, "还没有业绩信息", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VirtualPerformanceActivity.this, "业绩信息已加载", Toast.LENGTH_SHORT).show();
                            initChartDatas();
                            mChart.setVisibility(View.VISIBLE);
                            no_data_img.setVisibility(View.GONE);
                            no_data_txt.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(VirtualPerformanceActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(VirtualPerformanceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //从接口获取各变量的数据，并显示在textview上
    private void setText(final String select){
        try {
            if(ConnectUtil.isNetworkAvailable(VirtualPerformanceActivity.this))
            {
                myObj = obj.getJSONObject(select);
                recommend_num.setText(myObj.getString("recommend_num") + "笔");
                if(!myObj.getString("recommend_num").equals("0"))
                {
                    click.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(VirtualPerformanceActivity.this, VirtualPerformanceDetailActivity.class);
                            intent.putExtra("scope", select);
                            startActivity(intent);
                        }
                    });
                    forward.setVisibility(View.VISIBLE);
                } else{
                    click.setOnClickListener(null);
                    forward.setVisibility(View.INVISIBLE);
                }
                success_num.setText(myObj.getString("success_num") + "笔");
                success_money.setText(myObj.getInt("success_money")/10000 + "万");
                score.setText(myObj.getString("score"));
                exchanged_score.setText(myObj.getString("exchange_score"));
                not_exchanged_score.setText(myObj.getString("not_exchange_score"));
                Log.d("积分数", myObj.getString("score"));
                Log.d("累计兑换积分数", myObj.getString("exchange_score"));
                Log.d("未兑换积分数", myObj.getString("not_exchange_score"));
            } else {
                Toast.makeText(this,"网络连接失败",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initChartDatas() {
        new AlertDialog.Builder(this)
                .setTitle("初始化表格")
                .setMessage(sp.getAccount())
                .setIcon(R.mipmap.ic_launcher)
                .create().show();
        float groupSpace = 0.14f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.4f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

        int startYear = 1;
        int endYear = startYear + 12;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();

        for (int i = startYear; i < endYear; i++) {
            switch (i) {
                case 1:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 2:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 3:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 4:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 5:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 6:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 7:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 8:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 9:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 10:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                case 11:
                    yVals1.add(new BarEntry(i, recommend_number[i - 1]));
                    yVals2.add(new BarEntry(i, success_number[i - 1]));
                    break;
                default:
                    yVals1.add(new BarEntry(i, (float) 0.0000));
                    yVals2.add(new BarEntry(i, 0));
                    break;
            }
//            yVals1.add(new BarEntry(i, 10));
//            yVals2.add(new BarEntry(i, 2));
//            yVals3.add(new BarEntry(i, (float) (Math.random() * 1)));
//            yVals4.add(new BarEntry(i, (float) (Math.random() * 1)));
        }

        BarDataSet set1, set2;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);

            set1.setValues(yVals1);
            set2.setValues(yVals2);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, "成功总金额(万元)");
            set1.setColor(Color.rgb(255, 133, 133));
            set2 = new BarDataSet(yVals2, "成功业务量(笔)");
            set2.setColor(Color.rgb(98, 188, 255));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(mTfLight);

            mChart.setData(data);
        }

        // specify the width each bar should have
        mChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        mChart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        mChart.getXAxis().setAxisMaximum(startYear + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * 12);
        mChart.groupBars(startYear, groupSpace, barSpace);
        mChart.invalidate();

    }

    private void configChart() {
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //return String.valueOf((int) value);
                if (value == 0) {
                    return String.valueOf((int) value);
                } else
                    return (int) value + "月";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.getAxisRight().setEnabled(false);

        mChart.setOnChartValueSelectedListener(this);
        mChart.getDescription().setEnabled(false);

        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }}
