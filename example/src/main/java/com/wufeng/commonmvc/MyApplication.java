package com.wufeng.commonmvc;

import android.app.Application;

import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.wufeng.latte_core.config.ConfigManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigManager.getInstance()
                .withIcon(new FontAwesomeModule())
                .withContext(getApplicationContext())
                .withHost("http://192.168.200.90:9008")
                .config();
    }
}
