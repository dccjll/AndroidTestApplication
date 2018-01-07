package com.paiai.android.test;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 雄迈摄像机用到的时间轴控件
 */

public class XMTimeLineView extends HorizontalScrollView {


    public XMTimeLineView(Context context) {
        this(context, null);
    }

    public XMTimeLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
//
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {//抬起
//
//        }
        return super.onTouchEvent(event);
    }
}
