package com.xd.aselab.chinabank_shop.chat.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig;
import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.MainActivity;
import com.xd.aselab.chinabank_shop.chat.adapter.ChatAdapter;
import com.xd.aselab.chinabank_shop.chat.entity.ChatMsgEntity;
import com.xd.aselab.chinabank_shop.chat.entity.ChatMsgType;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageSettingUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

public class ChatActivity extends AppCompatActivity implements ImageSettingUtil.ImageUploadDelegate{

    private String TAG = this.getClass().getSimpleName();

    private RelativeLayout back;
    private EditText input_text;
    private Button send_btn;
    private ListView listView;
    private TextView title;
    private ImageView select_picture;
    private ImageView take_photo;
    private ImageView record_voice;
    private ImageView record_video;
    private ProgressDialog progDialog = null;

    private SharePreferenceUtil spu;
    private Handler handler;
    private List<ChatMsgEntity> list;
    private ChatAdapter chatAdapter;
    private List<LocalMedia> selectList = new ArrayList<>();
    private SimpleDateFormat format;

    private String receiver;
    private String receiver_name;
    private String receiver_head;

    private String sender;
    private String sender_name;
    private String sender_head;

    private String input;
    private String time;
    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        intiData();
        initViews();
        initSmallVideo();
        registerMessageReceiver();

    }

    private void intiData(){
        spu = new SharePreferenceUtil(ChatActivity.this, "user");
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sender = spu.getAccount();

        switch (spu.getUserType()){
            case "shop":
                sender_name = spu.getShopName();
                break;
            case "worker_2":
                sender_name = spu.getWorkerName();
                break;
        }
//        sender_name = spu.getName();
        sender_head = spu.getHead_image();

        Intent get_intent = getIntent();

        receiver = get_intent.getStringExtra("receiver");
        receiver_name = get_intent.getStringExtra("receiver_name");
        receiver_head = get_intent.getStringExtra("receiver_head");
        /*try {

            if(get_intent.getStringExtra("receiver")==null){
                String extra = spu.getChatExtra();
                JSONObject json = new JSONObject(extra);
                String str_extra = json.getString("extra");
                JSONObject json_extra = new JSONObject(str_extra);
                receiver = json_extra.getString("sender");
                receiver_name = json_extra.getString("sender_name");
                receiver_head = json_extra.getString("sender_head");
            }else {
                receiver = get_intent.getStringExtra("receiver");
                receiver_name = get_intent.getStringExtra("receiver_name");
                receiver_head = get_intent.getStringExtra("receiver_head");
            }
            Log.e("www","receiver:---"+receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    //历史聊天记录
                    case 0 :
                        try {
                            dissmissProgressDialog();
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e(TAG, "reCode---"+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ChatActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    list = new ArrayList<>();
                                    JSONArray chat_log = json.getJSONArray("chat_log");
                                    for (int i=0; i<chat_log.length(); i++){
                                        JSONObject temp = chat_log.getJSONObject(i);
                                        ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                                        if (sender.equals(temp.getString("sender"))){
                                            chatMsgEntity.setLeft(false);
                                            chatMsgEntity.setAccount(sender);
                                            chatMsgEntity.setName(sender_name);
                                            chatMsgEntity.setHead(sender_head);
                                        }
                                        else {
                                            chatMsgEntity.setLeft(true);
                                            chatMsgEntity.setAccount(receiver);
                                            chatMsgEntity.setName(receiver_name);
                                            chatMsgEntity.setHead(receiver_head);
                                        }
                                        chatMsgEntity.setTime(temp.getString("time"));
                                        chatMsgEntity.setType(ChatMsgType.getType(temp.getString("type")));
                                        chatMsgEntity.setContent(temp.getString("content"));
                                        chatMsgEntity.setUrl(temp.getString("url"));
                                        list.add(chatMsgEntity);
                                    }
                                    chatAdapter = new ChatAdapter(list, ChatActivity.this);
                                    listView.setAdapter(chatAdapter);
                                } else {
                                    Log.e("Login_Activity", ChatActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Log.e("Login_Activity", "reCode为空");
                                if (ConnectUtil.isNetworkAvailable(ChatActivity.this)){
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.server_repairing), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //发送文字
                    case 1 :
                        try {
                            dissmissProgressDialog();
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e(TAG, "reCode---"+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    Toast.makeText(ChatActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    input_text.setText("");
                                    ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                                    chatMsgEntity.setLeft(false);
                                    chatMsgEntity.setAccount(sender);
                                    chatMsgEntity.setName(sender_name);
                                    chatMsgEntity.setType(ChatMsgType.TEXT);
                                    chatMsgEntity.setHead(sender_head);
                                    chatMsgEntity.setContent(input);
                                    chatMsgEntity.setTime(time);
                                    list.add(chatMsgEntity);
                                    chatAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("Login_Activity", ChatActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Log.e("Login_Activity", "reCode为空");
                                if (ConnectUtil.isNetworkAvailable(ChatActivity.this)){
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.server_repairing), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //发送图片、语音、视频
                    case 2 :
                        try {
                            String reCode = (String) msg.obj;
                            if (reCode != null) {
                                Log.e(TAG, "reCode---"+reCode);
                                JSONObject json = new JSONObject(reCode);
                                String status = json.getString("status");
                                if ("false".equals(status)) {
                                    dissmissProgressDialog();
                                    Toast.makeText(ChatActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if ("true".equals(status)) {
                                    ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
                                    chatMsgEntity.setLeft(false);
                                    chatMsgEntity.setAccount(sender);
                                    chatMsgEntity.setName(sender_name);
                                    chatMsgEntity.setType(ChatMsgType.getType(json.getString("type")));
                                    chatMsgEntity.setHead(sender_head);
                                    chatMsgEntity.setUrl(json.getString("url"));
                                    chatMsgEntity.setTime(time);
                                    list.add(chatMsgEntity);
                                    chatAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("Login_Activity", ChatActivity.this.getResources().getString(R.string.status_exception));
                                }
                            } else {
                                Log.e("Login_Activity", "reCode为空");
                                if (ConnectUtil.isNetworkAvailable(ChatActivity.this)){
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(ChatActivity.this, ChatActivity.this.getResources().getString(R.string.server_repairing), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Log.e("Login_Activity",ChatActivity.this.getResources().getString(R.string.handler_what_exception));
                        break;
                }
            }
        };
    }

    private void initViews(){

        back = (RelativeLayout) findViewById(R.id.act_chat_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView) findViewById(R.id.act_chat_title);
        title.setText(receiver_name);

        input_text = (EditText) findViewById(R.id.chat_edit);

        send_btn = (Button) findViewById(R.id.chat_send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = input_text.getText().toString().trim();
                if (!"".equals(input)){
                    showProgressDialog("发送中...");
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            PostParameter[] params = new PostParameter[7];
                            time = format.format(new Date());
                            params[0] = new PostParameter("time", time);
                            params[1] = new PostParameter("sender", sender);
                            params[2] = new PostParameter("sender_name", sender_name);
                            params[3] = new PostParameter("receiver", receiver);
                            params[4] = new PostParameter("type", "TEXT");
                            params[5] = new PostParameter("content", input);
                            params[6] = new PostParameter("sender_head", sender_head);
                            String reCode = ConnectUtil.httpRequest(ConnectUtil.SingleChat, params, ConnectUtil.POST);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = reCode;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });

        select_picture = (ImageView) findViewById(R.id.select_picture);
        select_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(5)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(false)// 是否裁剪 true or false
                        .compress(false)// 是否压缩 true or false
                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        .isGif(true)// 是否显示gif图片 true or false
                        //.compressSavePath("/CustomPath")//压缩图片保存地址
                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                        //.minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                        //.videoQuality()// 视频录制质量 0 or 1 int
                        //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                        //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                        //.recordVideoSecond()//视频秒数录制 默认60s int
                        .forResult(Constants.ChatToSelectPicture);//结果回调onActivityResult code
            }
        });

        take_photo = (ImageView) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openCamera(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        //.theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        //.maxSelectNum(5)// 最大图片选择数量 int
                        //.minSelectNum(1)// 最小选择数量 int
                        //.imageSpanCount(4)// 每行显示个数 int
                        //.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        //.previewImage(true)// 是否可预览图片 true or false
                        //.previewVideo(true)// 是否可预览视频 true or false
                        //.enablePreviewAudio(true) // 是否可播放音频 true or false
                        //.isCamera(false)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(false)// 是否裁剪 true or false
                        .compress(false)// 是否压缩 true or false
                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        //.isGif(true)// 是否显示gif图片 true or false
                        //.compressSavePath("/CustomPath")//压缩图片保存地址
                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                        //.minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                        //.videoQuality()// 视频录制质量 0 or 1 int
                        //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                        //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                        //.recordVideoSecond()//视频秒数录制 默认60s int
                        .forResult(Constants.ChatToSelectPicture);//结果回调onActivityResult code
            }
        });

        record_voice = (ImageView) findViewById(R.id.record_voice);
        record_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openCamera(PictureMimeType.ofAudio())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        //.theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        //.maxSelectNum(5)// 最大图片选择数量 int
                        //.minSelectNum(1)// 最小选择数量 int
                        //.imageSpanCount(4)// 每行显示个数 int
                        //.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        //.previewImage(true)// 是否可预览图片 true or false
                        //.previewVideo(true)// 是否可预览视频 true or false
                        //.enablePreviewAudio(true) // 是否可播放音频 true or false
                        //.isCamera(false)// 是否显示拍照按钮 true or false
                        //.imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                        //.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        //.enableCrop(false)// 是否裁剪 true or false
                        //.compress(false)// 是否压缩 true or false
                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //.hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        //.isGif(true)// 是否显示gif图片 true or false
                        //.compressSavePath("/CustomPath")//压缩图片保存地址
                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                        //.previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                        //.minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                        //.videoQuality()// 视频录制质量 0 or 1 int
                        //.videoMaxSecond(15)// 显示多少秒以内的视频，音频也可适用 int
                        //.videoMinSecond(10)// 显示多少秒以内的视频，音频也可适用 int
                        //.recordVideoSecond()//视频秒数录制 默认60s int
                        .forResult(Constants.ChatToRecordVoice);//结果回调onActivityResult code
            }
        });

        record_video = (ImageView) findViewById(R.id.record_video);
        record_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openCamera(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        //.theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        //.maxSelectNum(5)// 最大图片选择数量 int
                        //.minSelectNum(1)// 最小选择数量 int
                        //.imageSpanCount(4)// 每行显示个数 int
                        //.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        //.previewImage(true)// 是否可预览图片 true or false
                        //.previewVideo(true)// 是否可预览视频 true or false
                        //.enablePreviewAudio(true) // 是否可播放音频 true or false
                        //.isCamera(false)// 是否显示拍照按钮 true or false
                        //.imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                        //.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        //.enableCrop(false)// 是否裁剪 true or false
                        //.compress(false)// 是否压缩 true or false
                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //.hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        //.isGif(true)// 是否显示gif图片 true or false
                        //.compressSavePath("/CustomPath")//压缩图片保存地址
                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(false)// 是否开启点击声音 true or false
                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                        //.previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        //.cropCompressQuality()// 裁剪压缩质量 默认90 int
                        //.minimumCompressSize(100)// 小于100kb的图片不压缩
                        //.synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                        .videoQuality(1)// 视频录制质量 0 or 1 int
                        //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                        //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                        .recordVideoSecond(300)//视频秒数录制 默认60s int
                        .forResult(Constants.ChatToRecordVideo);//结果回调onActivityResult code
            }
        });

        listView = (ListView) findViewById(R.id.listview_chat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        showProgressDialog("请稍候...");
        initListView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ChatToSelectPicture:
                    // 图片选择结果回调
                    showProgressDialog("图片上传中...");
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (int i=0; i<selectList.size(); i++) {
                        final LocalMedia media = selectList.get(i);
                        Log.e("图片------", media.getPath());
                        final String[] temp = media.getPath().split("\\.");
                        //Log.e("temp.length", ""+temp.length);
                        String extension = temp[temp.length-1];
                        final String fileName="singlechat"+"_"+sender+"_"+"to"+"_"+receiver+"_"+ System.currentTimeMillis()+"."+extension;
                        final int finalI = i;

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();

                                PostParameter[] params = new PostParameter[7];
                                time = format.format(new Date());
                                params[0] = new PostParameter("time", time);
                                params[1] = new PostParameter("sender", sender);
                                params[2] = new PostParameter("sender_name", sender_name);
                                params[3] = new PostParameter("receiver", receiver);
                                params[4] = new PostParameter("type", "PICTURE");
                                params[5] = new PostParameter("sender_head", sender_head);
                                params[6] = new PostParameter("fileName", fileName);

                                File file = new File(media.getPath());
                                InputStream inputStream = null;
                                try {
                                    inputStream = new FileInputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                ImageSettingUtil.uploadImage(ChatActivity.this, finalI, params, inputStream, ConnectUtil.SingleChat+"?");
                            }
                        }.start();

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                copyFile(new File(media.getPath()), new File(getImagePath(fileName)));
                            }
                        }.start();
                    }
                    break;
                case Constants.ChatToRecordVoice:
                    showProgressDialog("语音上传中...");
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (int i=0; i<selectList.size(); i++) {
                        final LocalMedia media = selectList.get(i);
                        Log.e("语音------", media.getPath());
                        final String[] temp = media.getPath().split("\\.");
                        //Log.e("temp.length", ""+temp.length);
                        String extension = temp[temp.length-1];
                        final String fileName="singlechat"+"_"+sender+"_"+"to"+"_"+receiver+"_"+ System.currentTimeMillis()+"."+extension;
                        final int finalI = i;

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();

                                PostParameter[] params = new PostParameter[7];
                                time = format.format(new Date());
                                params[0] = new PostParameter("time", time);
                                params[1] = new PostParameter("sender", sender);
                                params[2] = new PostParameter("sender_name", sender_name);
                                params[3] = new PostParameter("receiver", receiver);
                                params[4] = new PostParameter("type", "VOICE");
                                params[5] = new PostParameter("sender_head", sender_head);
                                params[6] = new PostParameter("fileName", fileName);

                                File file = new File(media.getPath());
                                InputStream inputStream = null;
                                try {
                                    inputStream = new FileInputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                ImageSettingUtil.uploadImage(ChatActivity.this, finalI, params, inputStream, ConnectUtil.SingleChat+"?");
                            }
                        }.start();

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                copyFile(new File(media.getPath()), new File(getMediaPath(fileName)));
                            }
                        }.start();
                    }
                    break;
                case Constants.ChatToRecordVideo:
                    showProgressDialog("视频上传中...");
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (int i=0; i<selectList.size(); i++) {
                        final LocalMedia media = selectList.get(i);
                        Log.e("视频------", media.getPath());
                        final String[] temp = media.getPath().split("\\.");
                        //Log.e("temp.length", ""+temp.length);
                        String extension = temp[temp.length-1];
                        final String fileName="singlechat"+"_"+sender+"_"+"to"+"_"+receiver+"_"+ System.currentTimeMillis()+"."+extension;
                        final int finalI = i;

                        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                        final LocalMediaConfig config = buidler
                                .setVideoPath(media.getPath())
                                .captureThumbnailsTime(1)
                                .doH264Compress(new AutoVBRMode())
                                .setFramerate(10)
                                .setScale(1.0f)
                                .build();
                        final OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();

                        if (onlyCompressOverBean.isSucceed()){
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();

                                    PostParameter[] params = new PostParameter[7];
                                    time = format.format(new Date());
                                    params[0] = new PostParameter("time", time);
                                    params[1] = new PostParameter("sender", sender);
                                    params[2] = new PostParameter("sender_name", sender_name);
                                    params[3] = new PostParameter("receiver", receiver);
                                    params[4] = new PostParameter("type", "VIDEO");
                                    params[5] = new PostParameter("sender_head", sender_head);
                                    params[6] = new PostParameter("fileName", fileName);

                                    File file = new File(onlyCompressOverBean.getVideoPath());
                                    InputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(file);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    ImageSettingUtil.uploadImage(ChatActivity.this, finalI, params, inputStream, ConnectUtil.SingleChat+"?");
                                }
                            }.start();

                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    copyFile(new File(media.getPath()), new File(getMediaPath(fileName)));
                                }
                            }.start();
                        }


                    }
                    break;
            }
        }
    }

    private void initListView(){

        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[2];
                params[0] = new PostParameter("sender", sender);
                params[1] = new PostParameter("receiver", receiver);
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetHistoryChatLog, params, ConnectUtil.POST);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = reCode;
                handler.sendMessage(msg);
            }
        }.start();

    }

    public void initSmallVideo() {
        String filePath="";
        File appCacheDir=null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),"ChinaBank/compress_video/");
            //目录是否存在
            if (!appCacheDir.exists())
                appCacheDir.mkdirs();
            filePath=appCacheDir.getAbsolutePath()+ File.separator;
        }
        if (appCacheDir==null){ //没有SD卡
            appCacheDir = getDir("images", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath()+ File.separator;
        }
        //Log.e("getFilePath", filePath);
        JianXiCamera.setVideoCachePath(filePath);
        // 初始化拍摄，遇到问题可选择开启此标记，以方便生成日志
        JianXiCamera.initialize(false,null);
    }

    private void copyFile(File source, File dest){
        try {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(source);
                output = new FileOutputStream(dest);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } finally {
                input.close();
                output.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getMediaPath(String filename) {
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
            appCacheDir = getDir("media", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
        Log.e("getFilePath", filePath);
        return filePath;
    }

    private String getImagePath(String filename) {
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
            appCacheDir = getDir("images", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath()+ File.separator+filename;
        }
        Log.e("getFilePath", filePath);
        return filePath;
    }

    // 上传过程中需要给更新的进度
    // index表示第几个进度条，x表示进度,当x为负数时，表示上传出现问题
    @Override
    public void setUploadProgress(int index, double x) {
        //Log.e("index-----",""+index);
        //Log.e("x------",""+x);
    }

    // 上传回调方法
    @Override
    public void getRecodeFromServer(int index, String reCode) {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = reCode;
        handler.sendMessage(msg);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (MainActivity.MESSAGE_UPDATE_MESSAGCHAT_ACTION.equals(intent.getAction())) {
                Log.e("wenqianru", "刷新聊天消息列表");
                initListView();
            }
        }
    }

    //注册消息接收器
    private MessageReceiver mMessageReceiver;
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MainActivity.MESSAGE_UPDATE_MESSAGCHAT_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
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
