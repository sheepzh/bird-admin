package com.bird.utils;

import java.util.Arrays;

/**
 * 字符串工具
 *
 * @author zhy
 */
public class StringUtil {
    public final static String EMPTY_STRING = "";
    public final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 驼峰转下划线
     *
     * @param camel 驼峰
     * @return 下划线
     */
    public static String camel2lowerCaseWithUnderline(String camel) {
        int len = camel.length();
        char[] chars = new char[len << 1];
        int i = 0;
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                chars[i++] = '_';
                chars[i++] = Character.toLowerCase(c);
            } else {
                chars[i++] = c;
            }
        }
        return new String(Arrays.copyOf(chars, i));
    }

    /**
     * 字节数组转16进制字符串.
     *
     * @param bytes 待转换字节
     * @return 转后字符串
     */
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[(b & 0xf0) >> 4]);
            sb.append(HEX_DIGITS[b & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 在数字的左右补零至指定长度
     *
     * @param number   原数字
     * @param length   补零后长度
     * @param leftFill true在左边补，false在右边
     * @return 补零后字符串
     */
    public static String alignZeroToLength(int number, int length, boolean leftFill) {
        StringBuilder nStr = new StringBuilder(String.valueOf(number));
        int l = nStr.length();
        if (length == l) {
            return nStr.toString();
        } else if (l < length) {
            while (l++ < length) {
                if (leftFill) {
                    nStr.insert(0, 0);
                } else {
                    nStr.append(0);
                }
            }
            return nStr.toString();
        } else {
            return leftFill ? nStr.substring(0, length) : nStr.substring(l - length, l);
        }
    }

    /**
     * unescape
     *
     * @param src escaped chars
     * @return unescaped chars
     */
    public static String unescape(String src) {
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src, lastPos, pos);
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}
