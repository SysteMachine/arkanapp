package com.example.android.arkanoid.Util;

public class Util {
    public static boolean probabilita(int probabilita){
        boolean[] arrayProbabilita = new boolean[100];
        int elementiInseriti = 0;
        while(elementiInseriti < probabilita){
            for(int i = 0; i < arrayProbabilita.length && elementiInseriti < probabilita; i++){
                if(arrayProbabilita[i] != true && Math.random() > 0.5){
                    arrayProbabilita[i] = true;
                    elementiInseriti ++;
                }
            }
        }

        return arrayProbabilita[(int)Math.round( Math.random() * (arrayProbabilita.length - 1) )];
    }
}
