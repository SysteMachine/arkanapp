package com.example.android.arkanoid;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

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
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;

public class MainActivity extends AppCompatActivity {
    private static GameLoop gameLoop;
    private static ModalitaClassica modalitaClassica;
    private static GameStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        boolean restore = false;
        if(savedInstanceState != null && savedInstanceState.getBoolean("RESTORE"))
            restore = true;

        if(!restore){
            this.gameLoop = new GameLoop(this, 60, 720, 1280);
            this.gameLoop.setShowFPS(true);
            this.gameLoop.start();

            Stile stile = new StileAtzeco();
            this.status = new GameStatus(5, 0, GameStatus.TOUCH);
            PMList powerupList = new PMList();

            powerupList.addPowerupMalus(BallSpeedUp.class, 10);
            powerupList.addPowerupMalus(BallSpeedDown.class, 5);
            powerupList.addPowerupMalus(MultiBall.class, 2);
            powerupList.addPowerupMalus(BrickHealthUp.class, 20);
            powerupList.addPowerupMalus(PaddleUp.class, 10);
            powerupList.addPowerupMalus(PaddleDown.class, 5);
            powerupList.addPowerupMalus(PaddleSpeedUp.class, 10);
            powerupList.addPowerupMalus(PaddleSpeedDown.class, 5);

            this.modalitaClassica = new ModalitaClassica(stile, status, powerupList);
            this.gameLoop.addGameComponentWithSetup(modalitaClassica);
        }else{
            if(this.gameLoop == null){
                System.out.println("E' nullo");
            }
        }

        this.setContentView(this.gameLoop);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("RESTORE", true);
        ((ViewGroup)this.gameLoop.getParent()).removeView(this.gameLoop);
    }

    protected void onPause() {
        super.onPause();
        if(this.gameLoop != null)
            this.gameLoop.stop();
    }

    protected void onResume() {
        super.onResume();
        if(this.gameLoop != null)
            this.gameLoop.start();
    }
}