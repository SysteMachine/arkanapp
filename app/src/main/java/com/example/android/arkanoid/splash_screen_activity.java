package com.example.android.arkanoid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.arkanoid.Util.AudioUtil;

public class splash_screen_activity extends AppCompatActivity implements View.OnClickListener {
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

        AudioUtil.loadAudio("background_music", R.raw.background_music, this);
        AudioUtil.getMediaPlayer("background_music").setLooping(true);
        AudioUtil.getMediaPlayer("background_music").start();


        ImageView splashscreen = (ImageView)findViewById(R.id.splashscreen);
        Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splashscreen_animation);
        splashscreen.startAnimation(aniSlide);


        this.tapToStartButton = this.findViewById(R.id.tapToStartButton);
        if(this.tapToStartButton != null){
            //Avviamo l'animazione del pulsante
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.tap_to_start_button_animation);
            this.tapToStartButton.startAnimation(animation);

            //Listener del tocco
            this.tapToStartButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.tapToStartButton)) {
            Intent intent = new Intent(this, login_activity.class);
            this.startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
    }
}