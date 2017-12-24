package com.xd.aselab.chinabank_shop.util;

/**
 * Created by Dorise on 2017/10/11.
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;



import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageSettingUtil {


    public interface ImageUploadDelegate {
        // 上传过程中需要给更新的进度
        public void setUploadProgress(int index, double x);// index
        // 表示第几个进度条，x表示进度,当x为负数时，表示上传出现问题

        public void getRecodeFromServer(int index, String reCode);
    }

    // 上传图片,后台为struct2服务器
    public static void uploadImage(ImageUploadDelegate delegate, int index, PostParameter[] postParams, InputStream in,
                                   String uploadIU) {

        String uploadUrl = uploadIU + ConnectUtil.encodeParameters(postParams);
        Log.i("kmj", "imageUploadUrl---->" + uploadUrl);
        String reCode = "";
        String imageUrl = "";
        String message = "";
        try {
            // 获取http请求
            String boundary = "******";
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置相关参数
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(20 * 1000);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            // 得到输出流
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            byte[] buffer = new byte[1024 * 8];
            int total = in.available();
            int length = 0;
            int count = 0;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                count += length;
                delegate.setUploadProgress(index, 1.0 * count / total);
            }

            out.flush();
            out.close();
            Log.i("kmj", "upload over");
            String line = "";
            Log.i("kmj", "result=====>" + httpURLConnection.getResponseCode());
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((line = br.readLine()) != null) {
                reCode += line;
            }
            Log.i("kmj", "result=====>" + reCode);
            delegate.getRecodeFromServer(index,reCode);

        } catch (Exception e) {
            e.printStackTrace();
            delegate.getRecodeFromServer(index, reCode);
        }
    }

    // 将图片压缩至1M以内
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) { // 200kb
            // //循环判断如果压缩后图片是否大于1M,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options <= 0)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // 将图片压缩，并且解决拍照旋转的问题
    public static InputStream compressJPG(String filePath) {

        InputStream is = null;
        try {
            Options newOpts = new Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);// 此时返回bm为空

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 1136f;// 这里设置高度为800f
            float ww = 960f;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(filePath, newOpts);

            /**
             * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
             */
            int degree = readPictureDegree(filePath);

            /**
             * 把图片旋转为正的方向
             */
            // if(degree!=0){
            Bitmap cameraBitmap = BitmapFactory.decodeFile(filePath, newOpts);

            bitmap = rotaingImageView(degree, cameraBitmap);
            // }

            Bitmap finalImage = compressImage(bitmap);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            finalImage.compress(CompressFormat.JPEG, 100, os);
            is = new ByteArrayInputStream(os.toByteArray());
            Log.i("liuhaoxian", "compress over");
        } catch (Error error) {
            is = null;
        } finally {
            return is;
        }

    }

    // 将图片进行旋转
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    // 得到图片的旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 压缩网络图片，返回bitmap
//	public static Bitmap getBitmapFromUrl(String url) {
//		Options options = new Options();
//		options.inSampleSize = scale(url);
//		InputStream in;
//		Bitmap bitmap = null;
//		try {
//			// in = new URL(url).openStream();
//			in = ConnectUtil.httpRequest2(url, null, null);
//			bitmap = BitmapFactory.decodeStream(in, null, options);
//		} finally {
//
//			return bitmap;
//		}
//
//	}
//
//	static private int scale(String url) {
//		int be = 1;
//		InputStream in = ConnectUtil.httpRequest2(url, null, null);
//		BitmapFactory.Options newOpts = new Options();
//
//		newOpts.inJustDecodeBounds = true;
//		BitmapFactory.decodeStream(in, null, newOpts);
//		int w = newOpts.outWidth;
//
//		int h = newOpts.outHeight;
//
//		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		float hh = 480f;// 这里设置高度为800f
//		float ww = 320f;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//
//		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//			be = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//			be = (int) (newOpts.outHeight / hh);
//		}
//		if (be <= 0)
//			be = 1;
//		return be;
//
//	}

    // 设置图片，解决图片旋转的问题
    public static void setImage(String filePath, ImageView imageView) {
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1136f;// 这里设置高度为800f
        float ww = 960f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // bitmapOptions.inSampleSize =4;

        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        int degree = ImageSettingUtil.readPictureDegree(filePath);
        Bitmap cameraBitmap = BitmapFactory.decodeFile(filePath, newOpts);

        /**
         * 把图片旋转为正的方向
         */
        Bitmap bitmap = ImageSettingUtil.rotaingImageView(degree, cameraBitmap);
        imageView.setImageBitmap(bitmap);

    }

}
