package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.ElementiGioco.Sfondo;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileMultiplayer;
import com.example.android.arkanoid.Multiplayer.ClientListener;
import com.example.android.arkanoid.Multiplayer.ClientMultiplayer;
import com.example.android.arkanoid.Multiplayer.MessaggioServerRecord;
import com.example.android.arkanoid.Multiplayer.ServerMultiplayer;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.LoopTimer;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.TimerListener;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;

public class ModalitaMultiplayer extends AbstractModalita implements ClientListener, TimerListener {
    private final int TICK_AGGIORNAMENTO_PALLA = 200;               //Intervallo di tempo entro il quale aggiornare la posizione della palla

    private ClientMultiplayer client;                               //Client associato alla modalita

    //Elementi di gioco
    private Ball palla;
    private Paddle paddleGiocatore;
    private Paddle paddleAvversario;
    private Sfondo sfondo;
    private String esitoPunteggio;

    private ArrayList<MessaggioServerRecord> messaggiServer;        //Messaggi ricevuti dal server
    private boolean isBallOwner;                                    //Flag che indica se il client è il possessore della palla e deve occuparsi dell'aggiornamento
    private LoopTimer timerAggiornamentoPalla;                      //Timer per l'aggiornamento della posizione della palla quando si è owner

    public ModalitaMultiplayer(ClientMultiplayer client) {
        super(new StileMultiplayer(), new GameStatus(1, 0, GameStatus.TOUCH));
        this.client = client;
        this.client.setClientListener(this);

        this.messaggiServer = new ArrayList<>();
        this.isBallOwner = false;

        this.timerAggiornamentoPalla = new LoopTimer(this, this.TICK_AGGIORNAMENTO_PALLA);
    }

    @Override
    protected void disegnaVita(int screenWidth, int screenHeight, Canvas canvas, Paint paint) { }
    @Override
    protected void disegnaAlterazioni(int screenWidth, int screenHeight, Canvas canvas, Paint paint) {}
    @Override
    protected void disegnaPunteggio(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        if(this.esitoPunteggio != null){
            float lastSize = paint.getTextSize();
            paint.setTextSize(screenHeight * this.PERCENTUALE_DIMENSIONE_FONT);

            String messaggio = this.esitoPunteggio;
            Rect bound = new Rect();
            paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

            float startX = (screenWidth * 0.5f) - (bound.right * 0.5f);
            float startY = (this.DIMENSIONE_ZONA_PUNTEGGIO * 0.5f) - (bound.bottom * 0.5f) + 25;

            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(messaggio, startX, startY, paint);
            paint.setTextSize(lastSize);
        }
    }

    protected void logicaRipristinoPosizioni() {}
    @Override
    protected void logicaEliminazioneVita() {}
    @Override
    protected void logicaAvanzamentoLivello() {}
    @Override
    protected void inizializzazioneAudio() {
        AudioUtil.clear();
        AudioUtil.loadAudio("background", this.stile.getSuonoBackground(), AudioUtil.MUSICA, true, this.owner.getContext());
        AudioUtil.avviaAudio("background");
    }

    @Override
    protected void iniziallizzazioneEntita(int screenWidth, int screenHeight) {
        this.palla = new Ball(
                new Vector2D(screenWidth / 2, screenHeight / 2),
                new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()),
                this.stile.getImmaginePallaStile(this.owner),
                (int)(this.stile.getPercentualeRaggioPalla() * screenWidth),
                this.stile.getAngoloDiLancioMassimoPalla()
        ){
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
        };
        this.palla.setOffsetCollisioneSuperiore(this.DIMENSIONE_ZONA_PUNTEGGIO);

