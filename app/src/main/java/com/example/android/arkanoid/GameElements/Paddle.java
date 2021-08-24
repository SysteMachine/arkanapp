package com.example.android.arkanoid.GameElements;

import android.graphics.Rect;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Paddle extends AbstractEntity {
    private final float TOLLERANZA = 25;                 //Tolleranza della distanza nel raggiungimento della posizione target

    private final Vector2D startPosition;               //Posizione iniziale del paddle
    private float targetX;                              //Posizione target da raggiongere del paddle

    public Paddle(float speed, Vector2D startPosition, Vector2D size) {
        this.startPosition = startPosition;

        this.setSpeed(speed);
        this.setDirection(new Vector2D(1, 0));
        this.setSize(size);
        this.setPosition(startPosition);
        this.setName("Paddle");
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        super.setup(screenWidth, screenHeight, params);
        this.setPosition(this.startPosition);
        this.targetX = (int)( screenWidth * 0.5 );
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        boolean destra = ( this.targetX > this.position.getPosX() );
        Vector2D nextStep = this.position;

        //Calcola il nextStep in base alla direzione
        if(destra && Math.abs( this.targetX - this.position.getPosX() ) > this.TOLLERANZA){
            nextStep = Vector2D.sommaVettoriale(nextStep, this.direction.prodottoPerScalare(this.speed * dt));
        }else if(!destra && Math.abs( this.targetX - this.position.getPosX() ) > this.TOLLERANZA){
            nextStep = Vector2D.sommaVettoriale(nextStep, this.direction.prodottoPerScalare(-1 * this.speed * dt));
        }

        //Controllo della collisione con lo schermo
        if( nextStep.getPosX() + (this.getSize().getPosX() * 0.5) < screenWidth && nextStep.getPosX() - (this.getSize().getPosX() * 0.5) > 0){
            Ball palla = params.<AbstractScene>get(AbstractScene.PARAMETRO_SCENA).<Ball>getFirstEntityByName("Ball");

            Rect collisionePaddle = this.getBounds(nextStep.getPosX(), nextStep.getPosY());
            Rect collisionePalla = palla.getBounds();

            if(!collisionePaddle.intersect(collisionePalla))
                this.setPosition(nextStep);
        }
    }

    /**
     * Imposta la posizione target della paddle
     * @param targetX Posizione target della paddle
     */
    public void setTargetX(float targetX){
        this.targetX = targetX;
    }
}
