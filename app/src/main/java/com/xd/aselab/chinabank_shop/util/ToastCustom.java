package com.xd.aselab.chinabank_shop.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//自定义Toast
public class ToastCustom {

    public static Toast getToastCenter(Context context,String str){
		 Toast toast = Toast.makeText(context,
					str, Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   return toast;
	 }

    public static void makeToastCenter(Context context,String str){
		 Toast toast = Toast.makeText(context,
					str, Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
	 }

    public static Toast getToastButtom(Context context,String str){
        Toast toast = Toast.makeText(context,
                str, Toast.LENGTH_SHORT);
        return toast;
    }

    public static void makeToastButtom(Context context,String str){
        Toast toast = Toast.makeText(context,
                str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
