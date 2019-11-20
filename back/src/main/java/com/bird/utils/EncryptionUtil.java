package com.bird.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 加密相关工具
 *
 * @author zhyyy
 */
public class EncryptionUtil {

    /**
     * md5
     *
     * @param source    源字符串
     * @param urlEncode 是否进行url编码
     * @return 加密后字符串
     */
    public static String md5(String source, boolean urlEncode) {
        try {
            if (urlEncode) {
                source = URLEncoder.encode(source, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return DigestUtils.md5Hex(source).toUpperCase();
    }

    /**
     * @param source 源字符串
     * @return 加密后字符串
     * @see EncryptionUtil#md5(String, boolean)
     */
    public static String md5(String source) {
        return md5(source, false);
    }

}
