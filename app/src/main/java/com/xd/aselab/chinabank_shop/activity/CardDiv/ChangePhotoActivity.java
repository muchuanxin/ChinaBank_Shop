package com.xd.aselab.chinabank_shop.activity.CardDiv;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.photo.AlbumActivity;
import com.xd.aselab.chinabank_shop.photo.Bimp;
import com.xd.aselab.chinabank_shop.photo.ImageItem;
import com.xd.aselab.chinabank_shop.util.ImageVo;
import com.xd.aselab.chinabank_shop.photo.util.FileUtils;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.ImageSettingUtil;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

public class ChangePhotoActivity extends AppCompatActivity implements ImageSettingUtil.ImageUploadDelegate {
    private SharePreferenceUtil spu;
    private RelativeLayout back;
    private TextView select;
    private ImageView iv_head_photo;
    private ImageLoader imageLoader;

    private ProgressDialog progDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        spu = new SharePreferenceUtil(ChangePhotoActivity.this, "user");
        imageLoader = ImageLoader.getInstance();

        back = (RelativeLayout) findViewById(R.id.act_change_head_photo_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select = (TextView) findViewById(R.id.act_change_head_photo_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoice();
            }
        });

        iv_head_photo = (ImageView) findViewById(R.id.user_headphoto);
        if (getIntent().getStringExtra("jump").equals("personal_info")) {
            imageLoader.loadBitmap(ChangePhotoActivity.this, spu.getHead_image(), iv_head_photo, R.drawable.portrait);
        } else if (getIntent().getStringExtra("jump").equals("group_head")) {
            imageLoader.loadBitmap(ChangePhotoActivity.this, getIntent().getStringExtra("head_image"), iv_head_photo, R.drawable.portrait);
        }

    }

    protected void showChoice() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePhotoActivity.this);
        builder.setItems(new String[]{"本机相册", "拍摄"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent;
                        switch (which) {
                            case 0:
                                /*intent = new Intent(ChangePhotoActivity.this, AlbumActivity.class);
                                startActivityForResult(intent, 3);*/
                                PictureSelector.create(ChangePhotoActivity.this)
                                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                        .theme(R.style.picture_benyedie_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                                        .maxSelectNum(1)// 最大图片选择数量 int
                                        .minSelectNum(1)// 最小选择数量 int
                                        .imageSpanCount(4)// 每行显示个数 int
                                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                        .previewImage(true)// 是否可预览图片 true or false
                                        .previewVideo(true)// 是否可预览视频 true or false
                                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                                        .isCamera(false)// 是否显示拍照按钮 true or false
                                        .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                        //可能用到         .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置该参数，会导致 .glideOverride()无效
                                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                                        .enableCrop(false)// 是否裁剪 true or false
                                        .compress(true)// 是否压缩 true or false
                                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                        .isGif(true)// 是否显示gif图片 true or false
                                        .compressSavePath(getFilePath())//压缩图片保存地址
                                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                        .openClickSound(false)// 是否开启点击声音 true or false
                                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                        .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                                        //.videoQuality()// 视频录制质量 0 or 1 int
                                        //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                                        //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                                        //.recordVideoSecond()//视频秒数录制 默认60s int
                                        .forResult(Constants.LOCAL_PHOTO);//结果回调onActivityResult code
                                break;
                            case 1:
                                /*Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(openCameraIntent, Constants.TAKE_PICTURE);*/
                                PictureSelector.create(ChangePhotoActivity.this)
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
                                        .compress(true)// 是否压缩 true or false
                                        //可能用到         .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                        //.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                        //.isGif(true)// 是否显示gif图片 true or false
                                        .compressSavePath(getFilePath())//压缩图片保存地址
                                        //.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
                                        //.circleDimmedLayer()// 是否圆形裁剪 true or false
                                        //.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                        //.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                        .openClickSound(false)// 是否开启点击声音 true or false
                                        //.selectionMedia(selectList)// 再次启动时是否传入上次已选图片 List<LocalMedia> list
                                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                        .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                                        //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                                        //.rotateEnabled() // 裁剪是否可旋转图片 true or false
                                        //.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
                                        //.videoQuality()// 视频录制质量 0 or 1 int
                                        //.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
                                        //.videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
                                        //.recordVideoSecond()//视频秒数录制 默认60s int
                                        .forResult(Constants.LOCAL_PHOTO);//结果回调onActivityResult code
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PICTURE:
                    /*Bimp.tempSelectBitmap.clear();
                    if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                        String fileName = String.valueOf(System.currentTimeMillis());
                        Bitmap bm = (Bitmap) data.getExtras().get("data");
                        FileUtils.saveBitmap(bm, fileName);

                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setBitmap(bm);
                        takePhoto.setImagePath(FileUtils.SDPATH + fileName + ".JPEG");
                        Bimp.tempSelectBitmap.add(takePhoto);
                        Log.i("kmj", "-----paths.get(i).jpg------------" + Bimp.tempSelectBitmap.get(0).getImagePath());
                    }*/
                    uploadPicture(data);
                    break;
                case Constants.LOCAL_PHOTO:
                    uploadPicture(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void uploadPicture(Intent data) {
        // TODO Auto-generated method stub
        showProgressDialog("图片上传中...");
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
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
            final int finalI = i;

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    PostParameter[] postParams = new PostParameter[2];

                    if (getIntent().getStringExtra("jump").equals("personal_info")) {
                        postParams[0] = new PostParameter("account", spu.getAccount());
                        postParams[1] = new PostParameter("type", "worker_2");
                        InputStream is = ImageSettingUtil.compressJPG(media.getPath());
                        if (media.isCompressed())
                            is = ImageSettingUtil.compressJPG(media.getCompressPath());
                        ImageSettingUtil.uploadImage(ChangePhotoActivity.this, finalI, postParams, is, Constants.uploadHeadImage);

                    } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                        postParams[0] = new PostParameter("account", spu.getAccount());
                        postParams[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                        InputStream is = ImageSettingUtil.compressJPG(media.getPath());
                        if (media.isCompressed())
                            is = ImageSettingUtil.compressJPG(media.getCompressPath());
                        ImageSettingUtil.uploadImage(ChangePhotoActivity.this, finalI, postParams, is, Constants.ChangeGroupHeadPhoto);

                    }

                }
            }.start();

        }
        /*new Thread() {

            @Override
            public void run() {
                super.run();
                if (Bimp.tempSelectBitmap.size() > 1) {
                    Looper.prepare();
                    Toast.makeText(ChangePhotoActivity.this, "您最多只能选择一张图片", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Log.i("kmj", "-----p1111--------");
                    for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                        *//*ImageVo imageVo = new ImageVo();
                        imageVo.setImagePath(Bimp.tempSelectBitmap.get(i).getImagePath());
                        String str = spu.getAccount().toString() + "_"
                                + System.currentTimeMillis() + "";
                        imageVo.setImageName(str);*//*
                        PostParameter[] postParams;
                        postParams = new PostParameter[2];
                        //这个要改
                        //postParams[0] = new PostParameter("imageName", str + ".png");
                        if (getIntent().getStringExtra("jump").equals("personal_info")) {
                            postParams[0] = new PostParameter("account", spu.getAccount());
                            postParams[1] = new PostParameter("type", "manager");
                            InputStream is = ImageSettingUtil.compressJPG(Bimp.tempSelectBitmap.get(i).getImagePath());
//                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.uploadHeadPhoto);
                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.uploadHeadPhoto);

                        } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                            postParams[0] = new PostParameter("account", spu.getAccount());
                            postParams[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                            InputStream is = ImageSettingUtil.compressJPG(Bimp.tempSelectBitmap.get(i).getImagePath());
//                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.uploadHeadPhoto);
                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.ChangeGroupHeadPhoto);

                        }
                    }

                }
            }
        }.start();*/

    }

    @Override
    public void setUploadProgress(int index, double x) {

    }

    @Override
    public void getRecodeFromServer(int index, String reCode) {
        try {
            JSONObject jb = new JSONObject(reCode);
            Log.d("Dorise  reCode",reCode);
            String status = jb.getString("status");
            if (status.equalsIgnoreCase("true") && index == 0) {
                String imageUrl = jb.getString("image_url");
                Looper.prepare();
                if (getIntent().getStringExtra("jump").equals("personal_info")) {
                    spu.setHead_image(imageUrl);
                } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                    spu.setGroup_Head_image(imageUrl);
                }
                Toast.makeText(ChangePhotoActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();
                Looper.loop();
//			sp.setHeadPhoto(headPhoto);
            } else {
                Looper.prepare();
                Toast.makeText(ChangePhotoActivity.this, "上传失败，请重试", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFilePath()
    {
        String filePath="";
        File appCacheDir=null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),"ChinaBank/compress/images");
            //目录是否存在
            if (!appCacheDir.exists())
                appCacheDir.mkdirs();
            filePath=appCacheDir.getAbsolutePath();
        }
        if (appCacheDir==null){ //没有SD卡
            appCacheDir = ChangePhotoActivity.this.getDir("images", Context.MODE_PRIVATE);
            filePath = appCacheDir.getAbsolutePath();
        }
        Log.i("liuhaoxian", filePath);
        return filePath;
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
}

