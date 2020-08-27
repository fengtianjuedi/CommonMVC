package com.wufeng.latte_core.database;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

public class DatabaseManager {
    private DaoSession mDaoSession = null;
    private UserProfileDao mUserProfileDao = null;
    private MerchantCardDao mMerchantCardDao = null;

    private DatabaseManager(){}

    public DatabaseManager init(Context context){
        initDao(context);
        return this;
    }

    private static final class Holder{
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    public static DatabaseManager getInstance(){return Holder.INSTANCE;}

    private void initDao(Context context){
        final ReleaseOpenHelper releaseOpenHelper = new ReleaseOpenHelper(context, "nongxin.db");
        final Database db = releaseOpenHelper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
        mUserProfileDao = mDaoSession.getUserProfileDao();
        mMerchantCardDao = mDaoSession.getMerchantCardDao();
    }

    public final UserProfileDao getUserProfileDao(){return mUserProfileDao;}

    public final MerchantCardDao getMerchantCardDao() {return mMerchantCardDao;}
}
