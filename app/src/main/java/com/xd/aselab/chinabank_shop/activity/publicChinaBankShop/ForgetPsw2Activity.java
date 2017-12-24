package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xd.aselab.chinabank_shop.R;

public class ForgetPsw2Activity extends AppCompatActivity {

    private RelativeLayout back;
    private EditText question_edit;
    private EditText answer_edit;
    private Button submit;
    private String type;

    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        back = (RelativeLayout) findViewById(R.id.act_forget_psw2_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        question_edit = (EditText) findViewById(R.id.act_forget_psw2_question);
        question_edit.setText(getIntent().getStringExtra("question"));
        type=getIntent().getStringExtra("type");
        answer_edit = (EditText) findViewById(R.id.act_forget_psw2_answer);

        submit = (Button) findViewById(R.id.act_forget_psw2_submit_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = answer_edit.getText().toString().trim();
                if (answer==null || "".equals(answer)){
                    Toast.makeText(ForgetPsw2Activity.this, "答案不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if (!answer.equals(getIntent().getStringExtra("answer"))){
                    Toast.makeText(ForgetPsw2Activity.this, "答案错误！", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(ForgetPsw2Activity.this, ForgetPsw3Activity.class);
                    intent.putExtra("account",getIntent().getStringExtra("account"));
                    intent.putExtra("type",getIntent().getStringExtra("type"));
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}
