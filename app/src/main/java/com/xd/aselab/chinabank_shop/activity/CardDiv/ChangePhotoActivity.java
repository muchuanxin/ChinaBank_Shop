package com.xd.aselab.chinabank_shop.activity.CardDiv;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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

import java.io.InputStream;

public class ChangePhotoActivity extends AppCompatActivity implements ImageSettingUtil.ImageUploadDelegate {
    private SharePreferenceUtil spu;
    private RelativeLayout back;
    private TextView select;
    private ImageView iv_head_photo;
    private ImageLoader imageLoader;

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
        if (getIntent().getStringExtra("jump").equals("my_info")) {
            imageLoader.loadBitmap(ChangePhotoActivity.this, spu.getPhotoUrl(), iv_head_photo, R.drawable.portrait);
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
                            case 0: {
                                intent = new Intent(ChangePhotoActivity.this, AlbumActivity.class);
                                startActivityForResult(intent, 3);
                            }
                            break;
                            case 1: {
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(openCameraIntent, 0x000001);
                            }
                            break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_PICTURE:
                Bimp.tempSelectBitmap.clear();
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    takePhoto.setImagePath(FileUtils.SDPATH + fileName + ".JPEG");
                    Bimp.tempSelectBitmap.add(takePhoto);
                    Log.i("kmj", "-----paths.get(i).jpg------------" + Bimp.tempSelectBitmap.get(0).getImagePath());
                }
                uploadPicture();
                break;
            case Constants.LOCAL_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    uploadPicture();
                }
                break;
            default:
                break;
        }
    }

    private void uploadPicture() {
        // TODO Auto-generated method stub
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                if (Bimp.tempSelectBitmap.size() > 1) {
                    Looper.prepare();
                    Toast.makeText(ChangePhotoActivity.this, "您最多只能选择一张图片", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Log.i("kmj", "-----p1111--------");
                    for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                        ImageVo imageVo = new ImageVo();
                        imageVo.setImagePath(Bimp.tempSelectBitmap.get(i).getImagePath());
                        String str = spu.getAccount().toString() + "_"
                                + System.currentTimeMillis() + "";
                        imageVo.setImageName(str);
                        PostParameter[] postParams;

                        if (getIntent().getStringExtra("jump").equals("my_info")) {
                            postParams = new PostParameter[3];
                            //这个要改
                            postParams[0] = new PostParameter("imageName", str + ".png");
                            postParams[1] = new PostParameter("account", spu.getAccount());
                            postParams[2] = new PostParameter("type", spu.getUserType());
                            InputStream is = ImageSettingUtil.compressJPG(Bimp.tempSelectBitmap.get(0).getImagePath());
                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.uploadHeadImage);
                        } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                            postParams = new PostParameter[2];
                            postParams[0] = new PostParameter("account", spu.getAccount());
                            postParams[1] = new PostParameter("group_id", getIntent().getStringExtra("group_id") + "");
                            Log.d("Dorise final  groupid ", getIntent().getStringExtra("group_id") + "");
                            InputStream is = ImageSettingUtil.compressJPG(Bimp.tempSelectBitmap.get(0).getImagePath());
                            ImageSettingUtil.uploadImage(ChangePhotoActivity.this, i, postParams, is, Constants.ChangeGroupHeadPhoto);
                        }
                    }

                }
            }
        }.start();

    }

    @Override
    public void setUploadProgress(int index, double x) {

    }

    @Override
    public void getRecodeFromServer(int index, String reCode) {
        try {
            JSONObject jb = new JSONObject(reCode);
            String status = jb.getString("status");
            Log.d("Dorise status", "-----" + status);
            Log.d("Dorise message", "-----" + jb.getString("message"));
            if (status.equalsIgnoreCase("true") && index == 0) {
                String imageUrl = jb.getString("image_url");
                Looper.prepare();
                if (getIntent().getStringExtra("jump").equals("my_info")) {
                    spu.setHead_image(imageUrl);
                    spu.setPhotoUrl(imageUrl);
                } else if (getIntent().getStringExtra("jump").equals("group_head")) {
                    //head_image保存群头像
                    spu.setGroup_Head_image(imageUrl);
                }
                Toast.makeText(ChangePhotoActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                //自己粘的
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
    /*@Override
    public void getRecodeFromServer(int index, String reCode, String imageUrl) {
        if (reCode.equalsIgnoreCase("true") && index == 0) {
            Looper.prepare();
            spu.setPhotoUrl(imageUrl);
            Toast.makeText(ChangePhotoActivity.this,"上传成功",Toast.LENGTH_LONG).show();
            SharePreferenceUtil sp;
            sp=new SharePreferenceUtil(ChangePhotoActivity.this,"user");
            sp.setHead_image(imageUrl);
            finish();
            Looper.loop();
//			sp.setHeadPhoto(headPhoto);
        } else{
            Looper.prepare();
            Toast.makeText(ChangePhotoActivity.this,"上传失败，请重试",Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }*/
}

