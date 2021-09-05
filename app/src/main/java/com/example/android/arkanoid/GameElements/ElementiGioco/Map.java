package com.example.android.arkanoid.GameElements.ElementiGioco;

import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Method;

public class Map {
    public static final int MAX_HEALTH_BRICK = 10;          //Vita massima che viene dato al singolo brick
    private final int nRighe;                               //Numero di colonne della mappa
    private final int nColonne;                             //Numero di righe della mappa

    private float vitaBlocchi;                              //Vita massima da associare ai blocchi

    private int posX;                                       //Posizione X della mappa
    private int posY;                                       //Posizione Y della mappa
    private int mapWidth;                                   //Larghezza della mappa
    private int mapHeight;                                  //Altezza della mappa

    private final Sprite[] coloriBrick;                     //Colore dei brick
    private final Sprite bickIndistruttibile;               //Sprite del brick indistruttibile
    private final MultiSprite spriteCrepe;                  //Sprite delle crepe

    private boolean aviable;                                //Flag di aviabilità della mappa

    private Brick[][] elementiMappa;                        //Brick presenti nella mappa

    private int rigaCorrente;                               //Riga corrente per restituire il prossimo elemento
    private int colonnaCorrente;                            //Colonna corrente per restituire il prossimo elemento

    public Map(int nRighe, int nColonne, Vector2D position, Vector2D size, int vitaBlocchi, Sprite[] coloriBrick, Sprite brickIndistruttibile, MultiSprite spriteCrepe){
        this.nRighe = nRighe;
        this.nColonne = nColonne;

        this.posX = (int)position.getPosX();
        this.posY = (int)position.getPosY();
        this.mapWidth = (int)size.getPosX();
        this.mapHeight = (int)size.getPosY();

        this.rigaCorrente = 0;
        this.colonnaCorrente = 0;

        this.coloriBrick = coloriBrick;
        this.spriteCrepe = spriteCrepe;
        this.bickIndistruttibile = brickIndistruttibile;

        this.setVitaBlocchi(vitaBlocchi);

        this.aviable = false;

        try{
            this.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneBase", int.class, int.class), this);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Metodo per il controllo della generazione
     * @param riga Riga del brick da posizionare
     * @param colonna Colonna del brik da posizionare
     * @return Restituisce true se il brick deve essere posizionato, altrimenti restituisce false
     */
    public boolean metodoGenerazioneBase(int riga, int colonna){
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
    public void generaMappa(Method metodoGenerazione, Object possessoreMetodo) throws Exception{
        this.aviable = false;
        this.elementiMappa = new Brick[this.nRighe][this.nColonne]; //Svuota l'array degli elementi

        //Calcolo della dimensione dei blocchi sulla base della dimensione della mappa e della dimensione logica
        int brickWidth = this.mapWidth / this.nColonne;
        int brickHeight = this.mapHeight / this.nRighe;

        //la posizione startX e startY fanno si che la posizione del blocco sia al centro per questo incrementiamo la prima volta della metà della dimensione sull'asse
        int startY = (int)(this.posY + (brickHeight * 0.5));
        for(int i = 0; i < this.nRighe; i++){
            int startX = (int)(this.posX + (brickWidth * 0.5));
            for(int j = 0; j < this.nColonne; j++){
                if((boolean)metodoGenerazione.invoke(possessoreMetodo, i, j)){
                    //Prima generazione casuale, il meccanismo può essere facilmente modificato
                    try{
                        Brick brick = new Brick(
                                new Vector2D(startX, startY),
                                new Vector2D(brickWidth, brickHeight),
                                this.coloriBrick[(i + j) % this.coloriBrick.length],
                                this.spriteCrepe,
                                (int)Math.floor(this.vitaBlocchi)
                        );
                        this.elementiMappa[i][j] = brick;   //Impostiamo il valore
                    }catch (Exception e){e.printStackTrace();}
                }
                startX += brickWidth;
            }
            startY += brickHeight;
        }

        this.aviable = true;
    }

    /**
     * Inserisce all'interno della mappa un numero di blocchi indistruttibili
     * @param nOstacoli Numero di ostacoli da inserire
     */
    public void inserisciOstacoli(int nOstacoli){
        int nGenerati = 0;

        //Calcolo delle dimensioni dei blocchi e delle posizioni di partenza
        int brickWidth = this.mapWidth / this.nColonne;
        int brickHeight = this.mapHeight / this.nRighe;
        float startX = this.posX + (brickWidth * 0.5f);
        float startY = this.posY + (brickHeight * 0.5f);

        while(nGenerati < nOstacoli && nGenerati < this.getnRighe() * this.getnColonne()){
            //LA generazione continua fino a quando non sono stati generati tutti gli ostacoli o fino a quando c'è spazio
            int i = (int)Math.floor(Math.random() * this.nRighe);
            int j = (int)Math.floor(Math.random() * this.nColonne);

            Brick elemento = this.elementiMappa[i][j];
            if(elemento != null){
                //Se l'elemento esiste e non è invincibile lo sostituisce
                if(elemento.getHealth() != Brick.INF_HEALTH){
                    this.elementiMappa[i][j] = new Brick(
                                                    elemento.getPosition(),
                                                    elemento.getSize(),
                                                    this.bickIndistruttibile,
                                                    this.spriteCrepe,
                                                    Brick.INF_HEALTH);
                    nGenerati++;
                }

            }else{
                //Crea il blocco nella posizione vuota
                float posX = startX + (brickWidth * j);
                float posY = startY + (brickHeight * i);

                this.elementiMappa[i][j] = new Brick(
                                                new Vector2D(posX, posY),
                                                new Vector2D(brickWidth, brickHeight),
                                                this.bickIndistruttibile,
                                                this.spriteCrepe,
                                                Brick.INF_HEALTH);
                nGenerati++;
            }
        }
    }

    /**
     * Incrementa i contatori per il prossimo elemento
     */
    private void incrementaContatori(){
        this.colonnaCorrente ++;
        if(this.colonnaCorrente == this.nColonne){
            this.colonnaCorrente = 0;
            this.rigaCorrente ++;
        }
    }

    /**
     * Azzera i contatori della mappa
     */
    public void azzeraContatori(){
        this.rigaCorrente = 0;
        this.colonnaCorrente = 0;
    }

    /**
     * Restituisce il prossimo brick nella mappa scansionando riga per riga
     * @return Restituisce il prossimo brick o null nel caso tutti gli elementi siano stati forniti
     */
    public Brick getNextBrick(){
        Brick next = null;
        while(next == null && this.colonnaCorrente < this.nColonne && this.rigaCorrente < this.nRighe){
            next = this.elementiMappa[this.rigaCorrente][this.colonnaCorrente];
            this.incrementaContatori();
        }
        return next;
    }

    /**
     * Restituisce la somma della vita rimanente di tutti i blocchi
     * @return Somma della vita restante di tutti i blocch
     */
    public int getTotalHealth(){
        int somma = 0;

        this.azzeraContatori();
        Brick brick = this.getNextBrick();
        while(brick != null){
            if(brick.getHealth() != Brick.INF_HEALTH){
                somma += brick.getHealth();
            }
            brick = this.getNextBrick();
        }

        return somma;
    }

    //Beam
    public float getVitaBlocchi() {
        return vitaBlocchi;
    }

    public void setVitaBlocchi(float vita) {
        if(vita > 0 && vita <= Map.MAX_HEALTH_BRICK)
            this.vitaBlocchi = vita;
        else
            this.vitaBlocchi = 1;
    }

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

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
}