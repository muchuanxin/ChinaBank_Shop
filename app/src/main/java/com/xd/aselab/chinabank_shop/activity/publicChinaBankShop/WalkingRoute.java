package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amap.api.services.route.WalkPath;
import com.umeng.analytics.MobclickAgent;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.fragment.WalkSegmentListAdapter;

public class WalkingRoute extends AppCompatActivity {

    private RelativeLayout back;
    private ListView list_view;

    private WalkPath mWalkPath;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_route);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        back = (RelativeLayout) findViewById(R.id.act_walking_route_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_view = (ListView) findViewById(R.id.act_walking_route_list_view);
        Intent intent = getIntent();
        mWalkPath = intent.getParcelableExtra("walk_path");
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        list_view.setAdapter(mWalkSegmentListAdapter);
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
