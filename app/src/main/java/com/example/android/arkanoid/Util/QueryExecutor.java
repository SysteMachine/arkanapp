package com.example.android.arkanoid.Util;
import android.widget.ArrayAdapter;

import com.example.android.arkanoid.AgentSystem.GA;
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
    private static String QUERY_RECUPERO_LIVELLI_CREATI = "SELECT creazioni_nome AS NOME FROM creazioni WHERE creazioni_user_email LIKE EMAIL ORDER BY creazioni_nome";
    private static String QUERY_RECUPERO_LIVELLI = "SELECT creazioni_nome AS NOME FROM creazioni ORDER BY creazioni_nome";
    private static String QUERY_RECUPERO_LIVELLI_CON_FILTRO = "SELECT creazioni_nome AS NOME FROM creazioni WHERE creazioni_nome LIKE FILTRO ORDER BY creazioni_nome";
    private static String QUERY_RECUPERO_CLASSIFICA = "SELECT punteggio_punteggio AS PUNTEGGIO, user_username AS USERNAME, punteggio_modalita AS MODALITA from punteggio INNER JOIN user ON punteggio_user_email = user_email ORDER BY punteggio_punteggio DESC LIMIT 20";
    private static String QUERY_RECUPERO_PUNTEGGIO_MASSIMO_CLASSIFICA_GIOCATORE = "SELECT max(punteggio_punteggio) AS MASSIMO FROM punteggio WHERE punteggio_user_email LIKE EMAIL";
    private static String QUERY_RECUPERO_MEDIA_MASSIMO_PUNTEGGIO = "SELECT MAX(punteggio_punteggio) AS MASSIMO, AVG(punteggio_punteggio) AS MEDIA, punteggio_user_email AS USER FROM punteggio WHERE punteggio_user_email LIKE EMAIL";

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

    /**
     * Restotiosce un'array di stringhe con il livelli creati
     */
    public static String[] recuperoLivelli() throws Exception{
        ArrayList<String> esito = new ArrayList<>();

        String esitoQuery = DBUtil.executeQuery(QueryExecutor.QUERY_RECUPERO_LIVELLI);
        if(!esitoQuery.equals("ERROR")){
            BufferedReader reader = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(esitoQuery.getBytes()) ) );
            String riga;
            while((riga = reader.readLine()) != null)
                esito.add(new JSONObject(riga).getString("NOME"));
        }

        return esito.toArray(new String[0]);
    }

    /**
     * Restotiosce un'array di stringhe con il livelli creati
     */
    public static String[] recuperoLivelliConFiltro(String filtro) throws Exception{
        ArrayList<String> esito = new ArrayList<>();

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_RECUPERO_LIVELLI_CON_FILTRO, "FILTRO", "%" + filtro + "%");
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR")){
            BufferedReader reader = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(esitoQuery.getBytes()) ) );
            String riga;
            while((riga = reader.readLine()) != null)
                esito.add(new JSONObject(riga).getString("NOME"));
        }

        return esito.toArray(new String[0]);
    }

    /**
     * Recupera la classifica e restituisce l'arraydegli elementi
     */
    public static String[] recuperaClassifica() throws Exception{
        ArrayList<String> esito =  new ArrayList<>();

        String esitoQuery = DBUtil.executeQuery(QueryExecutor.QUERY_RECUPERO_CLASSIFICA);
        if(!esitoQuery.equals("ERROR")){
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(esitoQuery.getBytes()) ) );
            String riga;
            int contatore = 1;
            while((riga = reader.readLine()) != null){
                JSONObject jsonObject = new JSONObject(riga);
                int punteggio = jsonObject.getInt("PUNTEGGIO");
                String username = jsonObject.getString("USERNAME");
                int modalita = jsonObject.getInt("MODALITA");

                esito.add(contatore + ") " + username + " MOD(" + modalita + "):" + punteggio + "p");
                contatore++;
            }
        }

        return esito.toArray(new String[0]);
    }

    /**
     * Restituisce il punteggio massimo del giocatore
     */
    public static String recuperoPunteggioMassimoGiocatore(String email) throws Exception{
        String esito = null;

        String query = DBUtil.repalceJolly(QueryExecutor.QUERY_RECUPERO_PUNTEGGIO_MASSIMO_CLASSIFICA_GIOCATORE, "EMAIL", email);
        String esitoQuery = DBUtil.executeQuery(query);
        if(!esitoQuery.equals("ERROR") && !esitoQuery.equals("")) {
            JSONObject jsonObject = new JSONObject(esitoQuery);
            int massimo = jsonObject.getInt("MASSIMO");
            esito = massimo + "p";
        }

        return esito;
    }

    /**
     * Restituisce i parametri dell'account
     * @param email Email dell'account
     * @return Parametri dell'account
     */
    public static float[] getParametriAccount(String email){
        float[] parametri = new float[2];

        if(GA.salvataggio.isLogin()){
            String query = DBUtil.repalceJolly(QueryExecutor.QUERY_RECUPERO_MEDIA_MASSIMO_PUNTEGGIO, "EMAIL", email);
            try{
                String esitoQuery = DBUtil.executeQuery(query);
                if(!esitoQuery.equals("ERROR") && !esitoQuery.contains("null")){
                    JSONObject jsonObject = new JSONObject(esitoQuery);
                    parametri[0] = (float)jsonObject.getDouble("MASSIMO");
                    parametri[1] = (float)jsonObject.getDouble("MEDIA");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return parametri;
    }
}