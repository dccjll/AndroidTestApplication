package com.paiai.android.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private XMTimeLineView xmTimeLineView;
    private EditText timeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xmTimeLineView = findViewById(R.id.timeLineView);
        timeEditText = findViewById(R.id.timeEditText);
    }

//    public void onLeftScrollClick(View view) {
//        view.scrollBy(100, 0);
//    }

    public void settingTime(View view) {
        String time = timeEditText.getText().toString().trim();
        xmTimeLineView.setCurrentTime(time);
    }
}
