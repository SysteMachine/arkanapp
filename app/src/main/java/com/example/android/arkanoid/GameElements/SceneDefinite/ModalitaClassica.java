package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener, SensorEventListener {
    private Ball palla;
    private float rotazionePalla = 0;
    private int velocitaRotazionePalla = 50;

    private Sprite sPalla;

    public ModalitaClassica() {
        super(0);

        this.palla = new Ball(300, new Vector2D(300, 500), 30, 50);
        this.palla.setName("palla");

        this.addEntita(this.palla);
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
        this.sPalla = new Sprite(R.drawable.palla_palla1, this.owner);
        this.sPalla.replaceColor(Color.rgb(255, 255, 255), Color.rgb(255, 100, 0), 200);
    }

    @Override
    protected void removeGameLoop() {
        this.owner.setOnTouchListener(null);
        super.removeGameLoop();
    }

    /**
     * Disegna la palla della scena
     * @param canvas Canvas di disegno
     * @param paint Modificatore dello stile
     */
    private void disegnaPalla(Canvas canvas, Paint paint){
        if(palla != null){
            //this.sPalla.ruotaImmagine(this.rotazionePalla); //TODO Vedere la rotazione degli sprite

            //Controllo sulla palla

            /*
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.YELLOW);

            canvas.drawRect(
                    new Rect(
                            (int)(this.palla.getPosition().getPosX() - this.palla.getRaggio()),
                            (int)(this.palla.getPosition().getPosY() - this.palla.getRaggio()),
                            (int)(this.palla.getPosition().getPosX() + this.palla.getRaggio()),
                            (int)(this.palla.getPosition().getPosY() + this.palla.getRaggio())
                    ),
                    paint
            );*/

            if(this.sPalla != null){
                this.sPalla.drawSprite(
                        (int)this.palla.getPosition().getPosX(),
                        (int)this.palla.getPosition().getPosY(),
                        this.palla.getRaggio() * 2,
                        this.palla.getRaggio() * 2,
                        canvas,
                        paint
                        );
            }
        }
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        super.setup(screenWidth, screenHeight);
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.update(dt, screenWidth, screenHeight, canvas, paint);
        this.rotazionePalla += this.velocitaRotazionePalla * dt;
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        this.disegnaPalla(canvas, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!this.palla.isMoving())
            this.palla.startPalla();

        System.out.println(event.getX() + " - " + event.getY());
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
