package com.wufeng.latte_core.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity(nameInDb = "Merchant_Card")
public class MerchantCard {
    @Id(autoincrement = true)
    private Long id;
    @Unique@NotNull
    private String cardNo;
    private String cardName;
    private boolean isCollectionAccount;
    @Generated(hash = 913079133)
    public MerchantCard(Long id, @NotNull String cardNo, String cardName,
            boolean isCollectionAccount) {
        this.id = id;
        this.cardNo = cardNo;
        this.cardName = cardName;
        this.isCollectionAccount = isCollectionAccount;
    }
    @Generated(hash = 718650106)
    public MerchantCard() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardNo() {
        return this.cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    public String getCardName() {
        return this.cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    public boolean getIsCollectionAccount() {
        return this.isCollectionAccount;
    }
    public void setIsCollectionAccount(boolean isCollectionAccount) {
        this.isCollectionAccount = isCollectionAccount;
    }
}
