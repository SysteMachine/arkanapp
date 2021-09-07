package com.example.android.arkanoid.Editor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.AbstractGameComponent;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.VectorMat.Vector3D;

import java.util.ArrayList;

public class PannelloMappa extends AbstractGameComponent implements View.OnTouchListener {
    private Vector3D colore1;
    private Vector3D colore10;

    private LayerLivello layer;     //Layer del livello da disegnare
    private int vitaBrick;          //Vita del brickImpostato

    public PannelloMappa(LayerLivello layer) {
        super(0);

        this.layer = layer;
        this.vitaBrick = 0;
        this.colore1 = new Vector3D(0, 1, 0);
        this.colore10 = new Vector3D(1, 0, 0);
    }

    /**
     * Interpola la il colore sulla base della vita
     * @param vita Vita del blocco
     * @return Restituisce la vita del blocco
     */
    private int interpolaColore(int vita){
        int color = Color.rgb(50, 50, 50);

        if(vita > 0){
            Vector3D direzione = Vector3D.differenzaVettoriale(this.colore10, this.colore1).prodottoPerScalare(1/10.0f).prodottoPerScalare(vita);
            Vector3D valore = Vector3D.sommaVettoriale(this.colore1, direzione);
            color = Color.rgb((int)(valore.getPosX() * 255), (int)(valore.getPosY() * 255), (int)(valore.getPosZ() * 255));
        }

        return color;
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
    }

    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        paint.setColor(ContextCompat.getColor(this.owner.getContext(), R.color.background));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        this.disegnaBlocco(screenWidth, screenHeight, canvas, paint);
        this.disegnaGriglia(screenWidth, screenHeight, canvas, paint);
    }

    /**
     * Disegna la gliglia della mappa
     * @param screenWidth
     * @param screenHeight
     * @param canvas
     * @param paint
     */
    private void disegnaGriglia(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        if(layer != null){
            int larghezzaUnitaria = screenWidth / this.layer.getColonne();
            int altezzaUnitaria = this.layer.getAltezza() / this.layer.getRighe();

            paint.setColor(Color.rgb(0, 0, 0));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            for(int i = 0; i < this.layer.getRighe(); i++){
                for(int j = 0; j < this.layer.getColonne(); j++){
                    canvas.drawRect(
                            j * larghezzaUnitaria,
                            i * altezzaUnitaria,
                            j * larghezzaUnitaria + larghezzaUnitaria,
                            i * altezzaUnitaria + altezzaUnitaria,
                            paint
                    );
                }
            }
        }
    }

    /**
     * Disegna i blocchi disegnati sul layer
     * @param screenWidth
     * @param screenHeight
     * @param canvas
     * @param paint
     */
    private void disegnaBlocco(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        if(this.layer != null){
            int larghezzaUnitaria = screenWidth / this.layer.getColonne();
            int altezzaUnitaria = this.layer.getAltezza() / this.layer.getRighe();
            ArrayList<int[]> brick = this.layer.parseListaBlocchi();
            paint.setStyle(Paint.Style.FILL);
            for(int[] b : brick){
                int color = this.interpolaColore(b[2]);
                paint.setColor(color);
                canvas.drawRect(
                        b[0] * larghezzaUnitaria,
                        b[1] * altezzaUnitaria,
                        (b[0] * larghezzaUnitaria) + larghezzaUnitaria,
                        (b[1] * altezzaUnitaria) + altezzaUnitaria,
                        paint
                );
            }
        }
    }

    //Beam
    public int getVitaBrick() {
        return vitaBrick;
    }

    public void setVitaBrick(int vitaBrick) {
        this.vitaBrick = vitaBrick;
    }

    //Eventi
    @Override
    public void setup(int screenWidth, int screenHeight) {}
    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {}
    @Override
    public void finalStep(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.layer != null && event.getAction() == MotionEvent.ACTION_UP){
            if(event.getY() >= 0 && event.getY() <= this.layer.getAltezza()){
                int posX = (int)( this.layer.getColonne() * (event.getX() / (float)v.getWidth()) );
                int posY = (int)( this.layer.getRighe() * (event.getY() / (float)this.layer.getAltezza()) );

                if( this.vitaBrick != 0 ){
                    this.layer.addBlocco(posX, posY, this.vitaBrick);
                }else{
                    this.layer.rimuoviBlocco(posX, posY);
                }
            }
        }

        return true;
    }
}