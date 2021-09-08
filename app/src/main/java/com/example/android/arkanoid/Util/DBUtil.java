package com.example.android.arkanoid.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBUtil {
    //Michele se mai userai nuovamente la classe ricorda di aggiungere nel manifest android:usesCleartextTraffic="true" in application By Michele del passato
    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    //StrictMode.setThreadPolicy(policy);
    //Aggiungi anche questo, altrimenti non ti fa eseguire l'operazione sul thread della ui

    private static final String CONNECTOR = "http://www.bigcompany.altervista.org/DBAndroidConnectivity/connection.php";    //Connettore al database

    /**
     * Esegue una query sul database remoto e restituisce il risultato
     * @param query Query da eseguire
     * @return Restituisce una stringa con l'esito della query
     * @throws Exception Può restituire un eccezione
     */
    public static String executeQuery(String query) throws Exception{
        String esito = null;

        String paramsString = "query=" + query;

        URL urlObject = new URL(DBUtil.CONNECTOR);
        HttpURLConnection con = (HttpURLConnection)urlObject.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(1000);
        con.setReadTimeout(1500);

        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(paramsString.getBytes());
        os.flush();
        os.close();

        if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();

            esito = response.toString();
        }

        return esito;
    }

    /**
     * Sostituisce una parola joolly all'interno di una query
     * @param query Query da modificare
     * @param jolly Parola jolly da sostituire
     * @param value Valore con cui sostituire la parola jolly
     * @return Restituisce la query modificata
     */
    public static String repalceJolly(String query, String jolly, Object value){
        String newQuery = query;

        String newPart = "";
        if(value instanceof String)
            newPart = "\"" + value + "\"";
        else
            newPart = "" + value;

        newQuery = newQuery.replace(jolly, newPart);

        return newQuery;
    }
}
