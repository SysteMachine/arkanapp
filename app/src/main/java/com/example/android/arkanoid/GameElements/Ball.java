package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Ball extends AbstractEntity {
    private final int angoloInizialeLancioMassimo;  //Angolo massimo di rotazione della palla nel momento del lancio
    private final Vector2D startPosition;           //Posizione iniziale della palla
    private boolean isMoving;                       //Flag di controllo del movimento della palla
    private int raggio;                             //Raggio della palla

    public Ball(float speed, Vector2D startPosition, int angoloInizialeLancioMassimo, int raggio){
        this.angoloInizialeLancioMassimo = angoloInizialeLancioMassimo;
        this.speed = speed;
        this.position = startPosition;
        this.startPosition = startPosition;
        this.raggio = raggio;

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

            //Cambia la posizione della palla
            this.position = nextStep;
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