package com.example.android.arkanoid.GameElements.ElementiGioco.SpacePaddle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;
import com.example.android.arkanoid.VectorMat.Vector3D;

import java.util.ArrayList;

public class Background extends Entity {
    private int numeroStelle;                   //Numero di stelle da renderizzare
    private int velocitaStelle;                 //Velocita di movimento delle stelle
    private ArrayList<Vector3D> posizioniStelle;   //Posizioni delle stelle

    public Background(Vector2D position, Vector2D size, int numeroStelle, int velocitaStelle) {
        super(
                "background",
                position,
                new Vector2D(0, 0),
                size,
                new Vector2D(0, 0),
                null
        );

        this.numeroStelle = numeroStelle;
        this.velocitaStelle = velocitaStelle;
    }

    /**
     * Genera delle stelle
     */
    private void generaStelle(int screenWidth, int screenHeight){
        this.posizioniStelle = new ArrayList<>();
        for(int i = 0; i < this.numeroStelle; i++){
            int posX = (int)(Math.random() * screenWidth);
            int posY = (int)(Math.random() * screenHeight);
            int size = (int)(2 + (Math.random() * 2));

            this.posizioniStelle.add(new Vector3D(posX, posY, size));
        }
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        super.setup(screenWidth, screenHeight, params);
        this.generaStelle(screenWidth, screenHeight);
    }

    /**
     * Disegna una stella sullo schermo
     * @param posX Posizione X della stella
     * @param posY Posizione Y della stella
     * @param size Dimensione della stella
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    private void disegnaStella(int posX, int posY, int size, Canvas canvas, Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 255, 0));
        canvas.drawRect(
                posX - (size / 2),
                posY - (size / 2),
                posX + (size / 2),
                posY + (size / 2),
                paint
        );
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {
        super.logica(dt, screenWidth, screenHeight, params);
        for(Vector3D stella : this.posizioniStelle){
            stella.setPosY(stella.getPosY() + (this.velocitaStelle * dt));
            if(stella.getPosY() > screenHeight + (stella.getPosZ() * 0.5f) ) {
                float distnace = Math.abs(stella.getPosY() - screenHeight);
                stella.setPosY(-(stella.getPosZ() * 0.5f) + distnace);
                stella.setPosX((float)Math.random() * screenWidth);
            }
        }
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(
                0,
                0,
                this.size.getPosX(),
                this.size.getPosY(),
                paint
        );
        for(Vector3D stella : this.posizioniStelle){
            disegnaStella((int)stella.getPosX(), (int)stella.getPosY(), (int)stella.getPosZ(), canvas, paint);
        }
    }
}