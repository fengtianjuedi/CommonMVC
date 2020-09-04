package com.wufeng.latte_core.database;

import android.database.SQLException;

import java.util.List;

/**
 * 商户卡绑定记录 增 删 改 查 操作
 */
public class MerchantCardManager {
    private static final class Holder{
        private static final MerchantCardManager INSTANCE = new MerchantCardManager();
    }

    public static MerchantCardManager getInstance(){return Holder.INSTANCE;}

    //插入一条绑卡记录
    public boolean insert(MerchantCard record){
        long id = DatabaseManager.getInstance().getMerchantCardDao().insert(record);
        return id != -1;
    }

    //删除一条绑卡记录
    public boolean deleteByCardNo(String cardNo){
        try{
            DatabaseManager.getInstance().getMerchantCardDao().queryBuilder()
                    .where(MerchantCardDao.Properties.CardNo.eq(cardNo))
                    .buildDelete()
                    .executeDeleteWithoutDetachingEntities();
            return true;
        }catch (SQLException se){
            se.printStackTrace();
            return false;
        }
    }

    //修改记录
    public boolean modify(MerchantCard record){
        MerchantCard merchantCard = DatabaseManager.getInstance().getMerchantCardDao().queryBuilder()
                .where(MerchantCardDao.Properties.CardNo.eq(record.getCardNo()))
                .build().unique();
        if (merchantCard != null){
            merchantCard.setIsCollectionAccount(record.getIsCollectionAccount());
            long id = DatabaseManager.getInstance().getMerchantCardDao().insertOrReplace(merchantCard);
            return id != -1;
        }
        return false;
    }

    //查询所有记录
    public List<MerchantCard> query(){
        return DatabaseManager.getInstance().getMerchantCardDao().queryBuilder().list();
    }

    //根据卡号查询记录
    public MerchantCard queryByCardNo(String cardNo){
        return DatabaseManager.getInstance().getMerchantCardDao().queryBuilder()
                .where(MerchantCardDao.Properties.CardNo.eq(cardNo))
                .build()
                .unique();
    }

}
