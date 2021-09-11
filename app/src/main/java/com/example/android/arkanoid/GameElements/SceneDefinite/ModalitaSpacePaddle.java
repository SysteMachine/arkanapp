package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Particella;
import com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle.Asteroide;
import com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle.Background;
import com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle.Proiettile;
import com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle.SpaceBrick;
import com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle.SpacePaddle;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;

public class ModalitaSpacePaddle extends AbstractModalita implements Runnable{
    private final int PUNTI_PER_COLPO = 200;                //Punti per colpo
    private final int PUNTI_PER_DISTRUZIONE = 1000;         //Punti per distruzione del blocco
    private final int NUMERO_PARTICELLE_ESPLOSIONE = 50;    //Numero di particelle dopo un esplosione
    private final int NUMERO_MASSIMO_ELEMENTI_BUFFER_BRICK = 50;    //Numero massimo di brick presenti all'interno del buffer
    private final int NUMERO_MINIMO_ELEMENTI_BUFFER_BRICK = 30;     //Numero minimo di elementi che devono essere presenti nel buffer

    private final int INTERVALLO_CAMBIO_LIVELLO = 20000;    //Intervallo per il cambio di livello
    private final int VELOCITA_SPARO = 800;     //Velocità dello sparo
    private final int INTERVALLO_SPARO = 400;   //Intervallo in ms tra uno sparo ed un altro sparo
    private final int COLONNE_SPAWN_BRICK = 5;  //Numero di colonne per lo spawn dei brick
    private final int VELOCITA_INIZIALE = 300;  //Velocita iniziale degli elementi
    private final int INCREMENTO_VELOCITA = 80; //Incremento della velocita degli elementi
    private final int DECREMENTO_INTERVALLO_SPAWN_ASTEROIDI = 50;       //Decremento dell'intervallo di spawn degli asteroidi
    private final int DECREMENTO_INTERVALLO_SPAWN_BRICK = 20;           //Decremento dell'intervallo di spawn dei brick
    private final int INTERVALLO_SPAWN_ASTEROIDI_INIZIALE = 3500;       //Intervallo di spawn iniziale degli asteroidi
    private final int INTERVALLO_SPAWN_BRICK_INIZIALE = 5000;           //Intervallo di spawn iniziale dei brick

    //Gestion spawn asteroidi e brick
    private float velocitaElementi;             //Velocita dei brick e degli asteroidi
    private float intervalloSpawnAsteroidi;     //Tempo che deve trascorrere tra uno spawn ed un altro di un asteroide
    private long timeStampAsteroide;            //Timestamp dell'asteroide
    private float intervalloSpawnBrick;         //Tempo che deve trascorrere tra uno spawn ed un altro di un brock
    private long timeStampBrick;                //TimeStamp del brick
    private long timeStampLivello;              //TimeStap del livello
    //-------------------------------

    private boolean giocoPartito;               //Flag di partenza del gioco

    private boolean screenPressed;              //Flag di controllo del tocco dello schermo
    private int posizioneXDito;                 //Posizione X del dito
    private int posizioneYDito;                 //Posizione Y del dito

    private Background sfondo;                  //Sfondo della modalita
    private SpacePaddle paddle;                 //Paddle della modalita

    private ArrayList<SpaceBrick> bufferBrick;  //Buffer dei brick
    private boolean puoGenerare;                //Flag per i permessi di generazione


    private Sprite spriteIconaSparo;            //Sprite dell'icona di sparo

    //Gestione dello sparo
    private long lastTimeStamp;
    //--------------------

    public ModalitaSpacePaddle(Stile stile, GameStatus status) {
        super(stile, status);
        this.codiceModalita = 2;

        this.giocoPartito = false;
        this.screenPressed = false;
        this.puoGenerare = true;

        this.lastTimeStamp = System.currentTimeMillis();
        this.bufferBrick = new ArrayList<>();

        this.velocitaElementi = this.VELOCITA_INIZIALE;
        this.intervalloSpawnAsteroidi = this.INTERVALLO_SPAWN_ASTEROIDI_INIZIALE;
        this.intervalloSpawnBrick = this.INTERVALLO_SPAWN_BRICK_INIZIALE;
        this.timeStampAsteroide = System.currentTimeMillis();
        this.timeStampBrick = System.currentTimeMillis();
        this.timeStampLivello = System.currentTimeMillis();

        this.associaEventi();
    }

