package com.wufeng.commonmvc;

import android.app.Application;

import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.database.DatabaseManager;
import com.wufeng.latte_core.device.PosDevice;
import com.wufeng.latte_core.icon.IconFontModule;
import com.wufeng.latte_core.net.interceptors.DebugInterceptor;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigManager.getInstance()
                .withIcon(new FontAwesomeModule())
                .withIcon(new IconFontModule())
                .withContext(getApplicationContext())
                //.withHost("http://nongxintest.nongxintop.com:9008")
                .withHost("http://xidi.nongxintop.com:9008")
                //.withHost("http://192.168.10.18:9008")
                .withInterceptor(new DebugInterceptor())
                .withPosModel(PosDevice.LIANDIA8)
                .withPrintNumber(2)
                .config();
        PosDevice.Init(getApplicationContext());
        //初始化数据库
        DatabaseManager.getInstance().init(getApplicationContext());
    }


}
