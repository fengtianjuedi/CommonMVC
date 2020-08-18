package com.wufeng.latte_core.device.card;

import com.vanstone.trans.api.PiccApi;
import com.vanstone.trans.api.SystemApi;
import com.vanstone.utils.CommonConvert;
import com.wufeng.latte_core.util.ByteStringUtil;

/**
 * 艾体威尔A90非接IC卡读写
 */
public class AISINOA90ReadCard extends ReadCard {
    @Override
    public void read(ReadCardCallback callback) {
        int result = 0xff;
        result = PiccApi.PiccOpen_Api();
        if (result != 0){
            if (callback != null)
                callback.result(false, "打开设备失败");
            PiccApi.PiccClose_Api();
            SystemApi.SystemExit_Api();
            return;
        }
        byte []serialNo = new byte[16];
        byte []cardType = new byte[2];
        byte[] keyA = CommonConvert.hexStringToByte(ReadCard.KeyA);
        byte[] data = new byte[16];
        result = PiccApi.PiccCheck_Api('m', cardType, serialNo);
        if (result != 0){
            if (callback != null)
                callback.result(false, "寻卡失败");
            PiccApi.PiccClose_Api();
            return;
        }
        result = PiccApi.M1Authority_Api('A', 5, keyA);
        if (result != 0){
            if (callback != null)
                callback.result(false, "扇区认证失败");
            PiccApi.PiccClose_Api();
            return;
        }
        result = PiccApi.M1ReadBlock_Api(4, data);
        if (result != 0){
            if (callback != null)
                callback.result(false, "块数据读取失败");
            PiccApi.PiccClose_Api();
            return;
        }
        SystemApi.Beef_Api(4, 300);
        if (callback != null)
            callback.result(true, CommonConvert.bytes2HexString(data).substring(0, 20));
        PiccApi.PiccClose_Api();
    }

    @Override
    public void write(String data, WriteCardCallback callback) {
        int result = 0xff;
        result = PiccApi.PiccOpen_Api();
        if (result != 0){
            if (callback != null)
                callback.result(false, "打开设备失败");
            PiccApi.PiccClose_Api();
            SystemApi.SystemExit_Api();
            return;
        }
        byte []serialNo = new byte[16];
        byte []cardType = new byte[2];
        byte[] keyB = CommonConvert.hexStringToByte(ReadCard.KeyB);
        result = PiccApi.PiccCheck_Api('m', cardType, serialNo);
        if (result != 0){
            if (callback != null)
                callback.result(false, "寻卡失败");
            PiccApi.PiccClose_Api();
            return;
        }
        result = PiccApi.M1Authority_Api('B', 5, keyB);
        if (result != 0){
            if (callback != null)
                callback.result(false, "扇区认证失败");
            PiccApi.PiccClose_Api();
            return;
        }
        result = PiccApi.M1WriteBlock_Api(4, ByteStringUtil.hex2byte(data));
        if (result != 0){
            if (callback != null)
                callback.result(false, "块数据写入失败");
            PiccApi.PiccClose_Api();
            return;
        }
        SystemApi.Beef_Api(4, 300);
        if (callback != null)
            callback.result(true, "");
        PiccApi.PiccClose_Api();
    }
}
