package com.example.android.arkanoid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.arkanoid.ActivityUtil.SoundControlActivity;
import com.example.android.arkanoid.AgentSystem.Agente;
import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Util.Timer;
import com.example.android.arkanoid.Util.TimerListener;

import java.util.ArrayList;

public class multiplayer_activity extends SoundControlActivity{
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        this.listView = this.findViewById(R.id.listaMessaggi);
    }

}