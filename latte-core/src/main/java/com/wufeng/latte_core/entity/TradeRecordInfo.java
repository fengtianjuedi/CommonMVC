package com.wufeng.latte_core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TradeRecordInfo implements Parcelable {
    private String tradeOrderCode; //交易订单号
    private String terminalOrderCode; //终端订单号
    private String sellerCardNo; //卖家卡号
    private String sellerAccount; //卖家账户
    private String sellerCode; //卖家商户编号
    private String sellerName; //卖家姓名
    private String buyerCardNo; //买家卡号
    private String buyerAccount; //买家账户
    private String buyerCode; //买家商户编号
    private String buyerName; //买家姓名
    private String buyerPassword; //买家密码
    private String receivableAmount; //应收金额
    private String actualAmount; //实收金额
    private String tradeTime; //交易时间
    private int payType; //支付方式  0:现金 1:一卡通
    private List<CategoryRecordInfo> categoryRecordInfoList; //商品列表

    public TradeRecordInfo(){
        categoryRecordInfoList = new ArrayList<>();
    }

    public String getTradeOrderCode(){return tradeOrderCode;}
    public void setTradeOrderCode(String value){tradeOrderCode = value;}

    public String getTerminalOrderCode(){return terminalOrderCode;}
    public void setTerminalOrderCode(String value){terminalOrderCode = value;}

    public String getSellerCardNo(){return sellerCardNo;}
    public void setSellerCardNo(String value){sellerCardNo = value;}

    public String getSellerAccount(){return sellerAccount;}
    public void setSellerAccount(String value){sellerAccount = value;}

    public String getSellerCode(){return sellerCode;}
    public void setSellerCode(String value){sellerCode = value;}

    public String getSellerName(){return sellerName;}
    public void setSellerName(String value){sellerName = value;}

    public String getBuyerCardNo(){return buyerCardNo;}
    public void setBuyerCardNo(String value){buyerCardNo = value;}

    public String getBuyerAccount(){return buyerAccount;}
    public void setBuyerAccount(String value){buyerAccount = value;}

    public String getBuyerCode(){return buyerCode;}
    public void setBuyerCode(String value){buyerCode = value;}

    public String getBuyerName(){return buyerName;}
    public void setBuyerName(String value){buyerName = value;}

    public String getBuyerPassword() {return buyerPassword;}
    public void setBuyerPassword(String value){buyerPassword = value;}

    public String getReceivableAmount(){return receivableAmount;}
    public void setReceivableAmount(String value){receivableAmount = value;}

    public String getActualAmount(){return actualAmount;}
    public void setActualAmount(String value){actualAmount = value;}

    public String getTradeTime(){return tradeTime;}
    public void setTradeTime(String value){tradeTime = value;}

    public int getPayType(){return payType;}
    public void setPayType(int value){payType = value;}

    public List<CategoryRecordInfo> getCategoryRecordInfoList(){return categoryRecordInfoList;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tradeOrderCode);
        dest.writeString(terminalOrderCode);
        dest.writeString(sellerCardNo);
        dest.writeString(sellerAccount);
        dest.writeString(sellerCode);
        dest.writeString(sellerName);
        dest.writeString(buyerCardNo);
        dest.writeString(buyerAccount);
        dest.writeString(buyerName);
        dest.writeString(receivableAmount);
        dest.writeString(actualAmount);
        dest.writeString(tradeTime);
        dest.writeTypedList(categoryRecordInfoList);
    }

    public static final Parcelable.Creator<TradeRecordInfo> CREATOR = new Parcelable.Creator<TradeRecordInfo>(){

        @Override
        public TradeRecordInfo createFromParcel(Parcel source) {
            TradeRecordInfo tradeRecordInfo = new TradeRecordInfo();
            tradeRecordInfo.setTradeOrderCode(source.readString());
            tradeRecordInfo.setTerminalOrderCode(source.readString());
            tradeRecordInfo.setSellerCardNo(source.readString());
            tradeRecordInfo.setSellerAccount(source.readString());
            tradeRecordInfo.setSellerCode(source.readString());
            tradeRecordInfo.setSellerName(source.readString());
            tradeRecordInfo.setBuyerCardNo(source.readString());
            tradeRecordInfo.setBuyerAccount(source.readString());
            tradeRecordInfo.setBuyerName(source.readString());
            tradeRecordInfo.setReceivableAmount(source.readString());
            tradeRecordInfo.setActualAmount(source.readString());
            tradeRecordInfo.setTradeTime(source.readString());
            source.readTypedList(tradeRecordInfo.getCategoryRecordInfoList(), CategoryRecordInfo.CREATOR);
            return tradeRecordInfo;
        }

        @Override
        public TradeRecordInfo[] newArray(int size) {
            return new TradeRecordInfo[size];
        }
    };
}
