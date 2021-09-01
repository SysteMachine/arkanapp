package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Brick extends Entity {
    public static int INF_HEALTH = -1;              //Vita infinita del blocco

    private final int MAX_SHAKE = 5;               //Massimo scostamento dello shake

    private long shakeStart;                        //Inizio dello shake
    private long shakeTimeStamp;                    //Controllo del tempo per il cambio degli index
    private Vector2D shakeOffset;                   //Offset di spostamento dello shake
    private int shakeDuration;                      //Durata dello shake
    private int msShake;                            //Ms di riferimento dello shake

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
        this.shakeOffset = new Vector2D(0, 0);
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        this.health = this.maxHealth;
    }

    /**
     * Avvia lo shake del brick
     * @param duration Durata dello shake
     * @param fps Fps dello shake
     */
    public void shake(int duration, int fps){
        if(this.health != Brick.INF_HEALTH){
            //Se il blocco è distruttibile allora si esegue l'animazione di shake
            this.shakeStart = System.currentTimeMillis();
            this.msShake = 1000 / fps;
            this.shakeTimeStamp = System.currentTimeMillis();
            this.shakeDuration = duration;
        }
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        super.logica(dt, screenWidth, screenHeight, params);
        if(System.currentTimeMillis() - this.shakeStart < this.shakeDuration){
            //Se è attivo lo shake
            if(System.currentTimeMillis() - this.shakeTimeStamp > this.msShake){
                this.shakeTimeStamp = System.currentTimeMillis();
                this.shakeOffset = new Vector2D(-this.MAX_SHAKE + ((float)Math.random() * this.MAX_SHAKE * 2), -this.MAX_SHAKE + ((float)Math.random() * this.MAX_SHAKE * 2));
            }
        }else{
            this.shakeOffset = new Vector2D(0, 0);
        }
    }

    /**
     * Decrementa la vita del brick
     */
    public void decrementaVita(){
        if(this.health != Brick.INF_HEALTH){
            this.health --;
            if(this.health < 0)
                this.health = 0;
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        Vector2D posizioneAlterata = Vector2D.sommaVettoriale(this.position, this.shakeOffset);

        if(this.sprite != null && this.sprite.isAviable() && this.isVisible ){
            //Se lo sprite esiste ed è visibile
            this.sprite.drawSprite(
                    (int)posizioneAlterata.getPosX(),
                    (int)posizioneAlterata.getPosY(),
                    canvas,
                    paint
            );
        }

        if(this.spriteCrepe != null && this.spriteCrepe.isAviable() && this.isVisible){
            float pesoVita = 1 - ( (float)this.getHealth() / (float)this.getMaxHealth() );
            int indexImmagine = (int)Math.ceil( (this.spriteCrepe.getnImages() - 1) * pesoVita );
            this.spriteCrepe.setCurrentFrame(indexImmagine);

            this.spriteCrepe.drawSprite(
                    (int)posizioneAlterata.getPosX(),
                    (int)posizioneAlterata.getPosY(),
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

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public void setSize(Vector2D size) {
        super.setSize(size);
        if(this.spriteCrepe != null)
            this.spriteCrepe.resizeImage(size);
    }
}
