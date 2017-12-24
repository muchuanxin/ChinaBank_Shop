package com.xd.aselab.chinabank_shop.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.xd.aselab.chinabank_shop.R;

//import com.wiseserver.chuhua.xd.wise_server.R;

public class AsynLoaderPic{
    private String path;
    private Context context;
    /**
     * create liuhaoixan
     * 2013/8/22
     */
    //图片缓冲区
    public Map<String,SoftReference<Drawable>> cache=new HashMap<String,SoftReference<Drawable>>();
    public AsynLoaderPic(Context context)
    {
//		path = new TakePhotoUtil(context,"pictrues").getImgPathParent()+"/";
//		//创建图片文件夹
//		File file=new File(path);
//		if(!file.exists())
//			file.mkdir();
        this.context=context;
    }
    //加载图片
    public Drawable getDrawable(final String url,final ImageView view)
    {

        if(cache.containsKey(url))
        {
            SoftReference<Drawable> sRef=cache.get(url);
            Drawable drawable=sRef.get();
            if(drawable!=null)
                return drawable;
            else
                cache.remove(url);
        }

        final Handler myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what)
                {
                    case 0:
                        if(msg.obj!=null)
                        {
                            view.setImageDrawable((Drawable)msg.obj);

                        }
                        else{
                           // ToastCustom.makeToastButtom(context,"图片加载失败");
//						   Toast.makeText(context,"头像加载失败", Toast.LENGTH_SHORT).show();
                           // Drawable drawable = context.getResources().getDrawable(R.drawable.default_pic);//用假图片来欺骗
                          //  view.setImageDrawable(drawable);
                            view.setImageResource(R.drawable.default_pic);//用假图片来欺骗
                        }
                        break;
                    default:;
                }
            }

        };

        new Thread()//下载图片
        {

            @Override
            public void run() {
                System.out.println("URL--------->"+url);
                Drawable drawable;
//				    InputStream in=ConnectUtil.httpRequest(url,null,ConnectUtil.GET);
                try{
                    InputStream in=null;
                    //
                    if(url.startsWith("http")){
                        in=new URL(url).openStream();
                        Log.i("liuhaoxian","pic size="+in.available());
                        BitmapFactory.Options bitmapOptions=new BitmapFactory.Options();
                        if(in.available()>=2*1024*1024)
                            bitmapOptions.inSampleSize=4;
                        else
                            bitmapOptions.inSampleSize=2;
                        Bitmap bitmap=BitmapFactory.decodeStream(new URL(url).openStream(),null,bitmapOptions);
                        drawable=new BitmapDrawable(bitmap);
                    }
                    else
                    {
                        //in=new FileInputStream(new File(url));
                        in=ImageSetingUtil.compressJPG(url);

                        Bitmap bitmap=BitmapFactory.decodeStream(in);
                        drawable=new BitmapDrawable(bitmap);

                    }


                }catch(Exception e){
                    e.printStackTrace();
                    drawable=null;
                }
                Message msg=new Message();
                msg.what=0;
                msg.obj=drawable;
                myHandler.sendMessage(msg);
                if(drawable!=null)
                    cache.put(url, new SoftReference<Drawable>(drawable));
                //存到本地
//					if(1==head_photo&&drawable!=null)
//				     writeToSDcard(drawable,"head_photo.png");
            }
        }.start();
        return null;
    }
    //    //将Drawable图片写入SDcard
//    public void writeToSDcard(Drawable drawable,String fileName)
//    {   
//    	System.out.println("fileName = "+fileName);
//    	System.out.println("drawable = "+drawable);
//    	
//    	File file=new File(path+fileName);
//    	if (!file.exists()) {
//			File fileParent = file.getParentFile();
//			fileParent.mkdirs();
//		}
//    	
//    	System.out.println("file = "+file);
//    	
//    	OutputStream out=null;
//    	try {
//			out=new FileOutputStream(file);
//			BitmapDrawable bd=(BitmapDrawable)drawable;
//			Bitmap x=bd.getBitmap();
//			out.write(bitMapToByte(x));
//			out.flush();
//			System.out.println("图片写入成功");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	finally{
//    		try {
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//    	}
//    		
//    }
    //将bitmap转化为byte[]
    private byte[] bitMapToByte(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //从SDcard中得到图片
    public Drawable getDrawableFromLocal(String fileName)
    {
        Drawable drawable=null;
        File file=new File(path+fileName);
        if(file.exists())
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            Bitmap bm=BitmapFactory.decodeFile(path+fileName,opts);
            drawable= new BitmapDrawable(bm);
            System.out.println("---------->In local");
        }
        return drawable;
    }

}
