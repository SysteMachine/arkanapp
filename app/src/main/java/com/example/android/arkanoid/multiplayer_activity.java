package com.example.android.arkanoid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.arkanoid.AgentSystem.Agente;
import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Util.Timer;
import com.example.android.arkanoid.Util.TimerListener;

import java.util.ArrayList;

public class multiplayer_activity extends AppCompatActivity implements View.OnClickListener, TimerListener {
    private ListView listView;
    private EditText editText;
    private Button button;
    private AgenteInvioMessaggio agenteMessaggi;
    private CompitoPrelievoMessaggi compito;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        this.listView = this.findViewById(R.id.listaMessaggi);
        this.editText = this.findViewById(R.id.zonaTesto);
        this.button = this.findViewById(R.id.invioButton);
        this.button.setOnClickListener(this);

        this.agenteMessaggi = new AgenteInvioMessaggio();
        GA.container.addAgente(this.agenteMessaggi);
        this.compito = new CompitoPrelievoMessaggi();
        this.agenteMessaggi.addCompito(this.compito);

        timer = new Timer(3);
        timer.setListener(this);
        timer.avviaTimer();
    }

    @Override
    public void onClick(View v) {
        String testo = this.editText.getText().toString();
        System.out.println("Invio messaggio: " + testo);
        this.agenteMessaggi.inviaMessaggio(testo);
    }

    public void v(){
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout, compito.getListaMessaggi()));
    }

    @Override
    public void timeIsZero() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                v();
            }
        });
        timer = new Timer(3);
        timer.setListener(this);
        timer.avviaTimer();
    }

    private class AgenteInvioMessaggio extends Agente{
        public AgenteInvioMessaggio() {
            super("AgenteMessaggi");
        }

        public void inviaMessaggio(String messaggio) {
            RecordSalvataggio recordSalvataggio = GA.salvataggio;
            MessageBox message = new MessageBox(
                    recordSalvataggio.getEmail(),
                    this.getNomeAgente(),
                    MessageBox.BROADCAST_MESSAGE,
                    "AgenteMessaggi",
                    MessageBox.TYPE_TEXT_MESSAGE,
                    messaggio
                    );
            super.inviaMessaggio(message);
        }
    }

    private class CompitoPrelievoMessaggi extends Compito{
        private ArrayList<String> listaMessaggi;

        public CompitoPrelievoMessaggi() {
            super("CompitoPrelievoMessaggi");
            this.listaMessaggi = new ArrayList<>();
        }

        @Override
        public void action() {
            MessageBox m = myAgent.prelevaMessaggio();
            if(m != null && m.getMessageType().equals(MessageBox.TYPE_TEXT_MESSAGE)){
                String messaggio = new StringBuilder().append(m.getFrom()).append(": ").append(m.getContent()).toString();
                this.listaMessaggi.add(messaggio);
            }
        }

        public String[] getListaMessaggi(){
            return this.listaMessaggi.toArray(new String[0]);
        }
    }
}