package com.xd.aselab.chinabank_shop.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class HistogramView extends View{

    private Context context;

    private Paint xLinePaint;// 坐标轴 轴线 画笔
    private Paint textPaint;// 绘制文本的画笔
    private Paint histogramPaint;// 矩形画笔

    private float[] data;
    private String[] xAxes;//X轴
    private float[] yAxes;//Y轴

    private boolean flag=true;//是否显示柱上数字

    private static final int CHART_LEFT_ASIDE = 15;//柱形图左边空出来的距离
    private static final int BOTTOM_LINE_LEFT_ASIDE = 15;//底部线条左边空出来的距离
    private static final int Y_AXES_TOP_ASIDE = 22;//Y轴上部空出来的距离
    private static final int Y_AXES_BOTTOM_ASIDE = 28;//Y轴底部空出来的距离


    //柱状图
    public HistogramView(Context context, String[] name, float[] data) {
        super(context);
        this.context = context;
        xAxes = name;
        this.data = data;
        initPaint();
        yAxes = initYAxes(data);
    }

    private void initPaint() {
        xLinePaint = new Paint();
        xLinePaint.setColor(Color.LTGRAY);
        xLinePaint.setStrokeWidth(3.0f);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp2px(14));
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);// 抗锯齿效果
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.DKGRAY);

        histogramPaint = new Paint();
        histogramPaint.setAntiAlias(true);
        histogramPaint.setStyle(Paint.Style.FILL);
        histogramPaint.setColor(0xFFF55E4E);
    }

    private float[] initYAxes(float data[]){
        float max = data[0];
        for (int i=1; i<data.length; ++i){
            if (max<data[i]){
                max = data[i];
            }
        }
        if (max<5){
            max = 5;
        }
        float[] y = new float[]{0, 1*max/4, 1*max/2, 3*max/4, max};
        return y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // 坐标从控件内开始计算，即（0，0）坐标位于该控件的左上角，而不是屏幕或activity的左上角
        // 绘制底部的线条
        canvas.drawLine(dp2px(BOTTOM_LINE_LEFT_ASIDE), height - dp2px(32), width, height - dp2px(32), xLinePaint);
        float leftHeight = height - dp2px(Y_AXES_TOP_ASIDE+Y_AXES_BOTTOM_ASIDE);// 左侧外周的需要划分的高度
        float leftPerHeight = leftHeight / 4;// 分成四部分

        // 绘制Y轴数字，倒着写的
//        for (int i = yAxes.length-1; i>=0; i--) {
//            canvas.drawText("" + yAxes[i], dp2px(25), dp2px(Y_AXES_TOP_ASIDE) + (4-i) * leftPerHeight, textPaint);
//        }

        // 绘制X轴坐标
        float xAxesLength = width - dp2px(CHART_LEFT_ASIDE); // 左边空出 CHART_LEFT_ASIDE 给Y轴坐标
        //float xAxesLength = width ;
        float perDistance = xAxesLength / (5 * data.length + 1); // 设柱状图的宽度是间距的4倍，并加上最右边的间距
        float perWidth = perDistance * 4;

        // 设置底部坐标
        for (int i = 0; i < xAxes.length; i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            Rect bounds = new Rect();
            textPaint.getTextBounds(xAxes[i], 0, xAxes[i].length(), bounds);
            canvas.drawText(xAxes[i], dp2px(CHART_LEFT_ASIDE) + perDistance + (perDistance + perWidth) * i + perWidth/2, height-dp2px(5), textPaint);
        }

        // 绘制矩形
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) { // 循环遍历将柱状图画出来
                float value = data[i];
                RectF rectf = new RectF() ;// 柱状图的形状

                rectf.left = (perDistance + perWidth) * i + perDistance + dp2px(CHART_LEFT_ASIDE);
                rectf.right = (perDistance + perWidth) * (i + 1) + dp2px(CHART_LEFT_ASIDE);
                float rh =  leftHeight - leftHeight * (value / yAxes[yAxes.length-1])+dp2px(10);
                rectf.top = rh + dp2px(7);
                rectf.bottom = height - dp2px(32);

                canvas.drawRect(rectf, histogramPaint);

                // 是否显示柱状图上方的数字
                if (flag && value>0) {
                    canvas.drawText(""+(int)value, dp2px(CHART_LEFT_ASIDE) + perDistance + (perDistance + perWidth) * i + perWidth/2, rh+dp2px(2), textPaint);
                }
            }
        }
    }

    private int dp2px(int value) {
        float v = context.getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

}
