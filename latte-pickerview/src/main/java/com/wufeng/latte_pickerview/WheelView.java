package com.wufeng.latte_pickerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//滚动控件 created wufeng 20200701
public class WheelView extends View {
    private Context mContext;
    private ArrayList mData; //数据源
    private int mTopBottomTextColor; //未选中文本的颜色
    private int mCenterTextColor; //选中文本的颜色
    private int mCenterLineColor; //选中文本上下边线的颜色
    private boolean mCanLoop; //是否可循环滚动
    private int mInitPosition; //初始滚动位置
    private int mTextSize; //文本大小
    private int mDrawItemsCount; //显示子项数量
    private String mDrawItem[];//显示子项数组
    private int mCurrentIndex;//当前项索引
    private int mTotalScrollY;//总滚动距离

    private Paint mTopBottomTextPaint; //未选中文本绘图
    private Paint mCenterTextPaint; //选中文本绘图
    private Paint mCenterLinePaint; //选中文本上下边线绘图
    private GestureDetector mGestureDetector; //手势检测
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener;
    private ScheduledExecutorService mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(); //定时任务
    private ScheduledFuture<?> mScheduledFuture; //定时任务线程对象

    private float mItemScale; //子项缩放倍数
    private int mDiameter; //直径
    private int mRadius; //半径
    private int mMaxTextWidth;//文本最大宽度
    private int mMaxTextHeight;//文本最大高度
    private int mItemHeight;//子项高度
    private int mTopBottomMargin;//左右边距
    private int mLeftRihtMargin;//上下边距
    private int mWidth;//控件宽度
    private int mHeight;//控件高度

    private int mTopLineY;//上边线
    private int mBottomLineY;//下边线

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    //初始化属性
    private void initView(Context context, AttributeSet attrs){
        //设置自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mTopBottomTextColor = array.getColor(R.styleable.WheelView_topBottomTextColor, 0xffafafaf);
        mCenterTextColor = array.getColor(R.styleable.WheelView_centerTextColor, 0xff313131);
        mCenterLineColor = array.getColor(R.styleable.WheelView_lineColor, 0xffc5c5c5);
        mCanLoop = array.getBoolean(R.styleable.WheelView_canLoop, true);
        mInitPosition = array.getInt(R.styleable.WheelView_initPosition, 0);
        mTextSize = array.getDimensionPixelSize(R.styleable.WheelView_textSize, sp2px(context, 16));
        mDrawItemsCount = array.getInt(R.styleable.WheelView_drawItemCount, 7);
        array.recycle();

        mContext = context;
        mTopBottomTextPaint = new Paint();
        mTopBottomTextPaint.setColor(mTopBottomTextColor);
        mTopBottomTextPaint.setAntiAlias(true);
        mTopBottomTextPaint.setTextSize(mTextSize);
        mTopBottomTextPaint.setTypeface(Typeface.MONOSPACE);
        mCenterTextPaint = new Paint();
        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextPaint.setTextSize(mTextSize);
        mCenterTextPaint.setTypeface(Typeface.MONOSPACE);
        mCenterLinePaint = new Paint();
        mCenterLinePaint.setColor(mCenterLineColor);
        mCenterLinePaint.setAntiAlias(true);
        mCenterLinePaint.setTextSize(mTextSize);
        mItemScale = 2.0F;
        mDrawItem = new String[mDrawItemsCount];

        //触摸手势
        mSimpleOnGestureListener = new WheelViewGestureListener();
        mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);

