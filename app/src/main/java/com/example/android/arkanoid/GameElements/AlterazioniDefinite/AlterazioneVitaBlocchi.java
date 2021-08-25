package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.Util.ParamList;

public class AlterazioneVitaBlocchi extends AbstractAlterazione {
    public AlterazioneVitaBlocchi() {
        super(1000, "Brick health-up");
    }

    @Override
    protected boolean attuaAlterazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Entity[] entita = scena.getEntityByName("brick");
            for(Entity e : entita){
                Brick b = (Brick)e;
                if(b.getHealth() != 0 && b.getHealth() + 1 <= b.getMaxHealth())
                    b.setHealth(b.getHealth() + 1);
            }
        }

        return false;
    }

    @Override
    protected void disattivaAlterazione(GameStatus status, ParamList parametri) {}
}
