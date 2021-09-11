package com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class SpaceBrick extends Brick {
    public static String EVENTO_RIMOZIONE_BRICK = "SPACE_BRICK_RIMOZIONE_BRICK";

    public SpaceBrick(Vector2D position, Vector2D size, Sprite sprite, MultiSprite spriteCrepe, int velocita) {
        super(
                position,
                size,
                sprite,
                spriteCrepe,
                (int)(1 + Math.random() * 9));
        this.setDirection(new Vector2D(0, 1));
        this.setSpeed(new Vector2D(0, velocita));

        this.maxHealth = (int)(1 + (Math.random() * Map.MAX_HEALTH_BRICK));
        this.health = this.maxHealth;
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        super.logica(dt, screenWidth, screenHeight, params);
        Vector2D nextStep = this.getNextStep(dt);
        this.setPosition(nextStep);

        if(this.getPosition().getPosY() > screenHeight * 0.8f){
            float currY = this.getPosition().getPosY() - (screenHeight * 0.8f);
            float maxH = screenHeight * 0.2f;

            float peso = 1 - (currY / maxH);
            if(peso < 0)
                peso = 0;
            this.sprite.setAlpha((int)(100 * peso));
        }

        AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);
        if(scena != null){
            ParamList parametri = new ParamList();
            parametri.add("brick", this);
            if(this.getPosition().getPosY() > screenHeight + (this.getSize().getPosY() * 0.5f)) {
                scena.sendEvent(SpaceBrick.EVENTO_RIMOZIONE_BRICK, parametri);
            }
        }
    }
}
