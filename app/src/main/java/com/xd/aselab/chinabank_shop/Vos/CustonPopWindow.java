package com.xd.aselab.chinabank_shop.Vos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

/**
 * Created by Dorise on 2017/10/7.
 */

public class CustonPopWindow extends PopupWindow {
    private View popwindow;
    private AdapterView.OnItemClickListener mListener;
    private SharePreferenceUtil sp;

    public CustonPopWindow(final Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        popwindow = inflater.inflate(R.layout.activity_carddiv_call_manager, null);
        popwindow.setFocusableInTouchMode(true);
        setContentView(popwindow);
        setAnimationStyle(R.style.mypopwindow_anim_style);



        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setBackgroundDrawable(new ColorDrawable(0x00000077));// 设置背景透明
        this.setOutsideTouchable(true);
        popwindow.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = popwindow.findViewById(R.id.height).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        TextView communi = (TextView) popwindow.findViewById(R.id.communi);
        communi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView call = (TextView) popwindow.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},
                            Constants.ActivityCompatRequestPermissionsCode);
                    return;
                }
                String tel="tel:"+sp.getShopManagerTel().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(tel));

                context.startActivity(intent);
            }
        });

        TextView cancel= (TextView) popwindow.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

}