        //Creazione dei paddle
        this.paddleGiocatore = new Paddle(
                new Vector2D(screenWidth * 0.5f, screenHeight - 80),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()),
                ((StileMultiplayer)this.stile).getImmaginePaddleStileGiocatore(this.owner)
        );

        this.paddleAvversario = new Paddle(
                new Vector2D(screenWidth * 0.5f, this.DIMENSIONE_ZONA_PUNTEGGIO + 80),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()),
                ((StileMultiplayer)this.stile).getImmaginePaddleStileAvversario(this.owner)
        );

        this.sfondo = new Sfondo(
                new Vector2D(screenWidth * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidth, screenHeight),
                this.stile.getImmagineSfondoStile(this.owner)
        );
    }

    @Override
    protected void inserimentoEntita() {
        this.addEntita(this.sfondo);
        this.addEntita(this.palla);
        this.addEntita(this.paddleAvversario);
        this.addEntita(this.paddleGiocatore);

        //Chiamiamo il server per le informazioni finali di inizializzazione
        this.client.inviaMessaggioServer(ServerMultiplayer.RICHIESTA_POSIZIONE_DIREZIONE_PALLA);
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.render(dt, screenWidth, screenHeight, canvas, paint);
        this.disegnaMessaggioServer(screenWidth, screenHeight, canvas, paint);
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.update(dt, screenWidth, screenHeight, canvas, paint);
        if(this.risorseCaricate)
            this.controlloPunteggio();
    }

    /**
     * Disegna un messaggio ricevuto dal server
     * @param screenWidth
     * @param screenHeight
     * @param canvas
     * @param paint
     */
    private void disegnaMessaggioServer(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        float lastSize = paint.getTextSize();
        paint.setTextSize(50);
        paint.setColor(Color.rgb(50, 50, 50));
        String messaggio = "";

        for(MessaggioServerRecord msr : this.messaggiServer){
            if(msr.isValid())
                messaggio = msr.getMessaggio();
        }

        Rect bound = new Rect();
        paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

        float posX = screenWidth * 0.5f - bound.right * 0.5f;
        float posY = screenHeight * 0.5f - bound.left * 0.5f;

        canvas.drawText(messaggio, posX, posY, paint);

        paint.setTextSize(lastSize);
    }

    @Override
    public void touchEvent(Vector2D position, View v, MotionEvent e) {
        if(this.paddleGiocatore != null) {
            this.paddleGiocatore.setTargetX(position.getPosX());
        }
    }

    @Override
    public void orientationChanged(float degree) {}

    //Gestione del client

    @Override
    public void clientMessage(String message) {
        this.aggiornamentoPosizioneDirezionePalla(message);
        this.riceviMessaggioServer(message);
        this.riceviStart(message);
        this.riceviStop(message);
        this.riceviStartOwner(message);
        this.riceviTargetXAvversario(message);
        this.riceviEsitoPunteggio(message);
        this.riceviFinePartita(message);
    }

    /**
     * Invia la posizione e la direzione della palla al server
     */
    private void inviaPosizioneDirezionePalla(){
        String messaggio = new StringBuilder().append(ServerMultiplayer.AGGIORNA_POSIZIONE_DIREZIONE_PALLA)
                .append("=")
                .append(this.palla.getPosition().getPosX()).append(":")
                .append(this.palla.getPosition().getPosY()).append(":")
                .append(this.palla.getDirection().getPosX()).append(":")
                .append(this.palla.getDirection().getPosY()).toString();
        this.client.inviaMessaggioServer(messaggio);
    }

    /**
     * Invia il valore del target x della paddle al server
     * @param targetX Target X
     */
    private void inviaTargetXPaddle(float targetX){
        String messaggio = ServerMultiplayer.AGGIORNA_TARGET_X_PADDLE + "=" + targetX;
        this.client.inviaMessaggioServer(messaggio);
    }

    /**
     * Controlla se la palla raggiunge una zona di morte dei giocatori
     */
    private void controlloPunteggio(){
        if(this.isBallOwner){
            if(this.palla != null && this.palla.isMoving()){
                if(this.palla.getPosition().getPosY() > this.screenHeight - 40) {
                    this.palla.stopPalla();
                    this.client.inviaMessaggioServer(ServerMultiplayer.PUNTO + "=false");
                }
                if(this.palla.getPosition().getPosY() < this.DIMENSIONE_ZONA_PUNTEGGIO + 40) {
                    this.palla.stopPalla();
                    this.client.inviaMessaggioServer(ServerMultiplayer.PUNTO + "=true");
                }
            }
        }
    }

    /**
     * Riceve un messaggio dal server
     * @param message Messaggio ricevuto dal server
     */
    private void riceviMessaggioServer(String message){
        if(message.startsWith(ServerMultiplayer.INVIO_MESSAGGIO_SERVER)){
            String[] parti = message.split("=")[1].split(":");
            String messaggio = parti[0];
            int durata = Integer.valueOf(parti[1]);
            this.messaggiServer.add(new MessaggioServerRecord(messaggio, durata));
        }
    }

    /**
     * Riceve l'esito del punteggio
     * @param message messaggio ricevuto dal server
     */
    public void riceviEsitoPunteggio(String message){
        if(message.startsWith(ServerMultiplayer.ESITO_PUNTEGGIO)){
            this.esitoPunteggio = message.split("=")[1];
            System.out.println("ricevuto il messaggio " + this.esitoPunteggio);
        }
    }

    /**
     * Riceve il messaggio di start dal server
     * @param message messaggio dal server
     */
    public void riceviStart(String message){
        if(message.equals(ServerMultiplayer.INVIO_START))
            this.palla.startPalla();
    }

    /**
     * Riceve il messaggio di stop dal server
     * @param message messaggio dal server
     */
    public void riceviStop(String message){
        if(message.equals(ServerMultiplayer.INVIO_STOP))
            this.palla.stopPalla();
    }

    /**
     * Riceve il messaggio di finePartita
     * @param message
     */
    public void riceviFinePartita(String message){
        if(message.equals(ServerMultiplayer.FINE_PARTITA)) {
            System.out.println("FINISCO LA PARTITA");
            this.client.stopClient();
            if(this.gameOverListener != null) {
                this.timerAggiornamentoPalla.stop();
                this.gameOverListener.gameOver(null);
            }
        }
    }

    /**
     * Riceve il messaggio di start per il possesso della palla
     * @param message messaggio dal server
     */
    public void riceviStartOwner(String message){
        if(message.equals(ServerMultiplayer.START_OWNER)) {
            System.out.println("Divento owner");
            this.isBallOwner = true;
        }
    }

    /**
     * Riceve ed aggiorna la posizione targetX dell'avversario
     * @param message messaggio dal server
     */
    private void riceviTargetXAvversario(String message){
        if(message.startsWith(ServerMultiplayer.TARGET_X_AVVERSARIO)){
            float targetX = Float.valueOf(message.split("=")[1]);
            this.paddleAvversario.setPosition(new Vector2D(targetX, this.paddleAvversario.getPosition().getPosY()));
        }
    }

    /**
     * Aggiorna la posizione e la direzione della palla nel client
     * @param message Messaggio ricevuto dal server
     */
    private void aggiornamentoPosizioneDirezionePalla(String message){
        if(message.startsWith(ServerMultiplayer.RISPOSTA_POSIZIONE_DIREZIONE_PALLA)){
            if(this.palla != null){
                String[] parti = message.split("=")[1].split(":");
                float posX = Float.valueOf(parti[0]);
                float posY = Float.valueOf(parti[1]);
                float dirX = Float.valueOf(parti[2]);
                float dirY = Float.valueOf(parti[3]);
                this.palla.setPosition(new Vector2D(posX, posY));
                this.palla.setDirection(new Vector2D(dirX, dirY));
            }
        }
    }

    @Override
    public void timeIsZero() {
        this.inviaTargetXPaddle(this.paddleGiocatore.getPosition().getPosX());
        if(this.isBallOwner)
            this.inviaPosizioneDirezionePalla();
        if(!this.client.isServerAlive()) {
            this.timerAggiornamentoPalla.stop();
            if(this.gameOverListener != null)
                this.gameOverListener.gameOver(null);
        }
    }
}
