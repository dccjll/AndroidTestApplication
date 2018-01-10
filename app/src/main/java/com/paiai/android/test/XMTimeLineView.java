package com.paiai.android.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 雄迈摄像机用到的时间轴控件
 */

public class XMTimeLineView extends HorizontalScrollView {

    private static final String TAG = "XMTimeLineView";

    //描述时间线粗细的属性(单位：dp)
    private float lineSize = 0.5f;
    //线的颜色
    private int lineColor = 0xFF9B9B9B;
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
    private int timeFontColor = 0xFF333333;
    //时间距离标尺的垂直距离
    private float verticalSpaceSize = 3.5f;
    //视频回放指示器图片的资源ID
    private int pointerImgResID = R.mipmap.video_playback_pointer;
    //无录像的背景颜色
    private int noVideoBgColor = 0xFFE7E7E7;
    //普通录像的背景颜色
    private int commmonVideoBgColor = 0xFFFFCFBE;
    //报警录像的背景颜色
    private int alarmVideoBgColor = 0xFFFF7345;

    //控件宽度
    private float mWidth;
    //控件高度
    private float mHeight;
    //控件最小高度
    private float minHeight;
    //指定高度与需要的最新高度之间的差值
    private float halfHeightPlus;
    //录像背景的主面板高度
    private int mainBlockHeight;
    //录像背景的主面板宽度
    private float mainBlockWidth;
    //一天有多少分钟
    private static final long totalMunite = 24 * 60;
    //时间信息列表
    private List<String> timeList = new ArrayList<>();
    //普通录像的时间轴列表
    private List<VideoTimeLine> commonVideoTimeLineList = new ArrayList<>();
    //报警录像的时间轴列表
    private List<VideoTimeLine> alarmVideoTimeLineList = new ArrayList<>();
    //屏幕宽度
    private int screenWidth;
    //小格子的总数量
    private int totalSmallGridNum;
    //大格子的总数量
    private int totalBigGridNum;
    //主面板画笔
    private Paint mainBlockPaint;
    //画线的画笔
    private Paint linePaint;
    //时间文字画笔
    private Paint fontPaint;
    //画指示器图片的画笔
    private Paint pointPaint;
    //指示器图片
    private Bitmap pointBitmap;
    //按下时刻的横坐标
    private float lastX;
    //视图滑动的偏移量
    private float offset;

    public XMTimeLineView(Context context) {
        this(context, null);
    }

