package com.xd.aselab.chinabank_shop.util;

/**
 * Created by Dorise on 2017/10/11.
 */

public class Constants {
    //upload_photo
    public final static int LOCAL_PHOTO = 3;
    public static final int TAKE_PICTURE = 0x000001;
    public static final int FROM_MAIN_ME_TO_LOGIN = 004;
    public static final int LOGIN_TO_MAIN_ME = 005;
    public static final int INFO_TO_MAIN = 007;
    //me_uploadHeadPhoto
    public static String uploadHeadImage = ConnectUtil.UploadHeadImage + "?";
    public static String ChangeGroupHeadPhoto = ConnectUtil.ChangeGroupHeadPhoto + "?";

    public static final int ChatToSelectPicture  = 19945;
    public static final int ChatToRecordVoice  = 19946;
    public static final int ChatToRecordVideo  = 19947;

    public static String RECOMMEND =  "RECOMMEND"; //推荐
    public static String CHAT = "SINGLECHAT"; //单聊
    public static String GROUPCHAT = "GROUPCHAT";//群聊

    public static final int ActivityCompatRequestPermissionsCode = 1003;
}
