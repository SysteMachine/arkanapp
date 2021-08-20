package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Method;

public class Map {
    public static final int MAX_HEALTH_BRICK = 10;
    private final int nRighe;
    private final int nColonne;

    private final int posX;
    private final int posY;
    private final int mapWidth;
    private final int mapHeight;

    private boolean aviable;

    private Brick[][] elementiMappa;

    public Map(int nRighe, int nColonne, int posX, int posY, int mapWidth, int mapHeight){
        this.nRighe = nRighe;
        this.nColonne = nColonne;

        this.posX = posX;
        this.posY = posY;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        try{
            this.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazione", int.class, int.class), this);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Metodo per il controllo della generazione
     * @param riga Riga del brick da posizionare
     * @param colonna Colonna del brik da posizionare
     * @return Restituisce true se il brick deve essere posizionato, altrimenti restituisce false
     */
    private boolean metodoGenerazione(int riga, int colonna){
        boolean esito = false;

        if(Math.random() > 0.5)
            esito = true;

        return esito;
    }

    /**
     * Metodo di generazione della mappa di Brikck
     * @param metodoGenerazione Metodo di generazione della mappa
     * @param possessoreMetodo Oggetto che possiede il metodo di generazione della mappa
     * @throws Exception Eccezzione lanciata nel caso di problemi con la generazione
     */
    private void generaMappa(Method metodoGenerazione, Object possessoreMetodo) throws Exception{
        this.aviable = false;
        this.elementiMappa = new Brick[this.nRighe][this.nColonne]; //Svuota l'array degli elementi

        //Calcolo della dimensione dei blocchi sulla base della dimensione della mappa e della dimensione logica
        int brickWidth = this.mapWidth / this.nColonne;
        int brickHeight = this.mapHeight / this.nRighe;

        //la posizione startX e startY fanno si che la posizione del blocco sia al centro per questo incrementiamo la prima volta della metà della dimensione sull'asse
        int startY = (int)(this.posY + (brickHeight * 0.5));
        for(int i = 0; i < this.elementiMappa.length; i++){
            int startX = (int)(this.posX + (brickWidth * 0.5));
            for(int j = 0; j < this.elementiMappa[0].length; j++){
                if((boolean)metodoGenerazione.invoke(possessoreMetodo, i, j)){
                    //Prima generazione casuale, il meccanismo può essere facilmente modificato
                    Brick brick = new Brick(
                            1 + (int)(Math.random() * (Map.MAX_HEALTH_BRICK - 1) ),
                            new Vector2D(
                                    startX,
                                    startY
                            ),
                            new Vector2D(brickWidth, brickHeight)
                    );
                    this.elementiMappa[i][j] = brick;   //Impostiamo il valore
                }
                startX += brickWidth;
            }
            startY += brickHeight;
        }

        this.aviable = true;
    }

    //Beam
    public boolean isAviable() {
        return aviable;
    }

    public int getnRighe() {
        return nRighe;
    }

    public int getnColonne() {
        return nColonne;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public Brick[][] getElementiMappa() {
        return elementiMappa;
    }
}