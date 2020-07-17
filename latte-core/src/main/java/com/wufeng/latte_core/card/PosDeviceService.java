package com.wufeng.latte_core.card;

import android.content.Context;

import com.agripos.ui.SerialPortActivity;
import com.utils.CommonUtils;
import com.vanstone.trans.api.SystemApi;

public class PosDeviceService {
    public static void Init(Context context){
        if (CommonUtils.getIntStatic(SerialPortActivity.posModel, SerialPortActivity.posModel_liandiA8) == SerialPortActivity.posModel_aisinoA90){
            SystemApi.SystemInit_Api(0, new byte[0], context);
        }
    }

    public static void Exit(){
        if (CommonUtils.getIntStatic(SerialPortActivity.posModel, SerialPortActivity.posModel_liandiA8) == SerialPortActivity.posModel_aisinoA90){
            SystemApi.SystemExit_Api();
        }
    }
}
