package com.example.android.arkanoid.GameElements.StiliDefiniti;

import android.graphics.Color;

import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ReplaceColorRecord;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class StileAtzeco extends Stile {

    protected void setStilePalla(){
        this.immaginePalla = R.drawable.stilefuturistico_ball;
        this.angoloDiLancioMassimoPalla = 40;
        this.velocitaInizialePalla = 800;
        this.velocitaRotazionePalla = 45;
        this.percentualeRaggioPalla = 0.03f;
        this.percentualePosizionePalla = new Vector2D(0.5f, 0.8f);
        this.coloriPalla = new ReplaceColorRecord[0];

    }

    protected void setStilePaddle(){
        this.immaginePaddle = R.drawable.stilefuturistico_paddle;
        this.velocitaInizialePaddle = 1000;
        this.percentualeDimensionePaddle = new Vector2D(0.2f, 0.03f);
        this.percentualePosizionePaddle = new Vector2D(0.5f, 0.85f);
        this.coloriPaddle = new ReplaceColorRecord[0];
    }

    protected void setStileBrick(){
        this.immagineBrick = R.drawable.stileatzeco_brick;
        this.numeroColonneMappa = 8;
        this.numeroRigheMappa = 6;
        this.percentualePosizioneMappa = new Vector2D(0, 0.15f);
        this.percentualeDimensioneMappa = new Vector2D(1, 0.25f);
        this.coloriBrick = new ReplaceColorRecord[0];
        this.coloriCasualiBrick = new int[4];
        this.coloriCasualiBrick[0] = Color.YELLOW;
        this.coloriCasualiBrick[1] = Color.LTGRAY;
        this.coloriCasualiBrick[2] = Color.rgb(218, 160, 109);
        this.coloriCasualiBrick[3] = Color.rgb(176, 224, 230);
    }

    protected void setStileSfondo(){
        this.immagineSfondo = R.drawable.stileatzeco_background;
        this.coloriSfondo = new ReplaceColorRecord[0];
    }
}