    public XMTimeLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //加载属性
        loadAttibute(context, attrs);
        //加载画笔
        loadPaint();
        //加载参数
        loadParams();
        //响应点击
        setClickable(true);
    }

    /**
     * 加载自定义属性
     */
    private void loadAttibute(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XMTimeLineView);
        lineSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_lineSize, lineSize));
        lineColor = typedArray.getColor(R.styleable.XMTimeLineView_lineColor, lineColor);
        tinySpace = DensityUtils.dp2px(getContext(), typedArray.getInt(R.styleable.XMTimeLineView_tinySpace, tinySpace));
        longVerticalLineLength = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_longVerticalLineLength, longVerticalLineLength));
        shortVerticalLineLength = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_shortVerticalLineLength, shortVerticalLineLength));
        unitTimeLength = typedArray.getInt(R.styleable.XMTimeLineView_unitTimeLength, unitTimeLength);
        unitSize = typedArray.getInt(R.styleable.XMTimeLineView_unitSize, unitSize);
        timeFontSize = DensityUtils.sp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_timeFontSize, timeFontSize));
        timeFontColor = typedArray.getColor(R.styleable.XMTimeLineView_timeFontColor, timeFontColor);
        verticalSpaceSize = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_verticalSpaceSize, verticalSpaceSize));
        pointerImgResID = typedArray.getResourceId(R.styleable.XMTimeLineView_pointerImgResID, pointerImgResID);
        noVideoBgColor = typedArray.getColor(R.styleable.XMTimeLineView_noVideoBgColor, noVideoBgColor);
        commmonVideoBgColor = typedArray.getColor(R.styleable.XMTimeLineView_commmonVideoBgColor, commmonVideoBgColor);
        alarmVideoBgColor = typedArray.getColor(R.styleable.XMTimeLineView_alarmVideoBgColor, alarmVideoBgColor);
        typedArray.recycle();
    }

    /**
     * 加载画笔
     */
    private void loadPaint() {
        //主面板画笔
        mainBlockPaint = new Paint();
        mainBlockPaint.setAntiAlias(true);
        mainBlockPaint.setDither(true);
        mainBlockPaint.setColor(noVideoBgColor);
        mainBlockPaint.setStyle(Paint.Style.FILL);
        //时间线画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(lineSize);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        //时间文字画笔
        fontPaint = new Paint();
        fontPaint.setAntiAlias(true);
        fontPaint.setDither(true);
        fontPaint.setTextSize(timeFontSize);
        fontPaint.setColor(timeFontColor);
        //指示器图片画笔
        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);
    }

    /**
     * 加载参数
     */
    private void loadParams() {
        //计算有多少个小格子
        totalSmallGridNum = (int) (totalMunite / unitTimeLength);
        //计算有多少个大格子
        totalBigGridNum = (int) (totalMunite / (unitTimeLength * unitSize));
        //指示器图片
        pointBitmap = ((BitmapDrawable)getResources().getDrawable(pointerImgResID)).getBitmap();
        //屏幕宽度
        screenWidth = SystemUtils.getScreenWidth(getContext()).getWidth();
        //计算主面板宽度(不包含开始与结束时间字符超出的部分)
        mainBlockWidth = totalSmallGridNum * tinySpace;
        //计算主面板高度
        mainBlockHeight = pointBitmap.getHeight();
        //控件最小高度
        minHeight = pointBitmap.getHeight() + lineSize + longVerticalLineLength + verticalSpaceSize + getTextSpaceHeight(timeFontSize);
        //计算需要画的时间列表
        timeList = computeTimeLine();
        //测量左边第一个时间信息的宽度
        float firstTimeWidth = fontPaint.measureText(timeList.get(0));
        //测量右边最后一个时间信息的宽度
        float endTimeWidth = fontPaint.measureText(timeList.get(timeList.size() - 1));
        //控件的总宽度为主面板的宽度加上第一和最后一个时间信息的宽度的各一半
        mWidth = mainBlockWidth + firstTimeWidth /2 + endTimeWidth /2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = minHeight;
        } else {
            if (height > minHeight) {
                mHeight = height;
                halfHeightPlus = (height - minHeight)/2;
            } else {
                mHeight = minHeight;
            }
        }
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //画时间线主面板的参数
        int mainBlockLeft;
        int mainBlockTop = (int) halfHeightPlus;
        int mainBlockRight;
        int mainBlockBottom = (int) (mainBlockHeight + halfHeightPlus);
        //画时间线的参数
        int timeLineStartX;
        int timeLineStartY = (int) (mainBlockHeight + halfHeightPlus);
        int timeLineEndX;
        int timeLineEndY = (int) (mainBlockHeight + halfHeightPlus);
        //画时间刻度的参数
        int ruleLineStartX;
        int ruleLineStartY = (int) (mainBlockHeight + lineSize + halfHeightPlus);
        //画时间文字的参数
        int textStartX;
        int textStartY = (int) (mainBlockHeight + lineSize + longVerticalLineLength + verticalSpaceSize + halfHeightPlus);
        //画标尺指示器图片的参数
        int pointBitmapX = (screenWidth - pointBitmap.getWidth())/2;
        int pointBitmapY = (int) halfHeightPlus;
        //调整参数
        if (mWidth <= screenWidth) {
            int startX = (int) ((screenWidth - mWidth)/2);
            int blockStartX = (int) ((screenWidth - mainBlockWidth)/2);
            mainBlockLeft = blockStartX;
            timeLineStartX = blockStartX;
            ruleLineStartX = blockStartX;
            textStartX = startX;
        } else {
            if (mainBlockWidth <= screenWidth) {
                int startX = (int) (- (mWidth - screenWidth)/2 + offset);
                float halfBlockOffset = (screenWidth - mainBlockWidth)/2;
                int blockStartX = (int) (halfBlockOffset + offset);
                mainBlockLeft = blockStartX;
                timeLineStartX = blockStartX;
                ruleLineStartX = blockStartX;
                textStartX = startX;
            } else {
                int startX = (int) (- (mWidth - screenWidth)/2 + offset);
                float halfBlockOffset = (mainBlockWidth - screenWidth)/2;
                int blockStartX = (int) (-halfBlockOffset + offset);
                mainBlockLeft = blockStartX;
                timeLineStartX = blockStartX;
                ruleLineStartX = blockStartX;
                textStartX = startX;
            }
        }
        //画时间刻度
        canvas.drawLine(ruleLineStartX, ruleLineStartY, ruleLineStartX, ruleLineStartY + longVerticalLineLength, linePaint);
        for (int i=0; i<totalSmallGridNum; i++) {
            ruleLineStartX += tinySpace;
            float ruleEndY = ruleLineStartY + shortVerticalLineLength;
            if ((i + 1) % unitSize == 0) {
                ruleEndY = ruleLineStartY + longVerticalLineLength;
            }
            canvas.drawLine(ruleLineStartX, ruleLineStartY, ruleLineStartX, ruleEndY, linePaint);
        }
        mainBlockRight = ruleLineStartX;
        timeLineEndX = ruleLineStartX;
        //画时间文字
        float bigGridWidth = unitSize * tinySpace;//大格子的宽度
        for (int i = 0; i < timeList.size(); i++) {
            canvas.drawText(timeList.get(i), textStartX + bigGridWidth * i, textStartY, fontPaint);
        }
        //画时间线主面板
        canvas.drawRect(mainBlockLeft, mainBlockTop, mainBlockRight, mainBlockBottom, mainBlockPaint);
        //画时间线
        canvas.drawLine(timeLineStartX, timeLineStartY, timeLineEndX, timeLineEndY, linePaint);
        //画标尺指示器图片
        canvas.drawBitmap(pointBitmap, pointBitmapX, pointBitmapY, pointPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = ev.getRawX();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float moveX = ev.getRawX();
            float currentOffset = moveX - lastX;
            offset +=  currentOffset;
            Log.i(TAG, "moveX=" + moveX + "\nlastX=" + lastX + "\ncurrentOffset=" + currentOffset + "\n");
            lastX = moveX;
            float minClickableY = (mHeight - minHeight)/2;
            float maxClickableY = minClickableY + mainBlockHeight;
            if (mWidth <= screenWidth || ev.getY() < minClickableY || ev.getY() > maxClickableY) {
                return true;
            }
            requestInvalidate("MOVE");

        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            requestInvalidate("UP");
        }
        return true;
    }

    /**
     * 计算滑动偏移量，重绘视图
     */
    private void requestInvalidate(String tag) {
        float maxOffset = mainBlockWidth/2;
        if (offset < -maxOffset) {
            offset = -maxOffset;
        }
        if (offset > maxOffset) {
            offset = maxOffset;
        }
        Log.i(TAG, tag + "-------\noffset=" + offset + "\nmaxOffset(+-)=" + maxOffset);
        postInvalidate();
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

    /**
     * 计算需要画的时间信息列表
     */
    private List<String> computeTimeLine() {
        List<Date> dateList = new ArrayList<>();
        Date startTime = new Date();
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);
        dateList.add(startTime);
        for (int i = 1; i <= totalBigGridNum; i++) {
            Date date = new Date();
            //计算当前大格子的分钟量
            int currentGridMunites = i * unitTimeLength * unitSize;
            date.setHours(currentGridMunites / 60);
            date.setMinutes(currentGridMunites % 60);
            dateList.add(date);
        }
        List<String> timeList = new ArrayList<>();
        for (Date date : dateList) {
            String hour = date.getHours() + "";
            String minute = date.getMinutes() + "";
            String dateString = (hour.length() == 1 ? "0" + hour : hour) + ":" + (minute.length() == 1 ? "0" + minute : minute);
            timeList.add(dateString);
        }
        return timeList;
    }

    /**
     * 获取控件宽度
     */
    public float getmWidth() {
        return mWidth;
    }

    /**
     * 获取控件高度
     */
    public float getmHeight() {
        return mHeight;
    }
}
