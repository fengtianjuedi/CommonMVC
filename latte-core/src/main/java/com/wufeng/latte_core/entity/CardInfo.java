package com.wufeng.latte_core.entity;

/**
 * 卡信息
 */
public class CardInfo {
    private String cardNo;
    private String name;

    public String getCardNo(){return cardNo;}
    public void setCardNo(String value){cardNo = value;}

    public String getName(){return name;}
    public void setName(String value){name = value;}

    public CardInfo(){}

    public CardInfo(String cardNo, String name){
        this.cardNo = cardNo;
        this.name = name;
    }


}
