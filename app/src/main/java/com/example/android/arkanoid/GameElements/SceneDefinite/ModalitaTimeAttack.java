package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.TimeRecover;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.Timer;

public class ModalitaTimeAttack extends ModalitaClassica {

    protected static int DIMENSIONE_ZONA_TIMER = 60;

    private final int TEMPO_INIZIALE = 180; //3 minuti iniziali
    private final int SECONDI_BONUS_ROTTURA_BLOCCO = 2; //2 secondi bonus ogni volta che si rompe un blocco
    private final int SECONDI_PERSI_MORTE = -30;
    private final int SECONDI_BONUS_COMPLETAMENTO_LIVELLO = 60;
    public static String PARAMETRI_ALTERAZIONI_TIMER = "TIMER";

    private Timer timer;
    private String tempoRimanente = "";


    public ModalitaTimeAttack(Stile stile, GameStatus status) {
        super(stile, status);
        this.timer = new Timer(TEMPO_INIZIALE);
        timer.avviaTimer();
        this.inizializzaPowerUP();
        this.registraEventi();
        this.codiceModalita = 3;
        this.status.setHealth(0);

    }

    @Override
    protected void inizializzaPowerUP() {
        super.inizializzaPowerUP();
        this.listaPowerUP.addPowerupMalus(TimeRecover.class, 15);
    }


    private void updateTimer(){

        int secondi = timer.getTimerAttuale();

        if(secondi < 10)
            tempoRimanente = "0" + secondi;
        else
            tempoRimanente = "" + secondi;

    }


    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.render(dt, screenWidth, screenHeight, canvas, paint);
        this.disegnaTimer(dt, screenWidth, screenHeight, canvas, paint);
    }

    protected void disegnaTimer(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        float lastSize = paint.getTextSize();
        paint.setTextSize(screenHeight * this.PERCENTUALE_DIMENSIONE_FONT);

        updateTimer();
        String messaggio = tempoRimanente;
        if (timer.getTimerAttuale() <= 0)
            messaggio = "00";
        Rect bound = new Rect();
        paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

        float startX = (screenWidth * 0.5f) - (bound.right * 0.5f);
        float startY = (this.DIMENSIONE_ZONA_TIMER * 0.5f) - (bound.bottom * 0.5f);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(messaggio, startX, startY, paint);
        paint.setTextSize(lastSize);

    }

    @Override
    protected void eventoColpoBlocco(ParamList paramList) {
        super.eventoColpoBlocco(paramList);
    }

    @Override
    protected void eventoColpoPaddle(ParamList paramList) {
        super.eventoColpoPaddle(paramList);
    }

    @Override
    protected void eventoRotturaBlocco(ParamList parametri) {
        super.eventoRotturaBlocco(parametri);
        timer.aggiungiSecondi(SECONDI_BONUS_ROTTURA_BLOCCO);
    }


    @Override
    protected void eventoRimozioneParticella(ParamList parametri) {
        super.eventoRimozioneParticella(parametri);
    }

    @Override
    protected void eventoRimozionePowerup(ParamList parametri) {
        super.eventoRimozionePowerup(parametri);
    }

    @Override
    protected void eventoRaccoltaPowerup(ParamList parametri) {
        super.eventoRaccoltaPowerup(parametri);
    }

    @Override
    protected void registraEventi() {
        super.registraEventi();
    }

    @Override
    public boolean metodoGenerazioneMappa(int i, int j) {
        return super.metodoGenerazioneMappa(i, j);
    }

    @Override
    protected void logicaEliminazioneVita() {
        if(this.palla != null && this.status != null){
            if(this.palla.getPosition().getPosY() > this.PERCENTUALE_DEATH_ZONE * this.screenHeight){
                this.timer.aggiungiSecondi(SECONDI_PERSI_MORTE);
                this.logicaRipristinoPosizioni();
                AudioUtil.avviaAudio("life_lost");
            }
        }
    }

    @Override
    protected void logicaAvanzamentoLivello() {
        if(this.status != null && this.mappa.getTotalHealth() == 0 && this.status.getHealth() > 0){
            //Se la vita totale dei blocchi presenti nella scena è 0 allora il giocatore ha terminato il livello
            this.timer.aggiungiSecondi(SECONDI_BONUS_COMPLETAMENTO_LIVELLO);
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
    protected ParamList creaParametriAlterazioni() {
        ParamList parametri = super.creaParametriAlterazioni();
        parametri.add(PARAMETRI_ALTERAZIONI_TIMER, this.timer);
        return parametri;
    }

    @Override
    protected void logicaTerminazionePartita(){
        if(timer.getTimerAttuale() <= 0 && this.gameOverListener != null)
            this.gameOverListener.gameOver(this.status);
    }
}

