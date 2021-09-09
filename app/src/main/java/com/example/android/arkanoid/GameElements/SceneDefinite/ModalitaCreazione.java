package com.example.android.arkanoid.GameElements.SceneDefinite;

import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.R;
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
    protected void logicaAvanzamentoLivello() {
        boolean incrementoLivello = this.logicaAvanzamentoAggiuntiva();
        super.logicaAvanzamentoLivello();
        if(incrementoLivello){
            this.cambiaParametriPerLayer();
            this.creaEInserisciMappa();
        }
    }

    /**
     * Logica di avanzamento aggiunta
     * @return Restitusce true se il livello è stato incrementato
     */
    private boolean logicaAvanzamentoAggiuntiva(){
        boolean esito = false;
        if(this.mappa.getTotalHealth() == 0 && this.status.getHealth() > 0){
            if(this.livello != null){
                if(this.layerAttuale + 1 < this.livello.getNLivelli()) {
                    this.layerAttuale ++;
                    esito = true;
                }else if(this.gameOverListener != null){
                    this.gameOverListener.gameOver(this.status);
                }
            }else if(this.gameOverListener != null){
                this.gameOverListener.gameOver(this.status);
            }
        }
        return esito;
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
     * Rimuove tutti i brick dalla scena
     */
    private void rimuoviBrickScena(){
        Entity[] entita = this.getEntityByName("brick");
        for(Entity e : entita)
            this.removeEntita(e);
    }

    /**
     * Crea e inserisce i brick all'interno della scena dalla mappa
     */
    private void creaEInserisciMappa(){
        if(this.mappa != null){
            try{
                this.mappa.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneMappa", int.class, int.class), this);
                this.mappa.azzeraContatori();
                Brick b;
                while( (b = this.mappa.getNextBrick()) != null )
                    this.addEntita(b);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Cambia i parametri sulla base del layer attuale
     */
    private void cambiaParametriPerLayer(){
        if(this.livello != null){
            LayerLivello layerLivello = this.livello.getLayerLivello(this.layerAttuale);
            if(layerLivello != null){
                this.rimuoviBrickScena();
                this.creaMappaLayer();
                this.stile.setIncrementoVelocitaPallaLivello(1);
                this.stile.setDecrementoVelocitaPaddleLivello(1);

                //Impostazione degli elementi su palla e paddle
                if(this.palla != null){
                    this.palla.setSpeed(new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()).prodottoPerScalare(layerLivello.getPercentualeIncrementoVelocitaPalla()));
                }

                if(this.paddle != null){
                    this.paddle.setSpeed(new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()).prodottoPerScalare(layerLivello.getPercentualeIncrementoVelocitaPaddle()));
                }

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
