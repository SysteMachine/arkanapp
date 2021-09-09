package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import com.example.android.arkanoid.ActivityUtil.SoundControlActivity;

public class crediti_activity extends SoundControlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crediti);
    }

    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(this, main_menu_activity.class));
    }
}