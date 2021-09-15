package com.example.android.arkanoid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteDiMatching;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaMultiplayer;
import com.example.android.arkanoid.Multiplayer.ClientMultiplayer;
import com.example.android.arkanoid.Util.AudioUtil;

public class multiplayer_activity extends MultiFragmentActivity implements GameOverListener {
    private FrameLayout containerModalita;
    private GameLoop gameLoop;

    private boolean inGame;

    private AgenteDiMatching agenteMatching;            //Agente che si occupa di eseguire il match dei giocatori
    private ClientMultiplayer client;                   //Client Multiplayer
    private ModalitaMultiplayer modalita;               //Modlaita multiplayer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.containerModalita = this.findViewById(R.id.containerModalita);
        this.gameLoop = new GameLoop(this, 60, 720, 1280);
        if(this.containerModalita != null)
            this.containerModalita.addView(this.gameLoop);

        //Creiamo l'agente di matching
        if(!this.inGame)
            this.agenteMatching = new AgenteDiMatching(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.gameLoop.stop();
        this.tornaAlMenu();
    }

    /**
     * Avvia la modlità multiplayer
     * @param ipServer Ip del server
     * @param portaServer Porta del server
     */
    public void avviaModalita(String ipServer, int portaServer){
        try{
            Thread.sleep(1000);
        }catch (Exception e){e.printStackTrace();}

        this.client = new ClientMultiplayer(ipServer, portaServer);
        this.modalita = new ModalitaMultiplayer(this.client);
        this.modalita.setGameOverListener(this);
        if(this.containerModalita != null){
            this.gameLoop.removeAll();
            this.gameLoop.addGameComponent(this.modalita);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    containerModalita.setVisibility(View.VISIBLE);
                }
            });
            this.gameLoop.setShowFPS(true);
            this.gameLoop.start();
            this.inGame = true;
        }
    }

    @Override
    public void onBackPressed() {
        this.agenteMatching.doDelete();
        if(this.inGame)
            this.client.stopClient();
        this.tornaAlMenu();
    }

    /**
     * Torna al menu principale
     */
    public void tornaAlMenu(){
        Intent intent = new Intent(this, main_menu_activity.class);
        AudioUtil.setVolumeAudioMusica(100);
        AudioUtil.clear();
        AudioUtil.loadAudio("background_music", R.raw.background_music, AudioUtil.MUSICA, true, this);
        AudioUtil.avviaAudio("background_music");
        this.startActivity(intent);
    }

    @Override
    public void gameOver(GameStatus status) {
        this.tornaAlMenu();
    }
}