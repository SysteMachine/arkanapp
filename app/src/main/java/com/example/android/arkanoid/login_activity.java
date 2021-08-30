package com.example.android.arkanoid;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.arkanoid.Controller.LoginController;

public class login_activity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ConstraintLayout mainFrame;
    private Button loginButton;
    private Button singinButton;
    private Fragment fragmentRegistrazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
    }

    /**
     * Scrive sulla view il messaggio di errore
     * @param messaggio Messaggio di errore da scrivere sulla view
     */
    private void scriviMessaggioErrore(String messaggio){
        TextView messaggioErroreView = this.findViewById(R.id.messaggioErrore);
        if(messaggioErroreView != null && messaggio != null){
            messaggioErroreView.setText(messaggio);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Visualizzazione del messaggio d'errore
        String messaggioErrore = "";
        Intent intent = this.getIntent();
        if(intent != null){
            String messaggio = intent.getStringExtra("LOGIN_ERRORE");
            if(messaggio != null && !messaggio.equals(""))
                messaggioErrore = messaggio;
        }
        this.scriviMessaggioErrore(messaggioErrore);
        //---------------------------------------

        this.fragmentRegistrazione = new singin_fragment();
        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentRegistrazione, this.fragmentRegistrazione, null)
                .addToBackStack(null)
                .hide(this.fragmentRegistrazione)
                .commit();

        //Riferimento alle viste
        this.loginButton = this.findViewById(R.id.pulsanteLogin);
        if(this.loginButton != null)
            this.loginButton.setOnClickListener(this);
        this.singinButton = this.findViewById(R.id.pulsanteRegistrazione);
        if(this.singinButton != null)
            this.singinButton.setOnClickListener(this);
        this.mainFrame = this.findViewById(R.id.mainFrame);
        if(this.mainFrame != null)
            this.mainFrame.setOnTouchListener(this);
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
                    //Se i campi non sono vuoti richiamiamo il controller del login
                    Intent intent = new Intent(this, LoginController.class);
                    intent.putExtra("LOGIN_EMAIL", email);
                    intent.putExtra("LOGIN_PASSWORD", password);

                    this.startActivity(intent);
                }else{
                    this.scriviMessaggioErrore(this.getResources().getString(R.string.login_messaggio_errore_campi_vuoti));
                }
            }
        }

        if(v.equals(this.singinButton)){
            System.out.println("Singin");
            if(this.fragmentRegistrazione != null){
                FrameLayout fl = this.findViewById(R.id.fragmentRegistrazione);
                this.getSupportFragmentManager().beginTransaction().show(this.fragmentRegistrazione).commit();
                this.findViewById(R.id.fragmentRegistrazione).requestFocus();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.fragmentRegistrazione != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(this.fragmentRegistrazione)
                    .commit();
        }
        return true;
    }
}