package com.xd.aselab.chinabank_shop.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 应用程序更新工具包
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 * @modified Bryan 2013-7-26
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
	
    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL   = 1;
    
	private static UpdateManager updateManager;
	
	private Context mContext;
	//通知对话框
	private Dialog noticeDialog;
	//下载对话框
	private Dialog downloadDialog;
	//'已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
    //进度值
    private int progress;
    //进度条
    private ProgressBar mProgress;
    //显示下载数值
    private TextView mProgressText;
    //查询动画
    private ProgressDialog mProDialog;
    //下载线程
    private Thread downLoadThread;
    //终止标记
    private boolean interceptFlag;
	//返回的安装包url
	private String apkUrl;
	//下载包保存路径
    private String savePath = "";
	//apk保存完整路径
	private String apkFilePath = "";
	//临时下载文件路径
	private String tmpFilePath = "";
	//下载文件总大小
	private String apkFileSize;
	//已下载文件大小
	private String tmpFileSize;
	
	private String new_version = "";

	private Handler mHandler = new Handler(){
    	
		public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				ToastCustom.makeToastButtom(mContext,"无法下载安装文件，请检查SD卡是否挂载");
				break;
			}
    	};
    };

    /**
     * 单例,获取UpdateManager实例
     * @return
     */
	public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

	public void judgeAppUpdate(String new_version, Context context){
        this.new_version = new_version;
        mContext = context;
		String current_version=null;
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			current_version = pi.versionName;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		if (!current_version.equals(new_version)){
			if (Build.VERSION.SDK_INT >= 23) {
				//判断有没有权限
				if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE }, 10012);
				}
				else {
					showNoticeDialog();
				}
			}
			else {
				showNoticeDialog();
			}
		}
        else {
            System.out.println("已经是最新版本");
        }
	}

    /**
     * 显示版本更新通知对话框
     */
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("温馨提示");
		builder.setMessage("发现新版本，请更新");
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
		builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
	}
	
	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("正在下载最新客户端");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.update_progress);
		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
		
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();

		downloadApk();
	}

    private String getFilePath(String filename)
    {
        String filePath="";
        File appCacheDir=null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),"ChinaBank/install");
            //目录是否存在
            if (!appCacheDir.exists())
                appCacheDir.mkdirs();
            filePath=appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
        if (appCacheDir==null){ //没有SD卡
            appCacheDir = mContext.getDir("install", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
        Log.e("liuhaoxian", filePath);
        return filePath;
    }

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String apkName = "ChinaBankShop"+ new_version +".apk";
				String tmpApk = "ChinaBankShop"+ new_version +".tmp";

				tmpFilePath = getFilePath(tmpApk);
				apkFilePath = getFilePath(apkName);

				File ApkFile = new File(apkFilePath);

				//是否已下载更新文件
				if(ApkFile.exists()){
					ApkFile.delete();
				}
				
				//输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				apkUrl = ConnectUtil.API_HOST_DOWNLOAD+"ChinaBankShop.apk";
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				//显示文件大小格式：2个小数点显示
		    	DecimalFormat df = new DecimalFormat("0.00");
		    	//进度条下面显示的总文件大小
		    	apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    		//进度条下面显示的当前下载文件大小
		    		tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
		    		//当前进度值
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成 - 将临时下载文件转成APK文件
						if(tmpFile.renameTo(ApkFile)){
							//通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};

	/**
	* 下载apk
	* @param
	*/
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
    * 安装apk
    * @param
    */
	private void installApk(){
		File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
		Intent localIntent = new Intent(Intent.ACTION_VIEW);
		//判断版本是否在7.0以上
		if (Build.VERSION.SDK_INT >= 24) {
			//provider authorities
			Uri apkUri = FileProvider.getUriForFile(mContext, "com.mydomain.fileprovider", apkfile);
			//Granting Temporary Permissions to a URI
			localIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			localIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			Uri apkUri = Uri.fromFile(apkfile);
			localIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
        mContext.startActivity(localIntent);
	}
}
