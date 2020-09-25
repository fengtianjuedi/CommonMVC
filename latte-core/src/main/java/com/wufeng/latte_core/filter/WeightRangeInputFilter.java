package com.wufeng.latte_core.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

//重量输入范围限制
public class WeightRangeInputFilter implements InputFilter {
    // 只允许输入数字和小数点
    private static final String REGEX = "([0-9]|\\.)*";
    // 输入的最大金额
    private static final int MAX_VALUE = 1000000;
    // 小数点后的位数
    private static final int POINTER_LENGTH = 3;
    private static final String POINTER = ".";
    private static final String ZERO_ZERO = "00";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();
        if (TextUtils.isEmpty(sourceText))
            return "";
        // 拼成字符串
        String temp = destText.substring(0, dstart)
                + sourceText.substring(start, end)
                + destText.substring(dend, dest.length());
        // 纯数字加小数点
        if (!temp.matches(REGEX)) {
            return dest.subSequence(dstart, dend);
        }
        // 小数点的情况
        if (temp.contains(POINTER)) {
            // 第一位就是小数点
            if (temp.startsWith(POINTER)) {
                return dest.subSequence(dstart, dend);
            }
            // 不止一个小数点
            if(temp.indexOf(POINTER) != temp.lastIndexOf(POINTER)) {
                return dest.subSequence(dstart, dend);
            }
        }

        double sumText = Double.parseDouble(temp);
        if (sumText >= MAX_VALUE) {
            // 超出最大值
            return dest.subSequence(dstart, dend);
        }
        // 有小数点的情况下
        if (temp.contains(POINTER)) {
            //验证小数点精度，保证小数点后只能输入两位
            if (!temp.endsWith(POINTER) && temp.split("\\.")[1].length() > POINTER_LENGTH) {
                return dest.subSequence(dstart, dend);
            }
        } else if (temp.startsWith(POINTER) || temp.startsWith(ZERO_ZERO)) {
            // 首位只能有一个0
            return dest.subSequence(dstart, dend);
        }

        return source;
    }
}
