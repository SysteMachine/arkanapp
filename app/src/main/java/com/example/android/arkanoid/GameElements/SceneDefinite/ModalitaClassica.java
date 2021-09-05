package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiGioco.PM;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.ElementiGioco.Particella;
import com.example.android.arkanoid.GameElements.ElementiGioco.Sfondo;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BrickHealthUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.MultiBall;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleUp;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Method;

public class ModalitaClassica extends AbstractModalita{
    //Costanti di modalita
    protected float PERCENTUALE_DEATH_ZONE = 0.95f;             //Percentuale della deathzone in base all'altezza dello schermo, al 95% comincia
    protected final int PARTICELLE_ROTTURA_BLOCCO = 40;         //Numero di particelle spawnate alla rottura del blocco
    protected final float PERCENTUALE_DIMENSIONE_POWERUP = 0.1f;//PErcentuale di dimensione del powerup
    protected int PUNTI_PER_COLPO = 100;                        //Punti per ogni colpo ad un blocco
    protected int PUNTI_PER_PARTITA = 1000;                     //Punti per ogni livello concluso
    protected int VITA_PER_PARTITA = 2;                         //Vita acquisita per ogni livello concluso

    //Elementi di gioco
    protected Ball palla;                                       //Palla di scena
    protected Paddle paddle;                                    //Paddle di scena
    protected Map mappa;                                        //Mappa della scena
    protected Sfondo sfondo;                                    //Sfondo della mappa

    protected PMList listaPowerUP;                              //Lista dei powerup della modalità

    public ModalitaClassica(Stile stile, GameStatus status) {
        super(stile, status);
        this.inizializzaPowerUP();
        this.registraEventi();
        this.codiceModalita = 1;
    }

    /**
     * Inizializza la lista dei powerup della modalita
     */
    protected void inizializzaPowerUP(){
        this.listaPowerUP = new PMList();

        this.listaPowerUP.addPowerupMalus(BallSpeedUp.class, 10);
        this.listaPowerUP.addPowerupMalus(BallSpeedDown.class, 5);
        this.listaPowerUP.addPowerupMalus(MultiBall.class, 2);
        this.listaPowerUP.addPowerupMalus(BrickHealthUp.class, 20);
        this.listaPowerUP.addPowerupMalus(PaddleUp.class, 10);
        this.listaPowerUP.addPowerupMalus(PaddleDown.class, 5);
        this.listaPowerUP.addPowerupMalus(PaddleSpeedUp.class, 10);
        this.listaPowerUP.addPowerupMalus(PaddleSpeedDown.class, 5);
    }

