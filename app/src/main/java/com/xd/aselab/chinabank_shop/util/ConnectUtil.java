package com.xd.aselab.chinabank_shop.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 发起HTTP请求的工具类
 */
public class ConnectUtil {
    private static String DLTAG = "com.xd.connect.ConnectUtil";

    public static final String HOST_ERROR = "HOST_ERROR";
    public static String HTTP_RE_ERROE_CODE = "";

    public static final String UTF_8 = "UTF-8";
    public static final String POST = "POST";
    public static final String GET = "GET";

    private final static int READ_TIMEOUT = 3000;
    private final static int CONNECT_TIMEOUT = 3000;
    private final static int TEST_TIMEOUT = 3000;

    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";

    // public final static String API_HOST = "http://117.34.117.107/ChinaBank/Shop/";
    //public final static String API_HOST = "http://www.juyunjinrongapp.com/ChinaBank/Shop/";
//    public final static String API_HOST = "http://47.93.31.35/ChinaBank/Shop/";
//    public final static String API_HOST = "http://192.168.0.129/ChinaBank/Shop/";
    //public final static String API_HOST = "http://47.93.31.35/ChinaBank/Shop/";
    //47.93.31.35
    //public final static String API_HOST = "http://10.170.13.113:8080/ChinaBank/Shop/";
    //public final static String API_HOST = "http://javaapp.gotoip2.com/ChinaBank/Shop/";


    public final static String IP = "http://www.juyunjinrongapp.com/";
    //public final static String IP = "http://47.93.31.35/";
//    public final static String IP = "http://192.168.0.73:8080/";
    //public final static String IP = "http://192.168.0.129/";
    //public final static String IP="http://10.170.13.113:8080/";
    //public final static String IP = "http://javaapp.gotoip2.com/";
    public final static String SERVER = IP + "ChinaBank/";

    public final static String API_HOST = SERVER + "Shop/";
    public final static String CHAT = SERVER + "Chat/";
    public final static String API_Chat = SERVER+"Chat/";

    public final static String API_HOST_DOWNLOAD = SERVER + "download/";
    public final static String API_HOST_IMAGE = API_HOST + "images/";
    public final static String RelieveWorker = API_HOST + "RelieveWorker";
    public final static String CreateChatGroup = CHAT + "CreateChatGroup";
    public final static String GetMyCreateGroup = CHAT + "GetMyCreateGroup";
    public final static String GetMyJoinGroup = CHAT + "GetMyJoinGroup";
    public final static String GetGroupMember = CHAT + "GetGroupMember";
    public final static String DissolveChatGroup = CHAT + "DissolveChatGroup";
    public final static String LeaveChatGroup = CHAT + "LeaveChatGroup";
    public final static String ChangeGroupHeadPhoto = CHAT + "ChangeGroupHeadPhoto";
    public final static String ExpelMemberFromGroup = CHAT + "ExpelMemberFromGroup";
    public final static String InviteMemberToGroup = CHAT + "InviteMemberToGroup";

