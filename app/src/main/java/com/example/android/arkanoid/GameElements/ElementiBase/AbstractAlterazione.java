package com.example.android.arkanoid.GameElements.ElementiBase;

import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;

public abstract class AbstractAlterazione {
    protected int durata;                       //Durata dell'alterazione

    protected long lastTimeStamp;               //Ultimo timeStamp per il controllo della durata
    protected boolean alterazioneAttiva;        //Flag di alterazione attiva

    protected String nomeAlterazione;           //Nome dell'alterazione
    protected Sprite spriteIcona;               //Sprite che rappresenta l'icona dell'alterazione

    public AbstractAlterazione(int durata, String nomeAlterazione) {
        this.nomeAlterazione = nomeAlterazione;
        this.durata = durata;

        this.lastTimeStamp = 0;
        this.alterazioneAttiva = false;
        this.spriteIcona = null;
    }

    /**
     * Logica di attivazione dell'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     * @return Restituisce l'esito dell'elaborazione
     */
    protected abstract boolean logicaAttivazione(GameStatus status, ParamList parametri);

    /**
     * Logica di disattivazione dell'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     */
    protected abstract void logicaDisattivazione(GameStatus status, ParamList parametri);

    /**
     * Attiva l'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     * @return Restituisce l'esito dell'elaborazione
     */
    public boolean attivaAlterazione(GameStatus status, ParamList parametri){
        boolean esito = false;
        if(!this.alterazioneAttiva){
            this.lastTimeStamp = System.currentTimeMillis();
            this.alterazioneAttiva = true;
            esito = this.logicaAttivazione(status, parametri);
        }
        return esito;
    }

    /**
     * Ferma l'aterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     */
    private void fermaAlterazione(GameStatus status, ParamList parametri){
        if(this.alterazioneAttiva && System.currentTimeMillis() - this.lastTimeStamp > this.durata){
            this.alterazioneAttiva = false;
            this.logicaDisattivazione(status, parametri);
        }
    }

    /**
     * Logica dell'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     */
    public void logica(GameStatus status, ParamList parametri){
        if(this.alterazioneAttiva)
            this.fermaAlterazione(status, parametri);
    }

    //beam
    public int getDurata() {
        return durata;
    }

    public boolean isAlterazioneAttiva() {
        return alterazioneAttiva;
    }

    public String getNomeAlterazione() {
        return nomeAlterazione;
    }

    public Sprite getSpriteIcona() {
        return spriteIcona;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public void setSpriteIcona(Sprite spriteIcona) {
        this.spriteIcona = spriteIcona;
    }
}
