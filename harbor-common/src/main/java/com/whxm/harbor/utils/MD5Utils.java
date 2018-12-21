package com.whxm.harbor.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Updated by Ftibw on 2018/6/14 22:46.
 */
public class MD5Utils {

    private static String getMD5Str(String str) {

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {

            System.out.println("NoSuchAlgorithmException caught!");

            return null;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer sb = new StringBuffer();

        /*
         for (byte aByteArray : byteArray) {
            String hexString = Integer.toHexString(0xFF & aByteArray);
         if (hexString.length() == 1)
            sb.append("0").append(hexString);
         else
            sb.append(hexString);
         }
         */
        IntStream.range(0, byteArray.length)
                .mapToObj(i -> Integer.toHexString(0xFF & byteArray[i]))
                .forEach(hexString -> {
                    if (hexString.length() == 1)
                        sb.append("0").append(hexString);
                    else
                        sb.append(hexString);
                });

        return sb.toString();
    }

    public static String getMD5Byte(byte[] bytes) {

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(bytes);
        } catch (NoSuchAlgorithmException e) {

            System.out.println("NoSuchAlgorithmException caught!");

            return null;
        }

        byte[] byteArray = messageDigest.digest();


        StringBuffer sb = new StringBuffer();

        IntStream.range(0, byteArray.length)
                .mapToObj(i -> Integer.toHexString(0xFF & byteArray[i]))
                .forEach(hexString -> {
                    if (hexString.length() == 1)
                        sb.append("0").append(hexString);
                    else
                        sb.append(hexString);
                });

        return sb.toString();
    }

    /**
     * MD5 32位加密
     *
     * @param sourceStr 原字符串
     * @return 加密后的字符串
     */
    public static String MD5(String sourceStr) {
        return getMD5Str(sourceStr);
    }

    /**
     * MD5 16位加密
     *
     * @param sourceStr 原字符串
     * @return 加密后的字符串
     */
    public static String MD5F16(String sourceStr) {
        return Objects.requireNonNull(getMD5Str(sourceStr)).substring(8, 24);
    }


    /*public static void main(String[] args) {
        String ss = MD5("e10adc3949ba59abbe56e057f20f883e");
        System.out.println();
    }*/
}
