package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Handler;
import android.os.Message;


import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.MyMarkerView;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CardDiv_My_Performance extends Activity implements OnChartValueSelectedListener {
    private TextView success_recommend;
    private TextView worker_timeSelect;
    private TextView success_num;
    private TextView success_money;
    private TextView score_inall;
    private TextView name;
    private TextView score_exchanged;
    private TextView score_unchanged;
    private ImageView return_button;

    private boolean flag;
    private String beginTime;
    private String endTime;
    private SimpleDateFormat sdf;
    private SharePreferenceUtil sp;

    private int recommend_number = 0;
    private int success_number = 0;
    private int success_getmoney = 0;
    private int getscore = 0;
    private int exchanged_getscore = 0;
    private int unexchanged_getscore = 0;
    private String str;
    private LinearLayout click;
    private JSONArray array;
    private JSONObject myobj;

    private int number1, number2, number3, number4;
    private int money1, money2, money3, money4;
    //    private float[] numbers = new float[12];
//    private float[] money = new float[12];
    private float[] numbers = new float[12];
    private float[] money = new float[12];
    private BarChart mChart;
    private String jsonStr;
    private JSONObject obj2;
    private JSONObject obj;
    protected Typeface mTfLight;
    private ImageView no_data_img;
    private ImageView forward;
    private TextView gray_bar;
    private TextView no_data_txt;
    private LinearLayout timeSelect;
    private LinearLayout hide;
    private CustomPopupWindow mPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__performance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mChart = (BarChart) findViewById(R.id.chart1);
        sp = new SharePreferenceUtil(this, "user");
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        name= (TextView) findViewById(R.id.name);
        name.setText(sp.getWorkerName());
        gray_bar = (TextView) findViewById(R.id.act_my_perf_gray_bar);
        gray_bar.setText(Calendar.getInstance().get(Calendar.YEAR) + "年各月分期业务情况分析");
        success_recommend = (TextView) findViewById(R.id.success_recommend);
        success_num = (TextView) findViewById(R.id.success_num);
        click = (LinearLayout) findViewById(R.id.click);
        success_money = (TextView) findViewById(R.id.success_money);
        no_data_txt = (TextView) findViewById(R.id.act_kafenqi_my_perf_no_data_txt);
        score_unchanged = (TextView) findViewById(R.id.score_unchanged);
        no_data_img = (ImageView) findViewById(R.id.act_kafenqi_my_perf_no_data_img);
        score_inall = (TextView) findViewById(R.id.score_inall);
        score_exchanged = (TextView) findViewById(R.id.score_exchanged);
        return_button = (ImageView) findViewById(R.id.return_button);
        hide = (LinearLayout) findViewById(R.id.hide);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        worker_timeSelect = (TextView) findViewById(R.id.worker_timeSelect);
        forward = (ImageView) findViewById(R.id.forward);
        timeSelect = (LinearLayout) findViewById(R.id.timeSelect);
        mPop = new CustomPopupWindow(this);
        timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPop.showAtLocation(CardDiv_My_Performance.this.findViewById(R.id.recommend_detail), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                LinearLayout linear = (LinearLayout) findViewById(R.id.worker_dataSelect);
                mPop.showAsDropDown(linear);
                Log.d("Dorise", "123");
                mPop.setOnItemClickListener(new CustomPopupWindow.OnItemClickListener() {

                    public void setOnItemClick(View v) {
                        Log.d("Dorise", "456");
                        Calendar ca = Calendar.getInstance();
                        ca.setTime(new Date());
                        switch (v.getId()) {

                            case R.id.week:

                                worker_timeSelect.setText("最近一周");
                                ca.add(Calendar.DAY_OF_MONTH, -7);
                                setText("one_week");
                                mPop.dismiss();
                                break;
                            case R.id.month:
                                worker_timeSelect.setText("最近一个月");
                                ca.add(Calendar.MONTH, -1);
                                setText("one_month");
                                mPop.dismiss();
                                break;
                            case R.id.three_month:
                                worker_timeSelect.setText("最近三个月");
                                ca.add(Calendar.MONTH, -3);
                                setText("three_month");
                                mPop.dismiss();
                                break;
                            case R.id.year:
                                worker_timeSelect.setText("最近一年");
                                ca.add(Calendar.YEAR, -1);
                                setText("one_year");
                                mPop.dismiss();
                                break;
                            default:
                                mPop.dismiss();
                                break;
                        }
                        beginTime = sdf.format(ca.getTime());

                    }
                });
            }
        });








        /*worker_timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CardDiv_My_Performance.this);
                builder.setTitle("请选择时间范围");
                final String[] timeScope = {"最近一周", "最近一个月", "最近三个月", "最近一年"};
                builder.setItems(timeScope, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        timeSelect = timeScope[i];
                        worker_timeSelect.setText(timeSelect);
                       //得到一个Calendar的实例
                        ca.setTime(new Date());//设置时间为当前时间
                        endTime = sdf.format(ca.getTime());
                        System.out.println("0当前时间endTime：" + endTime);
                        if (timeSelect.equals("最近一周")) {
                            ca.add(Calendar.DAY_OF_MONTH, -7);
                            setText("one_week");
                        } else if (timeSelect.equals("最近一个月")) {
                            ca.add(Calendar.MONTH, -1);
                            setText("one_month");
                        } else if (timeSelect.equals("最近三个月")) {
                            ca.add(Calendar.MONTH, -3);
                            setText("three_month");
                        } else if (timeSelect.equals("最近一年")) {
                            ca.add(Calendar.YEAR, -1);
                            setText("one_year");
                        }
                        beginTime = sdf.format(ca.getTime());

                        //在一个新线程里从后台读取业绩   后台返回全部时段的业绩  根据用户的选择进行显示

                    }

                });
                builder.show();
            }
        });*/


        new Thread() {

            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(CardDiv_My_Performance.this)) {
                    super.run();
                    PostParameter[] param = new PostParameter[1];
                    param[0] = new PostParameter("account", sp.getAccount());
                    String jsonStr = ConnectUtil.httpRequest(ConnectUtil.InstallmentWorkerMyPerformance, param, "POST");

                    if (("").equals(jsonStr) || jsonStr == null) {
                        msg.what = 2;
                        msg.obj = "连接服务器失败";

                    } else {
                        msg.what = 0;
                        msg.obj = jsonStr;
                    }
                } else {
                    //网络连接不可用
                    msg.what = 1;
                    msg.obj = "网络连接错误，请检查您的网络连接";
                }
                handler.sendMessage(msg);
            }
        }.start();
        configChart();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String str = (String) msg.obj;
                try {
                    obj = new JSONObject(str);
                    if (obj.getString("status").equals("true")) {
                        setText("one_week");

                        Log.d("Dadsada", (String) msg.obj);
                        flag = false;
                        JSONObject myobj = obj.getJSONObject("this_year");
                        for (int i = 0; i < 12; i++) {
                            String k = (i + 1) + "";
                            numbers[i] = (float) myobj.getJSONObject("" + (i + 1)).getDouble("number");
                            money[i] = (float) myobj.getJSONObject("" + (i + 1)).getDouble("money");

                            if (numbers[i] > 0) {
                                flag = true;
                            }
                            Log.d("aaaaa", numbers[i] + "");
                            Log.d("aaaaa", money[i] + "");

                        }
                        //          obj2 = obj.getJSONObject("this_year");


                        if (!flag) {
                          //  gray_bar.setVisibility(View.GONE);
                            //chart.setVisibility(View.GONE);

                            mChart.setVisibility(View.GONE);

                            no_data_img.setVisibility(View.VISIBLE);
                            no_data_txt.setVisibility(View.VISIBLE);
                            Toast.makeText(CardDiv_My_Performance.this, "还没有业绩信息~", Toast.LENGTH_SHORT).show();
                        } else {
                            initChartDatas();
                            no_data_img.setVisibility(View.GONE);
                            no_data_txt.setVisibility(View.GONE);
                            mChart.setVisibility(View.VISIBLE);
                         //   gray_bar.setVisibility(View.VISIBLE);

                        }

                    } else {
                        Toast.makeText(CardDiv_My_Performance.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(CardDiv_My_Performance.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {


    }

    private void setText(final String select) {
        try {
            if (ConnectUtil.isNetworkAvailable(CardDiv_My_Performance.this)) {

                myobj = obj.getJSONObject(select);

                success_recommend.setText(myobj.getString("recommend_num") + "笔");
                if (!myobj.getString("recommend_num").equals("0")) {
                    click.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CardDiv_My_Performance.this, CardDiv_My_Recommend_List.class);
                            intent.putExtra("scope", select);
                            startActivity(intent);
                        }
                    });
                    forward.setVisibility(View.VISIBLE);
                } else {
                    click.setOnClickListener(null);
                    forward.setVisibility(View.INVISIBLE);
                }
                Log.d("Listener显示", myobj.getString("recommend_num"));
                success_num.setText(myobj.getString("success_num") + "笔");
                success_money.setText(myobj.getString("success_money") + "万");
                score_inall.setText(myobj.getString("score"));
                score_exchanged.setText(myobj.getString("exchange_score"));
                score_unchanged.setText(myobj.getString("not_exchange_score"));
                Log.d("积分数", myobj.getString("score"));
                Log.d("累计兑换积分数", myobj.getString("exchange_score"));
                Log.d("未对换积分数", myobj.getString("not_exchange_score"));
            } else {
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("显示错误了", (String) success_num.getText());
        }


    }

    private void initChartDatas() {
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
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 2:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 3:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 4:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 5:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 6:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 7:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 8:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 9:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 10:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                case 11:
                    yVals1.add(new BarEntry(i, money[i - 1]));
                    yVals2.add(new BarEntry(i, numbers[i - 1]));
                    break;
                default:
                    yVals1.add(new BarEntry(i, (float) 0.00));
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
            set1 = new BarDataSet(yVals1, "成功总金额(元)");
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









/*

    timeSelect = timeScope[i];
    worker_timeSelect.setText(timeSelect);
    //得到一个Calendar的实例
    ca.setTime(new Date());//设置时间为当前时间
    endTime = sdf.format(ca.getTime());
    System.out.println("0当前时间endTime：" + endTime);
    if (timeSelect.equals("最近一周")) {
        ca.add(Calendar.DAY_OF_MONTH, -7);
    } else if (timeSelect.equals("最近一个月")) {
        ca.add(Calendar.MONTH, -1);
        setText("one_month");
    } else if (timeSelect.equals("最近三个月")) {
        ca.add(Calendar.MONTH, -3);
        setText("three_month");
    } else if (timeSelect.equals("最近一年")) {
        ca.add(Calendar.YEAR, -1);
        setText("one_year");
    }
    beginTime = sdf.format(ca.getTime());



*/


}