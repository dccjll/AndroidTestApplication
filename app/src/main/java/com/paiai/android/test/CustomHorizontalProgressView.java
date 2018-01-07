package com.paiai.android.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义进度条
 */

public class CustomHorizontalProgressView extends View {

    private float lineSize = 0.5f;//线条粗细大小
    private int lineColor = 0x9b9b9b;//默认线条颜色
    private int dragedLineColor = 0xdcbc94;//已拖动的线条颜色
    private float slidingBlockRadius = 23;//滑块半径
    private int slidingBlockColor = 0xffffff;//滑块背景
    
    private Paint linePaint;//画默认线的画笔
    private Paint dragedLinePaint;//画当前进度线条的画笔
    private Paint slidingBlockPaint;//画滑块的画笔
    private int progress = 100;//当前进度
    private final int maxProgress = 100;//最大进度

    private float tempX;
    private float tempY;
    private float lastX;//最后一次点击的横坐标
    private float lastY;//最后一次点击的纵坐标

    public CustomHorizontalProgressView(Context context) {
        this(context, null);
    }

    public CustomHorizontalProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomHorizontalProgressView);
        lineSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.CustomHorizontalProgressView_lineSize, 0.5f));
        lineColor = typedArray.getColor(R.styleable.CustomHorizontalProgressView_lineColor, 0x9b9b9b);
        dragedLineColor = typedArray.getColor(R.styleable.CustomHorizontalProgressView_dragedLineColor, 0xdcbc94);
        slidingBlockRadius = typedArray.getFloat(R.styleable.CustomHorizontalProgressView_slidingBlockRadius, 23);
        slidingBlockColor = typedArray.getColor(R.styleable.CustomHorizontalProgressView_slidingBlockColor, 0xffffff);
        typedArray.recycle();
        
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineSize);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        dragedLinePaint = new Paint();
        dragedLinePaint.setColor(dragedLineColor);
        dragedLinePaint.setStrokeWidth(lineSize);
        dragedLinePaint.setStyle(Paint.Style.STROKE);
        dragedLinePaint.setAntiAlias(true);

        slidingBlockPaint = new Paint();
        slidingBlockPaint.setColor(slidingBlockColor);
        linePaint.setStyle(Paint.Style.FILL);
        slidingBlockPaint.setAntiAlias(true);

        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) slidingBlockRadius;
        setMeasuredDimension(width, DensityUtils.dp2px(getContext(), height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画默认的线
        float startY = getHeight()/2;
        canvas.drawLine(0, startY, getWidth(),  startY, linePaint);
        //画进度线
        float startX = (getWidth() * progress * 1.0f)/maxProgress;
        canvas.drawLine(0, startY, startX, startY, dragedLinePaint);
        //画滑块
        float slidingBlockX = slidingBlockRadius + ((getWidth() - slidingBlockRadius * 2) * progress * 1.0f)/maxProgress;
        canvas.drawCircle(slidingBlockX, startY, slidingBlockRadius, slidingBlockPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
            tempX = event.getX();
            tempY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setProgress(computeProgress(adjustMoveX(event.getX())));
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {//抬起
            lastX = event.getX();
            lastY = event.getY();
            if (tempX == lastX && tempY == lastY) {//响应点击事件
                setProgress(computeProgress(adjustMoveX(lastX)));
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 调整移动的横坐标，防止越界
     * @param moveX 移动的横坐标
     * @return  调整后的横坐标
     */
    private float adjustMoveX(float moveX) {
        if (moveX - getLeft() < slidingBlockRadius) {
            moveX = getLeft() + slidingBlockRadius;
        } else if (moveX - getLeft() > getWidth() - slidingBlockRadius) {
            moveX = getLeft() + getWidth() - slidingBlockRadius;
        }
        return moveX;
    }

    /**
     * 根据X坐标计算进度条的百分比数字值
     * @param lastX 当前X坐标
     * @return 百分比数字值
     */
    private int computeProgress(float lastX) {
        float distanceX = lastX - getLeft() - slidingBlockRadius;
        return (int) ((distanceX * 100)/((getWidth() - slidingBlockRadius * 2)));
    }

    /**
     * 获取当前进度
     * @return 当前进度
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置当前进度
     * @param progress 当前进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
