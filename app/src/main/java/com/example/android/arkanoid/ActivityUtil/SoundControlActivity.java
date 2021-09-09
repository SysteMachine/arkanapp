package com.example.android.arkanoid.ActivityUtil;

import android.support.v7.app.AppCompatActivity;

import com.example.android.arkanoid.Util.AudioUtil;

public class SoundControlActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        super.onPause();
        AudioUtil.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioUtil.resume();
    }
}