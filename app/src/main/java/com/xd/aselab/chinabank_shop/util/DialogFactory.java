package com.xd.aselab.chinabank_shop.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xd.aselab.chinabank_shop.R;


public class DialogFactory {
	
	public final static int DATE_DIALOG = 0;
	public final static int TIME_DIALOG = 1;
    private static Calendar c = null;

    public static Dialog creatRequestDialog(final Context context, String tip) {

		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_load);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		int width = ScreenUtil.getScreenWidth(context);
		lp.width = (int) (0.6 * width);

		dialog.setCanceledOnTouchOutside(false);
		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		if (tip == null || tip.length() == 0) {
			titleTxtv.setText(R.string.sending_request);
		} else {
			titleTxtv.setText(tip);
		}

		return dialog;
	}
	
	public static void ToastDialog(Context context, String title, String msg) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", null).create().show();

	}
	
	
	
	
	 public static Dialog createPickerDialog(int id,final Context context,final Button button,final TextView textView) {
	        Dialog dialog = null;
	        switch (id) {
	        case DATE_DIALOG:
	        	 c = Calendar.getInstance();
	             dialog = new DatePickerDialog(context, 
	             		new DatePickerDialog.OnDateSetListener() {
	 						
	 						public void onDateSet(DatePicker view, int year, int monthOfYear,
	 								int dayOfMonth) {
	 							if (button != null) {
	 								button.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
	 							}
	 							if (textView != null) {
	 								textView.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
	 							}
	 							
	 						}
	 					}, 
	 					c.get(Calendar.YEAR), 
	 					c.get(Calendar.MONTH), 
	 					c.get(Calendar.DAY_OF_MONTH));
	             dialog.show();
	             break;
	        case TIME_DIALOG:
	        	c=Calendar.getInstance();
	        	dialog = new TimePickerDialog(context, 
	        			new OnTimeSetListener() {
							
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								if (button != null) {
									button.setText(hourOfDay+"时"+minute+"分");
								}
								if (textView != null) {
									textView.setText(hourOfDay+"时"+minute+"分");
								}
							}
						}, 
						c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE), 
						false);
	        	dialog.show();
	            break;
	        }
	        return dialog;
	    }
	
}
