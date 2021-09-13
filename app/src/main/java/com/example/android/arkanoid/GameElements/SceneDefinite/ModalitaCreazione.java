package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaCreazione extends ModalitaClassica{
    private Livello livello;                //Livello da utilizzare
    private int layerAttuale;               //Stage attuale del livello

    public ModalitaCreazione(Stile stile, GameStatus status, Livello livello) {
        super(stile, status);
        this.codiceModalita = 0;

        this.livello = livello;
        this.layerAttuale = 0;
    }

    @Override
    public boolean metodoGenerazioneMappa(int i, int j) {
        //Generazione della mappa in accordo con il salvataggio
        boolean esito = false;

        if(this.livello != null && this.mappa != null){
            LayerLivello ll = this.livello.getLayerLivello(this.layerAttuale);
            if(ll != null){
                int[] blocco = ll.getBloccoAt(j, i);
                if(blocco != null){
                    this.mappa.setVitaBlocchi(blocco[2]);
                    esito = true;
                }
            }
        }

        return esito;
    }

    @Override
    protected void logicaTerminazionePartita() {
        if(this.status.getHealth() == 0){
            this.gameOverListener.gameOver(this.status);
        }
    }

    @Override
    protected void logicaAvanzamentoLivello() {
        if(this.status != null && this.mappa.getTotalHealth() == 0 && this.status.getHealth() > 0){
            //Se la vita totale dei blocchi presenti nella scena è 0 allora il giocatore ha terminato il livello
            this.status.incrementaVita(this.VITA_PER_PARTITA);
            this.status.incrementaPunteggio(this.PUNTI_PER_PARTITA);

            try{
                //Eliminamo i blocchi non distruttibili rimasti
                Brick brick;
                this.mappa.azzeraContatori();
                while((brick = this.mappa.getNextBrick()) != null){
                    this.removeEntita(brick);
                }

                this.logicaAvanzamentoLayer();
                this.creaMappaLayer();

                //Generiamo il prossimo livello della mappa
                this.mappa.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneMappa", int.class, int.class), this);

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

    /**
     * Logica di avanzamento del layer
     */
    private void logicaAvanzamentoLayer(){
        if(this.livello != null){
            if(this.layerAttuale + 1 < this.livello.getNLivelli()) {
                this.layerAttuale ++;
            }else if(this.gameOverListener != null){
                this.gameOverListener.gameOver(this.status);
            }
        }else if(this.gameOverListener != null){
            this.gameOverListener.gameOver(this.status);
        }
    }

    /**
     * Genera la mappa del layer
     */
    private void creaMappaLayer(){
        if(this.livello != null){
            LayerLivello layerLivello = this.livello.getLayerLivello(this.layerAttuale);
            if(layerLivello != null){
                this.mappa = new Map(
                        layerLivello.getRighe(),
                        layerLivello.getColonne(),
                        this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(this.screenWidth, this.screenHeight)),
                        new Vector2D(screenWidth, layerLivello.getAltezza()),
                        1,
                        this.stile.getImmagineBrickStile(this.owner),
                        this.stile.getImmagineBrickIndistruttibileStile(this.owner),
                        new MultiSprite(R.drawable.crepebrick, this.owner, 10)
                );
            }
        }
    }

    /**
     * Cambia i parametri sulla base del layer attuale
     */
    private void cambiaParametriPerLayer(){
        if(this.livello != null){
            LayerLivello layerLivello = this.livello.getLayerLivello(this.layerAttuale);
            if(layerLivello != null){
                this.creaMappaLayer();
                this.stile.setIncrementoVelocitaPallaLivello(1);
                this.stile.setDecrementoVelocitaPaddleLivello(1);
                //Impostazione degli elementi su palla e paddle
                if(this.palla != null)
                    this.palla.setSpeed(new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()).prodottoPerScalare(layerLivello.getPercentualeIncrementoVelocitaPalla()));
                if(this.paddle != null)
                    this.paddle.setSpeed(new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()).prodottoPerScalare(layerLivello.getPercentualeIncrementoVelocitaPaddle()));
                //Modifica dei parametri della partita
                this.PUNTI_PER_COLPO = layerLivello.getPuntiPerColpo();
                this.PUNTI_PER_PARTITA = layerLivello.getPuntiTerminazione();
            }
        }
    }

    @Override
    protected void iniziallizzazioneEntita(int screenWidth, int screenHeight) {
        super.iniziallizzazioneEntita(screenWidth, screenHeight);
        this.cambiaParametriPerLayer();
        //Ricreiamo la mappa usando il metodo di questa classe
        if(this.mappa != null){
            try{
                this.mappa.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneMappa", int.class, int.class), this);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    //Eventi definiti
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
}
