package com.example.android.arkanoid.Editor;

import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;

public class ModalitaTestLivello extends ModalitaClassica {
    private LayerLivello livello;                   //Livello da mostrare nel gioco

    public ModalitaTestLivello(Stile stile, GameStatus status, LayerLivello livello) {
        super(stile, status);
        this.livello = livello;
        this.PUNTI_PER_COLPO = livello.getPuntiPerColpo();
        this.PUNTI_PER_PARTITA = livello.getPuntiTerminazione();
    }

    @Override
    protected void inizializzaPowerUP() {
        this.listaPowerUP = new PMList();
    }

    @Override
    public boolean metodoGenerazioneMappa(int i, int j) {
        boolean esito = false;

        if(this.mappa != null && this.livello != null){
            int[] blocco = this.livello.getBloccoAt(j, i);
            if(blocco != null){
                this.mappa.setVitaBlocchi(blocco[2]);
                esito = true;
            }
        }

        return esito;
    }

    @Override
    protected void logicaAvanzamentoLivello() {
        if(this.mappa.getVitaBlocchi() == 0) {
            if (this.gameOverListener != null) {
                this.gameOverListener.gameOver(this.status);
            }
        }
    }

    @Override
    public void eventoColpoBlocco(ParamList paramList) {
        super.eventoColpoBlocco(paramList);
    }

    @Override
    public void eventoColpoPaddle(ParamList paramList) {
        super.eventoColpoPaddle(paramList);
    }

    @Override
    public void eventoRotturaBlocco(ParamList parametri) {
        super.eventoRotturaBlocco(parametri);
    }

    @Override
    public void eventoRimozioneParticella(ParamList parametri) {
        super.eventoRimozioneParticella(parametri);
    }

    @Override
    public void eventoRimozionePowerup(ParamList parametri) {
        super.eventoRimozionePowerup(parametri);
    }

    @Override
    public void eventoRaccoltaPowerup(ParamList parametri) {
        super.eventoRaccoltaPowerup(parametri);
    }
}
