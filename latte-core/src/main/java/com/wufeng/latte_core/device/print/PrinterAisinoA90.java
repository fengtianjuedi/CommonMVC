package com.wufeng.latte_core.device.print;

import com.vanstone.trans.api.PrinterApi;

public class PrinterAisinoA90 implements IPrinter {
    public static final String AISINOA90 = "AISINOA90";

    @Override
    public void print() {
        PrinterApi.PrnClrBuff_Api();//清除打印缓冲区
        PrinterApi.printSetGray_Api(10);
        PrinterApi.PrnLineSpaceSet_Api((short)5, 0);
        PrinterApi.printSetAlign_Api(1);
        PrinterApi.PrnHTSet_Api(1);
    }

    /**
     * 获取艾体威尔A90打印错误描述
     */
    public String getErrorDescription(int code){
        switch (code) {
            case 1:
                return "扣款成功\r\n打印机忙";
            case 2:
                return "扣款成功\r\n缺纸";//"Paper-out, the operation is invalid this time";
            case 3:
                return "扣款成功\r\n打印机过热";//"Hardware fault, can not find HP signal";
            case 4:
                return "扣款成功\r\n手柄不在底座";
            case 5:
                return "扣款成功\r\n打印机故障";
            case 6:
                return "扣款成功\r\n打印机未装字库";
            case 7:
                return "扣款成功\r\n打印缓冲溢出";
            case 8:
                return "扣款成功\r\n其他错误";
            case 9:
                return "扣款成功\r\n打印缓冲区为空";
        }
        return "unknown error (" + code + ")";
    }
}
