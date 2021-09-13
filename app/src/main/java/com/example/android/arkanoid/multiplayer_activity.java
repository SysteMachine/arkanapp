package com.example.android.arkanoid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteDiMatching;

public class multiplayer_activity extends MultiFragmentActivity {
    private ListView listView;
    private Button button;

    private AgenteDiMatching agenteMatching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Creiamo l'agente di matching
        this.agenteMatching = new AgenteDiMatching();
    }

    @Override
    public void onBackPressed() {
        this.agenteMatching.doDelete();
        this.startActivity(new Intent(this, main_menu_activity.class));
    }
}