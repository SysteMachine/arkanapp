package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.arkanoid.GameCore.GameLoop;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private UpdateThread myThread;
    private Handler updateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // nastavi orientaciu obrazovky
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // vytvori novu hru
        //game = new Game(this, 3, 0);
        GameLoop tr = new GameLoop(this, 60);
        setContentView(tr);
        tr.start();

        //Test
        // vytvori handler a thread
        //VytvorHandler();
        //myThread = new UpdateThread(updateHandler);
        //.start();
    }

    private void VytvorHandler() {
        updateHandler = new Handler() {
            public void handleMessage(Message msg) {
                game.invalidate();
                game.update();
                super.handleMessage(msg);
            }
        };
    }

    protected void onPause() {
        super.onPause();
        //game.zastavSnimanie();
    }

    protected void onResume() {
        super.onResume();
        //game.spustiSnimanie();
    }

}
