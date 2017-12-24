package com.xd.aselab.chinabank_shop.activity.CardDiv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CustomPopupWindow extends PopupWindow implements View.OnClickListener {

    private TextView week, month, three_week,year;
    private View mPopView;
    private OnItemClickListener mListener;

    public CustomPopupWindow(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
        setPopupWindow();
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        three_week.setOnClickListener(this);
        year.setOnClickListener(this);

    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        //绑定布局
        mPopView = inflater.inflate(R.layout.custom_popup_window, null);

        week = (TextView) mPopView.findViewById(R.id.week);
        month = (TextView) mPopView.findViewById(R.id.month);
        three_week = (TextView) mPopView.findViewById(R.id.three_month);
        year = (TextView) mPopView.findViewById(R.id.year);
    }

    /**
     * 设置窗口的相关属性
     */
    private void setPopupWindow() {
        this.setContentView(mPopView);// 设置View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画

//        this.setBackgroundDrawable(new ColorDrawable(0x00000010));// 设置背景透明
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(10,0,0,10)));// 设置背景透明
/*        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPopView.findViewById(R.id.id_pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });*/
    }



    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的点击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mListener != null) {
            mListener.setOnItemClick(v);
        }
    }

}