    public final static String RecommendInstallment = API_HOST + "RecommendInstallment";
    public final static String RecommendInstallmentNew = API_HOST + "RecommendInstallmentNew";
    public static String InstallmentWorkerMyPerformance = API_HOST + "InstallmentWorkerMyPerformance";
    public static String GetNotExchangeScore = API_HOST + "GetNotExchangeScore";
    public final static String COMMON = SERVER + "Common/";
    public static String UploadHeadImage = COMMON + "UploadHeadPhoto";
    public final static String CHINA_BANK_BENEFIT = COMMON + "GetActivity";
    public final static String CHINA_BANK_NETWORK = COMMON + "GetBankInformation";
    public static String IS_AVAILABLE = API_HOST + "Is_Available";
    public static String InstallmentWorkerRegister = API_HOST + "InstallmentWorkerRegister";
    public final static String GetRollingPicture = COMMON + "GetRollingPicture";
    public final static String WorkerChangerPsw = API_HOST + "WorkerChangerPsw";
    public final static String GetCreditEvaluation = API_HOST + "GetCreditEvaluation";
    public final static String GetCreditScoreLine = API_HOST + "GetCreditScoreLine";
    public final static String GetAll4SShop = API_HOST + "GetAll4SShop";
    public final static String WorkerChangeTel = API_HOST + "WorkerChangeTel";
    public final static String InstallmentWorkerMyRecommendList = API_HOST + "InstallmentWorkerMyRecommendList";
    public static String USER_LOGIN = API_HOST + "ShopLogin";
    public static String GET_CARD_STATISTICS = API_HOST + "GetCardStatistics";
    public static String GET_INDUSTRY_TYPE = API_HOST + "GetIndustryType";
    public static String REGISTER_SHOP = API_HOST + "RegisterShop";
    public static String REGISTER_WORKER = API_HOST + "RegisterWorker";
    public static String RELIEVE_SHOP = API_HOST + "RelieveShop";
    public static String SHOP_CHANGE_PSW = API_HOST + "ShopChangePsw";
    public static String SHOP_CHANGE_TEL = API_HOST + "ShopChangeTel";
    public static String WORKER_CHANGE_PSW = API_HOST + "WorkerChangerPsw";
    public static String WORKER_CHANGE_TEL = API_HOST + "WorkerChangeTel";
    public static String GET_ACCOUNT_TYPE = API_HOST + "GetAccountType";
    public static String GetShopClientVersion = API_HOST + "GetShopClientVersion";
    public static String Installment_Worker_My_Recommend_List = API_HOST + "InstallmentWorkerMyRecommendList";
    //全部营业员业绩
    public static String GET_MY_WORKER = API_HOST + "GetMyWorker";
    public static String SetShopSecureQuestion = API_HOST + "SetShopSecureQuestion";//设置商铺安全问题
    public static String SetWorkerSecureQuestion = API_HOST + "SetWorkerSecureQuestion";//设置营业员安全问题
    public static String SetInstallmentWorkerSecureQuestion = API_HOST + "SetInstallmentWorkerSecureQuestion";//设置4s店销售安全问题
    public static String GetShopSecureQuestion = API_HOST + "GetShopSecureQuestion";//获取商铺/营业员/4s店销售安全问题

    public static String SingleChat = API_Chat+"SingleChat";
    public static String GetHistoryChatLog = API_Chat+"GetHistoryChatLog";
    public static String GroupChat = API_Chat+"GroupChat";
    public static String GetGroupHistoryChatLog = API_Chat+"GetGroupHistoryChatLog";

