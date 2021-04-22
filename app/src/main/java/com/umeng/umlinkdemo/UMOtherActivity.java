package com.umeng.umlinkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UMOtherActivity extends Activity {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umother);
        mTextView = (TextView) findViewById(R.id.valueText);
        mTextView.setText("OTHER");
    }
}