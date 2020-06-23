package com.wufeng.latte_core.loader;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;

import com.wufeng.latte_core.R;

import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import app.dinus.com.loadingdrawable.render.animal.FishLoadingRenderer;
import app.dinus.com.loadingdrawable.render.animal.GhostsEyeLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.jump.CollisionLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.jump.DanceLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.jump.GuardLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.jump.SwapLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.rotate.GearLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.rotate.LevelLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.rotate.MaterialLoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.rotate.WhorlLoadingRenderer;
import app.dinus.com.loadingdrawable.render.goods.BalloonLoadingRenderer;
import app.dinus.com.loadingdrawable.render.goods.WaterBottleLoadingRenderer;
import app.dinus.com.loadingdrawable.render.scenery.DayNightLoadingRenderer;
import app.dinus.com.loadingdrawable.render.scenery.ElectricFanLoadingRenderer;
import app.dinus.com.loadingdrawable.render.shapechange.CircleBroodLoadingRenderer;
import app.dinus.com.loadingdrawable.render.shapechange.CoolWaitLoadingRenderer;

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
