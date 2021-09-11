package com.example.android.arkanoid.AgentSystem;

public class DF extends Agente{
    private final String QUERY_RECUPERO_UTENTI_ATTIVI = "SELECT df_table_ip AS IP, df_table_port AS PORT, df_table_user_email AS EMAIL FROM df_table WHERE  TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, df_table_time_stamp)) < SECONDI";
    private final String QUERY_INSERIMENTO_GIOCATORE = "INSERT INTO df_table (df_table_ip, df_table_port, df_table_user_email) VALUES (IP, PORT, EMAIL)";
    private final String QUERY_AGGIORNAMENTO_QUERY = "UPDATE df_table SET df_table_time_stamp = CURRENT_TIMESTAMP, df_table_ip = IP, df_table.df_table_port = PORTA WHERE df_table_user_email LIKE EMAIL";
    private final String QUERY_ELIMINAZIONE_RIFERIMENTI = "DELETE FROM df_table WHERE df_table_user_email LIKE EMAIL";

    public DF(String nomeAgente) {
        super(nomeAgente);
    }
}
