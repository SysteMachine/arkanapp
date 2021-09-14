package com.example.android.arkanoid.GameElements.ElementiGioco.ModalitaMultiplayer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ServerPaddle extends Paddle {

    public ServerPaddle(Vector2D position, Vector2D size, Vector2D speed) {
        super(position, size, speed, null);
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        if(Math.abs( this.targetX - this.position.getPosX() ) > this.TOLLERANZA){
            //Se la tolleranza non è rispettata allora sposta il paddle
            if(this.targetX > this.position.getPosX())
                //Se va a destra
                this.setDirection(new Vector2D(1, 0));
            else
                this.setDirection(new Vector2D(-1, 0));
            Vector2D nextStep = this.getNextStep(dt);

            //Controllo della collisione con lo schermo
            if( nextStep.getPosX() - (this.getSize().getPosX() * 0.5) > 0 && nextStep.getPosX() + (this.getSize().getPosX() * 0.5) < screenWidth ){
                //Se non collide con lo schermo controlla la collisione con la palla

                try{
                    Ball palla = params.get("ball");
                    RectF collisionePaddle = this.getBounds(nextStep.getPosX(), nextStep.getPosY());
                    RectF collisionePalla = palla.getBounds();

                    if(!collisionePaddle.intersect(collisionePalla))
                        //Se non collide con la palla
                        this.setPosition(nextStep);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {}
}
