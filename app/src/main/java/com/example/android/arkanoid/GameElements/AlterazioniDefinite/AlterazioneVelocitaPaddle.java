package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.Util.ParamList;

public class AlterazioneVelocitaPaddle extends AbstractAlterazione {
    private final float percentualeIncremento;            //Percentuale di incremento della velocità della palla

    public AlterazioneVelocitaPaddle(int durata, float percentualeIncremento) {
        super(durata, "Paddle speed-up");
        this.percentualeIncremento = percentualeIncremento;
    }

    @Override
    protected boolean logicaAttivazione(GameStatus status, ParamList parametri) {
        boolean esito = false;
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("paddle");
            if(paddle != null){
                paddle.setSpeed(paddle.getSpeed().prodottoPerScalare(percentualeIncremento));
                esito = true;
            }
        }
        return esito;
    }

    @Override
    protected void logicaDisattivazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("paddle");
            if(paddle != null){
                paddle.setSpeed(paddle.getSpeed().prodottoPerScalare(1.0f / this.percentualeIncremento));
            }
        }
    }
}
