package com.wufeng.latte_core.device.print;

import android.util.Log;

public class PrintTemplate {
    private Printer mPrinter;

    public PrintTemplate(Printer printer){
        mPrinter = printer;
        mPrinter.init();
    }

    public void testTemplate1(final PrintResultCallback printResultCallback){
        try{
            mPrinter.printStr(Printer.AlignCenter, "我的测试");
            mPrinter.printStr(Printer.AlignLeft, "我的测试左对齐行数据");
            mPrinter.printStr(Printer.AlignRight, "我的测试右对齐行数据");
            mPrinter.printStr(Printer.AlignLeft, "\n\n");
            mPrinter.printBarCode(Printer.AlignCenter, 300, 100, "123456789");
            mPrinter.printQrCode(Printer.AlignCenter, 300, "123456789");
            mPrinter.startPrint(new Printer.PrintEndCallback() {
                @Override
                public void result(int code, String message) {
                    Log.d("PrintTemplate", "code: " + code + " message" + message);
                    if (printResultCallback != null)
                        printResultCallback.result(code, message);
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public interface PrintResultCallback {
        void result(int code, String message);
    }
}
