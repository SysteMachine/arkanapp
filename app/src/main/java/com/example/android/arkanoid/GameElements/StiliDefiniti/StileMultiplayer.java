package com.example.android.arkanoid.GameElements.StiliDefiniti;

import android.graphics.Color;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ReplaceColorRecord;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;

public class StileMultiplayer extends Stile {

    @Override
    protected void setStilePalla() {
        super.setStilePalla();
        this.immaginePalla = R.drawable.stilemultiplayer_ball;
        this.coloriPalla = new ReplaceColorRecord[0];
    }

    @Override
    protected void setStileSfondo() {
        super.setStileSfondo();
        this.immagineSfondo = R.drawable.stilemultiplayer_background;
    }

    /**
     * Restituisce l'immagine del paddle con lo stile impostato del giocatore
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmaginePaddleStileGiocatore(GameLoop gameLoop){
        this.coloriPaddle[0] = new ReplaceColorRecord(Color.WHITE, Color.rgb(0, 200, 0), 200);
        Sprite sprite = new Sprite(this.immaginePaddle, gameLoop);
        for(ReplaceColorRecord rc : this.coloriPaddle)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return sprite;
    }

    /**
     * Restituisce l'immagine del paddle con lo stile impostato dell'avversario
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmaginePaddleStileAvversario(GameLoop gameLoop){
        this.coloriPaddle[0] = new ReplaceColorRecord(Color.WHITE, Color.rgb(200, 0, 0), 200);
        Sprite sprite = new Sprite(this.immaginePaddle, gameLoop);
        for(ReplaceColorRecord rc : this.coloriPaddle)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return sprite;
    }
}
