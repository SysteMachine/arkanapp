package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.BaseElements.Alterazione;
import com.example.android.arkanoid.GameElements.BaseElements.GameStatus;
import com.example.android.arkanoid.Util.ParamList;

public class AlterazioneVelocitaPalla extends Alterazione{
    private float percentualeIncremento;            //Percentuale di incremento della velocità della palla
    private float vecchiaVelocita;                  //Vecchia velocità della palla

    public AlterazioneVelocitaPalla(int durata, float percentualeIncremento) {
        super(durata, "Ball speed-up");
        this.percentualeIncremento = percentualeIncremento;
    }

    @Override
    protected boolean attuaAlterazione(GameStatus status, ParamList parametri) {
        boolean esito = false;
        AbstractScene scena = parametri.get(AbstractScene.SCENA);
        if(scena != null){
            Ball palla = scena.getFirstEntityByName("Ball");
            if(palla != null){
                this.vecchiaVelocita = palla.getSpeed();
                palla.setSpeed(palla.getSpeed() * this.percentualeIncremento);
                esito = true;
            }
        }
        return esito;
    }

    @Override
    protected void disattivaAlterazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.SCENA);
        if(scena != null){
            Ball palla = scena.getFirstEntityByName("Ball");
            if(palla != null){
                palla.setSpeed(this.vecchiaVelocita);
            }
        }
    }
}