    /**
     * Registra gli eventi della modalita
     */
    protected void registraEventi(){
        try{
            this.collegaEventoScena(Ball.EVENTO_BLOCCO_COLPITO, this.getClass().getDeclaredMethod("eventoColpoBlocco", ParamList.class));
            this.collegaEventoScena(Ball.EVENTO_BLOCCO_ROTTO, this.getClass().getDeclaredMethod("eventoRotturaBlocco", ParamList.class));
            this.collegaEventoScena(Ball.EVENTO_PADDLE_COLPITO, this.getClass().getDeclaredMethod("eventoColpoPaddle", ParamList.class));
            this.collegaEventoScena(PM.EVENTO_RACCOLTA_POWERUP, this.getClass().getDeclaredMethod("eventoRaccoltaPowerup", ParamList.class));
            this.collegaEventoScena(PM.EVENTO_RIMOZIONE_POWERUP, this.getClass().getDeclaredMethod("eventoRimozionePowerup", ParamList.class));
            this.collegaEventoScena(Particella.EVENTO_RIMOZIONE_PARTICELLA, this.getClass().getDeclaredMethod("eventoRimozioneParticella", ParamList.class));
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Metodo standard per la generazione della mappa
     * @param i Posizione della riga da generare
     * @param j Posizione della colonna da generare
     * @return Restituisce true se deve essere posizionato un blocco, altrimenti restituisce false
     */
    public boolean metodoGenerazioneMappa(int i, int j){
        boolean esito = false;

        if(this.mappa != null){
            try{
                Method metodoGenerazioneMappa = this.mappa.getClass().getDeclaredMethod("metodoGenerazioneBase", int.class, int.class);
                esito = (boolean)metodoGenerazioneMappa.invoke(this.mappa, i, j);
            }catch (Exception e){e.printStackTrace();}
        }

        return esito;
    }

    @Override
    protected void logicaRipristinoPosizioni() {
        if(this.palla != null && this.paddle != null){
            this.palla.setup(this.screenWidth, this.screenHeight, new ParamList());
            this.palla.stopPalla();
            this.paddle.setup(this.screenWidth, this.screenHeight, new ParamList());
        }
    }

    @Override
    protected void logicaEliminazioneVita() {
        if(this.palla != null && this.status != null){
            if(this.palla.getPosition().getPosY() > this.PERCENTUALE_DEATH_ZONE * this.screenHeight){
                this.status.decrementaVita(1);
                this.logicaRipristinoPosizioni();
                AudioUtil.avviaAudio("life_lost");
            }
        }
    }

    @Override
    protected void logicaAvanzamentoLivello() {
        if(this.status != null && this.mappa.getTotalHealth() == 0 && this.status.getHealth() > 0){
            //Se la vita totale dei blocchi presenti nella scena è 0 allora il giocatore ha terminato il livello
            this.status.incrementaVita(this.VITA_PER_PARTITA);
            this.status.incrementaPunteggio(this.PUNTI_PER_PARTITA);

            this.paddle.setSpeed(this.paddle.getSpeed().prodottoPerScalare(this.stile.getDecrementoVelocitaPaddleLivello()));
            this.palla.setSpeed(this.palla.getSpeed().prodottoPerScalare(this.stile.getIncrementoVelocitaPallaLivello()));
            this.mappa.setVitaBlocchi(this.mappa.getVitaBlocchi() + this.stile.getTassoIncrementoVitaBlocchi());

            try{
                //Eliminamo i blocchi non distruttibili rimasti
                Brick brick;
                this.mappa.azzeraContatori();
                while((brick = this.mappa.getNextBrick()) != null){
                    this.removeEntita(brick);
                }

                //Generiamo il prossimo livello della mappa
                this.mappa.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneMappa", int.class, int.class), this);
                this.mappa.inserisciOstacoli(this.stile.getNumeroBlocchiIndistruttibili());

                //Aggiunta dei nuovi brick alla scena
                this.mappa.azzeraContatori();
                while((brick = this.mappa.getNextBrick()) != null){
                    this.addEntita(brick);
                }
            }catch (Exception e){e.printStackTrace();}

            //Ripristina la posizione post mote
            this.logicaRipristinoPosizioni();
            AudioUtil.avviaAudio("level_complete");
        }
    }

    @Override
    protected void iniziallizzazioneEntita(int screenWidth, int screenHeight) {
        //Creazione della palla
        this.palla = new Ball(
                this.stile.getPercentualePosizionePalla().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()),
                this.stile.getImmaginePallaStile(this.owner),
                (int)(this.stile.getPercentualeRaggioPalla() * screenWidth),
                this.stile.getAngoloDiLancioMassimoPalla()
        );
        this.palla.setOffsetCollisioneSuperiore(this.DIMENSIONE_ZONA_PUNTEGGIO);

        //Creazione del paddle
        this.paddle = new Paddle(
                this.stile.getPercentualePosizionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()),
                this.stile.getImmaginePaddleStile(this.owner)
        );

        //Creazione della mappa
        this.mappa = new Map(
                this.stile.getNumeroRigheMappa(),
                this.stile.getNumeroColonneMappa(),
                this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getVitaInizialeBlocco(),
                this.stile.getImmagineBrickStile(this.owner),
                this.stile.getImmagineBrickIndistruttibileStile(this.owner),
                new MultiSprite(R.drawable.crepebrick, this.owner, 10)
        );
        this.mappa.inserisciOstacoli(stile.getNumeroBlocchiIndistruttibili());

        //Creazione dello sfondo
        this.sfondo = new Sfondo(
                new Vector2D(screenWidth * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidth, screenHeight),
                this.stile.getImmagineSfondoStile(this.owner)
        );
    }

