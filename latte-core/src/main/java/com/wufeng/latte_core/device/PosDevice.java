package com.wufeng.latte_core.device;

import android.content.Context;

import com.vanstone.trans.api.SystemApi;
import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;

public class PosDevice {
    public static final String LIANDIA8 = "LIANDIA8"; //联迪A8
    public static final String AISINOA90 = "AISINOA90"; //艾体威尔A90
    public static void Init(Context context){
        if (AISINOA90.equals(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL))){
            SystemApi.SystemInit_Api(0, new byte[0], context);
        }
    }

    public static void Exit(){
        if (AISINOA90.equals(ConfigManager.getInstance().getConfig(ConfigKeys.P0SMODEL))){
            SystemApi.SystemExit_Api();
        }
    }
}
