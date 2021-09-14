package com.example.android.arkanoid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteDiMatching;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaMultiplayer;
import com.example.android.arkanoid.Multiplayer.ClientMultiplayer;

public class multiplayer_activity extends MultiFragmentActivity {
    private ListView listView;
    private Button button;

    private AgenteDiMatching agenteMatching;
    private ClientMultiplayer client;
    private ModalitaMultiplayer modalita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Creiamo l'agente di matching
        this.agenteMatching = new AgenteDiMatching(this);
    }

    /**
     * Avvia la modlità multiplayer
     * @param ipServer Ip del server
     * @param portaServer Porta del server
     */
    public void avviaModalita(String ipServer, int portaServer){
        this.client = new ClientMultiplayer(ipServer, portaServer);
        this.modalita = new ModalitaMultiplayer(this.client);
        //TODO: Continuare qui
    }

    @Override
    public void onBackPressed() {
        this.agenteMatching.doDelete();
        this.startActivity(new Intent(this, main_menu_activity.class));
    }
}