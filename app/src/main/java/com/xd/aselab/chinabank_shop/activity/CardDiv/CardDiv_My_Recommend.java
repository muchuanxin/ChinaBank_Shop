package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.xd.aselab.chinabank_shop.activity.shop.worksPerformanceList.WorkersPerAdapter;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.isMobileNo;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class CardDiv_My_Recommend extends AppCompatActivity {

    private EditText name;
    private EditText Tel;
    private float sum;
    private float[] score;
    private int[] flag;
    private boolean tag;
    private String name_text;
    private String Tel_text;
    private ListView listview;
    private Intent final_intent;
    private AlertDialog.Builder builder;
    private MyAdapter adapter;
    private int col_num;
    private String[] str;
    private List list_item;
    private List list_goal;
    private TextView item_select;
    private String[] item_string;
    private Float[] item_goal;
    private String[] titleString;
    private boolean isclear;
    private String evaluation_detail;
    private Map map;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    String jsonStr = (String) msg.obj;
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        if (obj.getString("status").equals("true")) {

                            JSONArray list_array = new JSONArray(obj.getString("credit_evaluation_list"));


                            //col_num记录的是授信条目个数
                            col_num = list_array.length();

                            //titleString 记录选项  list_item 记录全部项可选条目  list_goal记录全部条目分数

                            titleString = new String[col_num];
                            list_item = new ArrayList();
                            list_goal = new ArrayList();
                            for (int kk = 0; kk < col_num; kk++) {
                                JSONObject mid_object = (JSONObject) list_array.get(kk);
//                            JSONObject mid_object = new JSONObject(temp);

                                String title = mid_object.getString("title");

                                titleString[kk] = title;
                                String content = mid_object.getString("content");

                                JSONArray final_jsonarray = new JSONArray(content);

                                item_string = new String[final_jsonarray.length()];
                                item_goal = new Float[final_jsonarray.length()];


                                for (int k = 0; k < final_jsonarray.length(); k++) {
                                    JSONObject final_obj = (JSONObject) final_jsonarray.get(k);
                                    item_string[k] = final_obj.getString("item");
                                    item_goal[k] = Float.valueOf(final_obj.getString("goal"));
                                }
                                //list_item 记录所有选项里

                                list_item.add(item_string);
                                list_goal.add(item_goal);
                            }
                            adapter = new MyAdapter(list_array);
                            listview = (ListView) findViewById(R.id.listView);

                            listview.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(listview);

                        } else {
                            //获取数据失败  status为false
                            Toast.makeText(CardDiv_My_Recommend.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__recommend);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        //新建的线程执行之后在设置adapter  这样不至于设置好adapter之后数据没捕下来  界面是空的

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                if (ConnectUtil.isNetworkAvailable(CardDiv_My_Recommend.this)) {


                    //后台写完之后连
                    String jsonStr = ConnectUtil.httpRequest(ConnectUtil.GetCreditEvaluation, null, "POST");

                    if (("").equals(jsonStr) || jsonStr == null) {
                        msg.what = 1;
                        msg.obj = "连接服务器失败";
                    } else {
                        msg.what = 0;


                        msg.obj = jsonStr;
                    }

                } else {
                    msg.what = 2;
                    msg.obj = "网络连接错误，请检查您的网络连接设置";
                }
                handler.sendMessage(msg);
            }
        }.start();


        //设置输入法弹出框hidden
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final_intent = new Intent(CardDiv_My_Recommend.this, CardDiv_My_Recommend_Fail.class);
        map = new HashMap();
        isclear = false;
        builder = new AlertDialog.Builder(this);
        Tel = (EditText) findViewById(R.id.Tel);
        name = (EditText) findViewById(R.id.name);


        ImageView return_button = (ImageView) findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = true;

                name_text = name.getText().toString().trim();
                Tel_text = Tel.getText().toString().trim();
                if (name_text == null || name_text.equals("")) {
                    tag = false;
                    Toast.makeText(CardDiv_My_Recommend.this, "请输入申请人姓名", Toast.LENGTH_SHORT).show();
                    return;

                } else if (Tel_text == null || Tel_text.equals("")) {
                    tag = false;
                    Toast.makeText(CardDiv_My_Recommend.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                    return;

                } else if (!isMobileNo.isMobileNO(Tel_text)) {
                    tag = false;

                    Toast.makeText(CardDiv_My_Recommend.this, "请输入正确的联系方式", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (int i = 0; i < col_num; i++) {
                        Log.d("Dorise", tag + "");
                        if (flag[i] == 0) {

                            tag = false;
                            Toast.makeText(CardDiv_My_Recommend.this, "请输入完整的用户信息", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (tag) {
                        try {
                            sum = 0f;
                            JSONArray jsonArray = new JSONArray();

                            for (int i = 0; i < col_num; i++) {
                                JSONObject jsonObject = new JSONObject();

                                jsonObject.put("title", titleString[i]);

                                jsonObject.put("content", map.get(titleString[i]));
                                jsonObject.put("goal", score[i]);
                                jsonArray.put(jsonObject);
                                sum += score[i];

                            }
                                Log.d("Dorise--发送的jsonArray", jsonArray.toString());
                            evaluation_detail = jsonArray.toString() ;


                            {
                                final_intent.setClass(CardDiv_My_Recommend.this, CardDiv_My_Recommend_Result.class);
                                final_intent.putExtra("applicant", name_text);
                                final_intent.putExtra("telephone", Tel_text);
                                final_intent.putExtra("evaluation", sum);
                                final_intent.putExtra("evaluation_detail", evaluation_detail);

                                startActivityForResult(final_intent, 1);
                                //startActivity(final_intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {

            clearAll();
            Log.d("onactivityresult", "clear====");
        }
    }


    private void clearAll() {

        //clearAll是需要重写的

        TextView name = (TextView) findViewById(R.id.name);
        TextView teel = (TextView) findViewById(R.id.Tel);

        teel.setText(null);
        name.setText("");
        listview.setAdapter(adapter);
        //清空每一项

    }

    class MyAdapter extends BaseAdapter {
        private JSONArray jsonArray;

        public MyAdapter(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        //获得相应数据集合中特定位置的数据项  不会自动调用  但是必须写
        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        //getView返回了每个item项所显示的view  getview是从0开始算的


        //娟你的listview点击是带动画的  改一下改一下

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            Log.d("当前获取到的i值=======", i + "");
            View myview;
            final ViewHolder myholder;
            LinearLayout linear;
            if (view == null) {
                myview = LayoutInflater.from(CardDiv_My_Recommend.this).inflate(R.layout.get_item_score_listview_item, null);
                myholder = new ViewHolder();
                myholder.text_item = (TextView) myview.findViewById(R.id.item);
                myholder.text_select = (TextView) myview.findViewById(R.id.item_select);
                myholder.linear = (LinearLayout) myview.findViewById(R.id.item_linear);
                myview.setTag(myholder);

            } else {
                myview = view;
                myholder = (ViewHolder) view.getTag();
            }
            myholder.text_item.setText(titleString[i] + "");
            myholder.text_select.setHint("请选择" + titleString[i]);
            flag = new int[col_num];
            score = new float[col_num];
            for (int w = 0; w < col_num; w++) {
                flag[w] = 0;
                score[w] = 0;
            }

            myholder.linear.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    builder.setTitle("请选择" + titleString[i]);
                    item_string = (String[]) list_item.get(i);

                    InputMethodManager imm = (InputMethodManager) getSystemService(CardDiv_My_Recommend.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(myholder.linear.getWindowToken(), 0);
                    //设置标记位标记是否全部被选择  新线程里col_num不一定读取出来了 所以要放到handler里

                    Log.d("Dorise--score的col_num", col_num + "");


                    builder.setItems(item_string, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int k) {
                            //设置点击后文字内容的切换  并且记录分数的数组进行相应地更改  flag也更改

                            Log.d("Dorise--所要获取的i值", i + "");
                            Log.d("Dorise--所要获取的list_item", item_string[k]);
                            myholder.text_select.setText(item_string[k]);
                            map.put(titleString[i], item_string[k]);
                            Float[] flo = (Float[]) list_goal.get(i);
                            score[i] = flo[k];

                            for (int a = 0; a < col_num; a++) {
                                Log.d("Dorise--score[i]", score[a] + "");
                            }

                            flag[i] = 1;

                            Log.d("当前flag", flag[i] + "");
                            Log.d("当前score", score[i] + "");
                        }
                    });
                    builder.show();
                }

            });


            return myview;
        }

        void clear() {

        }

        class ViewHolder {
            TextView text_item;
            TextView text_select;
            LinearLayout linear;
        }
    }
}

