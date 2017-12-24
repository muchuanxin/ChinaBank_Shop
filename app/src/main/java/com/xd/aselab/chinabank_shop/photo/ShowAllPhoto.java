package com.xd.aselab.chinabank_shop.photo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.xd.aselab.chinabank_shop.photo.ImageItem;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.photo.util.PublicWay;

import java.util.ArrayList;
import java.util.List;


/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:49:10
 */
public class ShowAllPhoto extends Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private TextView okButton;
	// 预览按钮
	private TextView preview;
	// 返回按钮
	private ImageView back;
	// 取消按钮
	private TextView cancel;
	// 标题
	private TextView headTitle;
	private Intent intent;
	private Context mContext;
	public static List<ImageItem> dataList = new ArrayList<ImageItem>();
	private TextView numberOfImages;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_show_all_photo);
		PublicWay.activityList.add(this);
		mContext = this;
		back = (ImageView) findViewById(R.id.showallphoto_back);
		cancel = (TextView) findViewById(R.id.showallphoto_cancel);
		preview = (TextView) findViewById(R.id.showallphoto_preview);
		okButton = (TextView) findViewById(R.id.showallphoto_ok_button);
		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		numberOfImages = (TextView) findViewById(R.id.numberOfImages2);
		numberOfImages.setText(Bimp.tempSelectBitmap.size()+"");
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener(intent));
		preview.setOnClickListener(new PreviewListener());
		init();
		initListener();
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "2");
				intent.setClass(ShowAllPhoto.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private class BackListener implements OnClickListener {// 返回按钮监听
		Intent intent;

		public BackListener(Intent intent) {
			this.intent = intent;
		}

		public void onClick(View v) {
			intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);
		}

	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			//清空选择的图片
//	    	Bimp.tempSelectBitmap.clear();
			for(int i = 0; i < Bimp.tempSelectBitmap.size(); i--){
				Bimp.tempSelectBitmap.remove(i);
				i++;
			}
			Intent intent = new Intent();
//			setResult(InformationPublishActivity.LOCAL, intent);
			finish();
//			intent.setClass(mContext, UploadFragment.class);
//			startActivity(intent);
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(ShowAllPhoto.this, (ArrayList<ImageItem>) dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		okButton = (TextView) findViewById(R.id.showallphoto_ok_button);
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					public void onItemClick(final ToggleButton toggleButton,
											int position, boolean isChecked,
											Button button, Button chooseNo) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num&&isChecked) {
							button.setVisibility(View.GONE);
							chooseNo.setVisibility(View.VISIBLE);
							toggleButton.setChecked(false);
							Toast.makeText(ShowAllPhoto.this, "超出可选图片张数", Toast.LENGTH_SHORT)
									.show();
							return;
						}

						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							chooseNo.setVisibility(View.GONE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							numberOfImages.setText(Bimp.tempSelectBitmap.size()+"");
//							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size()
//									+ "/"+PublicWay.num+")");
						} else {
							button.setVisibility(View.GONE);
							chooseNo.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							numberOfImages.setText(Bimp.tempSelectBitmap.size()+"");
//							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}


				});

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
//				if (PublicWay.photoService != null) {
//					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
//					Bimp.tempSelectBitmap.clear();
//					PublicWay.photoService.onActivityResult(0, -2,
//							intent);
//				}
				Intent intent = new Intent();
//				setResult(InformationPublishActivity.LOCAL, intent);
//				finish();
//				intent.setClass(mContext, UploadFragment.class);

				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("selectedDataList",
				// selectedDataList);
				// intent.putExtras(bundle);
				// intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
				// startActivity(intent);
				finish();

			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			numberOfImages.setText(Bimp.tempSelectBitmap.size()+"");
//			okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(getResources().getColor(R.color.orange));
			preview.setTextColor(getResources().getColor(R.color.orange));
		} else {
			numberOfImages.setText(Bimp.tempSelectBitmap.size()+"");
//			okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);
		}

		return false;

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		isShowOkBt();
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}



}
