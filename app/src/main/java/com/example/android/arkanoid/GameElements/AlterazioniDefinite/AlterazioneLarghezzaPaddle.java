package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class AlterazioneLarghezzaPaddle extends AbstractAlterazione {
    private float percentualeIncremento;

    public AlterazioneLarghezzaPaddle(int durata, float percentualeIncremento) {
        super(durata, "Paddle-up");
        this.percentualeIncremento = percentualeIncremento;
    }

    @Override
    protected boolean logicaAttivazione(GameStatus status, ParamList parametri) {
        boolean esito = false;

        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("paddle");
            paddle.setSize(paddle.getSize().prodottoPerVettore(new Vector2D(this.percentualeIncremento, 1)));
            esito = true;
        }

        return esito;
    }

    @Override
    protected void logicaDisattivazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("paddle");
            paddle.setSize(paddle.getSize().prodottoPerVettore(new Vector2D(1.0f / this.percentualeIncremento, 1)));
        }
    }
}
