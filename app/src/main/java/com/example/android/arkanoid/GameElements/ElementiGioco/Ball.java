package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.RectF;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Ball extends Entity {
    //Identificatori d'evento
    public final static String EVENTO_BLOCCO_COLPITO = "BALL_BLOCCO_COLPITO";                //Evento per il blocco colpito
    public final static String EVENTO_BLOCCO_ROTTO = "BALL_BLOCCO_ROTTO";                    //Evento per il blocco rotto
    public final static String EVENTO_PADDLE_COLPITO = "BALL_PADDLE_COLPITO";                //Evento per la collisione con il paddle

    //Costanti
    protected final int DISTURBO_PADDLE = 20;     //Angolo massimo di disturbo dopo aver colpito il paddle

    //Parametri
    protected int angoloLancioMassimo;            //Angolo massimo di rotazione della palla nel momento del lancio
    protected Vector2D startPosition;             //Posizione iniziale della palla
    protected boolean isMoving;                   //Flag di controllo del movimento della palla
    protected int offsetCollisioneSuperiore;      //Numero sotto il quale la posizione y della palla non può essere impostato

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
    public void calcolaDirezioneIniziale(){
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
    protected Vector2D controllaCollisioneSchermo(Vector2D posizione, int screenWidth, int screenHeight){
        float xLeft = posizione.getPosX() - (this.size.getPosX() * 0.5f);
        float xRight = posizione.getPosX() + (this.size.getPosX() * 0.5f);
        float yUp = posizione.getPosY() - (this.size.getPosY() * 0.5f);
        float yDown = posizione.getPosY() + (this.size.getPosY() * 0.5f);

        //Controllo delle collisione sul bound
        float dirX = this.direction.getPosX() * (xLeft < 0 || xRight > screenWidth ? -1 : 1);
        float dirY = this.direction.getPosY() * (yUp < this.offsetCollisioneSuperiore || yDown > screenHeight ? -1 : 1);

        return new Vector2D(dirX, dirY);
    }

    protected Vector2D controllaCollisionePaddle(Vector2D posizione, AbstractScene scena){
        Vector2D esito = this.direction;

        Entity[] listaEntita = scena.getEntityByName("paddle");
        for(Entity entita : listaEntita){
            Paddle paddle = (Paddle)entita;

            RectF ballBounds = this.getBounds(posizione.getPosX(), posizione.getPosY());
            RectF paddleBounds = paddle.getBounds();

            if(ballBounds.intersect(paddleBounds)){
                ballBounds = this.getBounds();
                paddleBounds = paddle.getBounds();

                if(
                        (ballBounds.left > paddleBounds.left && ballBounds.left < paddleBounds.right) ||
                                (ballBounds.right > paddleBounds.left && ballBounds.right < paddleBounds.right)
                ){
                    esito = esito.prodottoPerVettore(new Vector2D(1, -1));
                    int angoloRotazione = (int)( -this.DISTURBO_PADDLE + (Math.random() * this.DISTURBO_PADDLE * 2) );
                    esito = esito.ruotaVettore( angoloRotazione );
                }
                if(
                        (ballBounds.top > paddleBounds.top && ballBounds.top < paddleBounds.bottom) ||
                                (ballBounds.bottom > paddleBounds.top && ballBounds.bottom < paddleBounds.bottom)
                ){
                    esito = esito.prodottoPerVettore(new Vector2D(-1, 1));
                }

                scena.sendEvent(Ball.EVENTO_PADDLE_COLPITO, new ParamList());
                break;  //Se viene trovata una paddle con cui collidere allora rompe il ciclo
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
    protected Vector2D controllaCollisioneBrick(Vector2D posizione, AbstractScene scena){
        Vector2D esito = this.direction;

        Entity[] listaBrick = scena.getEntityByName("brick");
        for(Entity entity: listaBrick){
            Brick brick = (Brick)entity;
            if(brick.getHealth() != 0){
                //Se il brick non è stato ancora distrutto
                RectF collisioneBrick = brick.getBounds();
                RectF collisionePalla = this.getBounds(posizione.getPosX(), posizione.getPosY());

                if(collisioneBrick.intersect(collisionePalla)){
                    //Bisogna ricalcolare i bounds perchè per qualche motivo diventano uguali, mi pesa il culo vedere la documentazione quindi vabien
                    collisioneBrick = brick.getBounds();
                    collisionePalla = this.getBounds();

                    //data una superficia A-B la collisione avviene se almeno uno dei due è all'interno della superfice A'-B' del brick
                    //Collisione sulla parte larga
                    if(
                            (collisionePalla.left > collisioneBrick.left && collisionePalla.left < collisioneBrick.right) ||
                                    (collisionePalla.right > collisioneBrick.left && collisionePalla.right < collisioneBrick.right)
                    ){
                        esito = esito.prodottoPerVettore(new Vector2D(1, -1));
                    }
                    if(
                            (collisionePalla.top > collisioneBrick.top && collisionePalla.top < collisioneBrick.bottom) ||
                                    (collisionePalla.bottom > collisioneBrick.top && collisionePalla.bottom < collisioneBrick.bottom)
                    ){
                        esito = esito.prodottoPerVettore(new Vector2D(-1, 1));
                    }

                    //Decrementa il valore della vita del blocco
                    brick.decrementaVita();
                    brick.shake(200, 60);
                    if(brick.getHealth() != Brick.INF_HEALTH)   //Solo se il brick non è indistruttibile
                        scena.sendEvent(Ball.EVENTO_BLOCCO_COLPITO, new ParamList());

                    if(brick.getHealth() == 0){
                        //Se il brick viene distrutto
                        ParamList paramList = new ParamList();
                        paramList.add("brick", brick);
                        scena.sendEvent(Ball.EVENTO_BLOCCO_ROTTO, paramList);
                    }

                    break;  //Usciamo dal ciclo se viene trovato un elemento con cui la palla collide
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
                AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);

                if(scena != null){
                    this.setDirection(this.controllaCollisioneSchermo(nextStep, screenWidth, screenHeight));
                    this.setDirection(this.controllaCollisionePaddle(nextStep, scena));
                    this.setDirection(this.controllaCollisioneBrick(nextStep, scena));
                }
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