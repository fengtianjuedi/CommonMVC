package com.wufeng.commonmvc;

import android.app.Application;

import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.device.PosDeviceService;
import com.wufeng.latte_core.device.card.LiandiA8ReadCard;
import com.wufeng.latte_core.net.interceptors.DebugInterceptor;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigManager.getInstance()
                .withIcon(new FontAwesomeModule())
                .withContext(getApplicationContext())
                .withHost("http://nongxintest.nongxintop.com:9008")
                //.withHost("http://192.168.200.90:9008")
                .withInterceptor(new DebugInterceptor())
                .withPosModel(LiandiA8ReadCard.LIANDIA8)
                .config();
        PosDeviceService.Init(getApplicationContext());
    }
}
