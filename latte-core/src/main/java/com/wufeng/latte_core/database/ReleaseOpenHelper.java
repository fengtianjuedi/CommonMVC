package com.wufeng.latte_core.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * 自定义数据库创建对象，主要是为了避免使用默认数据库创建DaoMaster.DevOpenHelper导致的数据库升级时，数据丢失问题。
 * 每个版本的数据升级时，都要在下面的升级方法中，添加自己的数据表结构修改语句，要考虑向前兼容性。
 */
public class ReleaseOpenHelper extends DaoMaster.OpenHelper {

    public ReleaseOpenHelper(Context context, String name) {
        super(context, name);
    }

    public ReleaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        try{
            //数据库升级，表结构变化sql语句
        }catch (SQLException se){
            Log.d("ReleaseOpenHelper", "onUpgrade: " + se.getMessage());
        }
    }
}
