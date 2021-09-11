package com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle;

import android.widget.Space;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Asteroide extends Entity {
    public static final String EVENTO_RIMOZIONE_ASTEROIDE = "ASTEROIDE_RIMOZIONE_ASTEROIDE";
    public static final String EVENTO_COLPO_PADDLE = "ASTEROIDE_COLPO_PADDLE";

    public Asteroide(Vector2D position, Vector2D speed, MultiSprite sprite) {
        super(
                "asteroide",
                position,
                new Vector2D(0, 1),
                new Vector2D(100, 100),
                speed,
                sprite
        );
        sprite.setCurrentFrame((int)Math.floor(sprite.getnImages() * Math.random()));
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        Vector2D nextStep = this.getNextStep(dt);
        this.setPosition(nextStep);

        AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            ParamList parametri = new ParamList();
            parametri.add("asteroide", this);

            if(this.position.getPosY() > screenHeight + (this.size.getPosY() * 0.5f))
                scena.sendEvent(Asteroide.EVENTO_RIMOZIONE_ASTEROIDE, parametri);

            SpacePaddle paddle = scena.getFirstEntityByName("paddle");
            if(paddle != null && paddle.getBounds().intersect(this.getBounds()))
                scena.sendEvent(Asteroide.EVENTO_COLPO_PADDLE, parametri);
        }

        this.sprite.setRotazione(this.sprite.getRotazione() + (80 * dt) );
    }
}