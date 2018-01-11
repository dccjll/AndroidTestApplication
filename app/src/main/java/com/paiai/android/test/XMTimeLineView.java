package com.paiai.android.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.paiai.android.test.utils.BitmapUtils;
import com.paiai.android.test.utils.DensityUtils;
import com.paiai.android.test.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 雄迈摄像机用到的时间轴控件
 */

public class XMTimeLineView extends HorizontalScrollView {

    private static final String TAG = "XMTimeLineView";

    //描述时间线粗细的属性(单位：dp)
    private float lineSize = 0.5f;
    //线的颜色
    private int lineColor = 0xFF9B9B9B;
    //录像背景的主面板高度
    private float mainBlockHeight = 60;
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
    private float verticalSpaceSize = 14f;
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
    //录像背景的主面板宽度
    private float mainBlockWidth;
    //一天有多少分钟
    private static final long totalMunite = 24 * 60;
    //一天多少秒
    private static final long totalSeconds = totalMunite * 60;
    //一秒钟所占的宽度
    private float unitSecondWidth;
    //视图加载时，时间指示器指示的默认时间
    private String currentTime = "12:00:00";
    //默认时间的秒数
    private static final long defaultTimeSeconds = 12*60*60;
    //点击错位滑动兼容，点击时会滑动一小段距离，设定最小滑动距离偏移量
    private static final int MinSlideOffsetX = 10;
    //当前时间的秒数
    private long currentTimeSeconds;
    //时间信息列表
    private List<String> timeList = new ArrayList<>();
    //普通录像的时间轴列表
    private List<VideoTimeLine> commonVideoTimeLineList = new ArrayList<>();
    //报警录像的时间轴列表
    private List<VideoTimeLine> alarmVideoTimeLineList = new ArrayList<>();
    //屏幕宽度
    private int screenWidth;
    //测量左边第一个时间信息的宽度
    private float firstTimeWidth;
    //小格子的总数量
    private int totalSmallGridNum;
    //大格子的总数量
    private int totalBigGridNum;
    //画没有视频的矩形区域的画笔
    private Paint noVideoPaint;
    //画线的画笔
    private Paint linePaint;
    //时间文字画笔
    private Paint fontPaint;
    //画指示器图片的画笔
    private Paint pointPaint;
    //画普通视频矩形色块的画笔
    private Paint commonVideoPaint;
    //画报警视频矩形色块的画笔
    private Paint alarmVideoPaint;
    //否是显示测试边框
    private static final boolean SHOW_TEST_BOUND_DRAW = false;
    //画测试边框的画笔
    private Paint testBoundPaint;
    //指示器图片
    private Bitmap pointBitmap;
    //按下时刻的横坐标
    private float downX;
    //不断移动的横坐标
    private float lastX;
    //视图滑动的偏移量
    private float offset;
    //画线坐标偏移量
    private float lineOffsetSize;
    //主面板左边起始坐标
    private int mainBlockLeft;
    //主面板右边结束坐标
    private int mainBlockRight;
    //从日期中提取规定时间信息格式的时间格式化对象
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    //时间信息改变监听器
    private OnXMTimeChangeListener onXMTimeChangeListener;
    interface OnXMTimeChangeListener {
        void onTimeChange(String time);
    }

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
        mainBlockHeight = DensityUtils.dp2px(getContext(), typedArray.getFloat(R.styleable.XMTimeLineView_mainBlockHeight, mainBlockHeight));
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
        noVideoPaint = new Paint();
        noVideoPaint.setAntiAlias(true);
        noVideoPaint.setDither(true);
        noVideoPaint.setColor(noVideoBgColor);
        noVideoPaint.setStyle(Paint.Style.FILL);
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
        //普通视频矩形色块画笔
        commonVideoPaint = new Paint();
        commonVideoPaint.setAntiAlias(true);
        commonVideoPaint.setDither(true);
        commonVideoPaint.setColor(commmonVideoBgColor);
        commonVideoPaint.setStyle(Paint.Style.FILL);
        //报警视频矩形色块画笔
        alarmVideoPaint = new Paint();
        alarmVideoPaint.setAntiAlias(true);
        alarmVideoPaint.setDither(true);
        alarmVideoPaint.setColor(alarmVideoBgColor);
        alarmVideoPaint.setStyle(Paint.Style.FILL);
        //测试画笔
        if (SHOW_TEST_BOUND_DRAW) {
            testBoundPaint = new Paint();
            testBoundPaint.setAntiAlias(true);
            testBoundPaint.setDither(true);
            testBoundPaint.setStyle(Paint.Style.STROKE);
            testBoundPaint.setStrokeWidth(5);
            testBoundPaint.setColor(Color.RED);
        }
    }

    /**
     * 加载参数
     */
    private void loadParams() {
        //计算画线偏移量
        lineOffsetSize = lineSize/2;
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
        //一秒钟所占的宽度
        unitSecondWidth = (mainBlockWidth * 1)/totalSeconds;
        //当前时间秒数
        currentTimeSeconds = 12*60*60;
        //缩放指示器图片高度为主面板高度
        pointBitmap = BitmapUtils.zoomBitmap(pointBitmap, pointBitmap.getWidth(), (int) mainBlockHeight);
        //控件最小高度
        minHeight = mainBlockHeight + lineSize + longVerticalLineLength + verticalSpaceSize + getTextBottomSpaceHeight(timeFontSize);
        //计算需要画的时间列表
        timeList = computeTimeLine();
        //测量左边第一个时间信息的宽度
        firstTimeWidth = fontPaint.measureText(timeList.get(0));
        float endTimeWidth = fontPaint.measureText(timeList.get(timeList.size() - 1));
        //控件的总宽度为主面板的宽度加上第一和最后一个时间信息的宽度的各一半，再减去时间居中的线宽(时间的中心点与竖线的中心点对齐)
        mWidth = mainBlockWidth + lineOffsetSize * 2 + (firstTimeWidth /2 - lineOffsetSize) + (endTimeWidth/2 - lineOffsetSize);
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
        //画主面板的参数
        int mainBlockTop = (int) halfHeightPlus;
        int mainBlockBottom = (int) (mainBlockHeight + halfHeightPlus);
        //画时间线的参数
        int timeLineStartX;
        int timeLineStartY = (int) (mainBlockHeight + halfHeightPlus + lineOffsetSize);
        int timeLineEndX;
        //画时间刻度的参数
        int ruleLineStartX;
        int ruleLineStartY = (int) (halfHeightPlus + mainBlockHeight + lineSize);
        //画时间文字的参数
        int textStartX;
        int textStartY = (int) (halfHeightPlus + mainBlockHeight + lineSize + longVerticalLineLength + verticalSpaceSize);
        //画标尺指示器图片的参数
        int pointBitmapX = (int) ((screenWidth - pointBitmap.getWidth())/2 + lineOffsetSize);
        int pointBitmapY = (int) halfHeightPlus;
        //调整参数
        int startX;
        int blockStartX;
        if (mWidth <= screenWidth) {
            startX = (int) ((screenWidth - mWidth)/2);
            blockStartX = (int) ((screenWidth - mainBlockWidth)/2 + lineOffsetSize);
        } else {
            startX = (int) (- (mWidth - screenWidth)/2 + offset);
            if (mainBlockWidth <= screenWidth) {
                float halfBlockOffset = (screenWidth - mainBlockWidth)/2;
                blockStartX = (int) (halfBlockOffset + offset + lineOffsetSize);
            } else {
                float halfBlockOffset = (mainBlockWidth - screenWidth)/2;
                blockStartX = (int) (-halfBlockOffset + offset + lineOffsetSize);
            }
        }
        mainBlockLeft = blockStartX;
        mainBlockRight = blockStartX + totalSmallGridNum * tinySpace;
        timeLineStartX = (int) (blockStartX - lineOffsetSize);
        ruleLineStartX = blockStartX;
        timeLineEndX = (int) (mainBlockRight + lineOffsetSize);
        //计算左边时间的中心点
        int firstTimePosition = (int) (startX + firstTimeWidth/2);
        textStartX = startX + (blockStartX - firstTimePosition);
        //画时间线主面板
        canvas.drawRect(mainBlockLeft, mainBlockTop, mainBlockRight, mainBlockBottom, noVideoPaint);
        //画时间线
        canvas.drawLine(timeLineStartX, timeLineStartY, timeLineEndX, timeLineStartY, linePaint);
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
        //画时间文字
        float bigGridWidth = unitSize * tinySpace;//大格子的宽度
        for (int i = 0; i < timeList.size(); i++) {
            canvas.drawText(timeList.get(i), textStartX + bigGridWidth * i, textStartY, fontPaint);
        }
        //画标尺指示器图片
        canvas.drawBitmap(pointBitmap, pointBitmapX, pointBitmapY, pointPaint);
        //画普通视频的矩形色块
        for (VideoTimeLine videoTimeLine : commonVideoTimeLineList) {
            Float[] rect = computeRectLeftRightBound(videoTimeLine.getStartTime(), videoTimeLine.getEndTime());
            if (rect == null || rect.length != 2) {
                continue;
            }
            boolean flag = true;
            for (Float bound : rect) {
                if (bound == null) {
                    flag = false;
                }
            }
            if (!flag) {
                continue;
            }
            canvas.drawRect(rect[0], mainBlockTop, rect[1], mainBlockBottom, commonVideoPaint);
        }
        //画报警视频的矩形色块
        for (VideoTimeLine videoTimeLine : alarmVideoTimeLineList) {
            Float[] rect = computeRectLeftRightBound(videoTimeLine.getStartTime(), videoTimeLine.getEndTime());
            if (rect == null || rect.length != 2) {
                continue;
            }
            boolean flag = true;
            for (Float bound : rect) {
                if (bound == null) {
                    flag = false;
                }
            }
            if (!flag) {
                continue;
            }
            canvas.drawRect(rect[0], mainBlockTop, rect[1], mainBlockBottom, alarmVideoPaint);
        }
        //测试画图
        if (SHOW_TEST_BOUND_DRAW) {
            canvas.drawRect(-(mWidth - screenWidth)/2 + offset + lineOffsetSize, halfHeightPlus, screenWidth + (mWidth - screenWidth)/2 + offset + lineOffsetSize, halfHeightPlus + minHeight, testBoundPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!insideMainBlock(ev.getX(), ev.getY())) {//按下区域不在主面板内部，不响应触屏与点击事件
                return false;
            }
            downX = ev.getX();
            lastX = ev.getX();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (mWidth <= screenWidth) {//控件宽度未超出屏幕，不响应滑动事件
                return true;
            }
            if (!insideMainBlock(ev.getX(), ev.getY())) {
                return true;
            }
            float moveX = ev.getX();
            float currentOffset = moveX - lastX;
            Log.i(TAG, "moveX=" + moveX + "\nlastX=" + lastX + "\ncurrentOffset=" + currentOffset + "\n");
            if (Math.abs(currentOffset) < MinSlideOffsetX) {
                return true;
            }
            lastX = moveX;
            //根据滑动位置计算偏移量
            computeOffsetByCurrentOffset(currentOffset);
            requestInvalidate("MOVE");
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (downX == lastX) {
                Log.i(TAG, "upuppppppp");
                handleClick();
            }
        }
        return true;
    }

    /**
     * 根据滑动位置计算偏移量
     */
    private void computeOffsetByCurrentOffset(float currentOffset) {
        offset +=  currentOffset;
    }

    /**
     * 根据当前点击的位置计算偏移量
     */
    private void computeOffsetByClickPosition() {
        offset += screenWidth/2 - lastX;
    }

    /**
     * 根据当前时间计算滑动的偏移量
     */
    private Float computeOffsetByCurrentTime(String currentTime) {
        Long timeSeconds = computeCurrentTimeTotalSeconds(currentTime);
        if (timeSeconds == null) {
            return null;
        }
        long timeOffset = defaultTimeSeconds - timeSeconds;
        return ((timeOffset * 1.0f)/totalSeconds) * mainBlockWidth;
    }

    /**
     * 根据当前时间计算矩形色块起始x坐标的的偏移量
     */
    private Float computeRectStartXOffsetByCurrentTime(String currentTime) {
        Long timeSeconds = computeCurrentTimeTotalSeconds(currentTime);
        if (timeSeconds == null) {
            return null;
        }
        return ((timeSeconds * 1.0f)/totalSeconds) * mainBlockWidth;
    }

    /**
     * 计算矩形色块起始x坐标与终点x坐标
     */
    private Float[] computeRectLeftRightBound(Date startDate, Date endDate) {
        Float[] timeArr = new Float[2];
        String startTime = null;
        String endTime = null;
        try {
            startTime = sdf.format(startDate);
            endTime = sdf.format(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return null;
        }
        Float startOffset = computeRectStartXOffsetByCurrentTime(startTime);
        Float endOffset = computeRectStartXOffsetByCurrentTime(endTime);
        if (startOffset == null || endOffset ==  null || startOffset >= endOffset) {
            return null;
        }
        timeArr[0] = mainBlockLeft + startOffset;
        timeArr[1] = mainBlockLeft + endOffset;
        return timeArr;
    }

    /**
     * 计算当前时间的总秒数
     */
    private Long computeCurrentTimeTotalSeconds(String currentTime) {
        if (TextUtils.isEmpty(currentTime)) {
            return null;
        }
        String[] timeAttr = currentTime.split(":");
        if (timeAttr.length != 3) {
            return null;
        }
        for (String seg : timeAttr) {
            if (TextUtils.isEmpty(seg) || !(seg.length() == 1 || seg.length() == 2)) {
                return null;
            }
        }
        String hours = timeAttr[0];
        String minutes = timeAttr[1];
        String seconds = timeAttr[2];
        return Long.parseLong(hours) * 60 * 60 + Long.parseLong(minutes) * 60 + Long.parseLong(seconds);
    }

    /**
     * 响应点击
     */
    private void handleClick() {
        computeOffsetByClickPosition();
        requestInvalidate("click");

    }

    /**
     * 计算按下的x坐标是否在主面板内
     */
    private boolean xInsideMainBlock(float x) {
        return x >= mainBlockLeft && x <= mainBlockRight;
    }

    /**
     * 计算按下的y坐标是否在主面板内
     */
    private boolean yInsideMainBlock(float y) {
        float minClickableY = halfHeightPlus;
        float maxClickableY = halfHeightPlus + mainBlockHeight;
        return y >= minClickableY && y <= maxClickableY;
    }

    /**
     * 计算按下的区域是否在主面板内
     */
    private boolean insideMainBlock(float x, float y) {
        return xInsideMainBlock(x) && yInsideMainBlock(y);
    }

    /**
     * 重绘视图
     */
    private void requestInvalidate(String tag) {
        float maxOffset = mainBlockWidth/2;
        if (offset < -maxOffset + unitSecondWidth) {
            offset = -maxOffset + unitSecondWidth;
        }
        if (offset > maxOffset) {
            offset = maxOffset;
        }
        Log.i(TAG, tag + "-------\noffset=" + offset + "\nmaxOffset(+-)=" + maxOffset);
        postInvalidate();
        computeCurrentTime();
        if (currentTime != null) {
            Log.i(TAG, "currentTime=====" + currentTime);
            if (onXMTimeChangeListener != null) {
                onXMTimeChangeListener.onTimeChange(currentTime);
            }
        }
    }

    /**
     * 根据当前偏移量计算指示的时间
     */
    private void computeCurrentTime() {
        //计算时间偏移的秒数
        if (offset != 0) {
            float pecent = Math.abs(offset)/mainBlockWidth;
            float totalSecondsOffset = totalSeconds * pecent;
            currentTimeSeconds = (long) (defaultTimeSeconds + totalSecondsOffset * (offset > 0 ? -1 : 1));
            if (currentTimeSeconds < 0) {
                currentTimeSeconds = 0;
            } else if (currentTimeSeconds > totalSeconds - 1) {
                currentTimeSeconds = totalSeconds - 1;
            }
            int hours = (int) (currentTimeSeconds/(60*60));
            int minutes = (int) ((currentTimeSeconds%(60*60))/60);
            int seconds = (int) ((currentTimeSeconds%(60*60))%60);
            currentTime = (hours < 10 ? "0" + hours : "" + hours) + ":" + (minutes < 10 ? "0" + minutes : "" + minutes) + ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
        }
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
        requestInvalidate("setVideoTimeLineList");
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
    private float getTextBottomSpaceHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return Math.abs(fontMetrics.bottom);
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

    /**
     * 设置当前时间
     */
    public void setCurrentTime(String currentTime) {
        Float timeFloat = computeOffsetByCurrentTime(currentTime);
        if (timeFloat == null) {
            return;
        }
        offset = timeFloat;
        this.currentTime = currentTime;
        requestInvalidate("setCurrentTime");

    }

    /**
     * 获取当前时间
     */
    public String getCurrentTime() {
        return currentTime;
    }

    /**
     * 设置时间状态改变监听器
     */
    public void setOnXMTimeChangeListener(OnXMTimeChangeListener onXMTimeChangeListener) {
        this.onXMTimeChangeListener = onXMTimeChangeListener;
    }
}
