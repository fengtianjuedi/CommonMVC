package com.wufeng.latte_core.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "Merchant_trade_goods")
public class MerchantTradeGoods {
    @Id
    private Long id;
    @Unique
    @NotNull
    private Long tradeId;
    private String goodsId; //商品ID
    private String goodsName; //商品名称
    private String goodsPrice; //商品单价
    private int goodsNumber; //商品数量
    private String goodsAmount; //商品金额
    @Generated(hash = 654017548)
    public MerchantTradeGoods(Long id, @NotNull Long tradeId, String goodsId,
            String goodsName, String goodsPrice, int goodsNumber,
            String goodsAmount) {
        this.id = id;
        this.tradeId = tradeId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsNumber = goodsNumber;
        this.goodsAmount = goodsAmount;
    }
    @Generated(hash = 242531100)
    public MerchantTradeGoods() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getTradeId() {
        return this.tradeId;
    }
    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }
    public String getGoodsId() {
        return this.goodsId;
    }
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
    public String getGoodsName() {
        return this.goodsName;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public String getGoodsPrice() {
        return this.goodsPrice;
    }
    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public int getGoodsNumber() {
        return this.goodsNumber;
    }
    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }
    public String getGoodsAmount() {
        return this.goodsAmount;
    }
    public void setGoodsAmount(String goodsAmount) {
        this.goodsAmount = goodsAmount;
    }
}
