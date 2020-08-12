package com.wufeng.latte_core.util;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;

import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;

public class SharedPreferenceUtil {
    private static final String SHAREDPREFERENCENAME = "global"; //全局共享配置文件
    private static SharedPreferences SHAREDPREFERENCES =
            ((Context)ConfigManager.getInstance().getConfig(ConfigKeys.CONTEXT)).getSharedPreferences(SHAREDPREFERENCENAME, Context.MODE_PRIVATE);
    private static SharedPreferences getAppPreference() {
        return SHAREDPREFERENCES;
    }

    public static void clearAppPreferences() {
        getAppPreference()
                .edit()
                .clear()
                .apply();
    }

    public static void removeAppProfile(String key) {
        getAppPreference()
                .edit()
                .remove(key)
                .apply();
    }

    //更新masterkey
    public static void setMasterKey(String value) {
        getAppPreference()
                .edit()
                .putString("masterKey", value)
                .apply();
    }

    //获取masterkey
    public static String getMasterKey(){
        return getAppPreference().getString("masterKey", "");
    }
}