    public static String httpRequest(String url, PostParameter[] postParams, String httpMethod) {
        InputStream input = null;
        String jsonSource = "";
        try {
            HttpURLConnection conn = null;
            OutputStream output = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setDoInput(true);
                // Log.i("mmm", "conn.getResponseCode():" + conn.getResponseCode());
                if (null != postParams || POST.equals(httpMethod)) {
                    conn.setRequestMethod(POST);
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Authorization", "Basic c2l0ZWFkbWluOjExMA==");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setConnectTimeout(CONNECT_TIMEOUT);
                    String postParam = "";
                    if (postParams != null) {
                        postParam = encodeParameters(postParams);
                    }
                    byte[] bytes = postParam.getBytes(UTF_8);
                    conn.setRequestProperty("Content-Length",
                            Integer.toString(bytes.length));

                    Log.i("mcx", "url----" + conn.getURL() + "?" + postParam);
                    output = conn.getOutputStream();
                    output.write(bytes);
                    output.flush();
                    output.close();
                }
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    input = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(
                            input, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(
                            inputStreamReader);
                    String temp = "";

                    while ((temp = bufferedReader.readLine()) != null) {
                        jsonSource += temp;
                    }
                    bufferedReader.close();
                    input.close();
                    Log.i("mmm", "jsonString=" + jsonSource);
                    return jsonSource;

                } else {
                    HTTP_RE_ERROE_CODE = String.valueOf(conn.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonSource = null;
            } finally {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonSource = null;
        } finally {

            return jsonSource;
        }

    }

    public static String encodeParameters(PostParameter[] postParams) {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < postParams.length; j++) {
            if (j != 0) {
                buf.append("&");
            }
            try {
                if (null != postParams[j]) {
                    if (null != postParams[j].getKey()) {
                        buf.append(
                                URLEncoder.encode(postParams[j].getKey(),
                                        "utf-8")).append("=");
                    }
                    if (null != postParams[j].getValue()) {
                        buf.append(URLEncoder.encode(postParams[j].getValue(),
                                "utf-8"));
                    }
                }
            } catch (java.io.UnsupportedEncodingException neverHappen) {
            }
        }
        return buf.toString();
    }

    public static String getEncode(String codeType, String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance(codeType);
            digest.reset();
            digest.update(content.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // System.out.println("connect: " + connect);
        if (connect == null) {
            return false;
        } else// get all network info
        {
            NetworkInfo[] info = connect.getAllNetworkInfo();
            // System.out.println("info: " + info);
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static InputStream httpRequest2(String url,
                                           PostParameter[] postParams, String httpMethod) {
        InputStream is = null;
        try {
            HttpURLConnection con = null;
            OutputStream osw = null;
            try {
                con = (HttpURLConnection) new URL(url).openConnection();
                con.setDoInput(true);
                if (null != postParams || POST.equals(httpMethod)) {
                    con.setRequestMethod(POST);
                    con.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    con.setDoOutput(true);
                    con.setReadTimeout(READ_TIMEOUT);
                    con.setConnectTimeout(CONNECT_TIMEOUT);

                    String postParam = "";
                    if (postParams != null) {
                        postParam = encodeParameters(postParams);
                    }
                    byte[] bytes = postParam.getBytes("UTF-8");

                    con.setRequestProperty("Content-Length",
                            Integer.toString(bytes.length));

                    Log.i("liuhaoxian", "url----" + con.getURL() + "?"
                            + postParam);
                    osw = con.getOutputStream();
                    osw.write(bytes);
                    osw.flush();
                    osw.close();
                }

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    is = con.getInputStream();
                } else {
                    HTTP_RE_ERROE_CODE = String.valueOf(con.getResponseCode());
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

    /**
     * 给服务器发送参数，不管返回
     */
    public static void httpPost(String url, PostParameter[] postParams, String httpMethod) {
        try {
            HttpURLConnection con = null;
            OutputStream osw = null;
            try {
                con = (HttpURLConnection) new URL(url).openConnection();
                con.setDoInput(true);
                if (null != postParams || POST.equals(httpMethod)) {
                    con.setRequestMethod(POST);
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setDoOutput(true);
                    con.setReadTimeout(READ_TIMEOUT);
                    con.setConnectTimeout(CONNECT_TIMEOUT);

                    String postParam = "";
                    if (null != postParams) {
                        //将参数进行编码防止中文乱码问题
                        postParam = encodeParameters(postParams);
                    }
                    byte[] bytes = postParam.getBytes("UTF-8");
                    con.setRequestProperty("Content-Length", Integer.toString(bytes.length));
                    osw = con.getOutputStream();
                    osw.write(bytes);
                    osw.flush();
                    osw.close();
                }
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证服务器是否可用
     */
    public static boolean isHostAvailable() {
        boolean net = false;
        HttpURLConnection con;
        int responseCode = -1;
        try {
            con = (HttpURLConnection) new URL(IS_AVAILABLE).openConnection();
            con.setReadTimeout(TEST_TIMEOUT);
            con.setConnectTimeout(TEST_TIMEOUT);
            con.setDoInput(true);
            responseCode = con.getResponseCode();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (HttpURLConnection.HTTP_OK == responseCode) {
            net = true;
        }
        return net;
    }

}
