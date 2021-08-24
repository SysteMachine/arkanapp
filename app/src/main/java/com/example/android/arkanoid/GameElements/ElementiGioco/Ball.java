package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.Rect;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractEntity;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Ball extends AbstractEntity {
    private int angoloLancioMassimo;            //Angolo massimo di rotazione della palla nel momento del lancio
    private Vector2D startPosition;             //Posizione iniziale della palla
    private boolean isMoving;                   //Flag di controllo del movimento della palla
    private int offsetCollisioneSuperiore;      //Numero sotto il quale la posizione y della palla non può essere impostato

    public Ball(Vector2D position, Vector2D speed, Sprite sprite, int raggio, int angoloLancioMassimo) {
        super(
                "ball",
                position,
                new Vector2D(0, -1),
                new Vector2D(raggio * 2, raggio * 2),
                speed,
                sprite
        );

        this.startPosition = position;
        this.angoloLancioMassimo = angoloLancioMassimo;
        this.offsetCollisioneSuperiore = 0;
        this.isMoving = false;

        this.calcolaDirezioneIniziale();
    }

    /**
     * Calcola la direzione iniziale della pallina
     */
    private void calcolaDirezioneIniziale(){
        this.direction = new Vector2D(0, -1);
        this.direction = this.direction.ruotaVettore(-this.angoloLancioMassimo + (float)(Math.random() * this.angoloLancioMassimo * 2) );
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        this.setPosition(this.startPosition);
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
        float xLeft = posizione.getPosX() - (this.size.getPosX() * 0.5f);
        float xRight = posizione.getPosX() + (this.size.getPosX() * 0.5f);
        float yUp = posizione.getPosY() - (this.size.getPosY() * 0.5f);
        float yDown = posizione.getPosY() + (this.size.getPosY() * 0.5f);

        //Controllo delle collisione sul bound
        float dirX = this.direction.getPosX() * (xLeft < 0 || xRight > screenWidth ? -1 : 1);
        float dirY = this.direction.getPosY() * (yUp < this.offsetCollisioneSuperiore || yDown > screenHeight ? -1 : 1);

        return new Vector2D(dirX, dirY);
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

    /**
     * Controlla la collisione con i brick all'interno della mappa
     * @param posizione Posizione della palla
     * @param scena Scena di gioco
     * @return Restituisce la nuova direzione in caso di collisione
     */
    private Vector2D controllaCollisioneBrick(Vector2D posizione, AbstractScene scena){
        Vector2D esito = this.direction;

        if(scena != null){
            AbstractEntity[] brick = (AbstractEntity[])scena.getEntityByName("brick");
            for(AbstractEntity ae: brick){
                Brick b = (Brick)ae;
                if(b.getHealth() > 0){
                    //Se il brick non è stato ancora distrutto
                    Rect collisioneBrick = b.getBounds();
                    Rect collisionePalla = this.getBounds(posizione.getPosX(), posizione.getPosY());

                    if(collisioneBrick.intersect(collisionePalla)){
                        //Se avviene la collisione

                        //Cambiamo gli elementi della collisione
                        if(posizione.getPosX() >= collisioneBrick.left && posizione.getPosX() <= collisioneBrick.right)
                            esito = esito.prodottoPerVettore(new Vector2D(1, -1));
                        if(posizione.getPosY() >= collisioneBrick.top && posizione.getPosY() <= collisioneBrick.bottom)
                            esito = esito.prodottoPerVettore(new Vector2D(-1, 1));

                        //Decrementa il valore della vita del blocco
                        b.decrementaVita();
                        if(b.getHealth() == 0){
                            //Se il brick viene distrutto
                            ParamList paramList = new ParamList();
                            paramList.add("brick", b);
                            scena.sendEvent(ModalitaClassica.EVENTO_BLOCCO_ROTTO, paramList);
                        }

                        break;  //Usciamo dal ciclo se viene trovato un elemento
                    }
                }
            }
        }

        return esito;
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        if(this.isMoving){
            //Se la palla si sta muovendo
            Vector2D nextStep = this.getNextStep(dt);

            try{
                //Controllo collisione con schermo, se ci sono problemi con i parametri tutto viene catturato dal catch
                this.setDirection(this.controllaCollisioneSchermo(nextStep, screenWidth, screenHeight));
                this.setDirection(this.controllaCollisionePaddle(nextStep, params.<AbstractScene>get(AbstractScene.PARAMETRO_SCENA).<Paddle>getFirstEntityByName("paddle")));
                this.setDirection(this.controllaCollisioneBrick(nextStep, params.<AbstractScene>get(AbstractScene.PARAMETRO_SCENA)));

                //Ricalcola la prossima direzione
                nextStep = this.getNextStep(dt);
            }catch (Exception e){e.printStackTrace();}

            //Cambia la posizione della palla con la nuova direzione
            this.setPosition(this.getNextStep(dt));
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

    public int getAngoloLancioMassimo() {
        return angoloLancioMassimo;
    }

    public Vector2D getStartPosition() {
        return startPosition;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getOffsetCollisioneSuperiore() {
        return offsetCollisioneSuperiore;
    }

    public void setAngoloLancioMassimo(int angoloLancioMassimo) {
        this.angoloLancioMassimo = angoloLancioMassimo;
    }

    public void setStartPosition(Vector2D startPosition) {
        this.startPosition = startPosition;
    }

    public void setOffsetCollisioneSuperiore(int offsetCollisioneSuperiore) {
        this.offsetCollisioneSuperiore = offsetCollisioneSuperiore;
    }
}