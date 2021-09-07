package com.example.android.arkanoid.VectorMat;

public class Vector3D extends Vector2D{
    protected float posZ;

    public Vector3D(float posX, float posY, float posZ) {
        super(posX, posY);
        this.posZ = posZ;
    }

    @Override
    public float getMagnitude() {
        return (float)Math.sqrt( Math.pow(this.posX, 2) + Math.pow(this.posY, 2) + Math.pow(this.posZ, 2));
    }

    @Override
    public Vector3D normalize() {
        float magnitude = this.getMagnitude();
        return new Vector3D(this.posX / magnitude, this.posY / magnitude, this.posZ / magnitude);
    }

    @Override
    public Vector3D prodottoPerScalare(float scalare) {
        return new Vector3D(this.posX * scalare, this.posY * scalare, this.posZ * scalare);
    }

    /**
     * Esegue la somma vettoriale tra il vettore v1 e il vettore v2
     * @param v1 Primo vettore da sommare
     * @param v2 Secondo vettore da sommare
     * @return Restituisce la somma del vettore v1 e v2
     */
    public static Vector3D sommaVettoriale(Vector3D v1, Vector3D v2){
        return new Vector3D(v1.posX + v2.posX, v1.posY + v2.posY, v1.posZ + v2.posZ);
    }

    /**
     * Esegue la differenza vettoriale tra il vettore v1 e il vettore v2
     * @param v1 Vettore a cui sottrarre
     * @param v2 Vettore da sottrarre
     * @return Restituisce la differenza vettoriale tra v1 e v2
     */
    public static Vector3D differenzaVettoriale(Vector3D v1, Vector3D v2){
        return Vector3D.sommaVettoriale(v1, v2.prodottoPerScalare(-1));
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }
}
