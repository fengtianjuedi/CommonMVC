package com.wufeng.latte_core.util;

import java.io.File;
import java.io.IOException;

/**
 */
public class ByteStringUtil {
    /** 10进制转16进制 **/
    public static byte[] dec2Hex(int data){
        byte[] bBuffer = new byte[2];
        bBuffer[0] = (byte) (data >> 8 & 0xFF);
        bBuffer[1] = (byte) (data & 0xFF);
        //String hex = Integer.toHexString(dec);
        return bBuffer;
    }


    /**
     * 填充0，0在左边
     * @param data:填充的数据
     * @param num: 0的总个数
     * @return: 填充后的结果
     */
    public static String fillDataWith0(String data, int num){
        if(data.length()>num){
            data = data.substring(data.length()-num);
        }
        StringBuffer sb0 = new StringBuffer();
        for(int i=0;i<num;i++){
            sb0.append("0");
        }
        StringBuffer sb = new StringBuffer(sb0.toString());
        sb.replace(num-data.length(), num, data);
        return sb.toString();
    }

    /**
     * 十六进制字符串转二进制
     * @param str 十六进制串
     * @return
     */
    public static byte[] hex2byte(String str) { //字符串转二进制
        int len = str.length();
        String stmp = null;
        byte bt[] = new byte[len / 2];
        for (int n = 0; n < len / 2; n++) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte) (java.lang.Integer.parseInt(stmp, 16));
        }
        return bt;
    }

    /**
     * 二进制转十六进制字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) { //二行制转字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + "";
            }
        }
        return hs.toUpperCase();
    }
}

