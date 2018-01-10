package com.paiai.android.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.paiai.android.test.utils.DensityUtils;
import com.paiai.android.test.utils.SystemUtils;

/**
 * 自定义进度条
 */

public class TestView extends View {
    //主面板画笔
    private Paint paint;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //主面板画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
//        //画默认的线
//        float startY = getHeight()/2;
//        canvas.drawLine(0, startY, getWidth(),  startY, linePaint);
//        //画进度线
//        float startX = (getWidth() * progress * 1.0f)/maxProgress;
//        canvas.drawLine(0, startY, startX, startY, dragedLinePaint);
//        //画滑块
//        float slidingBlockX = slidingBlockRadius + ((getWidth() - slidingBlockRadius * 2) * progress * 1.0f)/maxProgress;
//        canvas.drawCircle(slidingBlockX, startY, slidingBlockRadius, slidingBlockPaint);
        canvas.drawRect(-DensityUtils.dp2px(getContext(), 50), DensityUtils.dp2px(getContext(), 50), SystemUtils.getScreenWidth(getContext()).getWidth() + DensityUtils.dp2px(getContext(), 50), DensityUtils.dp2px(getContext(), 250), paint);
        canvas.drawText("Hello", 50, 50, paint);
    }
}
