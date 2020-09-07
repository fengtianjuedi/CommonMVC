package com.wufeng.latte_core.database;

import java.util.concurrent.Callable;

//商户交易记录管理
public class MerchantTradeManager {
    private static final class Holder {
        private static final MerchantTradeManager INSTANCE = new MerchantTradeManager();
    }

    public static MerchantTradeManager getInstance() {return Holder.INSTANCE;}

    //插入一条交易记录
    public boolean insert(final MerchantTrade trade){
        try{
            return DatabaseManager.getInstance().getDaoSession().callInTx(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DatabaseManager.getInstance().getMerchantTradeDao().insert(trade);
                    if (trade.getMerchantTradeGoodsList() != null){
                        for (int i = 0; i < trade.getMerchantTradeGoodsList().size(); i++){
                            MerchantTradeGoods merchantTradeGoods = trade.getMerchantTradeGoodsList().get(i);
                            merchantTradeGoods.setTradeId(trade.getId());
                            DatabaseManager.getInstance().getMerchantTradeGoodsDao().insert(merchantTradeGoods);
                        }
                    }
                    return true;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    //删除一条交易记录
    public boolean delete(final MerchantTrade trade){
        try{
            return DatabaseManager.getInstance().getDaoSession().callInTx(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DatabaseManager.getInstance().getMerchantTradeDao().delete(trade);
                    DatabaseManager.getInstance().getMerchantTradeGoodsDao().deleteInTx(trade.getMerchantTradeGoodsList());
                    return true;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    //查询未知状态交易
    public MerchantTrade queryUnknowStatusTrade(){
        return DatabaseManager.getInstance().getMerchantTradeDao().queryBuilder()
                .where(MerchantTradeDao.Properties.TradeStatus.eq(2))
                .build()
                .unique();
    }
}
