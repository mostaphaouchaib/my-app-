package com.spacester.tweetsterupdate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.spacester.tweetsterupdate.NightMode;
import com.spacester.tweetsterupdate.R;

public class AboutActivity extends AppCompatActivity {

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}