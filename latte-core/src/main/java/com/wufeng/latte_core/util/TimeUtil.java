package com.wufeng.latte_core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private final static ThreadLocal<SimpleDateFormat> dateFormatter1 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormatter2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormatter3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormatter4 = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        }
    };

    public static String currentDateYMDHMS(){
        try{
            Date date = new Date(System.currentTimeMillis());
            return dateFormatter1.get().format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public static String currentDateYMD(){
        try{
            Date date = new Date(System.currentTimeMillis());
            return dateFormatter2.get().format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String currentDateYMDHMSS(){
        try{
            Date date = new Date(System.currentTimeMillis());
            return dateFormatter4.get().format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String dateToStringYMD(Date date){
        return dateFormatter2.get().format(date);
    }

    public static Date parseStringToDateYMD(String date){
        try {
            return dateFormatter2.get().parse(date);
        }catch (ParseException pe){
            pe.printStackTrace();
            return null;
        }

    }

    public static Date parseStringToDateYMDHMS(String date){
        try {
            return dateFormatter1.get().parse(date);
        }catch (ParseException pe){
            pe.printStackTrace();
            return null;
        }

    }
}
