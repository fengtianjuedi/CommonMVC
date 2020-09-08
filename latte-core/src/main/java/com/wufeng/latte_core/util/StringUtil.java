package com.wufeng.latte_core.util;

import androidx.annotation.NonNull;

public class StringUtil {
    /**
     * 字符串左填充
     * @param src 源字符串
     * @param ch 填充字符
     * @param length 总长度
     * @return 填充后的字符串
     */
    public static String paddingLeft(@NonNull String src, char ch, int length){
         StringBuilder sb = new StringBuilder();
        int size = src.length();
        for (int i = 0; i < length - size; i++ ){
            sb.append(ch);
        }
        return sb.append(src).toString();
    }

    /**
     * 字符串右填充
     * @param src 源字符串
     * @param ch 填充字符
     * @param length 总长度
     * @return 填充后的字符串
     */
    public static String paddingRight(@NonNull String src, char ch, int length){
        StringBuilder sb = new StringBuilder(src);
        int size = src.length();
        for (int i = 0; i < length - size; i++ ){
            sb.append(ch);
        }
        return sb.toString();
    }
}
