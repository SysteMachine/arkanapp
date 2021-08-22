package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.GameElements.Stile;

public class MainActivity extends AppCompatActivity {
    private GameLoop gameLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.gameLoop = new GameLoop(this, 60);
        this.gameLoop.setShowFPS(true);
        this.setContentView(this.gameLoop);
        this.gameLoop.start();

        Stile stile = new Stile();
        ModalitaClassica modalitaClassica = new ModalitaClassica(stile);
        this.gameLoop.addGameComponentWithSetup(modalitaClassica);
    }

    protected void onPause() {
        super.onPause();
        this.gameLoop.stop();
    }

    protected void onResume() {
        super.onResume();
        this.gameLoop.start();
    }
}