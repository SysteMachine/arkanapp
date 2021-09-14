package com.example.android.arkanoid.GameElements.ElementiGioco.ModalitaMultiplayer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.Multiplayer.ServerMultiplayer;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ServerBall extends Ball{
    private ServerMultiplayer server;           //Server che ospita la palla

    public ServerBall(Vector2D position, Vector2D speed, int raggio, int angoloLancioMassimo, ServerMultiplayer server) {
        super(position, speed, null, raggio, angoloLancioMassimo);
        this.server = server;
    }

    /**
     * Controlla la coollisione con le paddle
     * @param posizione Posizione della paddle calcolata
     * @param listaEntita Lista generica delle entita
     * @param server ServerMultiplayer
     * @return Restituisce la nuova direzione
     */
    protected Vector2D controllaCollisionePaddle(Vector2D posizione, Entity[] listaEntita, ServerMultiplayer server){
        Vector2D esito = this.direction;

        for(Entity entita : listaEntita){
            if(entita.getName().equals("paddle")){
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

                    server.sendEvent(Ball.EVENTO_PADDLE_COLPITO, new ParamList());
                    break;  //Se viene trovata una paddle con cui collidere allora rompe il ciclo
                }
            }
        }

        return esito;
    }

    /**
     * Controlla la collisione con i brick all'interno della mappa
     * @param posizione Posizione della palla
     * @param listaEntita Lista delle entita del server
     * @param server Server che ospita l'entita
     * @return Restituisce la nuova direzione in caso di collisione
     */
    protected Vector2D controllaCollisioneBrick(Vector2D posizione, Entity[] listaEntita, ServerMultiplayer server){
        Vector2D esito = this.direction;

        for(Entity entity: listaEntita){
            if(entity.getName().equals("brick")){
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
                        if(brick.getHealth() != Brick.INF_HEALTH)   //Solo se il brick non è indistruttibile
                            server.sendEvent(Ball.EVENTO_BLOCCO_COLPITO, new ParamList());

                        if(brick.getHealth() == 0){
                            //Se il brick viene distrutto
                            ParamList paramList = new ParamList();
                            paramList.add("brick", brick);
                            server.sendEvent(Ball.EVENTO_BLOCCO_ROTTO, paramList);
                        }

                        break;  //Usciamo dal ciclo se viene trovato un elemento con cui la palla collide
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
                Entity[] listaEntita = params.get("entita");

                if(listaEntita != null){
                    this.setDirection(this.controllaCollisioneSchermo(nextStep, screenWidth, screenHeight));
                    this.setDirection(this.controllaCollisionePaddle(nextStep, listaEntita, this.server));
                    this.setDirection(this.controllaCollisioneBrick(nextStep, listaEntita, this.server));
                }
            }catch (Exception e){e.printStackTrace();}

            //Cambia la posizione della palla con la nuova direzione
            this.setPosition(this.getNextStep(dt));
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {}
}