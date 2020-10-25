package com.wufeng.latte_core.entity;

import android.os.Parcel;
import android.os.Parcelable;

//商品记录
public class CategoryRecordInfo implements Parcelable {
    private String goodsId; //商品ID
    private String goodsName; //商品名称
    private String goodsPrice; //商品单价
    private String goodsNumber; //商品数量
    private String goodsAmount; //商品金额
    private String goodsBatchNo; //商品批次号
    private String goodsTraceabilityCode; //商品溯源码

    public String getGoodsId() {return goodsId;}
    public void setGoodsId(String value) {goodsId = value;}

    public String getGoodsName() {return goodsName;}
    public void setGoodsName(String value) {goodsName = value;}

    public String getGoodsPrice() {return goodsPrice;}
    public void setGoodsPrice(String value) {goodsPrice = value;}

    public String getGoodsNumber(){return goodsNumber;}
    public void setGoodsNumber(String value){goodsNumber = value;}

    public String getGoodsAmount(){return goodsAmount;}
    public void setGoodsAmount(String value){goodsAmount = value;}

    public String getGoodsBatchNo(){return goodsBatchNo;}
    public void setGoodsBatchNo(String value){goodsBatchNo = value;}

    public String getGoodsTraceabilityCode(){return goodsTraceabilityCode;}
    public void setGoodsTraceabilityCode(String value){this.goodsTraceabilityCode = value;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsId);
        dest.writeString(goodsName);
        dest.writeString(goodsPrice);
        dest.writeString(goodsNumber);
        dest.writeString(goodsAmount);
        dest.writeString(goodsBatchNo);
        dest.writeString(goodsTraceabilityCode);
    }

    public static final Parcelable.Creator<CategoryRecordInfo> CREATOR = new Creator<CategoryRecordInfo>() {
        @Override
        public CategoryRecordInfo createFromParcel(Parcel source) {
            CategoryRecordInfo categoryRecordInfo = new CategoryRecordInfo();
            categoryRecordInfo.setGoodsId(source.readString());
            categoryRecordInfo.setGoodsName(source.readString());
            categoryRecordInfo.setGoodsPrice(source.readString());
            categoryRecordInfo.setGoodsNumber(source.readString());
            categoryRecordInfo.setGoodsAmount(source.readString());
            categoryRecordInfo.setGoodsBatchNo(source.readString());
            categoryRecordInfo.setGoodsTraceabilityCode(source.readString());
            return categoryRecordInfo;
        }

        @Override
        public CategoryRecordInfo[] newArray(int size) {
            return new CategoryRecordInfo[size];
        }
    };
}
