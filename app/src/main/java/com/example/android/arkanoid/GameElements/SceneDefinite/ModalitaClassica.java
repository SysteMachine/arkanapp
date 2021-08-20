package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.Paddle;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    private Ball palla;
    private Paddle paddle;

    //Sprite degli elementi
    private Sprite sPalla;
    private Sprite sPaddle;
    private MultiSprite sBrick;

    public ModalitaClassica() {
        super(0);
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
    }

    @Override
    protected void removeGameLoop() {
        this.owner.setOnTouchListener(null);
        super.removeGameLoop();
    }

    /**
     * Disegna la palla della scena
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    private void disegnaPalla(Canvas canvas, Paint paint){
        if(this.palla != null && this.sPaddle != null){
            this.sPalla.drawSprite(
                    (int)this.palla.getPosition().getPosX(),
                    (int)this.palla.getPosition().getPosY(),
                    (int)this.palla.getSize().getPosX(),
                    (int)this.palla.getSize().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    /**
     * Disegna il paddle della scena
     * @param canvas Cancas di disengo
     * @param paint Paint di disegno
     */
    private void disegnaPaddle(Canvas canvas, Paint paint){
        if(this.paddle != null && this.sPaddle != null){
            this.sPaddle.drawSprite(
                    (int)this.paddle.getPosition().getPosX(),
                    (int)this.paddle.getPosition().getPosY(),
                    (int)this.paddle.getSize().getPosX(),
                    (int)this.paddle.getSize().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.palla = new Ball(800, new Vector2D(screenWidth * 0.5f, screenHeight * 0.6f), 40, 50);
        this.sPalla = new Sprite(R.drawable.palla_palla1, this.owner);

        this.paddle = new Paddle(300, new Vector2D(screenWidth * 0.5f, screenHeight * 0.7f), 300, 40);
        this.sPaddle = new Sprite(R.drawable.paddle_paddle1, this.owner);

        //Inserimento delle entità per la logica di base
        this.addEntita(this.palla);
        this.addEntita(this.paddle);

        //Cambio di stile
        this.sPalla.replaceColor(Color.WHITE, Color.RED, 200);
        this.sPaddle.replaceColor(Color.WHITE, Color.rgb(200, 100, 25), 200);
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        this.disegnaPalla(canvas, paint);
        this.disegnaPaddle(canvas, paint);
    }

    //Eventi

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.palla != null && !this.palla.isMoving())
            this.palla.startPalla();

        if(this.paddle != null && event.getAction() == MotionEvent.ACTION_MOVE){
            System.out.println("Movimento");
            float lastPosY = this.paddle.getPosition().getPosY();
            this.paddle.setPosition( new Vector2D(event.getX(), lastPosY) );
        }

        return true;
    }


}
