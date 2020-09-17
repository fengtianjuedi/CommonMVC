package com.wufeng.latte_core.device.print;

import android.util.Log;

import com.wufeng.latte_core.config.ConfigKeys;
import com.wufeng.latte_core.config.ConfigManager;
import com.wufeng.latte_core.database.MerchantTrade;
import com.wufeng.latte_core.database.MerchantTradeGoods;
import com.wufeng.latte_core.entity.CategoryInfo;
import com.wufeng.latte_core.entity.CategoryRecordInfo;
import com.wufeng.latte_core.entity.TradeRecordInfo;
import com.wufeng.latte_core.util.StringUtil;

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

    //批发交易小票模板
    public void tradeTemplate(TradeRecordInfo trade, final PrintResultCallback printResultCallback){
        try{
            mPrinter.printStr(Printer.AlignCenter, "巨野农批pos交易单\n");
            mPrinter.printStr(Printer.AlignLeft, "订单号： " + trade.getTradeOrderCode());
            mPrinter.printStr(Printer.AlignLeft, "交易时间： " + trade.getTradeTime());
            mPrinter.printStr(Printer.AlignLeft, "------------------------------");
            mPrinter.printStr(Printer.AlignLeft, StringUtil.paddingRight("单价", ' ', 10) + StringUtil.paddingRight("数量", ' ', 10) + "小计");
            for (int i = 0; i < trade.getCategoryRecordInfoList().size(); i++){
                CategoryRecordInfo goods = trade.getCategoryRecordInfoList().get(i);
                mPrinter.printStr(Printer.AlignLeft, goods.getGoodsName());
                mPrinter.printStr(Printer.AlignLeft, StringUtil.paddingRight(goods.getGoodsPrice(), ' ', 10) + StringUtil.paddingRight(String.valueOf(goods.getGoodsNumber()), ' ', 10)+ goods.getGoodsAmount());
            }
            mPrinter.printStr(Printer.AlignLeft, "------------------------------");
            mPrinter.printStr(Printer.AlignLeft, "应收金额：" + StringUtil.paddingLeft(trade.getReceivableAmount(), ' ', 16));
            mPrinter.printStr(Printer.AlignLeft, "实收金额：" + StringUtil.paddingLeft(trade.getActualAmount(), ' ', 16));
            mPrinter.printStr(Printer.AlignLeft, "支付方式：" + StringUtil.paddingLeft((trade.getPayType() == 0 ? "一卡通" : "现金"), ' ', 16));
            mPrinter.printStr(Printer.AlignLeft, "------------------------------");
            mPrinter.printStr(Printer.AlignLeft, "卖家卡号：" + trade.getSellerCardNo());
            mPrinter.printStr(Printer.AlignLeft, "卖家名称： " + trade.getSellerName());
            if (trade.getPayType() == 0){ //一卡通交易
                mPrinter.printStr(Printer.AlignLeft, "买家卡号：" + trade.getBuyerCardNo());
                mPrinter.printStr(Printer.AlignLeft, "买家名称： " + trade.getBuyerName());
            }
            mPrinter.printStr(Printer.AlignLeft, "本人确认以上交易同意记入本卡账户");
            mPrinter.printStr(Printer.AlignLeft, "签名：");
            mPrinter.feedLine(6);
            int num = ConfigManager.getInstance().getConfig(ConfigKeys.PRINTNUMBER);
            for (int i = 0; i < num; i++){
                mPrinter.startPrint(new Printer.PrintEndCallback() {
                    @Override
                    public void result(int code, String message) {
                        Log.d("PrintTemplate", "code: " + code + " message" + message);
                        if (printResultCallback != null)
                            printResultCallback.result(code, message);
                    }
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public interface PrintResultCallback {
        void result(int code, String message);
    }
}