        //setLayerType(LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
    }

    //初始化数据
    private void initData(){
        if (mData == null)
            throw new IllegalArgumentException("data list must not be null");
        measureTextWidthAndHeight();
        int halfCircleLength = (int)(mMaxTextHeight * mItemScale * (mDrawItemsCount - 1));
        mDiameter = (int)(halfCircleLength * 2 / Math.PI);
        mRadius = (int)(halfCircleLength / Math.PI);
    }

    //设置数据内容
    public final void setDataList(List<String> list){
        this.mData = (ArrayList)list;
        initData();
    }

    //测量控件尺寸
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mItemHeight = (int) (mMaxTextHeight * mItemScale);
        mTopBottomMargin = (mHeight - mDiameter)/2;
        mLeftRihtMargin = (mWidth - mMaxTextWidth)/2;
        mTopLineY = (mDiameter - mItemHeight)/2 + mTopBottomMargin;
        mBottomLineY = (mDiameter + mItemHeight)/2 + mTopBottomMargin;
    }

    //绘图
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null || mData.size() == 0)
            return;
        canvas.drawLine(0F, mTopLineY, mWidth, mTopLineY, mCenterLinePaint);
        canvas.drawLine(0, mBottomLineY, mWidth, mBottomLineY, mCenterLinePaint);

        int count = 0;
        mCurrentIndex = mInitPosition + (mTotalScrollY / mItemHeight) % mData.size();
        if (mCurrentIndex < 0)
            mCurrentIndex = 0;
        else if (mCurrentIndex > mData.size() - 1)
            mCurrentIndex = mData.size() - 1;

        while(count < mDrawItemsCount){
            int templateItem = mCurrentIndex - (mDrawItemsCount / 2 - count);
            if (templateItem < 0){
                mDrawItem[count] = "";
            }else if (templateItem > mData.size() -1){
                mDrawItem[count] = "";
            }else{
                mDrawItem[count] = mData.get(templateItem).toString();
            }
            count++;
        }

        count = 0;
        while (count < mDrawItemsCount){
            double radian = (double) (mItemHeight * count) / mRadius; //求弧度
            int translateY = (int)(mRadius - Math.cos(radian)*mRadius-(Math.sin(radian) * mMaxTextHeight)/2) + mTopBottomMargin;
            canvas.save();
            canvas.translate(0.0F, translateY);
            canvas.scale(1.0F, (float) Math.sin(radian));
            if (translateY  < mTopLineY){
                canvas.save();
                canvas.clipRect(0, 0, mWidth, mTopLineY - translateY);
                canvas.drawText(mDrawItem[count], mLeftRihtMargin, mMaxTextHeight, mTopBottomTextPaint);
                canvas.restore();
                canvas.save();
                canvas.clipRect(0, mTopLineY - translateY, mWidth, mItemHeight);
                canvas.drawText(mDrawItem[count], mLeftRihtMargin, mMaxTextHeight, mCenterTextPaint);
                canvas.restore();
            } else if(mMaxTextHeight + translateY > mBottomLineY){
                canvas.save();
                canvas.clipRect(0, 0, mWidth, mBottomLineY-translateY);
                canvas.drawText(mDrawItem[count], mLeftRihtMargin, mMaxTextHeight, mCenterTextPaint);
                canvas.restore();
                canvas.save();
                canvas.clipRect(0, mBottomLineY - translateY, mWidth, mItemHeight);
                canvas.drawText(mDrawItem[count], mLeftRihtMargin, mMaxTextHeight, mTopBottomTextPaint);
                canvas.restore();
            }else if(mMaxTextHeight + translateY < mBottomLineY){
                canvas.clipRect(0, 0, mWidth, mItemHeight);
                canvas.drawText(mDrawItem[count], mLeftRihtMargin, mMaxTextHeight, mCenterTextPaint);
            }
            canvas.restore();
            count++;
        }
    }

    //触摸事件
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    //sp转px
    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //测量文本的宽高
    private void measureTextWidthAndHeight(){
        Rect rect = new Rect();
        for (int i = 0; i < mData.size(); i++){
            String item = mData.get(i).toString();
            mCenterTextPaint.getTextBounds(item, 0, item.length(), rect);
            int itemWidth = rect.width();
            int itemHeight = rect.height();
            if (itemWidth > mMaxTextWidth)
                mMaxTextWidth = itemWidth;
            if (itemHeight > mMaxTextHeight)
                mMaxTextHeight = itemHeight;
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
        mScheduledFuture = mScheduledExecutorService.scheduleWithFixedDelay(null, 0, 10, TimeUnit.MILLISECONDS);
    }

    //手势处理
    private class WheelViewGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            //cancelSchedule();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mTotalScrollY = (int) (mTotalScrollY + distanceY);
            int initPositionCircleLength = mInitPosition * mItemHeight;
            int initPositionStartY = -1 * initPositionCircleLength;
            if (mTotalScrollY < initPositionStartY)
                mTotalScrollY = initPositionStartY;
            int circleLength = (mData.size() - 1) * mItemHeight;
            if (mTotalScrollY >= circleLength)
                mTotalScrollY = circleLength;
            invalidate();
            return true;
        }
    }

    private class ScrollRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
