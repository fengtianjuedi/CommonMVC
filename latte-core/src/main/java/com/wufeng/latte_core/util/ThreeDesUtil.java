package com.wufeng.latte_core.util;

import android.util.Base64;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 */
public class ThreeDesUtil {

    /**
     * 3DES加密
     *
     * @param key    密钥
     * @param srcStr 将加密的字符串
     */
    public static String encode3Des(String key, String srcStr) {
        if (key == null || srcStr == null)
            return null;
        byte[] keybyte = hex(key);
        byte[] src = srcStr.getBytes();
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
            //加密
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return Base64.encodeToString(c1.doFinal(src), Base64.NO_WRAP);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 转换成十六进制字符串
     */
    private static byte[] hex(String key) {
        byte[] md5 = DigestUtils.md5(key);
        String md5Hex = new String(Hex.encodeHex(md5));
        byte[] bkeys = md5Hex.getBytes();
        byte[] enk = new byte[24];
        System.arraycopy(bkeys, 0, enk, 0, enk.length);
        return enk;
    }
}
