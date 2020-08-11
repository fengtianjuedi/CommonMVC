package com.wufeng.latte_core.loader;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;

import com.wufeng.latte_core.R;

import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import app.dinus.com.loadingdrawable.render.scenery.ElectricFanLoadingRenderer;

public class Loader {
    private static List<AppCompatDialog> LOADS = new ArrayList<>();

    public static void showLoading(Context context){
        final AppCompatDialog dialog = new AppCompatDialog(context, R.style.dialog);
        final LoadingView loadingView = new LoadingView(context);
        ElectricFanLoadingRenderer.Builder builder = new ElectricFanLoadingRenderer.Builder(context);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingView.setLoadingRenderer(builder.build());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);// 全屏
        dialog.setContentView(loadingView, lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        LOADS.add(dialog);
        dialog.show();
    }

    public static void stopLoading() {
        for (AppCompatDialog dialog : LOADS) {
            if (dialog != null) {
                if (dialog.isShowing())
                    dialog.cancel();
            }
        }
    }
}
