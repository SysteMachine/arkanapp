package com.example.android.arkanoid.FragmentMenu;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.QueryExecutor;
import com.example.android.arkanoid.login_activity;

import org.json.JSONObject;

public class singin_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener, Runnable {
    private Button pulsanteRegistrazione;
    private EditText emailField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText rPasswordField;

    private Thread threadControllo;         //Thread per il controllo dei valori
    private boolean running;                //Flag di controllo per l'esecuzione del thread
    private boolean canSingin;              //Flag per il controllo della registrazione


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.threadControllo = new Thread(this);
        this.running = true;
        this.threadControllo.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.running = false;
        try{
            this.threadControllo.join();
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singin_fragment, container, false);

        view.setOnTouchListener(this);

        this.pulsanteRegistrazione = view.findViewById(R.id.pulsanteRegistrazione);
        this.emailField = view.findViewById(R.id.inputEmail);
        this.usernameField = view.findViewById(R.id.inputUsername);
        this.passwordField = view.findViewById(R.id.inputPassword);
        this.rPasswordField = view.findViewById(R.id.inputRipetiPassword);

        if(this.pulsanteRegistrazione != null)
            this.pulsanteRegistrazione.setOnClickListener(this);

        return view;
    }

    /**
     * Controlla se la mail è stata registrata oppure no
     * @return Restituisce l'esito del controllo
     */
    private boolean controllaValiditaEmail(){
        boolean esito = false;

        if(this.emailField != null){
            String email = this.emailField.getText().toString();
            if(email.contains("@") && email.contains(".")){
                try{
                    esito = QueryExecutor.controlloEsistenzaEmailRegistrata(email);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        return esito;
    }

    /**
     * Controlla la validita dell'username
     * @return Restituisce l'esito del controllo
     */
    private boolean controllaValiditaUsername(){
        boolean esito = false;

        if(this.usernameField != null){
            if(this.usernameField.getText().length() != 0)
                esito = true;
        }

        return esito;
    }

    /**
     * Controlla la validita della password
     * @return Restituisce l'esito del controllo
     */
    private boolean controllaValiditaPassword(){
        boolean esito = false;

        if(this.passwordField != null && this.rPasswordField != null){
            String password = this.passwordField.getText().toString();
            String rPassword = this.rPasswordField.getText().toString();

            if(password.length() != 0 && password.equals(rPassword))
                esito = true;
        }

        return esito;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return true;
    }

    /**
     * Esegue la registrazione sul database
     * @return Restituisce l'esito della registrazione
     */
    private boolean registra(){
        boolean esito = false;

        if(this.emailField != null && this.passwordField != null && this.usernameField != null){
            String email = this.emailField.getText().toString().trim();
            String username = this.usernameField.getText().toString().trim();
            String password = this.passwordField.getText().toString().trim();

            try{
                esito = QueryExecutor.registrazioneUtente(email, username, password);
            }catch (Exception e){e.printStackTrace();}
        }

        return esito;
    }

    @Override
    public void onClick(View v) {
        if(canSingin){
            boolean esitoRegistrazione = this.registra();
            if(this.getActivity() != null) {
                if(!esitoRegistrazione)
                    ((login_activity) this.getActivity()).scriviMessaggioErrore(this.getResources().getString(R.string.singin_fragment_errore_registrazione));

                ((login_activity) this.getActivity()).nascondiFragment(true);
            }
        }
    }

    @Override
    public void run() {
        while(this.running){
            boolean controlloElementi = false;
            try{
                boolean controlloEmail = this.controllaValiditaEmail();
                if(this.emailField != null){
                    if(controlloEmail)
                        this.emailField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.inputFontColor));
                    else
                        this.emailField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
                }
                boolean controlloUsername = this.controllaValiditaUsername();
                boolean controlloPassword = this.controllaValiditaPassword();

                if(this.passwordField != null && this.rPasswordField != null){
                    if(controlloPassword) {
                        this.passwordField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.inputFontColor));
                        this.rPasswordField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.inputFontColor));
                    }else {
                        this.passwordField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
                        this.rPasswordField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
                    }
                }

                //Impostiamo il flag per l'abilitazione del tasto di registrazione
                controlloElementi = controlloEmail && controlloPassword && controlloUsername;
            }catch (Exception e){}

            this.canSingin = controlloElementi;
        }
    }
}