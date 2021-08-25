package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BrickHealthUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.MultiBall;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleUp;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;

public class MainActivity extends AppCompatActivity {
    private GameLoop gameLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.gameLoop = new GameLoop(this, 60, 720, 1280);
        this.gameLoop.setShowFPS(true);
        this.setContentView(this.gameLoop);
        this.gameLoop.start();

        Stile stile = new Stile();
        GameStatus status = new GameStatus(5, 0);
        PMList powerupList = new PMList();

        powerupList.addPowerupMalus(BallSpeedUp.class, 10);
        powerupList.addPowerupMalus(BallSpeedDown.class, 5);
        powerupList.addPowerupMalus(MultiBall.class, 2);
        powerupList.addPowerupMalus(BrickHealthUp.class, 20);
        powerupList.addPowerupMalus(PaddleUp.class, 10);
        powerupList.addPowerupMalus(PaddleDown.class, 5);
        powerupList.addPowerupMalus(PaddleSpeedUp.class, 10);
        powerupList.addPowerupMalus(PaddleSpeedDown.class, 5);

        ModalitaClassica modalitaClassica = new ModalitaClassica(stile, status, powerupList);
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