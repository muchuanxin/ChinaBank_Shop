package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

public class CardDiv_My_Recommend_Fail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_div__my__recommend__fail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ImageView return_btn= (ImageView) findViewById(R.id.back_btn);
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        float sum=getIntent().getFloatExtra("evaluation",0);
        Log.d("Dorise=======",sum+"");
        TextView score= (TextView) findViewById(R.id.final_score);
        score.setText(sum+"");
    }
}
