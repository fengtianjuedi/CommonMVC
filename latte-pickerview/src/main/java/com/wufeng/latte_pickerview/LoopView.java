package com.wufeng.latte_pickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LoopView extends View {
    private static final String TAG = LoopView.class.getSimpleName();

    public static final int MSG_INVALIDATE = 1000; //重绘
    public static final int MSG_SCROLL_LOOP = 2000; //循环滚动
    public static final int MSG_SELECTED_ITEM = 3000; //选中子项

    //定时任务
    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduledFuture;
    private int mTotalScrollY;
    private LoopScrollListener mLoopListener;
    private GestureDetector mGestureDetector; //手势检测
    private GestureDetector.SimpleOnGestureListener mOnGestureListener;
    private int mSelectedItem;
    private Context mContext;
    private Paint mTopBottomTextPaint;
    private Paint mCenterTextPaint;
    private Paint mCenterLinePaint;
    private ArrayList mDataList;
    private int mTextSize;
    private int mMaxTextWidth;
    private int mMaxTextHeight;
    private int mTopBottomTextColor;
    private int mCenterTextColor;
    private int mCenterLineColor;
    private float lineSpacingMultiplier;
    private boolean mCanLoop;
    private int mTopLineY;
    private int mBottomLineY;
    private int mCurrentIndex;
    private int mInitPosition;
    private int mPaddingLeftRight;
    private int mPaddingTopBottom;
    private float mItemHeight;
    private int mDrawItemsCount;
    private int mCircularDiameter;
    private int mWidgetHeight;
    private int mCircularRadius;
    private int mWidgetWidth;

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_INVALIDATE)
                invalidate();
            if (msg.what == MSG_SCROLL_LOOP)
                startSmoothScrollTo();
            else if(msg.what == MSG_SELECTED_ITEM)
                itemSelected();
            return false;
        }
    });

    public LoopView(Context context) {
        this(context, null);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        //设置自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mTopBottomTextColor = array.getColor(R.styleable.WheelView_topBottomTextColor, 0xffafafaf);
        mCenterTextColor = array.getColor(R.styleable.WheelView_centerTextColor, 0xff313131);
        mCenterLineColor = array.getColor(R.styleable.WheelView_lineColor, 0xffc5c5c5);
        mCanLoop = array.getBoolean(R.styleable.WheelView_canLoop, true);
        mInitPosition = array.getInt(R.styleable.WheelView_initPosition, -1);
        mTextSize = array.getDimensionPixelSize(R.styleable.WheelView_textSize, sp2px(context, 16));
        mDrawItemsCount = array.getInt(R.styleable.WheelView_drawItemCount, 7);
        array.recycle();

        lineSpacingMultiplier = 2.0F;
        this.mContext = context;
        mOnGestureListener = new LoopViewGestureListener();
        mTopBottomTextPaint = new Paint();
        mCenterTextPaint = new Paint();
        mCenterLinePaint = new Paint();

        setLayerType(LAYER_TYPE_SOFTWARE, null); //取消硬件加速
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
    }

    private void initData(){
        if (mDataList == null)
            throw new IllegalArgumentException("data list must not be null");
        //上下文本绘图参数设置
        mTopBottomTextPaint.setColor(mTopBottomTextColor);
        mTopBottomTextPaint.setAntiAlias(true);
        mTopBottomTextPaint.setTypeface(Typeface.MONOSPACE);
        mTopBottomTextPaint.setTextSize(mTextSize);

        //居中文本参数设置
        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextPaint.setTextScaleX(1.05F);
        mCenterTextPaint.setTypeface(Typeface.MONOSPACE);
        mCenterTextPaint.setTextSize(mTextSize);

        //居中文本边线参数设置
        mCenterLinePaint.setColor(mCenterLineColor);
        mCenterLinePaint.setAntiAlias(true);
        mCenterLinePaint.setTypeface(Typeface.MONOSPACE);
        mCenterLinePaint.setTextSize(mTextSize);

        measureTextWidthHeight();

        int mHalfCircumference = (int)(mMaxTextHeight * lineSpacingMultiplier * (mDrawItemsCount - 1)); //半圆的周长
        mCircularDiameter = (int)((mHalfCircumference * 2)/Math.PI); //直径
        mCircularRadius = (int)(mHalfCircumference / Math.PI); //半径

        if (mInitPosition == -1){
            if (mCanLoop){
                mInitPosition = (mDataList.size() + 1) / 2;
            }else{
                mInitPosition = 0;
            }
        }
        mCurrentIndex = mInitPosition;
        invalidate();
    }

    //测量文本的宽高
    private void measureTextWidthHeight(){
        Rect rect = new Rect();
        for(int i = 0; i < mDataList.size(); i++){
            String s1 = mDataList.get(i).toString();
            mCenterTextPaint.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > mMaxTextWidth)
                mMaxTextWidth = textWidth;
            int textHeight = rect.height();
            if (textHeight > mMaxTextHeight)
                mMaxTextHeight = textHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidgetWidth = getMeasuredWidth(); //控件宽度
        mWidgetHeight = MeasureSpec.getSize(heightMeasureSpec); //控件高度
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mItemHeight = lineSpacingMultiplier * mMaxTextHeight; //子项高度
        mPaddingLeftRight = (mWidgetWidth - mMaxTextWidth) / 2; //左右填充
        mPaddingTopBottom = (mWidgetHeight - mCircularDiameter) / 2; //上下填充

        mTopLineY = (int)((mCircularDiameter - mItemHeight) / 2.0F) + mPaddingTopBottom; //上边线Y
        mBottomLineY = (int)((mCircularDiameter + mItemHeight) / 2.0F) + mPaddingTopBottom; //下边线Y
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDataList == null)
            return;
        int mChangingItem = (int)(mTotalScrollY / mItemHeight);
        mCurrentIndex = mInitPosition + mChangingItem % mDataList.size();
        if (!mCanLoop){
            if (mCurrentIndex < 0)
                mCurrentIndex = 0;
            if (mCurrentIndex > mDataList.size() - 1)
                mCurrentIndex = mDataList.size() - 1;
        }else{
            if (mCurrentIndex < 0)
                mCurrentIndex = mDataList.size() + mCurrentIndex;
            if (mCurrentIndex > mDataList.size() - 1)
                mCurrentIndex = mCurrentIndex - mDataList.size();
        }

        int count = 0;
        String itemCount[] = new String[mDrawItemsCount];
        while (count < mDrawItemsCount){
            int templateItem = mCurrentIndex - (mDrawItemsCount / 2 - count);
            if (mCanLoop){
                if (templateItem < 0)
                    templateItem = templateItem + mDataList.size();
                if (templateItem > mDataList.size() - 1)
                    templateItem = templateItem - mDataList.size();
                itemCount[count] = mDataList.get(templateItem).toString();
            }else if(templateItem < 0){
                itemCount[count] = "";
            }else if(templateItem > mDataList.size() - 1){
                itemCount[count] = "";
            }else{
                itemCount[count] = mDataList.get(templateItem).toString();
            }
            count++;
        }

        canvas.drawLine(0.0F, mTopLineY, mWidgetWidth, mTopLineY, mCenterLinePaint);
        canvas.drawLine(0.0F, mBottomLineY, mWidgetWidth, mBottomLineY, mCenterLinePaint);

        count = 0;
        int changingLeftY = (int) (mTotalScrollY % mItemHeight);
        while (count < mDrawItemsCount){
            canvas.save();
            float itemHeight = mMaxTextHeight * lineSpacingMultiplier;
            double radian = (itemHeight * count - changingLeftY) / mCircularRadius; //求弧度
            float angle = (float)(radian * 180 / Math.PI);//求角度
            if (angle >= 180F || angle <= 0F)
                canvas.restore();
            else{
                int translateY = (int)(mCircularRadius - Math.cos(radian)*mCircularRadius-(Math.sin(radian) * mMaxTextHeight)/2) + mPaddingTopBottom;
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY  < mTopLineY){
                    canvas.save();
                    canvas.clipRect(0, 0, mWidgetWidth, mTopLineY - translateY);
                    canvas.drawText(itemCount[count], mPaddingLeftRight, mMaxTextHeight, mTopBottomTextPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mTopLineY - translateY, mWidgetWidth, (int)itemHeight);
                    canvas.drawText(itemCount[count], mPaddingLeftRight, mMaxTextHeight, mCenterTextPaint);
                    canvas.restore();
                }else if(mMaxTextHeight + translateY > mBottomLineY){
                    canvas.save();
                    canvas.clipRect(0, 0, mWidgetWidth, mBottomLineY-translateY);
                    canvas.drawText(itemCount[count], mPaddingLeftRight, mMaxTextHeight, mCenterTextPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, mBottomLineY - translateY, mWidgetWidth, (int)itemHeight);
                    canvas.drawText(itemCount[count], mPaddingLeftRight, mMaxTextHeight, mTopBottomTextPaint);
                    canvas.restore();
                }else if(mMaxTextHeight + translateY < mBottomLineY){
                    canvas.clipRect(0, 0, mWidgetWidth, (int)itemHeight);
                    canvas.drawText(itemCount[count], mPaddingLeftRight, mMaxTextHeight, mCenterTextPaint);
                    mSelectedItem = mDataList.indexOf(itemCount[count]);
                }
                canvas.restore();
            }
            count++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
            default:
                if (!mGestureDetector.onTouchEvent(event))
                    startSmoothScrollTo();
        }
        return true;
    }

    //设置是否可循环
    public final void setCanLoop(boolean canLoop){
        mCanLoop = canLoop;
        invalidate();
    }

    //设置文本大小
    public final void setTextSize(float size){
        if (size > 0)
            mTextSize = sp2px(mContext, size);
    }

    //设置初始位置
    public void setInitPosition(int initPosition){
        this.mInitPosition = initPosition;
        invalidate();
    }

    //设置循环监听事件
    public void setLoopListener(LoopScrollListener loopListener){
        mLoopListener = loopListener;
    }

    //设置数据内容
    public final void setDataList(List<String> list){
        this.mDataList = (ArrayList)list;
        initData();
    }

    //获取选中项
    public int getSelectedItem(){
        return mSelectedItem;
    }

    private void itemSelected(){
        if (mLoopListener != null){
            postDelayed(new SelectedRunnable(), 200L);
        }
    }

    //取消定时任务
    private void cancelSchedule(){
        if (mScheduledFuture != null && !mScheduledFuture.isCancelled()){
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }
    }

    private void startSmoothScrollTo(){
        int offset = (int)(mTotalScrollY % mItemHeight);
        cancelSchedule();
        mScheduledFuture = mExecutor.scheduleWithFixedDelay(new HalfHeightRunnable(offset), 0, 10, TimeUnit.MILLISECONDS);
    }

    private void startSmoothScrollTo(float velocityY){
        cancelSchedule();
        int velocityFling = 20;
        mScheduledFuture = mExecutor.scheduleWithFixedDelay(new FlingRunnable(velocityY), 0, velocityFling, TimeUnit.MILLISECONDS);
    }

    class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            cancelSchedule();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            startSmoothScrollTo(velocityY);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mTotalScrollY = (int)((float)mTotalScrollY + distanceY);
            if (!mCanLoop){
                int initPositionCircleLength = (int)(mInitPosition * mItemHeight);
                int initPositionStartY = -1 * initPositionCircleLength;
                if (mTotalScrollY < initPositionStartY)
                    mTotalScrollY = initPositionStartY;
                int circleLength = (int)((float) (mDataList.size() - 1 - mInitPosition) * mItemHeight);
                if (mTotalScrollY >= circleLength)
                    mTotalScrollY = circleLength;
            }else{
                int circle = (int)((float) (mDataList.size() - 1) * mItemHeight) * 2;
                if (Math.abs(mTotalScrollY) > circle)
                    mTotalScrollY = mTotalScrollY % circle + circle;
            }
            invalidate();
            return true;
        }
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    class SelectedRunnable implements Runnable{

        @Override
        public void run() {
            LoopScrollListener listener = LoopView.this.mLoopListener;
            int selectedItem = getSelectedItem();
           // mDataList.get(selectedItem);
            listener.onItemSelect(selectedItem);
        }
    }

    class HalfHeightRunnable implements Runnable{
        int realTotalOffset;
        int realOffset;
        int offset;
        public HalfHeightRunnable(int offset){
            this.offset = offset;
            realTotalOffset = Integer.MAX_VALUE;
            realOffset = 0;
        }

        @Override
        public void run() {
            if (realTotalOffset == Integer.MAX_VALUE){
                if((float)offset > mItemHeight / 2.0F)
                    realTotalOffset = (int)(mItemHeight - (float)offset);
                else
                    realTotalOffset = -offset;
            }
            realOffset = (int)((float)realTotalOffset * 0.1F);
            if (realOffset == 0){
                if (realTotalOffset < 0){
                    realOffset = -1;
                }else{
                    realOffset = 1;
                }
            }
            if (Math.abs(realTotalOffset) == 0){
                cancelSchedule();
                mHandler.sendEmptyMessage(MSG_SELECTED_ITEM);
            }else{
                mTotalScrollY = mTotalScrollY + realOffset;
                mHandler.sendEmptyMessage(MSG_INVALIDATE);
                realTotalOffset = realTotalOffset - realOffset;
            }
        }
    }

    class FlingRunnable implements Runnable{
        float velocity;
        final float velocityY;

        FlingRunnable(float velocityY){
            this.velocityY = velocityY;
            velocity = Integer.MAX_VALUE;
        }

        @Override
        public void run() {
            if (velocity == Integer.MAX_VALUE){
                if (Math.abs(velocityY) > 2000F){
                    if (velocityY > 0.0F)
                        velocity = 2000F;
                    else
                        velocity = -2000F;
                }else
                    velocity = velocityY;
            }
            if (Math.abs(velocity) >= 0.0F && Math.abs(velocity) <= 20F){
                cancelSchedule();
                mHandler.sendEmptyMessage(MSG_SCROLL_LOOP);
                return;
            }
            int i = (int)(velocity * 10F / 1000F);
            mTotalScrollY = mTotalScrollY - i;
            if (!mCanLoop){
                float itemHeight = lineSpacingMultiplier * mMaxTextHeight;
                if (mTotalScrollY <= (int)((-mInitPosition) * itemHeight)){
                    velocity = 40F;
                    mTotalScrollY = (int)((-mInitPosition) * itemHeight);
                }else if (mTotalScrollY >= (int)((mDataList.size() - 1 - mInitPosition) * itemHeight)){
                    mTotalScrollY = (int)((mDataList.size() - 1 - mInitPosition) * itemHeight);
                    velocity = -40F;
                }
            }
            if (velocity < 0.0F)
                velocity = velocity + 20F;
            else
                velocity = velocity - 20F;
            mHandler.sendEmptyMessage(MSG_INVALIDATE);
        }
    }
}

