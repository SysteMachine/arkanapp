package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.RectF;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Paddle extends Entity {
    protected final float TOLLERANZA = 25;                //Tolleranza della distanza nel raggiungimento della posizione target

    protected Vector2D startPosition;                     //Posizione iniziale del paddle
    protected float targetX;                              //Posizione target da raggiongere del paddle

    public Paddle(Vector2D position, Vector2D size, Vector2D speed, Sprite sprite) {
        super(
                "paddle",
                position,
                new Vector2D(1, 0),
                size,
                speed,
                sprite
        );
        this.startPosition = position;
        this.targetX = position.getPosX();
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        super.setup(screenWidth, screenHeight, params);
        this.setPosition(this.startPosition);
        this.targetX = this.startPosition.getPosX();
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
                    Ball palla = params.<AbstractScene>get(AbstractScene.PARAMETRO_SCENA).<Ball>getFirstEntityByName("ball");
                    RectF collisionePaddle = this.getBounds(nextStep.getPosX(), nextStep.getPosY());
                    RectF collisionePalla = palla.getBounds();

                    if(!collisionePaddle.intersect(collisionePalla))
                        //Se non collide con la palla
                        this.setPosition(nextStep);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    //Beam

    public float getTOLLERANZA() {
        return TOLLERANZA;
    }

    public Vector2D getStartPosition() {
        return startPosition;
    }

    public float getTargetX() {
        return targetX;
    }

    public void setStartPosition(Vector2D startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public void setPosition(Vector2D position) {
        super.setPosition(position);
        this.targetX = position.getPosX();
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }
}
