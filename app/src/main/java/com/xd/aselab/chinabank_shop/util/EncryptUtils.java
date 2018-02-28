package com.xd.aselab.chinabank_shop.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import java.security.Key;

import android.util.Base64;

/**
 * Created by xinye on 2017/7/26.
 *  3des加密/解密算法
 */

public class EncryptUtils {

    public static final String AppKey = "Eyt57BCZ2RiQ9CDx";
    public static final String AppSecret = "ZeteLs3fNBr5";

    private static void example() {
        String key = "shZHh9Qy4zCe7RKV";
        String str1 = "9527";//需要加密的字符串
        String str2 = "DjaFjCdjhaC/jjnuXqVseg==";//需要解密的字符串

        try {
            //加密
            String encryptString = encrypt(key, str1.getBytes());
            //解密
            String decryptString = decrypt(key, Base64.decode(str2,Base64.DEFAULT));

            System.out.println(encryptString);
            System.out.println(decryptString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //加密算法
    //key：加密的key
    //data：需要加密的数据
    public static String encrypt(String key, byte[] data) throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(getKeyByte(key));
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);

        return new String(Base64.encode(bOut,Base64.DEFAULT)).trim();
    }


    //解密算法
    //key：解密的key
    //data：需要解密的数据
    public static String decrypt(String key, byte[] data) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(getKeyByte(key));
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, deskey);

        byte[] bOut = cipher.doFinal(data);

        return new String(bOut, "UTF-8").trim();
    }


    //如果key的长度超过24，则截取24位，不足24位则补“0”凑齐24位
    private static byte[] getKeyByte(String key) {
        if (key == null) {
            return new byte[0];
        }

        int lenght = key.length();
        if (lenght >= 24) {
            return key.substring(0, 24).getBytes();
        } else {
            for (int i = 0; i < (24 - lenght); i++) {
                key += "0";
            }
            return key.getBytes();
        }
    }

}
