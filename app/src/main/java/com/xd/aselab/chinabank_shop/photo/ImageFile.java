package com.xd.aselab.chinabank_shop.photo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.photo.util.PublicWay;


/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:06
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private TextView bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (TextView) findViewById(R.id.cancel_2);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText("选择相册");
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			//清空选择的图片
//			Bimp.tempSelectBitmap.clear();
			for(int i = 0; i < Bimp.tempSelectBitmap.size(); i--){
				Bimp.tempSelectBitmap.remove(i);
				i++;
			}
			Intent intent = new Intent();
			setResult(RESULT_OK);
			finish();
//			Intent intent = new Intent();
//			intent.setClass(mContext, UploadFragment.class);
//			startActivity(intent);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
//			Intent intent = new Intent();
//			intent.setClass(mContext, UploadFragment.class);
//			startActivity(intent);
		}

		return true;
	}

}
