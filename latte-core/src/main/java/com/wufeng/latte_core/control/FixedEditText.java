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
    private int fixedTextGravity;
    private int leftPadding;
    private int rightPadding;
    private int fixedTextWidth;
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
        int fixedTextGravity = typedArray.getInt(R.styleable.FixedEditText_fixedTextGravity, 0);
        setFixedText(fixedText);
        setFixedTextGravity(fixedTextGravity);
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerFixedText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(fixedText)){
            if (fixedTextGravity == 0){
                canvas.drawText(fixedText, leftPadding + getScrollX(), getBaseline(), fixedTextPaint); //加上滚动距离，不然文本超出后固定文本会左移
            }else if (fixedTextGravity == 1){
                canvas.drawText(fixedText, (getWidth() - getPaddingRight() + getScrollX()), getBaseline(), fixedTextPaint); //加上滚动距离，不然文本超出后固定文本会左移
            }else if (fixedTextGravity == 2){
                canvas.drawText(fixedText, getPaddingLeft() - fixedTextWidth - 5 + getScrollX(), getBaseline(), fixedTextPaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        centerFixedText();
    }

    public void setFixedText(String text){
        fixedText = text;
        fixedTextWidth = (int) (fixedTextPaint.measureText(fixedText) + leftPadding + 5);
    }

    public void setFixedTextGravity(int gravity){
        fixedTextGravity = gravity;
        locationFixedText();
    }

    //定位固定文本
    private void locationFixedText(){
        if (fixedTextGravity == 0){ //左边
            leftPadding = getPaddingLeft();
            int left = (int) (getPaint().measureText(fixedText) + leftPadding + 5);
            setPadding(left, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }else if (fixedTextGravity == 1){ //右边
            int rightPadding = getPaddingRight();
            int right = (int) (getPaint().measureText(fixedText) + rightPadding + 5);
            setPadding(getPaddingLeft(), getPaddingTop(), right, getPaddingBottom());
        }else if (fixedTextGravity == 2){ //居中
            leftPadding = getPaddingLeft();
            rightPadding = getPaddingRight();
        }
        invalidate();
    }

    //固定文本居中
    private void centerFixedText(){
        if (fixedTextGravity == 2){
            int textWidth = (int) (getPaint().measureText(getText().toString()));
            if (textWidth <= 0)
                textWidth = (int)getTextSize();
            if ((textWidth + leftPadding + rightPadding) >= (getRight() - getLeft())){
                int left = (int) (getPaint().measureText(fixedText) + leftPadding + 5);
                setPadding(left, getPaddingTop(), rightPadding, getPaddingBottom());
            }
            else{
                int left = (getWidth() - textWidth - leftPadding - rightPadding) / 2;
                setPadding(left + leftPadding, getPaddingTop(), left + rightPadding, getPaddingBottom());
            }
        }
    }
}
