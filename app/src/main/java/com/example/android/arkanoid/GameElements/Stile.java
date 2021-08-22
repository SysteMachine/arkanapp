package com.example.android.arkanoid.GameElements;

import android.graphics.Color;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ReplaceColorRecord;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Stile {
    protected int immaginePalla;
    protected int angoloDiLancioMassimoPalla;
    protected int velocitaInizialePalla;
    protected int velocitaRotazionePalla;
    protected float percentualeRaggioPalla;
    protected Vector2D percentualePosizionePalla;
    protected ReplaceColorRecord[] coloriPalla;

    protected int immaginePaddle;
    protected int velocitaInizialePaddle;
    protected Vector2D percentualeDimensionePaddle;
    protected Vector2D percentualePosizionePaddle;
    protected ReplaceColorRecord[] coloriPaddle;

    protected int immagineBrick;
    protected int numeroColonneMappa;
    protected int numeroRigheMappa;
    protected Vector2D percentualePosizioneMappa;
    protected Vector2D percentualeDimensioneMappa;
    protected ReplaceColorRecord[] coloriBrick;
    protected int[] coloriCasualiBrick;

    protected int immagineSfondo;
    protected ReplaceColorRecord[] coloriSfondo;

    protected int immagineBottom;
    protected ReplaceColorRecord[] coloriBottom;

    protected int immagineZonaPunteggio;
    protected ReplaceColorRecord[] coloriZonaPunteggio;

    protected int suonoBackground;
    protected int suonoCollisionePalla;
    protected int suonoRotturaBlocco;

    public Stile(){
        this.setStilePalla();
        this.setStilePaddle();
        this.setStileBrick();
        this.setStileSfondoPunteggioBottom();
    }

    /**
     * Imposta lo stile della palla
     */
    protected void setStilePalla(){
        this.immaginePalla = R.drawable.stilebase_palla;
        this.angoloDiLancioMassimoPalla = 40;
        this.velocitaInizialePalla = 800;
        this.velocitaRotazionePalla = 360;
        this.percentualeRaggioPalla = 0.05f;
        this.percentualePosizionePalla = new Vector2D(0.5f, 0.8f);
        this.coloriPalla = new ReplaceColorRecord[2];
        this.coloriPalla[0] = new ReplaceColorRecord(Color.WHITE, Color.GREEN, 200);
        this.coloriPalla[1] = new ReplaceColorRecord(Color.RED, Color.WHITE, 200);
    }

    /**
     * Restituisce l'immagine della palla con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine'
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmaginePallaStile(GameLoop gameLoop){
        Sprite sprite = new Sprite(this.immaginePalla, gameLoop);
        for(ReplaceColorRecord rc : this.coloriPalla)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return sprite;
    }

    /**
     * Imposta lo stile del paddle
     */
    protected void setStilePaddle(){
        this.immaginePaddle = R.drawable.stilebase_paddle;
        this.velocitaInizialePaddle = 1000;
        this.percentualeDimensionePaddle = new Vector2D(0.3f, 0.03f);
        this.percentualePosizionePaddle = new Vector2D(0.5f, 0.85f);
        this.coloriPaddle = new ReplaceColorRecord[1];
        this.coloriPaddle[0] = new ReplaceColorRecord(Color.WHITE, Color.rgb(200, 100, 25), 200);
    }

    /**
     * Restituisce l'immagine del paddle con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmaginePaddleStile(GameLoop gameLoop){
        Sprite sprite = new Sprite(this.immaginePaddle, gameLoop);
        for(ReplaceColorRecord rc : this.coloriPaddle)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return sprite;
    }

    /**
     * Imposta lo stile del brick
     */
    protected void setStileBrick(){
        this.immagineBrick = R.drawable.stilebase_brick;
        this.numeroColonneMappa = 8;
        this.numeroRigheMappa = 6;
        this.percentualePosizioneMappa = new Vector2D(0, 0.15f);
        this.percentualeDimensioneMappa = new Vector2D(1, 0.25f);
        this.coloriBrick = new ReplaceColorRecord[0];
        this.coloriCasualiBrick = new int[4];
        this.coloriCasualiBrick[0] = Color.GREEN;
        this.coloriCasualiBrick[1] = Color.YELLOW;
        this.coloriCasualiBrick[2] = Color.rgb(255, 165, 0);
        this.coloriCasualiBrick[3] = Color.RED;
    }

    /**
     * Restituisce l'immagine del brick con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @param idColore Id del colore casuale richiesto
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmagineBrickStile(GameLoop gameLoop, int idColore){
        Sprite sprite = new Sprite(this.immagineBrick, gameLoop);
        int colore = 0;
        if(idColore >= 0 && idColore < this.coloriCasualiBrick.length)
            colore = idColore;
        sprite.replaceColor(Color.WHITE, this.coloriCasualiBrick[colore], 200);
        for(ReplaceColorRecord rc : this.coloriBrick)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return  sprite;
    }

    /**
     * Imposta lo stile dello sfondo, del punteggio e del bottom
     */
    protected void setStileSfondoPunteggioBottom(){
        this.immagineSfondo = R.drawable.stilebase_sfondo;
        this.coloriSfondo = new ReplaceColorRecord[2];
        this.coloriSfondo[0] = new ReplaceColorRecord(Color.BLACK, Color.rgb(65, 65, 128), 200);
        this.coloriSfondo[1] = new ReplaceColorRecord(Color.WHITE, Color.YELLOW, 200);

        this.immagineBottom = R.drawable.stilebase_bottom;
        this.coloriBottom = new ReplaceColorRecord[1];
        this.coloriBottom[0] = new ReplaceColorRecord(Color.WHITE, Color.RED, 200);

        this.immagineZonaPunteggio = R.drawable.stilebase_zonapunteggio;
        this.coloriZonaPunteggio =  new ReplaceColorRecord[1];
        this.coloriZonaPunteggio[0] = new ReplaceColorRecord(Color.WHITE, Color.BLUE, 200);
    }

    /**
     * Restituisce l'immagine dello sfondo con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmagineSfondoStile(GameLoop gameLoop){
        Sprite sprite = new Sprite(this.immagineSfondo, gameLoop);
        for(ReplaceColorRecord rc : this.coloriSfondo)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return  sprite;
    }

    /**
     * Restituisce l'immagine del bottom con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmagineBottomStile(GameLoop gameLoop){
        Sprite sprite = new Sprite(this.immagineBottom, gameLoop);
        for(ReplaceColorRecord rc : this.coloriBottom)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return  sprite;
    }

    /**
     * Restituisce l'immagine della zona del punteggio con lo stile impostato
     * @param gameLoop GameLoop per il caricamento dell' immagine
     * @return Restituisce lo sprite caricato
     */
    public Sprite getImmagineZonaPunteggioStile(GameLoop gameLoop){
        Sprite sprite = new Sprite(this.immagineZonaPunteggio, gameLoop);
        for(ReplaceColorRecord rc : this.coloriZonaPunteggio)
            sprite.replaceColor(rc.getFromColor(), rc.getTargetColor(), rc.getTollerance());
        return  sprite;
    }

    //Beam

    public int getImmaginePalla() {
        return immaginePalla;
    }

    public void setImmaginePalla(int immaginePalla) {
        this.immaginePalla = immaginePalla;
    }

    public int getAngoloDiLancioMassimoPalla() {
        return angoloDiLancioMassimoPalla;
    }

    public void setAngoloDiLancioMassimoPalla(int angoloDiLancioMassimoPalla) {
        this.angoloDiLancioMassimoPalla = angoloDiLancioMassimoPalla;
    }

    public int getVelocitaInizialePalla() {
        return velocitaInizialePalla;
    }

    public void setVelocitaInizialePalla(int velocitaInizialePalla) {
        this.velocitaInizialePalla = velocitaInizialePalla;
    }

    public int getVelocitaRotazionePalla() {
        return velocitaRotazionePalla;
    }

    public void setVelocitaRotazionePalla(int velocitaRotazionePalla) {
        this.velocitaRotazionePalla = velocitaRotazionePalla;
    }

    public float getPercentualeRaggioPalla() {
        return percentualeRaggioPalla;
    }

    public void setPercentualeRaggioPalla(float percentualeRaggioPalla) {
        this.percentualeRaggioPalla = percentualeRaggioPalla;
    }

    public Vector2D getPercentualePosizionePalla() {
        return percentualePosizionePalla;
    }

    public void setPercentualePosizionePalla(Vector2D percentualePosizionePalla) {
        this.percentualePosizionePalla = percentualePosizionePalla;
    }

    public ReplaceColorRecord[] getColoriPalla() {
        return coloriPalla;
    }

    public void setColoriPalla(ReplaceColorRecord[] coloriPalla) {
        this.coloriPalla = coloriPalla;
    }

    public int getImmaginePaddle() {
        return immaginePaddle;
    }

    public void setImmaginePaddle(int immaginePaddle) {
        this.immaginePaddle = immaginePaddle;
    }

    public int getVelocitaInizialePaddle() {
        return velocitaInizialePaddle;
    }

    public void setVelocitaInizialePaddle(int velocitaInizialePaddle) {
        this.velocitaInizialePaddle = velocitaInizialePaddle;
    }

    public Vector2D getPercentualeDimensionePaddle() {
        return percentualeDimensionePaddle;
    }

    public void setPercentualeDimensionePaddle(Vector2D percentualeDimensionePaddle) {
        this.percentualeDimensionePaddle = percentualeDimensionePaddle;
    }

    public Vector2D getPercentualePosizionePaddle() {
        return percentualePosizionePaddle;
    }

    public void setPercentualePosizionePaddle(Vector2D percentualePosizionePaddle) {
        this.percentualePosizionePaddle = percentualePosizionePaddle;
    }

    public ReplaceColorRecord[] getColoriPaddle() {
        return coloriPaddle;
    }

    public void setColoriPaddle(ReplaceColorRecord[] coloriPaddle) {
        this.coloriPaddle = coloriPaddle;
    }

    public int getImmagineBrick() {
        return immagineBrick;
    }

    public void setImmagineBrick(int immagineBrick) {
        this.immagineBrick = immagineBrick;
    }

    public int getNumeroColonneMappa() {
        return numeroColonneMappa;
    }

    public void setNumeroColonneMappa(int numeroColonneMappa) {
        this.numeroColonneMappa = numeroColonneMappa;
    }

    public int getNumeroRigheMappa() {
        return numeroRigheMappa;
    }

    public void setNumeroRigheMappa(int numeroRigheMappa) {
        this.numeroRigheMappa = numeroRigheMappa;
    }

    public Vector2D getPercentualePosizioneMappa() {
        return percentualePosizioneMappa;
    }

    public void setPercentualePosizioneMappa(Vector2D percentualePosizioneMappa) {
        this.percentualePosizioneMappa = percentualePosizioneMappa;
    }

    public Vector2D getPercentualeDimensioneMappa() {
        return percentualeDimensioneMappa;
    }

    public void setPercentualeDimensioneMappa(Vector2D percentualeDimensioneMappa) {
        this.percentualeDimensioneMappa = percentualeDimensioneMappa;
    }

    public ReplaceColorRecord[] getColoriBrick() {
        return coloriBrick;
    }

    public void setColoriBrick(ReplaceColorRecord[] coloriBrick) {
        this.coloriBrick = coloriBrick;
    }

    public int[] getColoriCasualiBrick() {
        return coloriCasualiBrick;
    }

    public void setColoriCasualiBrick(int[] coloriCasualiBrick) {
        this.coloriCasualiBrick = coloriCasualiBrick;
    }

    public int getImmagineSfondo() {
        return immagineSfondo;
    }

    public void setImmagineSfondo(int immagineSfondo) {
        this.immagineSfondo = immagineSfondo;
    }

    public ReplaceColorRecord[] getColoriSfondo() {
        return coloriSfondo;
    }

    public void setColoriSfondo(ReplaceColorRecord[] coloriSfondo) {
        this.coloriSfondo = coloriSfondo;
    }

    public int getImmagineBottom() {
        return immagineBottom;
    }

    public void setImmagineBottom(int immagineBottom) {
        this.immagineBottom = immagineBottom;
    }

    public ReplaceColorRecord[] getColoriBottom() {
        return coloriBottom;
    }

    public void setColoriBottom(ReplaceColorRecord[] coloriBottom) {
        this.coloriBottom = coloriBottom;
    }

    public int getImmagineZonaPunteggio() {
        return immagineZonaPunteggio;
    }

    public void setImmagineZonaPunteggio(int immagineZonaPunteggio) {
        this.immagineZonaPunteggio = immagineZonaPunteggio;
    }

    public ReplaceColorRecord[] getColoriZonaPunteggio() {
        return coloriZonaPunteggio;
    }

    public void setColoriZonaPunteggio(ReplaceColorRecord[] coloriZonaPunteggio) {
        this.coloriZonaPunteggio = coloriZonaPunteggio;
    }
}