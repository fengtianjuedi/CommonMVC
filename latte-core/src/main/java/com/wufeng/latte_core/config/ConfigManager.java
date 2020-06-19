package com.wufeng.latte_core.config;

import android.app.Activity;
import android.content.Context;

import com.joanzapata.iconify.IconFontDescriptor;
import com.joanzapata.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 配置管理
 */
public class ConfigManager {
    private static final HashMap<Object, Object> CONFIGS = new HashMap<>();
    private static final ArrayList<IconFontDescriptor> ICONS = new ArrayList<>();

    private ConfigManager(){
    }

    private static class Holder{
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance(){return Holder.INSTANCE;}

    final HashMap<Object, Object> getConfigs(){return CONFIGS;}

    public final void config(){
        initIcons();
    }

    public final ConfigManager withHost(String host){
        CONFIGS.put(ConfigKeys.HOST, host);
        return  this;
    }

    public final ConfigManager withLoadDelay(long delay){
        CONFIGS.put(ConfigKeys.LOADER_DELAYED, delay);
        return this;
    }

    public final ConfigManager withActivity(Activity activity) {
        CONFIGS.put(ConfigKeys.ACTIVITY, activity);
        return this;
    }

    public final ConfigManager withContext(Context context){
        CONFIGS.put(ConfigKeys.CONTEXT, context);
        return this;
    }

    public final ConfigManager withIcon(IconFontDescriptor iconFontDescriptor){
        ICONS.add(iconFontDescriptor);
        return this;
    }

    //初始化图标库
    private void initIcons(){
        if (ICONS.size() > 0){
            final Iconify.IconifyInitializer initializer = Iconify.with(ICONS.get(0));
            for (int i = 1; i < ICONS.size(); i++) {
                initializer.with(ICONS.get(i));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T getConfig(Object key){
        final Object value = CONFIGS.get(key);
        if (value == null)
            throw new NullPointerException(key.toString() + "is null");
        return (T)value;
    }
}
