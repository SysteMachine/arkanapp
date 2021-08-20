package com.example.android.arkanoid.GameElements;

import android.graphics.Rect;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Ball extends AbstractEntity {
    private final int angoloInizialeLancioMassimo;  //Angolo massimo di rotazione della palla nel momento del lancio
    private final Vector2D startPosition;           //Posizione iniziale della palla
    private boolean isMoving;                       //Flag di controllo del movimento della palla
    private int raggio;                             //Raggio della palla

    public Ball(float speed, Vector2D startPosition, int angoloInizialeLancioMassimo, int raggio){
        this.angoloInizialeLancioMassimo = angoloInizialeLancioMassimo;
        this.startPosition = startPosition;
        this.raggio = raggio;

        this.setSpeed(speed);
        this.setPosition(this.startPosition);
        this.setSize( new Vector2D(raggio * 2, raggio * 2) );
        this.setName("Ball");

        this.isMoving = false;
        this.calcolaDirezioneIniziale();
    }

    /**
     * Calcola la direzione iniziale della pallina
     */
    private void calcolaDirezioneIniziale(){
        this.direction = new Vector2D(0, -1);
        this.direction = this.direction.ruotaVettore(-this.angoloInizialeLancioMassimo + (float)(Math.random() * this.angoloInizialeLancioMassimo * 2) );
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        this.position = this.startPosition;
        this.calcolaDirezioneIniziale();
    }

    /**
     * Controlla se la posizione inserita collide
     * @param posizione Posizione da controllare
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @return Restituisce la nuova direzione della palla in caso di collisione
     */
    private Vector2D controllaCollisioneSchermo(Vector2D posizione, int screenWidth, int screenHeight){
        Vector2D esito = null;

        float xLeft = posizione.getPosX() - this.raggio;
        float xRight = posizione.getPosX() + this.raggio;
        float yUp = posizione.getPosY() - this.raggio;
        float yDown = posizione.getPosY() + this.raggio;

        float dirX = this.direction.getPosX() * (xLeft < 0 || xRight > screenWidth ? -1 : 1);
        float dirY = this.direction.getPosY() * (yUp < 0 || yDown > screenHeight ? -1 : 1);

        esito = new Vector2D(dirX, dirY);

        return esito;
    }

    /**
     * Controlla la collisione con il paddle
     * @param posizione Posizione di controllo della palla
     * @param paddle Paddle di cui controllare l'intersezione
     * @return Restituisce la nuova direzione dopo il controllo
     */
    private Vector2D controllaCollisionePaddle(Vector2D posizione, Paddle paddle){
        Vector2D esito = this.direction;

        if(paddle != null){
            Rect ballBounds = this.getBounds(posizione.getPosX(), posizione.getPosY());
            Rect paddleBounds = paddle.getBounds();

            if(ballBounds.intersect(paddleBounds)){
                float dirX = this.direction.getPosX();
                float dirY = this.direction.getPosY() * -1;

                esito = new Vector2D(dirX, dirY);
            }
        }

        return esito;
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        if(this.isMoving){
            //Se la palla si sta muovendo
            Vector2D nextStep = Vector2D.sommaVettoriale(
                    this.position,
                    this.direction.prodottoPerScalare(this.speed * dt)
            );

            //Controllo collisione con schermo
            this.direction = this.controllaCollisioneSchermo(nextStep, screenWidth, screenHeight);
            this.direction = this.controllaCollisionePaddle(nextStep, params.<AbstractScene>get(AbstractScene.SCENA).<Paddle>getFirstEntityByName("Paddle"));


            //Cambia la posizione della palla con la nuova direzione
            this.position = Vector2D.sommaVettoriale(
                    this.position,
                    this.direction.prodottoPerScalare(this.speed * dt)
            );
        }
    }

    /**
     * Fa partire il movimento della palla
     */
    public void startPalla(){
        this.isMoving = true;
    }

    /**
     * Termina il movimento della palla
     */
    public void stopPalla(){
        this.isMoving = false;
    }

    //Beam

    public int getRaggio() {
        return raggio;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setRaggio(int raggio) {
        this.raggio = raggio;
    }

    public int getAngoloInizialeLancioMassimo() {
        return angoloInizialeLancioMassimo;
    }
}