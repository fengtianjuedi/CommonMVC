package com.wufeng.commonmvc.entity;

import java.math.BigDecimal;

//商品记录
public class CategoryRecordInfo {
    private String goodsId; //商品ID
    private String goodsName; //商品名称
    private BigDecimal goodsPrice; //商品单价
    private int goodsNumber; //商品数量
    private BigDecimal goodsAmount; //商品金额

    public String getGoodsId() {return goodsId;}
    public void setGoodsId(String value) {goodsId = value;}

    public String getGoodsName() {return goodsName;}
    public void setGoodsName(String value) {goodsName = value;}

    public BigDecimal getGoodsPrice() {return goodsPrice;}
    public void setGoodsPrice(BigDecimal value) {goodsPrice = value;}

    public int getGoodsNumber(){return goodsNumber;}
    public void setGoodsNumber(int value){goodsNumber = value;}

    public BigDecimal getGoodsAmount(){return goodsAmount;}
    public void setGoodsAmount(BigDecimal value){goodsAmount = value;}
}
