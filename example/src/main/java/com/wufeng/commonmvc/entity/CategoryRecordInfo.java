package com.wufeng.commonmvc.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

//商品记录
public class CategoryRecordInfo implements Parcelable {
    private String goodsId; //商品ID
    private String goodsName; //商品名称
    private String goodsPrice; //商品单价
    private int goodsNumber; //商品数量
    private String goodsAmount; //商品金额

    public String getGoodsId() {return goodsId;}
    public void setGoodsId(String value) {goodsId = value;}

    public String getGoodsName() {return goodsName;}
    public void setGoodsName(String value) {goodsName = value;}

    public String getGoodsPrice() {return goodsPrice;}
    public void setGoodsPrice(String value) {goodsPrice = value;}

    public int getGoodsNumber(){return goodsNumber;}
    public void setGoodsNumber(int value){goodsNumber = value;}

    public String getGoodsAmount(){return goodsAmount;}
    public void setGoodsAmount(String value){goodsAmount = value;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsId);
        dest.writeString(goodsName);
        dest.writeString(goodsPrice);
        dest.writeInt(goodsNumber);
        dest.writeString(goodsAmount);
    }

    public static final Parcelable.Creator<CategoryRecordInfo> CREATOR = new Creator<CategoryRecordInfo>() {
        @Override
        public CategoryRecordInfo createFromParcel(Parcel source) {
            CategoryRecordInfo categoryRecordInfo = new CategoryRecordInfo();
            categoryRecordInfo.setGoodsId(source.readString());
            categoryRecordInfo.setGoodsName(source.readString());
            categoryRecordInfo.setGoodsPrice(source.readString());
            categoryRecordInfo.setGoodsNumber(source.readInt());
            categoryRecordInfo.setGoodsAmount(source.readString());
            return categoryRecordInfo;
        }

        @Override
        public CategoryRecordInfo[] newArray(int size) {
            return new CategoryRecordInfo[size];
        }
    };
}
