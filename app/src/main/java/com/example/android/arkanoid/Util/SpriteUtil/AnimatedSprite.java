package com.example.android.arkanoid.Util.SpriteUtil;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameCore.GameLoop;

public final class AnimatedSprite extends MultiSprite{
    private float targetMs;                     //ms di attesa tra un frame e l'altro
    private int fps;                            //fps dello sprite
    private long lastTimeStamp;                 //tempo di riferimento per l'aggiornamento dell'immagine

    public AnimatedSprite(int drawableId, GameLoop gameLoop, int nImages, int fps) {
        super(drawableId, gameLoop, nImages);

        this.fps = fps;
        this.targetMs = 1000.0f / this.fps;
        this.lastTimeStamp = System.currentTimeMillis();
    }

    /**
     * Aggiorna il frame visualizzato
     */
    private void aggiornaFrame(){
        if(System.currentTimeMillis() - this.lastTimeStamp > this.targetMs){
            boolean esito = this.setCurrentFrame(this.getCurrentFrame() + 1);
            if(!esito)
                this.setCurrentFrame(0);

            this.lastTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public void drawSprite(int posX, int posY, int width, int height, Canvas canvas, Paint paint) {
        this.aggiornaFrame();
        super.drawSprite(posX, posY, width, height, canvas, paint);
    }

    //Beam
    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
        this.targetMs = 1000.0f / this.fps;
    }
}
