package com.wufeng.latte_core.device.print;

import java.io.IOException;

public abstract class Printer {
    public static final int AlignLeft = 0; //左对齐
    public static final int AlignCenter = 1; //居中
    public static final int AlignRight = 2; //右对齐

    public abstract void init();
    public abstract void startPrint(PrintEndCallback printEndCallback);
    public abstract  void printStr(int align, String text) throws Exception;
    public abstract  void printBarCode(int align, int width, int height, String text) throws Exception;
    public abstract  void printQrCode(int align, int height, String text) throws Exception;
    public abstract  void printImage(int resourceId) throws IOException;
    public abstract  void feedLine(int line);
    public abstract  String getErrorDescription(int code);

    public interface PrintEndCallback{
        void result(int code, String message);
    }
}
