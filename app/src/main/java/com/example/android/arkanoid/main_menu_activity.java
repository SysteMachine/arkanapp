package com.example.android.arkanoid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.FragmentMenu.selezione_modalita_fragment;

public class main_menu_activity extends MultiFragmentActivity implements View.OnClickListener, DialogInterface.OnClickListener {
    private AlertDialog dialogoLogout;

    private Button pulsanteGioca;
    private Button pulsanteMultigiocatore;
    private Button pulsanteCreazioni;
    private Button pulsanteEditor;
    private Button pulsanteClassifica;
    private Button pulsanteImpostazioni;
    private Button pulsanteCrediti;
    private Button pulsanteLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.prendiRiferimenti();
        this.cambiaMenuPrincipale();
    }

    /**
     * Prende i riferimenti alle viste
     */
    private void prendiRiferimenti(){
        this.dialogoLogout = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(this.getResources().getString(R.string.main_menu_logout))
                .setMessage(this.getResources().getString(R.string.main_menu_logout_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();

        this.pulsanteGioca = findViewById(R.id.pulsanteGioca);
        this.pulsanteMultigiocatore = findViewById(R.id.pulsanteMultigiocatore);
        this.pulsanteCreazioni = findViewById(R.id.pulsanteCreazioni);
        this.pulsanteEditor = findViewById(R.id.pulsanteEditor);
        this.pulsanteClassifica = findViewById(R.id.pulsanteClassifica);
        this.pulsanteImpostazioni = findViewById(R.id.pulsanteImpostazioni);
        this.pulsanteCrediti = findViewById(R.id.pulsanteCrediti);
        this.pulsanteLogout = findViewById(R.id.pulsanteLogout);

        if(this.pulsanteGioca != null)
            this.pulsanteGioca.setOnClickListener(this);
        if(this.pulsanteMultigiocatore != null)
            this.pulsanteMultigiocatore.setOnClickListener(this);
        if(this.pulsanteCreazioni != null)
            this.pulsanteCreazioni.setOnClickListener(this);
        if(this.pulsanteEditor != null)
            this.pulsanteEditor.setOnClickListener(this);
        if(this.pulsanteClassifica != null)
            this.pulsanteClassifica.setOnClickListener(this);
        if(this.pulsanteImpostazioni != null)
            this.pulsanteImpostazioni.setOnClickListener(this);
        if(this.pulsanteCrediti != null)
            this.pulsanteCrediti.setOnClickListener(this);
        if(this.pulsanteLogout != null)
            this.pulsanteLogout.setOnClickListener(this);
    }

    /**
     * Cambia gli elementi del menu principale se l'utente non è loggato
     */
    private void cambiaMenuPrincipale(){
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        if(!recordSalvataggio.isLogin()){
            //Se l'utente non ha fatto il login nascondiamo dei pulsanti
            ((TableRow)this.pulsanteMultigiocatore.getParent()).setVisibility(View.GONE);
            ((TableRow)this.pulsanteEditor.getParent()).setVisibility(View.GONE);
            ((TableRow)this.pulsanteLogout.getParent()).setVisibility(View.GONE);
            TableLayout tl = this.findViewById(R.id.tableContainer);
            if(tl != null)
                tl.setWeightSum(5);
        }
    }

    /**
     * Esegue il logout dall'account
     */
    private void logOut(){
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        boolean wasLogged = recordSalvataggio.isLogin();

        recordSalvataggio.setLogin(false);
        recordSalvataggio.setNomeUtente("");
        recordSalvataggio.setEmail("");

        Intent intent = new Intent(this, login_activity.class);
        if(wasLogged)
            intent.putExtra(login_activity.ERROR_MESSAGE, this.getResources().getString(R.string.logout_messaggio_conferma));
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        if(recordSalvataggio.isLogin())
            this.dialogoLogout.show();
        else
            this.logOut();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.pulsanteGioca))
            this.mostraFragment(new selezione_modalita_fragment(), true);
        if(v.equals(this.pulsanteLogout))
            this.dialogoLogout.show();
        if(v.equals(this.pulsanteEditor))
            this.startActivity(new Intent(this, editor_activity.class));
    }

    @Override
    public void frameContrastoToccato(MotionEvent event) {
        this.nascondiFragment(true);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog.equals(this.dialogoLogout)){
            this.logOut();
        }
    }
}