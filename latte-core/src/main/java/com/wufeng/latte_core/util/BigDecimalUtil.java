package com.wufeng.latte_core.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    public static double sumD(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.add(bd2).doubleValue();
    }

    public static BigDecimal sumB(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.add(bd2);
    }

    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    public static double subD(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.subtract(bd2).doubleValue();
    }

    public static BigDecimal subB(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.subtract(bd2);
    }

    /**
     * double 乘法
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal mul(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.multiply(bd2).setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * double 乘法
     * 四舍五入保留整数
     * @param d1
     * @param d2
     * @return
     */
    public static int mul2Int(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        BigDecimal bd =new BigDecimal(bd1.multiply(bd2).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());//定义一个BigDecimal 类型
        int b=bd.intValue();//转换为int类型
        return bd.intValue();
    }


    /**
     * double 除法
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double div(String d1,String d2,int scale){
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理
        if(Double.parseDouble(d2) == 0){
            return 0.00;
        }
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.divide(bd2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
