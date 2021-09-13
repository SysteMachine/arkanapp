package com.example.android.arkanoid.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
    private final static String URL_MY_IP = "http://bigcompany.altervista.org/DBAndroidConnectivity/myip.php";

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

    /**
     * Preleva l'indirrizzo ip globale del client
     * @return Restituisce una stringa che contiente l'ip del dispositivo
     */
    public static String getMyIp(){
        String ip = "";

        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(Util.URL_MY_IP).openConnection();
            connection.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ip = reader.readLine();
            reader.close();
            connection.disconnect();
        }catch (Exception e){e.printStackTrace();}

        return ip;
    }
}
