package com.example.android.arkanoid;

import android.content.Intent;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.arkanoid.Util.AudioUtil;

public class SplashScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private Button tapToStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("start");

        AudioUtil.loadAudio("splashScreen", R.raw.splashscreen, this);
        AudioUtil.getMediaPlayer("splashScreen").setLooping(true);
        AudioUtil.getMediaPlayer("splashScreen").start();

        this.tapToStartButton = this.findViewById(R.id.tapToStartButton);
        if(this.tapToStartButton != null){
            this.tapToStartButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.tapToStartButton)) {
            Intent intent = new Intent(this, MainActivity.class);
            AudioUtil.getMediaPlayer("splashScreen").stop();
            this.startActivity(intent);
        }
    }
}