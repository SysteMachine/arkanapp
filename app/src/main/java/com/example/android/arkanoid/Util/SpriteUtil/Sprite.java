package com.example.android.arkanoid.Util.SpriteUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.VectorMat.Vector2D;
import com.example.android.arkanoid.VectorMat.Vector3D;

public class Sprite {
    protected boolean aviable;              //Flag di validità dello sprite
    protected Bitmap immagine;              //Bitmap associato allo sprite

    protected float rotazione;              //Rotazione dello sprite

    public Sprite(int drawableId, GameLoop gameLoop){
        this.rotazione = 0;

        this.aviable = false;
        if(gameLoop != null){
            try{
                this.immagine = BitmapFactory.decodeResource(gameLoop.getResources(), drawableId);
                this.immagine = this.immagine.copy(Bitmap.Config.ARGB_8888, true);  //Rendiamo l'immagine modificabile
                this.aviable = true;
            }catch(Exception e){
                this.aviable = false;
            }
        }
    }

    /**
     * Converte il formato numerico di un colore in un vettore contente i valori ARGB
     * @param color Colore da cui prelevare i singoli componenti
     * @return Restituisce un array delle componenti del colore nelle posizioni ARGB
     */
    protected int[] getArgbFromInt(int color){
        int[] esito = new int[4];

        esito[3] = color & 255;
        color = color >> 8;
        esito[2] = color & 255;
        color = color >> 8;
        esito[1] = color & 255;
        color = color >> 8;
        esito[0] = color & 255;

        return esito;
    }

    /**
     * Restituisce la distanza tra il colore sorgente e il colore di destinazione
     * @param sourceColor Colore sorgente
     * @param destColor Colore di destinazione
     * @return Restituisce la distanza tra i colori
     */
    protected float getDistanceBetweenColors(int sourceColor, int destColor){
        int[] aSourceColor = this.getArgbFromInt(sourceColor);
        int[] aDestColor = this.getArgbFromInt(destColor);

        Vector3D vSourceColor = new Vector3D(aSourceColor[1], aSourceColor[2], aSourceColor[3]);
        Vector3D vDestColor = new Vector3D(aDestColor[1], aDestColor[2], aDestColor[3]);

        return Vector3D.differenzaVettoriale(vDestColor, vSourceColor).getMagnitude();
    }

    /**
     * Restituisce l'immagine ruotata dei gradi impostati
     * @param degree Gradi di rotazione dell'immagine
     * @return Restituisce l'immagine ruotata
     */
    private Bitmap ruotaImmagine(float degree){
        Bitmap esito = null;
        if(this.aviable){
            Matrix mat = new Matrix();
            mat.postRotate(degree, (float)(this.immagine.getWidth() * 0.5), (float)(this.immagine.getHeight() * 0.5));
            esito = Bitmap.createBitmap(this.immagine, 0, 0, this.immagine.getWidth(), this.immagine.getHeight(), mat, true);
        }
        return esito;
    }

    /**
     * Sostituisce il colore sourceColor dell'immagine con il destColor inserito accettando una tolleranza di somiglianza del colore
     * @param sourceColor Colore sorgente da modificare
     * @param destColor Colore di destinazione da inserire
     * @param tolleranza Distanza massima accettata del colore da sostituire da quello di riferimento
     */
    public void replaceColor(int sourceColor, int destColor, float tolleranza){
        if(this.aviable){
            this.aviable = false;   //Fermiamo il disegno dello sprite fino alla fine della sostituzione del colore

            //Solo se l'immagine è stata caricata correttamente
            for(int i = 0; i < this.immagine.getHeight(); i++){
                for(int j = 0; j < this.immagine.getWidth(); j++){
                    int pixel = this.immagine.getPixel(j, i);
                    if(this.getArgbFromInt(pixel)[0] != 0){
                        //Se l'immagine è visibile
                        float distance = getDistanceBetweenColors(pixel, sourceColor);
                        if(distance < tolleranza)
                            this.immagine.setPixel(j, i, destColor);
                    }
                }
            }

            this.aviable = true;
        }
    }

    /**
     * Specchia l'immagine verticalmente
     */
    public void flipSprite(){
        if(this.aviable){
            this.aviable = false;

            Bitmap image = this.immagine.copy(Bitmap.Config.ARGB_8888, true);
            for(int i = this.immagine.getWidth() - 1 ; i >= 0; i--){
                for(int j = 0; j < this.immagine.getHeight(); j++){
                    image.setPixel(
                            this.immagine.getWidth() - 1 - i, //Parte da 0
                            j,
                            this.immagine.getPixel(i, j));
                }
            }
            this.immagine = image;

            this.aviable = true;
        }
    }

    /**
     * Disegna lo sprite sulla canvas
     * @param posX Posizione di disegno X dello sprite (indica il punto centrale X)
     * @param posY Posizione di disegno Y dello sprite (indica il punto centrale Y)
     * @param width Larghezza dello sprite
     * @param height Altezza dello sprite
     * @param canvas Canvas di disegno
     * @param paint Oggetto Paint
     */
    public void drawSprite(int posX, int posY, int width, int height, Canvas canvas, Paint paint){
        if(this.aviable && canvas != null && paint != null){
            Bitmap immagine = this.immagine;                        //Copiamo la sorgente per poter eseguire modifiche
            Vector2D pesi = new Vector2D(1, 1);          //Pesi per il ridimensionamento dell'immagine

            if(this.rotazione % 360 != 0){
                immagine = this.ruotaImmagine(this.rotazione);
                pesi = new Vector2D(((float)immagine.getWidth()) / this.immagine.getWidth(), ((float)immagine.getHeight()) / this.immagine.getHeight());
            }

            canvas.drawBitmap(
                    immagine,
                    null,
                    new Rect(
                            (int)( posX - (width * pesi.getPosX() * 0.5) ),
                            (int)(posY - (height * pesi.getPosY() * 0.5) ),
                            (int)(posX + (width * pesi.getPosX() * 0.5) ),
                            (int)(posY + (height * pesi.getPosY() * 0.5) )
                    ),
                    paint
            );
        }
    }

    //Beam
    public float getRotazione() {
        return rotazione;
    }

    public void setRotazione(float rotazione) {
        this.rotazione = rotazione;
    }

    public boolean isAviable() {
        return aviable;
    }
}