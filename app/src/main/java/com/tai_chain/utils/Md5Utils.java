package com.tai_chain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用md5的算法进行加密的Utils
 */
public class Md5Utils {


    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("MD5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : secretBytes) {
            String sHex = Integer.toHexString(b & 0xff);
            if (sHex.length() == 1) {
                sHex = "0" + sHex;
            }
            sb.append(sHex);
        }
        return sb.toString();
    }

}


