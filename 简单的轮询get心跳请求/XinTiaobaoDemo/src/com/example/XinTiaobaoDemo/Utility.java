package com.example.XinTiaobaoDemo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 包名：com.example.XinTiaobaoDemo
 * 描述：
 * User 张伟
 * Date 2015/5/22 0022
 * Time 下午 3:41.
 * 修改日期：
 * 修改内容：
 */
public class Utility {
    public static String md5(String s)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return "";
    }
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static String toHexString(byte[] b)
    { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++)
        {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}
