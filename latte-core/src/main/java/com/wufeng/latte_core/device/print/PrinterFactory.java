package com.wufeng.latte_core.device.print;

import android.content.Context;

import com.wufeng.latte_core.device.PosDevice;

public class PrinterFactory {
    public Printer getPrinter(String model, Context context){
        if (PosDevice.AISINOA90.equals(model))
            return new PrinterAisinoA90(context);
        else if (PosDevice.LIANDIA8.equals(model))
            return new PrinterLiandiA8(context);
        else
            return null;
    }
}
