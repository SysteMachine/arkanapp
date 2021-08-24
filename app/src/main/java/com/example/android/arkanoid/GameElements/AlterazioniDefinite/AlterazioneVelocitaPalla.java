package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.Util.ParamList;

public class AlterazioneVelocitaPalla extends AbstractAlterazione {
    private final float percentualeIncremento;            //Percentuale di incremento della velocità della palla

    public AlterazioneVelocitaPalla(int durata, float percentualeIncremento) {
        super(durata, "Ball speed-up");
        this.percentualeIncremento = percentualeIncremento;
    }

    @Override
    protected boolean attuaAlterazione(GameStatus status, ParamList parametri) {
        boolean esito = false;
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Ball palla = scena.getFirstEntityByName("Ball");
            if(palla != null){
                palla.setSpeed(palla.getSpeed().prodottoPerScalare(percentualeIncremento));
                esito = true;
            }
        }
        return esito;
    }

    @Override
    protected void disattivaAlterazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Ball palla = scena.getFirstEntityByName("Ball");
            if(palla != null){
                palla.setSpeed(palla.getSpeed().prodottoPerScalare(1.0f / this.percentualeIncremento));
            }
        }
    }
}
