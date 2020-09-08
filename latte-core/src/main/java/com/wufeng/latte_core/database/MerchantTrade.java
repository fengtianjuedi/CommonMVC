package com.wufeng.latte_core.database;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "Merchant_trade")
public class MerchantTrade {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    @NotNull
    private String terminalOrderCode; //终端订单号
    private String tradeOrderCode; //交易订单号
    private String sellerAccount; //卖家账户
    private String sellerName; //卖家姓名
    private String buyerAccount; //买家账户
    private String buyerName; //买家姓名
    private String receivableAmount; //应收金额
    private String actualAmount; //实收金额
    private String tradeTime; //交易时间
    @NonNull
    private int tradeStatus; //交易状态 0:成功 1:失败 2:未知
    private int payType; //支付方式 0:一卡通 1:现金

    @ToMany(referencedJoinProperty = "tradeId")
    private List<MerchantTradeGoods> merchantTradeGoodsList; //交易品种列表
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 725032744)
    private transient MerchantTradeDao myDao;

    @Generated(hash = 163877842)
    public MerchantTrade(Long id, @NotNull String terminalOrderCode, String tradeOrderCode,
            String sellerAccount, String sellerName, String buyerAccount, String buyerName,
            String receivableAmount, String actualAmount, String tradeTime, int tradeStatus,
            int payType) {
        this.id = id;
        this.terminalOrderCode = terminalOrderCode;
        this.tradeOrderCode = tradeOrderCode;
        this.sellerAccount = sellerAccount;
        this.sellerName = sellerName;
        this.buyerAccount = buyerAccount;
        this.buyerName = buyerName;
        this.receivableAmount = receivableAmount;
        this.actualAmount = actualAmount;
        this.tradeTime = tradeTime;
        this.tradeStatus = tradeStatus;
        this.payType = payType;
    }

    @Generated(hash = 1095239998)
    public MerchantTrade() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTerminalOrderCode() {
        return this.terminalOrderCode;
    }

    public void setTerminalOrderCode(String terminalOrderCode) {
        this.terminalOrderCode = terminalOrderCode;
    }

    public String getSellerAccount() {
        return this.sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getBuyerAccount() {
        return this.buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public String getBuyerName() {
        return this.buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getReceivableAmount() {
        return this.receivableAmount;
    }

    public void setReceivableAmount(String receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public String getActualAmount() {
        return this.actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getTradeTime() {
        return this.tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 213773628)
    public List<MerchantTradeGoods> getMerchantTradeGoodsList() {
        if (merchantTradeGoodsList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MerchantTradeGoodsDao targetDao = daoSession.getMerchantTradeGoodsDao();
            List<MerchantTradeGoods> merchantTradeGoodsListNew = targetDao
                    ._queryMerchantTrade_MerchantTradeGoodsList(id);
            synchronized (this) {
                if (merchantTradeGoodsList == null) {
                    merchantTradeGoodsList = merchantTradeGoodsListNew;
                }
            }
        }
        return merchantTradeGoodsList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 165526986)
    public synchronized void resetMerchantTradeGoodsList() {
        merchantTradeGoodsList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public int getTradeStatus() {
        return this.tradeStatus;
    }

    public void setTradeStatus(int tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeOrderCode() {
        return this.tradeOrderCode;
    }

    public void setTradeOrderCode(String tradeOrderCode) {
        this.tradeOrderCode = tradeOrderCode;
    }

    public int getPayType() {
        return this.payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1912088036)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMerchantTradeDao() : null;
    }
}
