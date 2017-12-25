package com.xd.aselab.chinabank_shop.util;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by wenqr on 2017/12/3.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Typeface mTypeface = Typeface.createFromAsset(getAssets(), "font/SourceHanSansCN-Regular_0.otf");

        try {
            Field field = Typeface.class.getDeclaredField("SANS_SERIF");
            field.setAccessible(true);
            field.set(null, mTypeface);
        } catch (NoSuchFieldException e) {
            Log.e("MyApp","加载第三方字体失败。") ;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    }
