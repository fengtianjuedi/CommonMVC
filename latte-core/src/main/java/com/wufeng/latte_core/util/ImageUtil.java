package com.wufeng.latte_core.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {

    /**
     * 缩放Bitmap位图
     * @param bm 原图
     * @param offset 偏移大小
     * @param maxWidth 最大宽度
     * @return 新图
     */
    public static Bitmap zoomImg(Bitmap bm, int offset, int maxWidth){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth = maxWidth - offset;
        if (newWidth <= 0) {
            return null;
        }
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        //float scaleHeight = ((float) height) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }
}
