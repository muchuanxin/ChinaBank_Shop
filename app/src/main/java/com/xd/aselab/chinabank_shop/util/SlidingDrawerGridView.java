package com.xd.aselab.chinabank_shop.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/12/12.
 */

//解决gridview滚动条显示的问题
public class SlidingDrawerGridView extends GridView {

    public SlidingDrawerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingDrawerGridView(Context context) {
        super(context);
    }

    public SlidingDrawerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
