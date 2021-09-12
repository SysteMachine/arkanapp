package com.example.android.arkanoid.AgentSystem;

import android.os.StrictMode;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DF extends Agente{
    private ArrayList<RecordClient> client;         //Client connessi

    public DF() {
        super("DF");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.client = new ArrayList<>();
        this.MS_DELAY = 5000;

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
        private final String QUERY_INSERIMENTO_GIOCATORE = "INSERT INTO df_table (df_table_ip, df_table_local_ip, df_table_port, df_table_user_email) VALUES (IP, LOCAL, PORT, EMAIL)";
        private final String QUERY_CONTROLLO_ESISTENZA_RIFERIMENTO = "SELECT count(*) AS N from df_table WHERE df_table_user_email LIKE EMAIL";
        private final String QUERY_AGGIORNAMENTO_QUERY = "UPDATE df_table SET df_table_time_stamp = CURRENT_TIMESTAMP, df_table_ip = IP, df_table_local_ip = LOCAL, df_table.df_table_port = PORTA WHERE df_table_user_email LIKE EMAIL";
        private final String QUERY_RECUPERO_UTENTI_ATTIVI = "SELECT df_table_ip AS IP, df_table_local_ip AS LOCAL, df_table_port AS PORT, df_table_user_email AS EMAIL FROM df_table WHERE  TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, df_table_time_stamp)) < SECONDI";


        public CompitoAggiornamentoStatoConnessione() {
            super("Compito di aggiornamento dello stato della connessione");
            this.rimuoviRiferimenti();
            if(!this.controlloPrimoRiferimento())
                this.aggiungiPrimoRiferimento();
            this.getMyLocalIp();
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
         * Preleva l'indirrizzo ip locale del client
         * @return Restituisce una stringa che contiente l'ip del dispositivo
         */
        private String getMyLocalIp(){
            String esito = null;

            try{
                List<NetworkInterface> interfaccieDiRete = Collections.list(NetworkInterface.getNetworkInterfaces());
                for(int i = 0; i < interfaccieDiRete.size() && esito == null; i++){
                    List<InetAddress> inetDiInterfaccia = Collections.list(interfaccieDiRete.get(i).getInetAddresses());
                    for(InetAddress inet : inetDiInterfaccia){
                        String host = inet.getHostAddress();
                        if(host.contains("192.168")) {
                            esito = host;
                            break;
                        }
                    }
                }
            }catch (Exception e){e.printStackTrace();}

            return esito;
        }

        /**
         * Rimuove i riferimenti nel df_table
         */
        private void rimuoviRiferimenti(){
            if(GA.salvataggio.isLogin()){
                String query = DBUtil.repalceJolly(this.QUERY_ELIMINAZIONE_RIFERIMENTI, "EMAIL", GA.salvataggio.getEmail());
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Aggiunge il nuovo riferimento nella df_table
         */
        private void aggiungiPrimoRiferimento(){
            String indirizzoIp = this.getMyIp();
            if(GA.salvataggio.isLogin() && !indirizzoIp.equals("")){
                String query = DBUtil.repalceJolly(this.QUERY_INSERIMENTO_GIOCATORE, "IP", this.getMyIp());
                query = DBUtil.repalceJolly(query, "LOCAL", this.getMyLocalIp());
                query = DBUtil.repalceJolly(query, "PORT", GA.channel.getPorta());
                query = DBUtil.repalceJolly(query, "EMAIL", GA.salvataggio.getEmail());
                System.out.println(query);
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Esegue un controllo d'esistenza del primo riferimento
         * @return Restituisce true se il primo riferimento esiste, altrimenti restituisce false
         */
        private boolean controlloPrimoRiferimento(){
            boolean esito = false;

            String indirizzoIp = this.getMyIp();
            if(GA.salvataggio.isLogin() && !indirizzoIp.equals("")){
                String query = DBUtil.repalceJolly(this.QUERY_CONTROLLO_ESISTENZA_RIFERIMENTO, "EMAIL", GA.salvataggio.getEmail());
                try{
                    String esitoQuery = DBUtil.executeQuery(query);
                    if(!esitoQuery.equals("ERROR") && new JSONObject(esitoQuery).getInt("N") == 1)
                        esito = true;
                }catch (Exception e){e.printStackTrace();}
            }

            return esito;
        }

        /**
         * Aggiorna il riferimento all'interno della df_table
         */
        private void aggiornaRiferimento(){
            String indirizzoIp = this.getMyIp();
            if(GA.salvataggio.isLogin() && !indirizzoIp.equals("")){
                String query = DBUtil.repalceJolly(this.QUERY_AGGIORNAMENTO_QUERY, "IP", this.getMyIp());
                query = DBUtil.repalceJolly(query, "LOCAL", this.getMyLocalIp());
                query = DBUtil.repalceJolly(query, "PORTA", GA.channel.getPorta());
                query = DBUtil.repalceJolly(query, "EMAIL", GA.salvataggio.getEmail());
                try{
                    DBUtil.executeQuery(query);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        /**
         * Recupera le connessioni del database
         */
        private void recuperaConnessioni(){
            if(GA.salvataggio.isLogin()){
                String query = DBUtil.repalceJolly(this.QUERY_RECUPERO_UTENTI_ATTIVI, "SECONDI", this.TTL);
                try{
                    String esitoQuery = DBUtil.executeQuery(query);
                    if(!esitoQuery.equals("ERROR")){
                        //Se la query viene eseguita correttamente
                        String mioip = this.getMyIp();

                        DF df = (DF)this.myAgent;
                        df.resetClient();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(esitoQuery.getBytes())));
                        String riga;
                        while ((riga = reader.readLine()) != null){
                            JSONObject jsonObject = new JSONObject(riga);
                            RecordClient rc = new RecordClient(jsonObject.getString("EMAIL"), mioip, jsonObject.getString("IP"), jsonObject.getString("LOCAL"), jsonObject.getInt("PORT"));
                            df.addClient(rc);
                        }
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        }

        @Override
        public void action() {
            super.action();
            if(this.controlloPrimoRiferimento())
                this.aggiornaRiferimento();
            this.recuperaConnessioni();
        }
    }
}