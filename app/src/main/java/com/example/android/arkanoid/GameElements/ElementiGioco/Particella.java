package com.example.android.arkanoid.GameElements.ElementiGioco;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Particella extends Entity {
    public static final String EVENTO_RIMOZIONE_PARTICELLA = "PARTICELLA_RIMOZIONE_PARTICELLA";       //Evento per la rimozione della particella

    private final int INCREMENTO_VELOCITA = 100;                                                //Incremento della velocita della particella al secondo

    private final int colore;             //Colore della particella
    private final int durata;                   //Durata di vita della particella
    private long startTime;               //Tempo d'inizio della visualizzazione della particella

    public Particella(Vector2D position, Vector2D size, int colore, int durata) {
        super(
                "particella",
                position,
                new Vector2D(0, 1),
                size,
                new Vector2D(0, 0),
                null
        );

        this.setDirection(this.direction.ruotaVettore((int)(-40 + Math.random() * 80)));
        float speed = 300 + (float)( Math.random() * 300 );
        this.setSpeed(new Vector2D(speed, speed));
        this.durata = durata;
        this.colore = colore;
        this.startTime = -1;
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        this.position = this.getNextStep(dt);
        this.speed = Vector2D.sommaVettoriale(this.speed, new Vector2D(this.INCREMENTO_VELOCITA, this.INCREMENTO_VELOCITA).prodottoPerScalare(dt));
        if(this.startTime == -1)
            this.startTime = System.currentTimeMillis();
        if(System.currentTimeMillis() - this.startTime  > this.durata){
            AbstractScene scena = params.get(AbstractScene.PARAMETRO_SCENA);
            if(scena != null){
                ParamList pm = new ParamList();
                pm.add("particella", this);
                scena.sendEvent(Particella.EVENTO_RIMOZIONE_PARTICELLA, pm);
            }
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        paint.setColor(this.colore);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(
                (int)(this.position.getPosX() - (this.size.getPosX() * 0.5f)),
                (int)(this.position.getPosY() - (this.size.getPosY() * 0.5f)),
                (int)(this.position.getPosX() + (this.size.getPosX() * 0.5f)),
                (int)(this.position.getPosY() + (this.size.getPosY() * 0.5f)),
                paint
        );
    }
}
