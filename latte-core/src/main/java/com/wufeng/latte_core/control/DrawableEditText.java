package com.wufeng.latte_core.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class DrawableEditText extends AppCompatEditText {

    private OnDrawableLeftListener onDrawableLeftListener;
    private OnDrawableRightListener onDrawableRightListener;

    public DrawableEditText(@NonNull Context context) {
        super(context);
    }

    public DrawableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnDrawableLeftListener(OnDrawableLeftListener listener){
        onDrawableLeftListener = listener;
    }

    public void setOnDrawableRightListener(OnDrawableRightListener listener){
        onDrawableRightListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 触摸状态
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // 监听DrawableLeft
            if (onDrawableLeftListener != null) {
                // 判断DrawableLeft是否被点击
                Drawable drawableLeft = getCompoundDrawables()[0];
                // 当按下的位置 < 在EditText的到左边间距+图标的宽度+Padding
                if (drawableLeft != null && event.getRawX() <= (getLeft() + getTotalPaddingLeft() + drawableLeft.getBounds().width())) {
                    // 执行DrawableLeft点击事件
                    onDrawableLeftListener.onDrawableLeftClick();
                    clearFocus();
                    return true;
                }
            }

            // 监听DrawableRight
            if (onDrawableRightListener != null) {
                Drawable drawableRight = getCompoundDrawables()[2];
                // 当按下的位置 > 在EditText的到右边间距-图标的宽度-Padding
                if (drawableRight != null && event.getRawX() >= (getRight() - getTotalPaddingRight() - drawableRight.getBounds().width())) {
                    // 执行DrawableRight点击事件
                    onDrawableRightListener.onDrawableRightClick();
                    clearFocus();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public interface OnDrawableLeftListener {
        void onDrawableLeftClick();
    }

    public interface OnDrawableRightListener {
        void onDrawableRightClick();
    }
}
