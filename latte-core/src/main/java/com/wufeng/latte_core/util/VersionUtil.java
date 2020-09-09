package com.wufeng.latte_core.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.core.content.pm.PackageInfoCompat;

//获取版本号
public class VersionUtil {
    /**
     * 获取软件版本号
     * @return 软件版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try{ // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = (int) PackageInfoCompat.getLongVersionCode(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
