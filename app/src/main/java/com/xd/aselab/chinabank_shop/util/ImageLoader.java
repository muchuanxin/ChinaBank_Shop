package com.xd.aselab.chinabank_shop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;


import com.xd.aselab.chinabank_shop.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.Environment.MEDIA_MOUNTED;


public class ImageLoader {

	private LruCache<String, Bitmap> mMemoryCache;
    private Context context=null;
	private static ImageLoader imageLoader=null;
	public static ImageLoader getInstance(){
		if(imageLoader==null){
			imageLoader=new ImageLoader();
		}
		return imageLoader;
	}
	private ImageLoader(){
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // 使用最大可用内存值的1/16作为缓存的大小。   
	    int cacheSize = maxMemory / 16;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // 重写此方法来衡量每张图片的大小，默认返回图片数量。   
	            return bitmap.getByteCount() / 1024;  
	        }
	    };
	}
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	private Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);  
	}  

	//异步加载图片
	public void loadBitmap(Context context, String url, ImageView imageView,int defalutPicID) {
        this.context = context;
        if (url.endsWith("null") || url.endsWith("/") || "".equals(url)){
			//Log.e("url","url="+url);
			if (defalutPicID!=0){
				imageView.setImageResource(defalutPicID);
			}
			else {
                imageView.setImageResource(R.drawable.placeholder2);
            }
        }
        else {
			final Bitmap bitmap = getBitmapFromMemCache(url);
            if (bitmap != null) {
                Log.i("liuhaoxian", "从内存获取图片" + url);
                imageView.setImageBitmap(bitmap);
            } else {
				if (defalutPicID!=0){
					imageView.setImageResource(defalutPicID);
				}
                BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                task.execute(url);
            }
        }
	}

	//后台加载图片
	private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	    private ImageView imageView;
	    public BitmapWorkerTask(ImageView imageView){
	    	this.imageView=imageView;
	    }
	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	String url=params[0];
	    	Bitmap bitmap=null;
	    	String[] parts=url.split("/");
	    	String filename="";
	    	if(parts!=null&&parts.length>0)
	    		filename=parts[parts.length-1];//将http映射成filename,必须保证filename的唯一
	    	if(!filename.equals(""))
	    	{
                   //从文件获取
	    		   bitmap=getBitmapFromFile(filename);
	    		   if(bitmap==null){
	    			 //网络获取
	  			     bitmap =getBitmapFromUrl(url);
	  			     Log.i("liuhaoxian", "从网络获取图片" + url);
	    		     //将图片写入到文件夹下
		    		 writeImageToFile(bitmap,filename);
	    		   }
	    	}
	    	//将图片存在内存中
            if (bitmap!=null)
			    addBitmapToMemoryCache(String.valueOf(url), bitmap);
	        return bitmap;
	    }
		@Override
		protected void onPostExecute(Bitmap result) {
			if(result==null){
				Log.i("liuhaoxian", "获取图片失败");
			}
			else
			{
				imageView.setImageBitmap(result);
			}
		}
	}

	private void writeImageToFile(Bitmap bitmap,String imagename){
    	 if(bitmap!=null)
				try {
                    File imageFile=new File(getFilePath(imagename));
                    if(!imageFile.exists())
                        imageFile.createNewFile();
                    FileOutputStream out = new FileOutputStream(imageFile);
                    bitmap.compress(CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    }

	private Bitmap getBitmapFromFile(String filename){
    	        Bitmap bitmap=null;
			    String filePath=getFilePath(filename);
			    if(!filePath.equals("")){
			    		File imageFile=new File(filePath);
 		                if(imageFile.exists()){
 		                	Log.i("liuhaoxian", "从本地文件获取" + filePath);
				            //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
		 		        	Options newOpts = new Options();
				            newOpts.inJustDecodeBounds = true;  
				            BitmapFactory.decodeFile(filePath, newOpts);//此时返回bm为空
				            newOpts.inJustDecodeBounds = false;  
				            int w = newOpts.outWidth;  
				            int h = newOpts.outHeight;  
				            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
				            float hh = 480f;//这里设置高度为480f  
				            float ww = 320f;//这里设置宽度为320f  
				            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
				            int be = 2;//be=1表示不缩放  
				            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
				                be = (int) (newOpts.outWidth / ww);  
				            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
				                be = (int) (newOpts.outHeight / hh);  
				            }  
				            if (be <= 0)  
				                be = 2;  
				            newOpts.inSampleSize = be;//设置缩放比例  
						    bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		                    }
			    }
		   return bitmap;
    }

	private String getFilePath(String filename)
    {
    	String filePath="";
        File appCacheDir=null;
    	 if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			    appCacheDir = new File(Environment.getExternalStorageDirectory(),"ChinaBank/cache/images");
			    //目录是否存在
		        if (!appCacheDir.exists())
			            appCacheDir.mkdirs();
		 		filePath=appCacheDir.getAbsolutePath()+ File.separator+filename;
         }
         if (appCacheDir==null){ //没有SD卡
             appCacheDir = context.getDir("images", Context.MODE_PRIVATE);
             filePath = appCacheDir.getAbsolutePath()+ File.separator+filename;
         }
        Log.i("liuhaoxian", filePath);
    	 return filePath;
    }

	private InputStream getInputStream(String url){
    	InputStream is = null;
		try {
			HttpURLConnection con = null;
			OutputStream osw = null;
			try {
				con = (HttpURLConnection) new URL(url).openConnection();
				con.setDoInput(true);
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					is = con.getInputStream();
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return is;
		}
    }

	private Bitmap getBitmapFromUrl(String url){
		Options options=new Options();
		options.inSampleSize=scale(url);
		InputStream in;
		Bitmap bitmap=null;
		try {
			in=getInputStream(url);
			bitmap= BitmapFactory.decodeStream(in, null, options);
		}
		finally{
			return bitmap;
		}
	}

	private int scale(String url){
		    int be=1;
			InputStream in=	getInputStream(url);
			Options newOpts=new Options();
			
			newOpts.inJustDecodeBounds=true;
			BitmapFactory.decodeStream(in, null, newOpts);
			int w = newOpts.outWidth;  
			
            int h = newOpts.outHeight;  
            
            //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
            float hh = 480f;//这里设置高度为480f  
            float ww = 320f;//这里设置宽度为320f  
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
           
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
                be = (int) (newOpts.outWidth / ww);  
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
                be = (int) (newOpts.outHeight / hh);  
            }  
            if (be <= 0)  
                be = 1;  
           return be;
	}
}

