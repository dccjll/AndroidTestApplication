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

public class TestView extends View {


    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
}
