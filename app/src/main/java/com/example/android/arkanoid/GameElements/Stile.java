package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.VectorMat.Vector2D;

public class Stile {
    private int immaginePalla;
    private int angoloDiLancioMassimoPalla;
    private int velocitaInizialePalla;
    private int velocitaRotazionePalla;
    private float percentualeRaggioPalla;
    private Vector2D percentualePosizionePalla;

    private int immaginePaddle;
    private int velocitaInizialePaddle;
    private Vector2D percentualeDimensionePaddle;
    private Vector2D percentualePosizionePaddle;

    private int immagineBrick;
    private int numeroColonneMappa;
    private int numeroRigheMappa;
    private Vector2D percentualePosizioneMappa;
    private Vector2D percentualeDimensioneMappa;

    private int immagineSfondo;
    private int immagineBottom;
    private int immagineZonaPunteggio;

    private int suonoBackground;
    private int suonoCollisionePalla;
    private int suonoRotturaBlocco;

    public Stile(){

    }
}
