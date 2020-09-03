package com.wufeng.commonmvc.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TradeRecordInfo {
    private String tradeOrderCode; //交易订单号
    private String terminalOrderCode; //终端订单号
    private String sellerAccount; //卖家账户
    private String sellerName; //卖家姓名
    private String buyerAccount; //买家账户
    private String buyerName; //买家姓名
    private String receivableAmount; //应收金额
    private String actualAmount; //实收金额
    private String tradeTime; //交易时间
    private List<CategoryRecordInfo> categoryRecordInfoList; //商品列表

    public TradeRecordInfo(){
        categoryRecordInfoList = new ArrayList<>();
    }

    public String getTradeOrderCode(){return tradeOrderCode;}
    public void setTradeOrderCode(String value){tradeOrderCode = value;}

    public String getTerminalOrderCode(){return terminalOrderCode;}
    public void setTerminalOrderCode(String value){terminalOrderCode = value;}

    public String getSellerAccount(){return sellerAccount;}
    public void setSellerAccount(String value){sellerAccount = value;}

    public String getSellerName(){return sellerName;}
    public void setSellerName(String value){sellerName = value;}

    public String getBuyerAccount(){return buyerAccount;}
    public void setBuyerAccount(String value){buyerAccount = value;}

    public String getBuyerName(){return buyerName;}
    public void setBuyerName(String value){buyerName = value;}

    public String getReceivableAmount(){return receivableAmount;}
    public void setReceivableAmount(String value){receivableAmount = value;}

    public String getActualAmount(){return actualAmount;}
    public void setActualAmount(String value){actualAmount = value;}

    public String getTradeTime(){return tradeTime;}
    public void setTradeTime(String value){tradeTime = value;}

    public List<CategoryRecordInfo> getCategoryRecordInfoList(){return categoryRecordInfoList;}
}
