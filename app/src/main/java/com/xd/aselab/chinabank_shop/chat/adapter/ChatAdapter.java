package com.xd.aselab.chinabank_shop.chat.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.chat.entity.ChatMsgEntity;
import com.xd.aselab.chinabank_shop.chat.entity.ChatMsgType;
import com.xd.aselab.chinabank_shop.util.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by xinye on 2017/11/4.
 */

public class ChatAdapter extends BaseAdapter {

    private List<ChatMsgEntity> list;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ProgressDialog progDialog = null;

    public ChatAdapter(List<ChatMsgEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ChatMsgEntity getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMsgEntity data = list.get(position);
        final ChatMsgType type = data.getType();

        if (type==ChatMsgType.TEXT){
            if (data.isLeft()){

                //使用传统的缓存机制会造成左右布局错乱（比如该在左边的变成右边，奇怪的是内容没错（头像、文字没错））

                /*TextViewHolder viewHolder=null;
                if (convertView==null){
                    convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_left_text, null);
                    viewHolder=new TextViewHolder();
                    viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_left_text_head);
                    viewHolder.text = (TextView)convertView.findViewById(R.id.list_view_chat_left_text_text);
                    convertView.setTag(viewHolder);
                }
                else {
                    viewHolder = (TextViewHolder)convertView.getTag();
                }
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.text.setText(data.getContent());
                return convertView;*/

                TextViewHolder viewHolder= new TextViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_left_text, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_left_text_head);
                viewHolder.text = (TextView)convertView.findViewById(R.id.list_view_chat_left_text_text);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.text.setText(data.getContent());
                return convertView;

            }
            else {

                /*TextViewHolder viewHolder=null;
                if (convertView==null){
                    convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_right_text, null);
                    viewHolder=new TextViewHolder();
                    viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_right_text_head);
                    viewHolder.text = (TextView)convertView.findViewById(R.id.list_view_chat_right_text_text);
                    convertView.setTag(viewHolder);
                }
                else {
                    viewHolder = (TextViewHolder)convertView.getTag();
                }
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.text.setText(data.getContent());
                return convertView;*/

                TextViewHolder viewHolder= new TextViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_right_text, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_right_text_head);
                viewHolder.text = (TextView)convertView.findViewById(R.id.list_view_chat_right_text_text);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.text.setText(data.getContent());
                return convertView;

            }
        }
        else if (type==ChatMsgType.PICTURE){

            if (data.isLeft()){
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_left_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                imageLoader.loadBitmap(context, data.getUrl(), viewHolder.picture, 0);
                /*Glide.with(context)
                        .load(data.getUrl())
                        .apply(new RequestOptions().fitCenter())
                        .into(viewHolder.picture);*/

                final String image_url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View root = LayoutInflater.from(context).inflate(R.layout.pop_window_chat_picture,null);

                        ImageView imageView = (ImageView) root.findViewById(R.id.pop_window_chat_picture_image);
                        //imageLoader.loadBitmap(context, image_url, imageView, 0);
                        Glide.with(context)
                                .load(image_url)
                                .apply(new RequestOptions().fitCenter())
                                .into(imageView);

                        final PopupWindow popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
                        popupWindow.setFocusable(true);
                        // 实例化一个ColorDrawable颜色为半透明
                        //ColorDrawable dw = new ColorDrawable(0xb0000000);
                        // popWindow出来的时候设置背景透明
                        //findViewById(R.id.activity_marketing_guide).setBackground(new ColorDrawable(0xb0000000));
                        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        // 设置popWindow的显示和消失动画
                        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
                        // 在中间显示
                        popupWindow.showAtLocation(((Activity)context).findViewById(R.id.chat_activity), Gravity.CENTER, 0, 0);

                        //popWindow消失监听方法
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });

                        RelativeLayout background = (RelativeLayout) root.findViewById(R.id.pop_window_chat_picture_rl);
                        background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                    }
                });
                dissmissProgressDialog();
                return convertView;
            }
            else {
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_right_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                imageLoader.loadBitmap(context, data.getUrl(), viewHolder.picture, 0);
                /*Glide.with(context)
                        .load(data.getUrl())
                        .apply(new RequestOptions().fitCenter())
                        .into(viewHolder.picture);*/

                final String image_url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View root = LayoutInflater.from(context).inflate(R.layout.pop_window_chat_picture,null);

                        ImageView imageView = (ImageView) root.findViewById(R.id.pop_window_chat_picture_image);
                        //imageLoader.loadBitmap(context, image_url, imageView, 0);
                        Glide.with(context)
                                .load(image_url)
                                .apply(new RequestOptions().fitCenter())
                                .into(imageView);

                        final PopupWindow popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
                        popupWindow.setFocusable(true);
                        // 实例化一个ColorDrawable颜色为半透明
                        //ColorDrawable dw = new ColorDrawable(0xb0000000);
                        // popWindow出来的时候设置背景透明
                        //findViewById(R.id.activity_marketing_guide).setBackground(new ColorDrawable(0xb0000000));
                        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        // 设置popWindow的显示和消失动画
                        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
                        // 在中间显示
                        popupWindow.showAtLocation(((Activity)context).findViewById(R.id.chat_activity), Gravity.CENTER, 0, 0);

                        //popWindow消失监听方法
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });

                        RelativeLayout background = (RelativeLayout) root.findViewById(R.id.pop_window_chat_picture_rl);
                        background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                    }
                });
                dissmissProgressDialog();
                return convertView;
            }
        }
        else if (type==ChatMsgType.VOICE) {

            if (data.isLeft()){
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_left_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.picture.setImageResource(R.drawable.ic_mic_black_24dp);

                final String url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog("语音读取中，请稍候...");
                        String[] parts=url.split("/");
                        String filename="";
                        if(parts.length>0)
                            filename=parts[parts.length-1];
                        if(!filename.equals("")){
                            download(filename, url, type);
                        }
                    }
                });
                dissmissProgressDialog();
                return convertView;
            }
            else {
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_right_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.picture.setImageResource(R.drawable.ic_mic_black_24dp);

                final String url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog("语音读取中，请稍候...");
                        String[] parts=url.split("/");
                        String filename="";
                        if(parts.length>0)
                            filename=parts[parts.length-1];
                        if(!filename.equals("")){
                            download(filename, url, type);
                        }
                    }
                });
                dissmissProgressDialog();
                return convertView;
            }
        }
        else if (type==ChatMsgType.VIDEO){

            if (data.isLeft()){
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_left_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_left_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.picture.setImageResource(R.drawable.ic_videocam_black_24dp);

                final String url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog("视频读取中，请稍候...");
                        String[] parts=url.split("/");
                        String filename="";
                        if(parts.length>0)
                            filename=parts[parts.length-1];
                        if(!filename.equals("")){
                            download(filename, url, type);
                        }
                    }
                });

                dissmissProgressDialog();
                return convertView;
            }
            else {
                PictureViewHolder viewHolder= new PictureViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.list_view_chat_right_picture, null);
                viewHolder.head = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_head);
                viewHolder.picture = (ImageView)convertView.findViewById(R.id.list_view_chat_right_picture_picture);
                imageLoader.loadBitmap(context, data.getHead(), viewHolder.head, 0);
                viewHolder.picture.setImageResource(R.drawable.ic_videocam_black_24dp);

                final String url = data.getUrl();
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog("视频读取中，请稍候...");
                        String[] parts=url.split("/");
                        String filename="";
                        if(parts.length>0)
                            filename=parts[parts.length-1];
                        if(!filename.equals("")){
                            download(filename, url, type);
                        }
                    }
                });
                dissmissProgressDialog();
                return convertView;
            }
        }
        return null;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dissmissProgressDialog();
                    PictureSelector.create((Activity) context).externalPictureAudio((String)msg.obj);
                    break;
                case 1:
                    dissmissProgressDialog();
                    PictureSelector.create((Activity) context).externalPictureVideo((String)msg.obj);
                    break;
            }
        }
    };

    private void download(final String fileName, final String url, final ChatMsgType type){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    String filePath = getFilePath(fileName);
                    File file = new File(filePath);
                    if(!file.exists()){
                        file.createNewFile();

                        InputStream inputStream = null;
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.setDoInput(true);
                        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            inputStream = con.getInputStream();
                        }

                        OutputStream fileOut=new FileOutputStream(file);

                        byte[] buffer=new byte[1024*8];
                        int length=0;
                        while((length=inputStream.read(buffer))!=-1){
                            fileOut.write(buffer, 0, length);
                        }
                        fileOut.flush();
                        fileOut.close();
                    }

                    Message msg = new Message();
                    switch (type){
                        case VOICE:
                            msg.what = 0;
                            break;
                        case VIDEO:
                            msg.what = 1;
                            break;
                    }
                    msg.obj = filePath;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private String getFilePath(String filename) {
        String filePath="";
        File appCacheDir=null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),"ChinaBank/cache/media");
            //目录是否存在
            if (!appCacheDir.exists())
                appCacheDir.mkdirs();
            filePath=appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
        if (appCacheDir==null){ //没有SD卡
            appCacheDir = context.getDir("media", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
         Log.e("getFilePath", filePath);
        return filePath;
    }

    private class TextViewHolder{
        ImageView head;
        TextView text;
    }

    private class PictureViewHolder{
        ImageView head;
        ImageView picture;
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(message);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
