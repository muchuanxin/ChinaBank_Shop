package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhereToGo extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;
    private SearchView search;

    private SimpleAdapter adapter;
    private List<Map<String, String>> list;
    private List<Map<String, String>> backup;
    private String[] names=null;
    private String[] addrs=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_to_go);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back = (RelativeLayout) findViewById(R.id.act_where_to_go_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_where_to_go_list_view);
        search = (SearchView) findViewById(R.id.act_where_to_go_search_bar);

        final Intent intent = getIntent();
        names = intent.getStringArrayExtra("names");
        addrs = intent.getStringArrayExtra("addrs");

        if (names!=null && names.length>0 && addrs!=null && addrs.length>0){
            list = new ArrayList<>();
            backup = new ArrayList<>();
            for (int i=0; i<names.length; ++i){
                Map<String, String> map = new HashMap<>();
                map.put("name", names[i]);
                map.put("addr", addrs[i]);
                list.add(map);
                backup.add(map);
            }
            adapter = new SimpleAdapter(WhereToGo.this, backup, R.layout.list_view_where_to_go,
                    new String[]{"name", "addr"},
                    new int[]{R.id.list_view_where_name, R.id.list_view_where_addr});
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(WhereToGo.this, backup.get(position).get("addr"), Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent();
                    intent2.putExtra("addr", backup.get(position).get("addr"));
                    setResult(172, intent2);
                    finish();
                }
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (list != null && list_view.getVisibility() == View.VISIBLE) {
                        backup = new ArrayList<>();
                        for (Map<String, String> map : list) {
                            if (map.get("name").contains(query)) {
                                backup.add(map);
                            }
                        }
                        adapter = new SimpleAdapter(WhereToGo.this, backup, R.layout.list_view_where_to_go,
                                new String[]{"name", "addr"},
                                new int[]{R.id.list_view_where_name, R.id.list_view_where_addr});
                        list_view.setAdapter(adapter);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return onQueryTextSubmit(newText);
                }
            });
        }
    }

    private void sleep_and_finish(final int s){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(s * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
