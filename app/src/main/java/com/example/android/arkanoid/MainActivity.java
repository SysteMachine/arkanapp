package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.arkanoid.GameCore.AbstractGameComponent;
import com.example.android.arkanoid.GameCore.GameLoop;

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

        for(int i = 0; i < 1000; i++){
            this.gameLoop.addGameComponentWithSetup(
                    new Test(0)
            );
        }
    }

    protected void onPause() {
        super.onPause();
        this.gameLoop.stop();
    }

    protected void onResume() {
        super.onResume();
        this.gameLoop.start();
    }

    private class Test extends AbstractGameComponent{
        private int posX;
        private int posY;

        private float dirX;
        private float dirY;

        private int speed;

        private int r, g, b;

        public Test(int zIndex) {
            super(zIndex);
        }


        @Override
        public void setup(int screenWidth, int screenHeight) {
            this.posX = (int)(Math.random() * 800);
            this.posY = (int)(Math.random() * 800);

            int angle = (int)( Math.random() * 360 );
            this.dirX = (float)Math.cos(Math.toRadians(angle));
            this.dirY = (float)Math.sin(Math.toRadians(angle));

            this.r = (int)(Math.random() * 255);
            this.g = (int)(Math.random() * 255);
            this.b = (int)(Math.random() * 255);

            this.speed = 350;
        }

        @Override
        public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
            float nextX = posX + (dirX * dt * speed);
            if(nextX > screenWidth || nextX < 0){
                this.dirX *= -1;
                nextX = posX + (dirX * dt * speed);
            }

            float nextY = posY + (dirY * dt * speed);
            if(nextY > screenHeight || nextY < 0){
                this.dirY *= -1;
                nextY = posY + (dirY * dt * speed);
            }

            this.posX = (int)nextX;
            this.posY = (int)nextY;
        }

        @Override
        public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
            paint.setColor(Color.rgb(this.r, this.g, this.b));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(
                    new Rect(this.posX - 20, this.posY - 20, this.posX + 20, this.posY + 20),
                    paint
            );
        }
    }
}