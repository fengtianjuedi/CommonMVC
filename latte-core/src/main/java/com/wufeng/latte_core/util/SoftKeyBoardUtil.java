package com.wufeng.latte_core.util;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SoftKeyBoardUtil {

    /**
     * 显示软键盘
     * @param context context
     * @param editText 输入框
     */
    public static void showSoftKeyBoard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            editText.requestFocus();
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    public static void showSoftKeyBoard(AppCompatActivity context, EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 隐藏软键盘
     * @param context context
     * @param view 布局控件
     */
    public static void hideSoftKeyBoard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }
}
