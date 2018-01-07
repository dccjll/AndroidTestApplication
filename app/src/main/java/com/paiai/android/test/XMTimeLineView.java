package com.paiai.android.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * 雄迈摄像机用到的时间轴控件
 */

public class XMTimeLineView extends HorizontalScrollView {

    private float lineSize = 0.5f;
    private int lineColor = 0x9B9B9B;
    private int tinySpace = 10;
    private float longVerticalLineLength = 10;
    private float shortVerticalLineLength = 5;
    private int unitTimeLength = 10;//默认小单元格表示10分钟
    private int unitSize = 6;//默认6个小单元格组成一个大单元格
//    private float indicatorLineSize;
//    private float indicatorRadius;
//    private int indicatorLineColor;
    private float timeFontSize = 9;
    private int timeFontColor = 0x333333;

    public XMTimeLineView(Context context) {
        this(context, null);
    }

    public XMTimeLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XMTimeLineView);
        lineSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_lineSize, lineSize));
        lineColor = typedArray.getColor(R.styleable.XMTimeLineView_lineColor, lineColor);
        tinySpace = DensityUtils.dp2px(getContext(), typedArray.getInt(R.styleable.XMTimeLineView_tinySpace, tinySpace));
        longVerticalLineLength = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_longVerticalLineLength, longVerticalLineLength));
        shortVerticalLineLength = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_shortVerticalLineLength, shortVerticalLineLength));
        unitTimeLength = DensityUtils.dp2px(getContext(), typedArray.getInt(R.styleable.XMTimeLineView_unitTimeLength, unitTimeLength));
        unitSize = DensityUtils.dp2px(getContext(), typedArray.getInt(R.styleable.XMTimeLineView_unitSize, unitSize));
//        indicatorLineSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_indicatorLineSize, indicatorLineSize));
//        indicatorRadius = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_indicatorRadius, indicatorRadius));
//        indicatorLineColor = typedArray.getColor(R.styleable.XMTimeLineView_indicatorLineColor, indicatorLineColor);
        typedArray.recycle();
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
}
