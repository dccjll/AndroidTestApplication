package com.paiai.android.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 雄迈摄像机用到的时间轴控件
 */

public class XMTimeLineView extends HorizontalScrollView {

    //描述时间线粗细的属性(单位：dp)
    private float lineSize = 0.5f;
    //线的颜色
    private int lineColor = 0x9B9B9B;
    //一个小格子的宽度
    private int tinySpace = 10;
    //长竖线长度
    private float longVerticalLineLength = 10;
    //短竖线长度
    private float shortVerticalLineLength = 5;
    //每一小格表示多长时间(单位：分钟)
    private int unitTimeLength = 10;//默认小单元格表示10分钟
    //几个小格连成一个大格
    private int unitSize = 6;//默认6个小单元格组成一个大单元格
    //时间字体大小
    private float timeFontSize = 9;
    //时间字体颜色
    private int timeFontColor = 0x333333;
    //时间距离标尺的垂直距离
    private float verticalSpaceSize = 3.5f;
    //视频回放指示器图片的资源ID
    private int pointerImgResID = R.mipmap.video_playback_pointer;
    //无录像的背景颜色
    private int noVideoBgColor = 0xE7E7E7;
    //普通录像的背景颜色
    private int commmonVideoBgColor = 0xFFCFBE;
    //报警录像的背景颜色
    private int alarmVideoBgColor = 0xFF7345;

    //录像背景的主面板高度
    private int mainBlockHight;
    //录像背景的主面板宽度
    private float mainBlockWidth;

    //总的时间线
    private VideoTimeLine totalVideoTimeLine;
    //普通录像的时间轴列表
    private List<VideoTimeLine> commonVideoTimeLineList = new ArrayList<>();
    //报警录像的时间轴列表
    private List<VideoTimeLine> alarmVideoTimeLineList = new ArrayList<>();

    //小格子的总数量
    private int totalSmallGridNum;
    //主面板画笔
    private Paint mainBlockPaint;

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
        timeFontSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_timeFontSize, timeFontSize));
        timeFontColor = typedArray.getColor(R.styleable.XMTimeLineView_timeFontColor, timeFontColor);
        verticalSpaceSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_verticalSpaceSize, verticalSpaceSize));
        pointerImgResID = typedArray.getResourceId(R.styleable.XMTimeLineView_pointerImgResID, pointerImgResID);
        typedArray.recycle();
        setClickable(true);
        //初始化总的时间线
        totalVideoTimeLine = new VideoTimeLine();
        Date startTime = new Date();
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);
        Date endTime = new Date();
        endTime.setHours(23);
        endTime.setMinutes(59);
        endTime.setSeconds(59);
        totalVideoTimeLine.setStartTime(startTime);
        totalVideoTimeLine.setEndTime(endTime);
        //计算主面板宽度
        computeMainBlockWidth();
        //主面板画笔
        mainBlockPaint = new Paint();
        mainBlockPaint.setAntiAlias(true);
        mainBlockPaint.setColor(noVideoBgColor);
        mainBlockPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 计算主面板宽度
     */
    private void computeMainBlockWidth() {
        //计算一天有多少分钟
        long totalMunite = 24 * 60;
        //计算需要多少个小格子
        totalSmallGridNum = (int) (totalMunite / unitTimeLength);
        mainBlockWidth = totalSmallGridNum * tinySpace + (totalSmallGridNum + 1) * lineSize;
    }

    /**
     * 计算主面板高度
     */
    private void computeMainBlockHeight(int mHeight) {
        mainBlockHight = (int) (mHeight - getTextSpaceHeight(timeFontSize) - verticalSpaceSize - longVerticalLineLength - lineSize);
    }

    /**
     * 设置视频段
     * @param commonVideoTimeLineList 普通录像视频段
     * @param alarmVideoTimeLineList 报警录像视频段
     */
    public void setVideoTimeLineList(List<VideoTimeLine> commonVideoTimeLineList, List<VideoTimeLine> alarmVideoTimeLineList) {
        if (commonVideoTimeLineList == null) {
            commonVideoTimeLineList = new ArrayList<>();
        }
        if (alarmVideoTimeLineList == null ) {
            alarmVideoTimeLineList = new ArrayList<>();
        }
        this.commonVideoTimeLineList = commonVideoTimeLineList;
        this.alarmVideoTimeLineList = alarmVideoTimeLineList;
    }

    /**
     * 获取普通录像的时间轴
     */
    public List<VideoTimeLine> getCommonVideoTimeLineList() {
        return commonVideoTimeLineList;
    }

    /**
     * 获取报警录像的时间轴
     */
    public List<VideoTimeLine> getAlarmVideoTimeLineList() {
        return alarmVideoTimeLineList;
    }

    /**
     * 根据字体大小获取字体所占空间的高度
     */
    private float getTextSpaceHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (float) Math.ceil(fontMetrics.bottom - fontMetrics.top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mHight = MeasureSpec.getSize(heightMeasureSpec);
        //计算主面板高度
        computeMainBlockHeight(mHight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画时间线主面板
        canvas.drawRect(getLeft(), getTop(), getLeft() + mainBlockWidth, getTop() + mainBlockHight, mainBlockPaint);
    }
}
