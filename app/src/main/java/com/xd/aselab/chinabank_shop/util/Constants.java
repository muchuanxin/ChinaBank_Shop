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
    public static final int EXIT_TO_LOGIN = 002;
    //me_uploadHeadPhoto
    public static String uploadHeadImage = ConnectUtil.UploadHeadImage + "?";
    public static String ChangeGroupHeadPhoto = ConnectUtil.ChangeGroupHeadPhoto + "?";

    public static final int ChatToSelectPicture  = 19945;
    public static final int ChatToRecordVoice  = 19946;
    public static final int ChatToRecordVideo  = 19947;

    public static String RECOMMEND =  "RECOMMEND"; //推荐
    public static String ConfirmInstallmentRecommend =  "ConfirmInstallmentRecommend"; //经理确认或拒绝业务之后给销售的推送
    public static String CHAT = "SINGLECHAT"; //单聊
    public static String GROUPCHAT = "GROUPCHAT";//群聊
    public static String CreateChatGroup = "CreateChatGroup";//创建群之后给群成员推送的消息类型
    public static String DissolveChatGroup = "DissolveChatGroup";//解散群之后给群成员推送的消息类型
    public static String InviteMemberToGroup = "InviteMemberToGroup";//群主拉人给被拉的人的推送
    public static String ExpelMemberFromGroup = "ExpelMemberFromGroup";//群主踢人给被踢的人的推送

    public static final int ActivityCompatRequestPermissionsCode = 1003;
}
