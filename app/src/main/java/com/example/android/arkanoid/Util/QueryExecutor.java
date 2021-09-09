package com.example.android.arkanoid.Util;
import android.widget.ArrayAdapter;

import com.example.android.arkanoid.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QueryExecutor {
    private static String QUERY_PUBBLICAZIONE_PUNTEGGIO = "INSERT INTO punteggio (punteggio_punteggio, punteggio_modalita, punteggio_user_email) VALUES (PUNTEGGIO, MODALITA, EMAIL)";
    private static String QUERY_CONTROLLO_EMAIL_REGISTRATA = "SELECT COUNT(*) AS N FROM user WHERE user_email LIKE EMAIL";
    private static String QUERY_REGISTRAZIONE_UTENTE = "INSERT INTO user VALUES (EMAIL, USERNAME, Password(PASSWORD))";
    private static String QUERY_LOGIN_UTENTE = "SELECT user_username AS USERNAME FROM user WHERE user_email LIKE EMAIL AND user_password LIKE Password(PASSWORD)";
    private static String QUERY_CONTROLLO_NOME_LIVELLO = "SELECT COUNT(*) AS N FROM creazioni WHERE creazioni_nome LIKE NOME";
    private static String QUERY_CONTROLLO_NOME_LIVELLO_LOCALE = "SELECT COUNT(*) AS N FROM creazioni WHERE creazioni.creazioni_nome LIKE NOME AND creazioni_user_email <> EMAIL";
    private static String QUERY_CONTROLLO_PROPRIETARIO_LIVELLO = "SELECT creazioni_user_email AS EMAIL FROM creazioni WHERE creazioni_nome LIKE NOME";
    private static String QUERY_INSERIMENTO_LIVELLO = "INSERT INTO creazioni VALUES(NOME, DATI, EMAIL)";
    private static String QUERY_AGGIORNAMENTO_LIVELLO = "UPDATE creazioni SET creazioni_dati = DATI WHERE creazioni_nome LIKE NOME";
    private static String QUERY_CARICAMENTO_DATI_LIVELLO = "SELECT creazioni_dati AS DATI FROM creazioni WHERE creazioni_nome LIKE NOME";
    private static String QUERY_RECUPERO_LIVELLI_CREATI = "SELECT creazioni_nome AS NOME FROM creazioni WHERE creazioni_user_email LIKE EMAIL";


    public static boolean pubblicaPunteggio(int punteggio, int codiceModalita, String email) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_PUBBLICAZIONE_PUNTEGGIO, "PUNTEGGIO", punteggio);
        query = DBUtil.repalceJolly(query, "MODALITA", codiceModalita);
        query = DBUtil.repalceJolly(query, "EMAIL", email);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR"))
            esito = true;

        return esito;
    }

    public static boolean controlloEsistenzaEmailRegistrata(String email) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_CONTROLLO_EMAIL_REGISTRATA, "EMAIL", email);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR") && new JSONObject(esitoQuery).getInt("N") == 0)
            esito = true;

        return esito;
    }

    public static boolean registrazioneUtente(String email, String username, String password) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_REGISTRAZIONE_UTENTE, "EMAIL", email);
        query = DBUtil.repalceJolly(query, "USERNAME", username);
        query = DBUtil.repalceJolly(query, "PASSWORD", password);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR"))
            esito = true;

        return esito;
    }

    /**
     * Restituisce l'username del giocatore se il login avviene con successo, altrimenti restituisce null
     */
    public static String loginUtente(String email, String password) throws Exception{
        String esito = null;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_LOGIN_UTENTE, "EMAIL", email);
        query = DBUtil.repalceJolly(query, "PASSWORD", password);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR")){
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(esitoQuery.getBytes())));
            int entryCount = 0;
            String targetRow = null;
            String row;
            while((row = reader.readLine()) != null){
                if(entryCount++ == 0)
                    targetRow = row;
            }

            if(entryCount == 1 && !targetRow.equals(""))
                esito = new JSONObject(targetRow).getString("USERNAME");

        }

        return esito;
    }

    public static boolean controlloEsistenzaNomeLivello(String nome) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_CONTROLLO_NOME_LIVELLO, "NOME", nome);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR") && new JSONObject(esitoQuery).getInt("N") == 0)
            esito = true;

        return esito;
    }

    public static boolean controlloEsistenzaNomeLivelloLocale(String nome, String email) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_CONTROLLO_NOME_LIVELLO_LOCALE, "NOME", nome);
        query = DBUtil.repalceJolly(query, "EMAIL", email);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR") && new JSONObject(esitoQuery).getInt("N") == 0)
            esito = true;

        return esito;
    }

    /**
     * Esegue il controllo sul proprietario del livello e restituisce l'email del proprietario o null
     */
    public static String controlloProprietarioLivello(String nome) throws Exception{
        String esito = null;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_CONTROLLO_PROPRIETARIO_LIVELLO, "NOME", nome);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR") && !esitoQuery.equals(""))
            esito = new JSONObject(esitoQuery).getString("EMAIL");

        return esito;
    }

    public static boolean inserisciLivello(String nome, String dati, String email) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_INSERIMENTO_LIVELLO, "NOME", nome);
        query = DBUtil.repalceJolly(query, "EMAIL", email);
        query = DBUtil.repalceJolly(query, "DATI", dati);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR"))
            esito = true;

        return esito;
    }

    public static boolean aggiornaLivello(String nome, String dati) throws Exception{
        boolean esito = false;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_AGGIORNAMENTO_LIVELLO, "NOME", nome);
        query = DBUtil.repalceJolly(query, "DATI", dati);

        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR"))
            esito = true;

        return esito;
    }

    /**
     * Carica i dati del livello e restituisce la stringa del livello
     */
    public static String caricaLivello(String nome) throws Exception{
        String esito = null;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_CARICAMENTO_DATI_LIVELLO, "NOME", nome);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR"))
            esito = new JSONObject(esitoQuery).getString("DATI");

        return esito;
    }

    /**
     * Restotiosce un'array di stringhe con il livelli creati
     */
    public static String[] recuperoLivelliCreati(String email) throws Exception{
        ArrayList<String> esito = new ArrayList<>();

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_RECUPERO_LIVELLI_CREATI, "EMAIL", email);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR")){
            BufferedReader reader = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(esitoQuery.getBytes()) ) );
            String riga;
            while((riga = reader.readLine()) != null)
                esito.add(new JSONObject(riga).getString("NOME"));
        }

        return esito.toArray(new String[0]);
    }
}
