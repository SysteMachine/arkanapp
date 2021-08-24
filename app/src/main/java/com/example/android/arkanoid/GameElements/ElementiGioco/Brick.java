package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractEntity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Brick extends AbstractEntity {
    private final int maxHealth;                    //Vita massima del brick
    private int health;                             //Vita attuale del brick

    private final MultiSprite spriteCrepe;          //Sprite delle crepe

    public Brick(Vector2D position, Vector2D size, Sprite sprite, MultiSprite spriteCrepe, int health) {
        super(
                "brick",
                position,
                new Vector2D(0, 0),
                size,
                new Vector2D(0, 0),
                sprite
        );

        this.spriteCrepe = spriteCrepe;
        this.maxHealth = health;
        this.health = health;
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        this.health = this.maxHealth;
    }

    /**
     * Decrementa la vita del brick
     */
    public void decrementaVita(){
        this.health --;
        if(this.health < 0)
            this.health = 0;
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        super.drawEntity(canvas, paint);

        if(this.spriteCrepe != null){
            float pesoVita = 1 - ( (float)this.getHealth() / (float)this.getMaxHealth() );
            int indexImmagine = (int)Math.ceil( (this.spriteCrepe.getnImages() - 1) * pesoVita );
            this.spriteCrepe.setCurrentFrame(indexImmagine);

            this.spriteCrepe.drawSprite(
                    (int)this.getPosition().getPosX(),
                    (int)this.getPosition().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    //Beam
    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public void setSize(Vector2D size) {
        super.setSize(size);
        if(this.spriteCrepe != null)
            this.spriteCrepe.resizeImage(size);
    }
}
