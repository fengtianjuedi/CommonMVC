package com.wufeng.latte_core.util;

/**
 * Created by rtdev on 2019/4/2.
 */

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 状态栏相关工具类
 *
 */ public class StatusBarUtil {
//     设置Activity对应的顶部状态栏的颜色
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

 //设置Dialog对应的顶部状态栏的颜色
 public static void setWindowStatusBarColor(Dialog dialog, String colorResId) {
     try {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             Window window = dialog.getWindow();
             window.requestFeature(Window.FEATURE_NO_TITLE);
             window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
             window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
             window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕高亮
             window.setStatusBarColor(Color.parseColor(colorResId));
         }
     } catch (Exception e) {
         e.printStackTrace();
     }
 }

    /**
     * 设置透明状态栏
     * <p>
     * 可在Activity的onCreat()中调用
     * <p>
     * 注意:需在顶部控件布局中加入以下属性让内容出现在状态栏之下:
     * android:clipToPadding="true"   // true 会贴近上层布局 ; false 与上层布局有一定间隙
     * android:fitsSystemWindows="true"   //true 会保留actionBar,title,虚拟键的空间 ; false 不保留
     *
     * @param activity activity
     */
    public static void setTransparentStatusBar(Activity activity) {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

}
