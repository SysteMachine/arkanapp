package com.example.android.arkanoid.AgentSystem;

import android.os.StrictMode;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DF extends Agente{
    private ArrayList<RecordClient> client;         //Client connessi

    public DF() {
        super("DF");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.client = new ArrayList<>();

        this.addCompito(new CompitoAggiornamentoStatoConnessione());
    }

    /**
     * Resetta la lista dei client connessi
     */
    protected void resetClient(){
        if(this.client != null)
            this.client.clear();
    }

    /**
     * Aggiunge un client alla lista
     * @param c Client da aggiungere alla lista
     */
    protected void addClient(RecordClient c){
        if(this.client != null){
            this.client.add(c);
        }
    }

    /**
     * Restituisce la lista dei client connessi
     * @return Lista dei client connessi
     */
    public RecordClient[] getListaClient(){
        RecordClient[] esito = null;

        if(this.client != null)
            esito = this.client.toArray(new RecordClient[0]);

        return esito;
    }

    /**
     * Restituisce il client partendo dall'email
     * @param emailClient Email del client da trovare
     * @return Restituisce il record del client
     */
    public RecordClient getClient(String emailClient){
        RecordClient esito = null;

        for(RecordClient rc : this.client){
            if(rc.getEmail().equals(emailClient)){
                esito = rc;
                break;
            }
        }

        return esito;
    }

    private class CompitoAggiornamentoStatoConnessione extends Compito{
        private final int TTL = 60;         //Tempo di vita massimo di connessione

        private final String URL_MY_IP = "http://bigcompany.altervista.org/DBAndroidConnectivity/myip.php";
        private final String QUERY_ELIMINAZIONE_RIFERIMENTI = "DELETE FROM df_table WHERE df_table_user_email LIKE EMAIL";
        private final String QUERY_INSERIMENTO_GIOCATORE = "INSERT INTO df_table (df_table_ip, df_table_port, df_table_user_email) VALUES (IP, PORT, EMAIL)";
        private final String QUERY_AGGIORNAMENTO_QUERY = "UPDATE df_table SET df_table_time_stamp = CURRENT_TIMESTAMP, df_table_ip = IP, df_table.df_table_port = PORTA WHERE df_table_user_email LIKE EMAIL";
        private final String QUERY_RECUPERO_UTENTI_ATTIVI = "SELECT df_table_ip AS IP, df_table_port AS PORT, df_table_user_email AS EMAIL FROM df_table WHERE  TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, df_table_time_stamp)) < SECONDI";

        public CompitoAggiornamentoStatoConnessione() {
            super("Compito di aggiornamento dello stato della connessione");
            this.rimuoviRiferimenti();
            this.aggiungiPrimoRiferimento();
        }

        /**
         * Preleva l'indirrizzo ip globale del client
         * @return Restituisce una stringa che contiente l'ip del dispositivo
         */
        private String getMyIp(){
            String ip = "";

            try{
                HttpURLConnection connection = (HttpURLConnection) new URL(this.URL_MY_IP).openConnection();
                connection.setDoOutput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ip = reader.readLine();
                reader.close();
                connection.disconnect();
            }catch (Exception e){e.printStackTrace();}

            return ip;
        }

        /**
         * Rimuove i riferimenti nel df_table
         */
        private void rimuoviRiferimenti(){
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(GA.contesto);
            if(recordSalvataggio.isLogin()){
                String query = DBUtil.repalceJolly(this.QUERY_ELIMINAZIONE_RIFERIMENTI, "EMAIL", recordSalvataggio.getEmail());
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Aggiunge il nuovo riferimento nella df_table
         */
        private void aggiungiPrimoRiferimento(){
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(GA.contesto);
            if(recordSalvataggio.isLogin()){
                String query = DBUtil.repalceJolly(this.QUERY_INSERIMENTO_GIOCATORE, "IP", this.getMyIp());
                query = DBUtil.repalceJolly(query, "PORT", GA.channel.getPorta());
                query = DBUtil.repalceJolly(query, "EMAIL", recordSalvataggio.getEmail());
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Aggiorna il riferimento all'interno della df_table
         */
        private void aggiornaRiferimento(){
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(GA.contesto);
            if(recordSalvataggio.isLogin()){
                String query = DBUtil.repalceJolly(this.QUERY_AGGIORNAMENTO_QUERY, "IP", this.getMyIp());
                query = DBUtil.repalceJolly(query, "PORTA", GA.channel.getPorta());
                query = DBUtil.repalceJolly(query, "EMAIL", recordSalvataggio.getEmail());
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Recupera le connessioni del database
         */
        private void recuperaConnessioni(){
            String query = DBUtil.repalceJolly(this.QUERY_RECUPERO_UTENTI_ATTIVI, "SECONDI", this.TTL);
            try{
                String esitoQuery = DBUtil.executeQuery(query);
                if(!esitoQuery.equals("ERROR")){
                    //Se la query viene eseguita correttamente
                    DF df = (DF)this.myAgent;
                    df.resetClient();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(esitoQuery.getBytes())));
                    String riga;
                    while ((riga = reader.readLine()) != null){
                        JSONObject jsonObject = new JSONObject(riga);
                        RecordClient rc = new RecordClient(jsonObject.getString("EMAIL"), jsonObject.getString("IP"), jsonObject.getInt("PORT"));
                        df.addClient(rc);
                    }
                }
            }catch (Exception e){e.printStackTrace();}
        }

        @Override
        public void action() {
            super.action();
            this.aggiornaRiferimento();
            this.recuperaConnessioni();
        }
    }
}