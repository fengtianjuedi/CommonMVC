package com.wufeng.latte_core.card;

import android.content.Context;

import com.vanstone.trans.api.SystemApi;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.config.PosModel;

public class PosDeviceService {
    public static void Init(Context context){
        if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.AISINOA90){
            SystemApi.SystemInit_Api(0, new byte[0], context);
        }
    }

    public static void Exit(){
        if (ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL) == PosModel.AISINOA90){
            SystemApi.SystemExit_Api();
        }
    }
}
