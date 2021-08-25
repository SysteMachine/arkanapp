package com.example.android.arkanoid.GameElements.AlterazioniDefinite;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;

public class AlterazioneNumeroPalle extends AbstractAlterazione {
    private final Entity[] palle;

    public AlterazioneNumeroPalle(int durata, int numeroPalle) {
        super(durata, "Multi ball");
        this.palle = new Entity[numeroPalle];
    }

    @Override
    protected boolean attuaAlterazione(GameStatus status, ParamList parametri) {
        boolean esito = false;

        GameLoop gameLoop = parametri.get(ModalitaClassica.PARAMETRO_ALTERAZIONE_GAMELOOP);
        Stile stile = parametri.get(ModalitaClassica.PARAMETRO_ALTERAZIONE_STILE);
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);

        if(gameLoop != null && stile != null && scena != null){
            Ball pallaOriginale = scena.getFirstEntityByName("ball");
            for(int i = 0; i < palle.length; i++){
                palle[i] = new Ball(
                    pallaOriginale.getPosition(),
                    pallaOriginale.getSpeed(),
                    stile.getImmaginePallaStile(gameLoop),
                    (int)(pallaOriginale.getSize().getPosX() * 0.5f),
                    stile.getAngoloDiLancioMassimoPalla()
                );
                ((Ball)palle[i]).startPalla();
                scena.addEntita(palle[i]);
            }
            esito = true;
        }

        return esito;
    }

    @Override
    protected void disattivaAlterazione(GameStatus status, ParamList parametri) {
        AbstractScene scena = parametri.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            for(Entity e : this.palle){
                scena.removeEntita(e);
            }
        }

    }
}
