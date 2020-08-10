package com.wufeng.latte_core.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//文件操作公共类
public class FileUtil {
    private static final String tradeCacheFileName = "tradeCache.json"; //交易记录缓存文件名
    /**
     * 保存交易数据缓存
     * @param context 程序上下文
     * @param content 缓存数据
     */
    public static void saveTradeCache(Context context, String content){
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try{
            fileOutputStream = context.openFileOutput(tradeCacheFileName, Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                if (bufferedWriter != null)
                    bufferedWriter.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * 读取交易数据缓存
     * @param context 程序上下文
     * @return 缓存数据
     */
    public static String readTradeCache(Context context){
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try{
            fileInputStream = context.openFileInput(tradeCacheFileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                if (bufferedReader != null)
                    bufferedReader.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }
}
