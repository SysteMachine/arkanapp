package com.example.android.arkanoid.VectorMat;

public class Vector2D {
    protected float posX;
    protected float posY;

    public Vector2D(float posX, float posY){
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Restituisce la lunghezza del vettore
     * @return Lunghezza del vettore
     */
    public float getMagnitude(){
        return (float)Math.sqrt( Math.pow(this.posX, 2) + Math.pow(this.posY, 2) );
    }

    /**
     * Restituisce il versore del vettore
     * @return Versore del vettore
     */
    public Vector2D normalize(){
        Vector2D esito = new Vector2D(0, 0);
        float mag = this.getMagnitude();
        if(mag != 0)
            esito = new Vector2D(this.posX / mag, this.posY / mag);

        return esito;
    }

    /**
     * Restituisce il prodotto per scalare del vettore
     * @param scalare Scalare da moltiplicare
     * @return Restituisce la moltiplicazione tra il vettore e lo scalare passato
     */
    public Vector2D prodottoPerScalare(float scalare){
        return new Vector2D(this.posX * scalare, this.posY * scalare);
    }

    /**
     * Ruota il vettore di gradi
     * @param angoloGradi Gradi di rotazione
     * @return Restituisce il vettore rutotato
     */
    public Vector2D ruotaVettore(float angoloGradi){
        float s = (float)Math.sin(Math.toRadians(angoloGradi));
        float c = (float)Math.cos(Math.toRadians(angoloGradi));

        float nPosX = this.posX * c - this.posY * s;
        float nPosY = this.posX * s + this.posY * c;

        return new Vector2D(nPosX, nPosY);
    }

    /**
     * Esegue la somma vettoriale tra v1 e v2
     * @param v1 Primo vettore da sommare
     * @param v2 Secondo vettore da sommare
     * @return Restituisce la somma vettoriale di v1 e v2
     */
    public static Vector2D sommaVettoriale(Vector2D v1, Vector2D v2){
        return new Vector2D(v1.posX + v2.posX, v1.posY + v2.posY);
    }

    /**
     * Esegue la differenza vettoriale tra v1 e v2
     * @param v1 Vettore a cui sotrarre v2
     * @param v2 Vettore da sottrarre
     * @return Restiuisce la differenza vettoriale tra v1 e v2
     */
    public static Vector2D differenzaVettoriale(Vector2D v1, Vector2D v2){
        Vector2D v2n = v2.prodottoPerScalare(-1);
        return Vector2D.sommaVettoriale(v1, v2n);
    }

    /**
     * Restituisce il prodotto scalare tra v1 e v2
     * @param v1 Primo vettore
     * @param v2 Secondo vettore
     * @return Restituisce il prodotto scalare tra v1 e v2
     */
    public static float prodottoScalare(Vector2D v1, Vector2D v2){
        return (v1.posX * v2.posX) + (v1.posY * v2.posY);
    }
}