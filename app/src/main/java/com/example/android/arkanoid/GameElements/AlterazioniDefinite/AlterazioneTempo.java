package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaTimeAttack;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.Timer;

public class AlterazioneTempo extends AbstractAlterazione {

    public AlterazioneTempo() {
        super(1000, "Time Recover");
    }

    @Override
    protected boolean logicaAttivazione(GameStatus status, ParamList parametri) {

        boolean esito = false;
        Timer timer = parametri.get(ModalitaTimeAttack.PARAMETRI_ALTERAZIONI_TIMER);
        if(timer != null){
            timer.aggiungiSecondi(10);
            esito = true;
        }
        return esito;

    }

    @Override
    protected void logicaDisattivazione(GameStatus status, ParamList parametri) {}


}
