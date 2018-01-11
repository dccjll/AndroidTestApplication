package com.paiai.android.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private XMTimeLineView xmTimeLineView;
    private EditText timeEditText;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xmTimeLineView = findViewById(R.id.timeLineView);
        timeEditText = findViewById(R.id.timeEditText);

        xmTimeLineView.setVideoTimeLineList(buildCommVideoList(), buildAlarmVideoList());
    }

//    public void onLeftScrollClick(View view) {
//        view.scrollBy(100, 0);
//    }

    public void settingTime(View view) {
        String time = timeEditText.getText().toString().trim();
        xmTimeLineView.setCurrentTime(time);
    }

    private List<VideoTimeLine> buildCommVideoList() {
        List<VideoTimeLine> videoTimeLineList = new ArrayList<>();
        try {
            VideoTimeLine videoTimeLine1 = new VideoTimeLine();
            videoTimeLine1.setStartTime(sdf.parse("01:23:10"));
            videoTimeLine1.setEndTime(sdf.parse("01:23:50"));
            VideoTimeLine videoTimeLine2 = new VideoTimeLine();
            videoTimeLine2.setStartTime(sdf.parse("1:50:23"));
            videoTimeLine2.setEndTime(sdf.parse("2:05:50"));
            VideoTimeLine videoTimeLine3 = new VideoTimeLine();
            videoTimeLine3.setStartTime(sdf.parse("5:44:32"));
            videoTimeLine3.setEndTime(sdf.parse("8:23:50"));
            VideoTimeLine videoTimeLine4 = new VideoTimeLine();
            videoTimeLine4.setStartTime(sdf.parse("7:20:46"));
            videoTimeLine4.setEndTime(sdf.parse("07:20:46"));
            videoTimeLineList.add(videoTimeLine1);
            videoTimeLineList.add(videoTimeLine2);
            videoTimeLineList.add(videoTimeLine3);
            videoTimeLineList.add(videoTimeLine4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return videoTimeLineList;
    }

    private List<VideoTimeLine> buildAlarmVideoList() {
        List<VideoTimeLine> videoTimeLineList = new ArrayList<>();
        try {
            VideoTimeLine videoTimeLine1 = new VideoTimeLine();
            videoTimeLine1.setStartTime(sdf.parse("9:50:10"));
            videoTimeLine1.setEndTime(sdf.parse("09:56:50"));
            VideoTimeLine videoTimeLine2 = new VideoTimeLine();
            videoTimeLine2.setStartTime(sdf.parse("10:12:23"));
            videoTimeLine2.setEndTime(sdf.parse("12:35:50"));
            VideoTimeLine videoTimeLine3 = new VideoTimeLine();
            videoTimeLine3.setStartTime(sdf.parse("15:44:32"));
            videoTimeLine3.setEndTime(sdf.parse("20:23:50"));
            VideoTimeLine videoTimeLine4 = new VideoTimeLine();
            videoTimeLine4.setStartTime(sdf.parse("21:44:32"));
            videoTimeLine4.setEndTime(sdf.parse("21:44:32"));
            videoTimeLineList.add(videoTimeLine1);
            videoTimeLineList.add(videoTimeLine2);
            videoTimeLineList.add(videoTimeLine3);
            videoTimeLineList.add(videoTimeLine4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return videoTimeLineList;
    }
}