    @Override
    protected void inizializzazioneAudio() {
        AudioUtil.clear();
        AudioUtil.loadAudio("background", this.stile.getSuonoBackground(), this.owner.getContext());
        AudioUtil.getMediaPlayer("background").setLooping(true);
        AudioUtil.getMediaPlayer("background").start();

        AudioUtil.loadAudio("hit_brick", this.stile.getSuonoHitBrick(), this.owner.getContext());
        AudioUtil.loadAudio("hit_paddle", this.stile.getSuonoHitPaddle(), this.owner.getContext());
        AudioUtil.loadAudio("level_complete", this.stile.getSuonoLevelComplete(), this.owner.getContext());
        AudioUtil.loadAudio("life_lost", this.stile.getSuonoLifeLost(), this.owner.getContext());
    }

    @Override
    protected void inserimentoEntita() {
        this.addEntita(this.sfondo);
        this.addEntita(this.palla);
        this.addEntita(this.paddle);
        Entity e;
        this.mappa.azzeraContatori();
        while( (e = this.mappa.getNextBrick()) != null ){
            this.addEntita(e);
        }

    }

    @Override
    public void touchEvent(Vector2D position, View v, MotionEvent e) {
        if(this.palla != null && !this.palla.isMoving() && e.getAction() == MotionEvent.ACTION_UP)
            this.palla.startPalla();
        if(this.status != null && this.status.getModalitaControllo() == GameStatus.TOUCH){
            if(this.paddle != null)
                this.paddle.setTargetX(position.getPosX());
        }
    }

    @Override
    public void orientationChanged(float degree) {
        System.out.println(degree);
    }

    //EventiModalita

    /**
     * Evento usato quando la palla colpisce un blocco
     * @param paramList Lista dei parametri
     */
    protected void eventoColpoBlocco(ParamList paramList){
        if(this.status != null)
            this.status.incrementaPunteggio(this.PUNTI_PER_COLPO);
        AudioUtil.avviaAudio("hit_brick");
    }

    /**
     * Evento usato quando la palla colpisce la paddle
     * @param paramList Lista dei parametri
     */
    protected void eventoColpoPaddle(ParamList paramList){
        AudioUtil.avviaAudio("hit_paddle");
    }

    /**
     * Evento usato quando la palla rompe un blocco
     * @param parametri Lista dei parametri
     */
    protected void eventoRotturaBlocco(ParamList parametri){
        Brick brick = parametri.get("brick");
        if(brick != null){
            //Distrugge il blocco e inserisce delle particelle
            for(int i = 0; i < this.PARTICELLE_ROTTURA_BLOCCO; i++){
                this.addEntita(new Particella(brick.getPosition(), new Vector2D(5, 5), Color.GRAY, 1500));
            }
            this.removeEntita(brick);

            PM powerup = this.listaPowerUP.getPowerup(
                    brick.getPosition(),
                    new Vector2D(
                            this.screenWidth * this.PERCENTUALE_DIMENSIONE_POWERUP,
                            this.screenWidth * this.PERCENTUALE_DIMENSIONE_POWERUP
                    ),
                    this.owner
            );
            if(powerup != null)
                this.addEntita(powerup);
        }
    }

    /**
     * Evento di rimozione di una particella
     * @param parametri Parametri passati
     */
    protected void eventoRimozioneParticella(ParamList parametri){
        Entity e = parametri.get("particella");
        if(e != null)
            this.removeEntita(e);
    }

    /**
     * Evento di rimozione dell'entità fisica powerup
     * @param parametri Parametri passati
     */
    protected void eventoRimozionePowerup(ParamList parametri){
        PM pm = parametri.get("pm");
        if(pm != null)
            this.removeEntita(pm);
    }

    /**
     * Evento di raccolta del powerup
     * @param parametri Parametri passati
     */
    protected void eventoRaccoltaPowerup(ParamList parametri){
        //Rimuove l'entità dalla scena
        PM pm = parametri.get("pm");
        if(pm != null){
            //Rimuoviamo l'entità fisica del powerup
            this.eventoRimozionePowerup(parametri);

            AbstractAlterazione alterazione = pm.getAlterazione();
            alterazione.setSpriteIcona(pm.getSprite());
            alterazione.attivaAlterazione(this.status, this.creaParametriAlterazioni());

            //Aggiunge l'alterazione alle alterazioni attive
            this.alterazioniAttive.add(alterazione);
        }
    }
}