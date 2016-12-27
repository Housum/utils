package com.hotusm.utils;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 统一Md5工具<br>
 *
 * @author qiesai
 * @create 2016-3-8
 *
 */
public class MD5Util {

    private static char[] Digit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    private MD5Util() {
    }

    /**
     * 根据给定的字节数组获取Md5后的字符<br>
     * 输入的byte数组不允许为null
     *
     * @param inputBytes
     * @return
     */
    public static String getMd5Sum(byte[] inputBytes) {
        if (inputBytes == null) {
            throw new IllegalArgumentException("Input Bytes Is Null");
        }

        // 获取标准的java Md5算法工具
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(inputBytes);

        byte[] md5Sum = digest.digest();
        // 算法1
        // StringBuilder sb = new StringBuilder();
        // for (int i = 0; i < 16; ++i) {
        // char[] ob = new char[] { Digit[md5sum[i] >> 4 & 15], Digit[md5sum[i]
        // & 15] };
        // String s = new String(ob);
        // sb.append(s);
        // }
        // 算法2
        int j = md5Sum.length;
        char[] finalValue = new char[j * 2];
        int k = 0;

        for (int i = 0; i < j; ++i) {
            byte encoded = md5Sum[i];
            finalValue[k++] = Digit[encoded >> 4 & 15];
            finalValue[k++] = Digit[encoded & 15];
        }

        return new String(finalValue);
    }

    /**
     * 根据给定的字符串计算其Md5<br>
     * 输入的字符串不允许为null<br>
     *
     * @param inputStr
     * @return
     */
    public static String getMd5Sum(String inputStr) {
        // 不允许为null
        if (inputStr == null) {
            throw new IllegalArgumentException("Input String Is Null !");
        }
        // 获取字符串字节流
        byte[] inputStrByte = null;
        try {
            inputStrByte = inputStr.getBytes("UTF-8");
            return getMd5Sum(inputStrByte);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 将key和要加密的字符串拼接后进行加密计<br>
     *
     * @param inputStr
     * @param key
     * @return
     */
    public static String getMd5Sum(String inputStr, String key) {
        String keyStr = inputStr + key;
        return MD5Util.getMd5Sum(keyStr);
    }

    /**
     * 将key和要加密的字符串用拼接串拼接后进行加密计<br>
     * @param inputStr
     * @param appendStr
     * @param key
     * @return
     */
    public static String getMd5Sum(String inputStr, String appendStr, String key) {
        String keyStr = inputStr + appendStr + key;
        return MD5Util.getMd5Sum(keyStr);
    }

    /**
     * 验证给定的字符串和要站在的签名是否一致<br>
     * 输入的字符串不允许为null
     *
     * @param text
     * @param sign
     * @return
     */
    public static boolean verify(String text, String sign) {
        String mysign = getMd5Sum(text);
        return mysign.equals(sign);
    }

    /**
     * 获取文件的Md5值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getMd5Sum(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");

            byte[] j = new byte[1024];

            while (true) {
                int fileLength = is.read(j);
                if (fileLength > 0) {
                    md5.update(j, 0, fileLength);
                }

                if (fileLength == -1) {
                    is.skip(0L);
                    break;
                }
            }

        } catch (Exception e) {
           throw new RuntimeException(e);

        } finally {
            is.close();
        }

        byte[] md5Sum = md5.digest();
        int len = md5Sum.length;
        char[] finalValue = new char[len * 2];
        int k = 0;

        for (int i = 0; i < len; ++i) {
            byte encoded = md5Sum[i];
            finalValue[k++] = Digit[encoded >> 4 & 15];
            finalValue[k++] = Digit[encoded & 15];
        }

        return new String(finalValue);
    }

    public static void main(String[] args){
        String pwd = UUID.randomUUID().toString();
        String salt = "654321";
        String pwdAfterMd5 = getMd5Sum(pwd);
        System.out.println("=====password after Md5======"+pwdAfterMd5);
        String pwdAfterSalt = getMd5Sum(pwdAfterMd5,salt);
        System.out.println("=====password after salt======" + pwdAfterSalt);

        System.out.println("===========generate uuid===========");
        for(int i = 0;i<3;i++){
            System.out.println(UUID.randomUUID());
        }

    }

}
