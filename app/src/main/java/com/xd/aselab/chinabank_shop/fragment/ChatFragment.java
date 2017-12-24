package com.xd.aselab.chinabank_shop.fragment;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.NewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private ViewPager mPager;//页卡内容
    private List<Fragment> fragmentList;// Tab页面列表
    private ImageView cursor;// 动画图片
    private TextView t1, t2, t3;// 页卡头标
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView add_button;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);


        InitTextView(root);
        InitViewPager(root);
        InitImageView(root);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewGroup.class);
                //new是新建分组
                intent.putExtra("jump", "new");
                startActivity(intent);

            }
        });
        return root;
    }

    /**
     * 初始化头标
     */
    private void InitTextView(View root) {
        t1 = (TextView) root.findViewById(R.id.news);
        t2 = (TextView) root.findViewById(R.id.weather);
        add_button = (ImageView) root.findViewById(R.id.add_button);
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }

    /**
     * 初始化ViewPager
     */
    private void InitViewPager(View root) {
        mPager = (ViewPager) root.findViewById(R.id.mcx_myorder_viewpager);
        fragmentList = null;
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new ChatFragment_Left());
        fragmentList.add(new ChatFragment_Right());
        FragAdapter fragAdapter = new FragAdapter(getChildFragmentManager(), fragmentList);
        mPager.setAdapter(fragAdapter);
        mPager.setCurrentItem(0);
        //   ChangeColor(t1, t2);
    }

    /**
     * 初始化动画
     */
    private void InitImageView(View root) {
        cursor = (ImageView) root.findViewById(R.id.mcx_myorder_cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.slider).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / fragmentList.size() - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }


    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            switch (v.getId()) {
                case R.id.news:
                    //     ChangeColor(t1,t2);
                    break;
                case R.id.weather:
                    //    ChangeColor(t2,t1);
                    break;
            }
        }
    }

    ;

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡0 -> 页卡1 偏移量
        int two = one * 2;// 页卡0 -> 页卡2 偏移量

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) { //arg0是跳转的目标页卡
                case 0:
                    if (currIndex == 1) { //从页卡1跳到页卡0
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }
                    add_button.setVisibility(View.INVISIBLE);
                    add_button.setClickable(false);
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }
                    add_button.setVisibility(View.VISIBLE);
                    add_button.setClickable(true);
                    break;
            }
            currIndex = arg0;
            if (animation != null) {
                animation.setFillAfter(true);// True:图片停在动画结束位置
                animation.setDuration(100);//动画时长
                cursor.startAnimation(animation);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    public class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            mFragments = fragmentList;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }

/*    private void ChangeColor(TextView view_orange , TextView view_black1){
        view_orange.setTextColor(0xFFEF8000);
        view_black1.setTextColor(Color.BLACK);
    }*/

}
