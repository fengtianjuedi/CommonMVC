package com.wufeng.latte_core.database;

import android.database.SQLException;

//终端信息表管理类
public class TerminalInfoManager {
    private static final class Holder{
        private static final TerminalInfoManager INSTANCE = new TerminalInfoManager();
    }

    public static TerminalInfoManager getInstance(){return Holder.INSTANCE;}

    //插入一条终端信息记录
    public boolean insert(TerminalInfo record){
        long id = DatabaseManager.getInstance().getmTerminalInfoDao().insert(record);
        return id != -1;
    }

    //删除终端信息
    public boolean deleteTerminalInfo(){
        try{
            DatabaseManager.getInstance().getmTerminalInfoDao().deleteAll();
            return true;
        }catch (SQLException se){
            se.printStackTrace();
            return false;
        }
    }

    //获取最新一条终端信息
    public TerminalInfo queryLastTerminalInfo(){
        return DatabaseManager.getInstance().getmTerminalInfoDao().queryBuilder()
                .orderDesc(TerminalInfoDao.Properties.Id)
                .limit(1)
                .build()
                .unique();
    }
}