    /**
     * Associa gli eventi della modalita
     */
    private void associaEventi(){
        try{
            this.collegaEventoScena(Proiettile.EVENTO_RIMOZIONE_PROIETTILE, this.getClass().getDeclaredMethod("eventoRimozioneProiettile", ParamList.class));
            this.collegaEventoScena(Asteroide.EVENTO_RIMOZIONE_ASTEROIDE, this.getClass().getDeclaredMethod("eventoRimozioneAsteroide", ParamList.class));
            this.collegaEventoScena(Asteroide.EVENTO_COLPO_PADDLE, this.getClass().getDeclaredMethod("eventoAsteroideColpoPaddle", ParamList.class));
            this.collegaEventoScena(Particella.EVENTO_RIMOZIONE_PARTICELLA, this.getClass().getDeclaredMethod("eventoRimozioneParticella", ParamList.class));
            this.collegaEventoScena(Proiettile.EVENTO_COLPO_ASTEROIDE, this.getClass().getDeclaredMethod("eventoProiettileColpoAsteroide", ParamList.class));
            this.collegaEventoScena(SpaceBrick.EVENTO_RIMOZIONE_BRICK, this.getClass().getDeclaredMethod("eventoRimozioneBrick", ParamList.class));
            this.collegaEventoScena(Proiettile.EVENTO_COLPO_BRICK, this.getClass().getDeclaredMethod("eventoColpoBrick", ParamList.class));
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void inizializzaElementiGlobali(int screenWidth, int screenHeight) {
        super.inizializzaElementiGlobali(screenWidth, screenHeight);
        this.spriteIconaSparo = new Sprite(R.drawable.space_paddle_fire_input, this.owner);
        spriteIconaSparo.resizeImage(new Vector2D(screenWidth, screenWidth).prodottoPerScalare(0.2f));
    }

    @Override
    protected void logicaRipristinoPosizioni() { }
    @Override
    protected void logicaEliminazioneVita() {}

    @Override
    protected void logicaAvanzamentoLivello() {
        if(this.giocoPartito){
            if(System.currentTimeMillis() - this.timeStampLivello > this.INTERVALLO_CAMBIO_LIVELLO){
                this.timeStampLivello = System.currentTimeMillis();
                this.intervalloSpawnBrick -= this.DECREMENTO_INTERVALLO_SPAWN_BRICK;
                this.intervalloSpawnAsteroidi -= this.DECREMENTO_INTERVALLO_SPAWN_ASTEROIDI;
                if(this.intervalloSpawnBrick < 100)
                    this.intervalloSpawnBrick = 100;
                if(this.intervalloSpawnAsteroidi < 100)
                    this.intervalloSpawnAsteroidi = 100;
                this.velocitaElementi += this.INCREMENTO_VELOCITA;
            }
        }else{
            this.timeStampLivello = System.currentTimeMillis();
        }
    }

    /**
     * Logica di spawn di un asteroide
     */
    private void logicaSpawnAsteroide(){
        if(this.giocoPartito){
            if(System.currentTimeMillis() - this.timeStampAsteroide > this.intervalloSpawnAsteroidi){
                this.timeStampAsteroide = System.currentTimeMillis();
                Asteroide asteroide = new Asteroide(
                        new Vector2D(0, 0),
                        new Vector2D(0, this.velocitaElementi),
                        new MultiSprite(R.drawable.space_paddle_asteroidi, this.owner, 5)
                );
                float width = asteroide.getSize().getPosX();
                float height = asteroide.getSize().getPosY();
                float posX = (width * 0.5f) + ((float)Math.random() * (this.screenWidth - width));
                float posY = -height * 0.5f;

                asteroide.setPosition(new Vector2D(posX, posY));
                this.addEntita(asteroide);
            }
        }else
            this.timeStampAsteroide = System.currentTimeMillis();

    }

    /**
     * Logica di spawn di un brick
     */
    private void logicaSpawnSpaceBrick(){
        if(this.bufferBrick.size() < this.NUMERO_MINIMO_ELEMENTI_BUFFER_BRICK && this.puoGenerare) {  //Riempiamo il buffer
            new Thread(this).start();
            this.puoGenerare = false;
        }
        if(this.giocoPartito){
            if(System.currentTimeMillis() - this.timeStampBrick > this.intervalloSpawnBrick){
                this.timeStampBrick = System.currentTimeMillis();

                SpaceBrick brick = this.bufferBrick.remove(0);
                brick.setSpeed(new Vector2D(0, this.velocitaElementi));
                this.addEntita(brick);
            }
        }else
            this.timeStampBrick = System.currentTimeMillis();

    }

    /**
     * Logica dello sparo di un proiettile
     */
    private void logicaSparo(){
        if(this.screenPressed && System.currentTimeMillis() - this.lastTimeStamp > this.INTERVALLO_SPARO && this.paddle != null){
            //Se lo schermo è premuto si prendono gli spot dei cannoni e si spawnano i proiettili, questo se il tempo trascorso dall'ultimo sparo è sufficiente
            Vector2D[] spot = this.paddle.getSpotCannoni();
            for(Vector2D s : spot){
                this.addEntita(new Proiettile(s, new Vector2D(0, this.VELOCITA_SPARO)));
                AudioUtil.avviaAudio("hit_paddle");
            }

            //Settiamo un nuovo timeStamp
            this.lastTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    protected void logicaDiPartita() {
        super.logicaDiPartita();
        this.logicaSparo();
        this.logicaSpawnAsteroide();
        this.logicaSpawnSpaceBrick();
    }

    @Override
    protected void iniziallizzazioneEntita(int screenWidth, int screenHeight) {
        //Caricamento dello sfondo
        this.sfondo = new Background(
                new Vector2D(screenWidth * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidth, screenHeight),
                30,
                600
        );

        //Caricamento del paddle
        this.paddle = new SpacePaddle(
                this.stile.getPercentualePosizionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()),
                this.stile.getImmaginePaddleStile(this.owner),
                new AnimatedSprite(R.drawable.space_paddle_razzo, this.owner, 4, 20),
                new Sprite(R.drawable.space_paddle_cannoni, this.owner)
        );
    }

    @Override
    protected void inizializzazioneAudio() {
        AudioUtil.clear();
        AudioUtil.loadAudio("background", R.raw.space_paddle_background, AudioUtil.MUSICA, true, this.owner.getContext());
        AudioUtil.avviaAudio("background");

        AudioUtil.loadAudio("hit_brick", this.stile.getSuonoHitBrick(), AudioUtil.EFFETTO, false, this.owner.getContext());
        AudioUtil.loadAudio("hit_paddle", this.stile.getSuonoHitPaddle(), AudioUtil.EFFETTO, false,this.owner.getContext());
        AudioUtil.loadAudio("life_lost", this.stile.getSuonoLifeLost(), AudioUtil.EFFETTO, false,this.owner.getContext());
    }

    @Override
    protected void inserimentoEntita() {
        this.addEntita(this.sfondo);
        this.addEntita(this.paddle);
    }

    /**
     * Disegna la zona di input
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    private void disegnaZonaInput(Canvas canvas, Paint paint){
        if(this.spriteIconaSparo != null && this.screenPressed){
            this.spriteIconaSparo.drawSprite(this.posizioneXDito, posizioneYDito, canvas, paint);
        }
    }

    /**
     * Effettua lo spawn di particelle
     * @param position Posizione di spawn
     */
    private void spawnParticelle(Vector2D position){
        for(int i = 0; i < this.NUMERO_PARTICELLE_ESPLOSIONE; i++){
            Particella particella = new Particella(position, new Vector2D(5, 5), Color.GRAY, 2000);
            particella.setDirection(new Vector2D(0, 1).ruotaVettore((int)(Math.random() * 360)));
            this.addEntita(particella);
        }
    }

    /**
     * Disegna il messaggio di start
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    private void disegnaMessaggioStart(Canvas canvas, Paint paint){
        if(!this.giocoPartito){
            float lastSize = paint.getTextSize();
            paint.setTextSize(40);
            paint.setColor(Color.rgb(255, 255, 0));
            String messaggio = "Tap to start!";
            Rect bound = new Rect();
            paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

            float posX = this.screenWidth * 0.5f - bound.right * 0.5f;
            float posY = this.screenHeight * 0.5f - bound.left * 0.5f;

            canvas.drawText(messaggio, posX, posY, paint);

            paint.setTextSize(lastSize);
        }
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.render(dt, screenWidth, screenHeight, canvas, paint);
        if(this.risorseCaricate){
            this.disegnaZonaInput(canvas, paint);
            this.disegnaMessaggioStart(canvas, paint);
        }
    }

    @Override
    public void touchEvent(Vector2D position, View v, MotionEvent e) {
        if(!this.giocoPartito)
            this.giocoPartito = true;

        if(this.status != null && status.getModalitaControllo() == GameStatus.TOUCH && this.paddle != null)
            this.paddle.setTargetX(position.getPosX());

        if(e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE){
            this.screenPressed = true;
            this.posizioneXDito = (int)position.getPosX();
            this.posizioneYDito = (int)position.getPosY();
        }else if(e.getAction() == MotionEvent.ACTION_UP)
            this.screenPressed = false;
    }

    @Override
    public void orientationChanged(float degree) {
        if(this.status != null && this.status.getModalitaControllo() == GameStatus.GYRO){
            //Se il giroscopio è attivo
            if(degree >= -40 && degree <= 40){
                float peso = Math.abs(degree) / 20.0f;
                float mezzoWidth = this.screenWidth * 0.5f;

                float targetX = mezzoWidth - ((degree / Math.abs(degree)) * peso * mezzoWidth);
                if(this.paddle != null)
                    this.paddle.setTargetX(targetX);
            }
        }
    }

    //Eventi
    /**
     * Evento usato quando bisogna rimuovere un proiettile
     * @param paramList Lista dei parametri
     */
    protected void eventoRimozioneProiettile(ParamList paramList){
        Proiettile proiettile = paramList.get("proiettile");
        if(proiettile != null){
            this.removeEntita(proiettile);
        }
    }

    /**
     * Evento usato quando bisogna rimuovere un asteroide
     * @param paramList Lista dei parametri
     */
    protected void eventoRimozioneAsteroide(ParamList paramList){
        Asteroide asteroide = paramList.get("asteroide");
        if(asteroide != null){
            this.removeEntita(asteroide);
        }
    }

    /**
     * Evento di rimozione di una particella
     * @param paramList Parametri passati
     */
    protected void eventoRimozioneParticella(ParamList paramList){
        Entity e = paramList.get("particella");
        if(e != null)
            this.removeEntita(e);
    }

    /**
     * Evento usato quando un asteroide colpisce la paddle
     * @param paramList Lista dei parametri
     */
    protected void eventoAsteroideColpoPaddle(ParamList paramList){
        this.eventoRimozioneAsteroide(paramList);
        Asteroide asteroide = paramList.get("asteroide");
        if(this.status != null) {
            this.status.decrementaVita(1);
            AudioUtil.avviaAudio("life_lost");
        }
        if(asteroide != null) {
            this.spawnParticelle(asteroide.getPosition());
            AudioUtil.avviaAudio("hit_brick");
        }
    }

    /**
     * Evento usato quando un proiettile colpisce un asteroide
     * @param paramList Lista dei parametri
     */
    protected void eventoProiettileColpoAsteroide(ParamList paramList){
        this.eventoRimozioneAsteroide(paramList);
        Asteroide asteroide = paramList.get("asteroide");
        if(asteroide != null) {
            this.spawnParticelle(asteroide.getPosition());
            AudioUtil.avviaAudio("hit_brick");
        }

    }

    /**
     * Evento usato quando un brick deve essere rimosso
     * @param paramList Lista dei parametri
     */
    protected void eventoRimozioneBrick(ParamList paramList){
        SpaceBrick brick = paramList.get("brick");
        if(brick != null)
            this.removeEntita(brick);
    }

    /**
     * Evento usato quando un proiettile colpisce un brick
     * @param paramList Lista dei parametri
     */
    protected void eventoColpoBrick(ParamList paramList){
        SpaceBrick brick = paramList.get("brick");
        if(brick != null){
            this.status.incrementaPunteggio(this.PUNTI_PER_COLPO);
            if(brick.getHealth() == 0){
                this.status.incrementaPunteggio(this.PUNTI_PER_DISTRUZIONE * brick.getMaxHealth());
                this.eventoRimozioneBrick(paramList);
                this.spawnParticelle(brick.getPosition());
                AudioUtil.avviaAudio("hit_brick");
            }
        }
    }

    @Override
    public void run() {
        for(int i = this.bufferBrick.size() - 1; i < this.NUMERO_MASSIMO_ELEMENTI_BUFFER_BRICK; i++){
            Sprite[] spriteBrick = this.stile.getImmagineBrickStile(this.owner);
            float width = this.screenWidth / this.COLONNE_SPAWN_BRICK;
            float height = width * 0.6f;
            float startX = width * 0.5f;
            float startY = -height * 0.5f;
            int index = (int)Math.floor(Math.random() * this.COLONNE_SPAWN_BRICK);

            float posX = startX + (width * index);
            float posY = startY;

            SpaceBrick brick = new SpaceBrick(
                    new Vector2D(posX, posY),
                    new Vector2D(width, height),
                    spriteBrick[(int)Math.floor(spriteBrick.length * Math.random())],
                    new MultiSprite(R.drawable.crepebrick, this.owner, 10),
                    (int)this.velocitaElementi
            );
            this.bufferBrick.add(brick);
        }
        this.puoGenerare = true;
    }
}