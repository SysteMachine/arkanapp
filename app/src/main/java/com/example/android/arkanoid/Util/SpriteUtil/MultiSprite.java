package com.example.android.arkanoid.Util.SpriteUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.android.arkanoid.GameCore.GameLoop;

public class MultiSprite extends Sprite{
    protected Bitmap fullImage;         //Immagine originale
    protected Bitmap[] images;          //Frame dell'immagine originale
    protected int nImages;              //Numero di immagini presenti nello sprite

    protected int singleWidht;          //Larghezza della singola immagine
    protected int singleHeight;         //Altezza della singola immagine

    protected int currentFrame;         //Frame correntemente visualizzato

    public MultiSprite(int drawableId, GameLoop gameLoop, int nImages) {
        super(drawableId, gameLoop);

        this.fullImage = this.immagine.copy(Bitmap.Config.ARGB_8888, true);

        this.images = new Bitmap[this.fullImage.getWidth() / this.fullImage.getHeight()];
        this.nImages = nImages;

        this.singleHeight = this.fullImage.getHeight();
        this.singleWidht = this.fullImage.getWidth() / nImages;

        this.prepareImages();
        this.setCurrentFrame(0);
    }

    @Override
    public void replaceColor(int sourceColor, int destColor, float tolleranza) {
        int lastIndex = this.currentFrame;
        for(int i = 0; i < this.nImages; i++){
            this.setCurrentFrame(i);
            super.replaceColor(sourceColor, destColor, tolleranza);
        }
        this.setCurrentFrame(lastIndex);
    }

    @Override
    public void resizeImage(int newWidth, int newHeight) {
        for(int i = 0; i < this.images.length; i++){
            this.images[i] = Bitmap.createScaledBitmap(this.images[i], newWidth, newHeight, true);
        }
        this.singleWidht = newWidth;
        this.singleHeight = newHeight;
    }

    /**
     * Divide le immagini dello sprite
     */
    private void prepareImages(){
        for(int i = 0; i < this.images.length; i++){
            Bitmap bitMap = Bitmap.createBitmap(this.singleWidht, this.singleHeight, Bitmap.Config.ARGB_8888);
            bitMap = bitMap.copy(Bitmap.Config.ARGB_8888, true);

            for(int y = 0; y < bitMap.getHeight(); y++){
                for(int x = 0; x < bitMap.getWidth(); x++){
                    bitMap.setPixel(x, y, this.fullImage.getPixel(x + (this.singleWidht * i), y) );
                }
            }

            this.images[i] = bitMap;
        }
    }

    /**
     * Imposta il frame dello sprite
     * @param currentFrame Nuovo frame da visualizzare
     * @return Restituisce l'esito della modifica
     */
    public boolean setCurrentFrame(int currentFrame){
        boolean esito = false;

        if(currentFrame >= 0 && currentFrame < this.nImages){
            esito = true;
            this.currentFrame = currentFrame;
            this.immagine = this.images[this.currentFrame];
        }

        return  esito;
    }

    //Beam
    public int getnImages() {
        return nImages;
    }

    public int getSingleWidht() {
        return singleWidht;
    }

    public int getSingleHeight() {
        return singleHeight;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}