package com.wufeng.latte_core.device.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vanstone.trans.api.PrinterApi;
import com.wufeng.latte_core.loader.Loader;
import com.wufeng.latte_core.util.ImageUtil;

import java.io.IOException;
import java.io.InputStream;

public class PrinterAisinoA90 extends Printer {
    private Context mContext;
    private final int MaxWidth = 384;

    public PrinterAisinoA90(Context context){
        mContext = context;
    }

    @Override
    public void init() {
        PrinterApi.PrnClrBuff_Api();//清除打印缓冲区
        PrinterApi.printSetGray_Api(10);
        PrinterApi.PrnLineSpaceSet_Api((short)5, 0);
        PrinterApi.printSetAlign_Api(1);
        PrinterApi.PrnHTSet_Api(1);
    }

    @Override
    public void startPrint(PrintEndCallback printEndCallback) {
        Loader.showLoading(mContext);
        int reslut = PrinterApi.PrnStart_Api();
        if (printEndCallback != null)
            printEndCallback.result(reslut, getErrorDescription(reslut));
        Loader.stopLoading();
    }

    @Override
    public void printStr(int align, String text) {
        PrinterApi.printSetAlign_Api(align);
        PrinterApi.PrnStr_Api(text + "\n");
    }

    @Override
    public void printBarCode(int align, int width, int height, String text) {
        PrinterApi.printAddBarCode_Api(align, width, height, text);
    }

    @Override
    public void printQrCode(int align, int height, String text) {
        PrinterApi.printAddQrCode_Api(align, height, text);
    }

    @Override
    public void printImage(int resourceId) throws IOException {
        InputStream inputStream = mContext.getResources().openRawResource(resourceId);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap.getWidth() > MaxWidth){
            bitmap = ImageUtil.zoomImg(bitmap, 0, MaxWidth);
            if (bitmap == null) {
                return;
            }
        }
        PrinterApi.PrnLogo_Api(bitmap);
        inputStream.close();
    }

    @Override
    public void feedLine(int line) {
        PrinterApi.printFeedLine_Api(line);
    }

    /**
     * 获取艾体威尔A90打印错误描述
     */
    @Override
    public String getErrorDescription(int code){
        switch (code) {
            case 1:
                return "打印机忙";
            case 2:
                return "缺纸";//"Paper-out, the operation is invalid this time";
            case 3:
                return "打印机过热";//"Hardware fault, can not find HP signal";
            case 4:
                return "手柄不在底座";
            case 5:
                return "打印机故障";
            case 6:
                return "打印机未装字库";
            case 7:
                return "打印缓冲溢出";
            case 8:
                return "其他错误";
            case 9:
                return "打印缓冲区为空";
        }
        return "unknown error (" + code + ")";
    }
}
