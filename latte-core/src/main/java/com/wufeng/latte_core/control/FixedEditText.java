package com.wufeng.latte_core.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.wufeng.latte_core.R;

public class FixedEditText extends AppCompatEditText {
    private String fixedText;
    private int leftPadding;
    private Paint fixedTextPaint;

    public FixedEditText(@NonNull Context context) {
        super(context);
        fixedTextPaint = new Paint();
        fixedTextPaint.setAntiAlias(true);
        fixedTextPaint.setDither(true);
        fixedTextPaint.setTextSize(getTextSize());
        fixedTextPaint.setColor(getCurrentTextColor());
    }

    public FixedEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        fixedTextPaint = new Paint();
        fixedTextPaint.setAntiAlias(true);
        fixedTextPaint.setDither(true);
        fixedTextPaint.setTextSize(getTextSize());
        fixedTextPaint.setColor(getCurrentTextColor());
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedEditText);
        String fixedText = typedArray.getString(R.styleable.FixedEditText_fixedText);
        setFixedText(fixedText);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(fixedText)){
            canvas.drawText(fixedText, leftPadding + getScrollX(), getBaseline(), fixedTextPaint); //加上滚动距离，不然文本超出后固定文本会左移
        }
    }

    public void setFixedText(String text){
        fixedText = text;
        leftPadding = getPaddingLeft();
        int left = (int) (getPaint().measureText(text) + leftPadding + 6);
        setPadding(left, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        invalidate();
    }
}
