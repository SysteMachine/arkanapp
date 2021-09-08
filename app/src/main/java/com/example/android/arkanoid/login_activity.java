package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.Controller.LoginController;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.FragmentMenu.singin_fragment;

public class login_activity extends MultiFragmentActivity implements View.OnClickListener, View.OnTouchListener {
    public static String ERROR_MESSAGE = "ERROR";

    private Button loginButton;
    private Button singinButton;
    private Button singinGuestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
    }

    /**
     * Scrive sulla view il messaggio di errore
     * @param messaggio Messaggio di errore da scrivere sulla view
     */
    public void scriviMessaggioErrore(String messaggio){
        TextView messaggioErroreView = this.findViewById(R.id.messaggioErrore);
        if(messaggioErroreView != null && messaggio != null){
            messaggioErroreView.setText(messaggio);
        }
    }

    /**
     * Richiama il controller del login
     * @param email Email dell'account
     * @param password Password dell'account
     */
    private void login(String email, String password){
        Intent intent = new Intent(this, LoginController.class);
        intent.putExtra(LoginController.ACCOUNT_LOGIN_TYPE, LoginController.TYPE_ACCOUNT);
        intent.putExtra(LoginController.ACCOUNT_LOGIN_EMAIL, email);
        intent.putExtra(LoginController.ACCOUNT_LOGIN_PASSWORD, password);
        this.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Visualizzazione del messaggio d'errore
        String messaggioErrore = "";
        Intent intent = this.getIntent();
        if(intent != null){
            String messaggio = intent.getStringExtra(login_activity.ERROR_MESSAGE);
            if(messaggio != null)
                messaggioErrore = messaggio;
        }
        this.scriviMessaggioErrore(messaggioErrore);

        //Riferimento alle viste
        this.loginButton = this.findViewById(R.id.pulsanteLogin);
        if(this.loginButton != null)
            this.loginButton.setOnClickListener(this);
        this.singinButton = this.findViewById(R.id.pulsanteRegistrazione);
        if(this.singinButton != null)
            this.singinButton.setOnClickListener(this);
        this.singinGuestButton = this.findViewById(R.id.pulsanteAccediComeOspite);
        if(this.singinGuestButton != null)
            this.singinGuestButton.setOnClickListener(this);

        //LoginSalvato
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        if(recordSalvataggio.isLogin()){
            this.login(recordSalvataggio.getEmail(), recordSalvataggio.getPassword());
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.loginButton)){
            //Richiamiamo il Controller del login
            EditText emailView = this.findViewById(R.id.inputEmail);
            EditText passwordView = this.findViewById(R.id.inputPassword);

            if(emailView != null && passwordView != null){
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                if(!email.equals("") && !password.equals("")){
                    this.login(email, password);
                }else{
                    this.scriviMessaggioErrore(this.getResources().getString(R.string.login_messaggio_errore_campi_vuoti));
                }
            }
        }

        if(v.equals(this.singinButton))
            this.mostraFragment(new singin_fragment(), true);

        if(v.equals(this.singinGuestButton)){
            //Entriamo come guest
            Intent intent = new Intent(this, LoginController.class);
            intent.putExtra(LoginController.ACCOUNT_LOGIN_TYPE, LoginController.TYPE_GUEST);
            this.startActivity(intent);
        }
    }

    @Override
    public void frameContrastoToccato(MotionEvent event) {
        this.nascondiFragment(true);
    }

    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(this, splash_screen_activity.class));
    }
}