package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

public class CompitoRecezione extends Compito {
    private final String QUERY_RECUPERO_MEDIA_MASSIMO_PUNTEGGIO = "SELECT MAX(punteggio_punteggio) AS MASSIMO, AVG(punteggio_punteggio) AS MEDIA, punteggio_user_email AS USER FROM punteggio WHERE punteggio_user_email LIKE EMAIL";

    public CompitoRecezione() {
        super("CompitoRecezione");
    }

    @Override
    public void action() {
        MessageBox messaggio = this.myAgent.prelevaMessaggio();
        if(messaggio.getMessageType().equals(MessageBox.TYPE_CALL_FOR_PROPOSAL)){

        }
    }

    /**
     * Restituisce i parametri dell'account
     * @return Parametri dell'account
     */
    private float[] getParametriAccount(){
        float[] parametri = new float[2];

        if(GA.salvataggio.isLogin()){
            String query = DBUtil.repalceJolly(this.QUERY_RECUPERO_MEDIA_MASSIMO_PUNTEGGIO, "EMAIL", GA.salvataggio.getEmail());
            try{
                String esitoQuery = DBUtil.executeQuery(query);
                if(!esitoQuery.equals("ERROR")){
                    JSONObject jsonObject = new JSONObject(esitoQuery);
                    parametri[0] = jsonObject.getInt("MASSIMO");
                    parametri[1] = (float)jsonObject.getDouble("MEDIA");
                }
            }catch (Exception e){e.printStackTrace();}
        }

        return parametri;
    }
}
