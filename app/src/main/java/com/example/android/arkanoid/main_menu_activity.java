package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.FragmentMenu.selezione_modalita_fragment;

public class main_menu_activity extends MultiFragmentActivity implements View.OnClickListener{
    private Button pulsanteGioca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.loadEssentials(savedInstanceState);

        this.pulsanteGioca = findViewById(R.id.pulsanteGioca);
        if(this.pulsanteGioca != null)
            this.pulsanteGioca.setOnClickListener(this);
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
    protected void loadEssentials(Bundle savedInstanceState) {
        this.loadFragmentLayout(R.id.containerFragment);
        this.loadFrameContrasto(R.id.frameContrasto);
        super.loadEssentials(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.pulsanteGioca))
            this.showFragment(selezione_modalita_fragment.class, true);
    }

    @Override
    protected void onFrameContrastoTouched(View v, MotionEvent e) {
        super.onFrameContrastoTouched(v, e);
        if(e.getAction() == MotionEvent.ACTION_UP)
            this.hideFragment(true);
    }

    @Override
    public void onBackPressed() {
        this.logOut();
    }
}