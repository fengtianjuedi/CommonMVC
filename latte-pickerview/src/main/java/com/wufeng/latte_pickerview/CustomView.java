package com.wufeng.latte_pickerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CustomView extends View {
    private Paint mCenterLinePaint;
    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mCenterLinePaint = new Paint();
        mCenterLinePaint.setColor(Color.BLACK);
        mCenterLinePaint.setAntiAlias(true);
        mCenterLinePaint.setTextSize(16);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawLine(0, 100, 200, 100, mCenterLinePaint);
        canvas.save();
        canvas.translate(0, 50);
        canvas.scale(1.0F, 0.5F);
        canvas.save();
        canvas.clipRect(0, 0, 50, 50);
        canvas.drawColor(Color.RED);
        canvas.restore();
    }
}
