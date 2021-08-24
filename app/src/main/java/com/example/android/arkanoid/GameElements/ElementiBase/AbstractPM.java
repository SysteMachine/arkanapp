package com.example.android.arkanoid.GameElements.ElementiBase;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class AbstractPM extends AbstractEntity{
    protected AbstractAlterazione alterazione;

    public AbstractPM(String name, Vector2D position, Vector2D size, Vector2D speed, Sprite sprite, AbstractAlterazione alterazione) {
        super(
                name,
                position,
                new Vector2D(0, 1),
                size,
                speed,
                sprite
        );
        this.alterazione = alterazione;
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        this.position = this.getNextStep(dt);

        AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            Paddle paddle = scena.getFirstEntityByName("paddle");
            ParamList esito = new ParamList();
            esito.add("powerup", this);
            if(paddle != null && paddle.getBounds().intersect(this.getBounds()))
                scena.sendEvent(ModalitaClassica.EVENTO_POWERUP, esito);
            if(this.position.getPosY() - (this.size.getPosY() * 0.5f) > screenHeight)
                scena.sendEvent(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP, esito);
        }
    }

    public AbstractAlterazione getAlterazione() {
        return this.alterazione;
    }
}
