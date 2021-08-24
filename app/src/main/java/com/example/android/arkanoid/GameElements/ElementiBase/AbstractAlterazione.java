package com.example.android.arkanoid.GameElements.ElementiBase;

import com.example.android.arkanoid.Util.ParamList;

public abstract class AbstractAlterazione {
    protected int durata;                       //Durata dell'alterazione

    protected long lastTimeStamp;               //Ultimo timeStamp per il controllo della durata
    protected boolean alterazioneAttiva;        //Flag di alterazione attiva

    protected String nomeAlterazione;           //Nome dell'alterazione

    public AbstractAlterazione(int durata, String nomeAlterazione) {
        this.nomeAlterazione = nomeAlterazione;
        this.durata = durata;

        this.lastTimeStamp = 0;
        this.alterazioneAttiva = false;
    }

    /**
     * Attua l'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     * @return Restituisce l'esito dell'elaborazione
     */
    protected abstract boolean attuaAlterazione(GameStatus status, ParamList parametri);

    /**
     * Disattiva l'alterazione
     * @param status Status del gioco
     * @param parametri Parametri della scena
     */
    protected abstract void disattivaAlterazione(GameStatus status, ParamList parametri);

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
            esito = this.attuaAlterazione(status, parametri);
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
            this.disattivaAlterazione(status, parametri);
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

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public boolean isAlterazioneAttiva() {
        return alterazioneAttiva;
    }

    public void setAlterazioneAttiva(boolean alterazioneAttiva) {
        this.alterazioneAttiva = alterazioneAttiva;
    }
}
