package com.example.android.arkanoid.GameElements.ElementiGioco;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class PM extends Entity {
    public static String EVENTO_RIMOZIONE_POWERUP = "PM_RIMOZIONE_POWERUP";
    public static String EVENTO_RACCOLTA_POWERUP = "PM_RACCOLTA_POWERUP";

    protected AbstractAlterazione alterazione;          //Alterazione associata all'entità

    public PM(String name, Vector2D position, Vector2D size, Vector2D speed, Sprite sprite, AbstractAlterazione alterazione) {
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
            ParamList esito = new ParamList();
            esito.add("pm", this);

            Entity[] listaEntita = scena.getEntityByName("paddle");
            for(Entity entita : listaEntita){
                Paddle paddle = (Paddle)entita;

                if(paddle.getBounds().intersect(this.getBounds())){
                    scena.sendEvent(PM.EVENTO_RACCOLTA_POWERUP, esito);
                }
            }

            if(this.position.getPosY() - (this.size.getPosY() * 0.5f) > screenHeight)   //Evento di fuoriuscita dallo schermo
                scena.sendEvent(PM.EVENTO_RIMOZIONE_POWERUP, esito);
        }
    }

    public AbstractAlterazione getAlterazione() {
        return this.alterazione;
    }
}
