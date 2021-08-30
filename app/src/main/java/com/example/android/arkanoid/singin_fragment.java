package com.example.android.arkanoid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

public class singin_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener, Runnable {
    private final String QUERY_CONTROLLO_EMAIL = "SELECT COUNT(*) AS N FROM user WHERE user_email LIKE EMAIL";

    private Button pulsanteRegistrazione;
    private EditText emailField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText rPasswordField;

    private Thread threadControllo;         //Thread per il controllo dei valori
    private boolean running;                //Flag di controllo per l'esecuzione del thread


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            try{
                String esitoQuery = DBUtil.executeQuery(DBUtil.repalceJolly(this.QUERY_CONTROLLO_EMAIL, "EMAIL", email));
                if(!esitoQuery.equals("ERROR")){
                    int nElementi = new JSONObject(esitoQuery.trim()).getInt("N");
                    if(nElementi == 0)
                        esito = true;
                }
            }catch (Exception e){}
        }

        return esito;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.pulsanteRegistrazione = view.findViewById(R.id.pulsanteRegistrazione);
        if(this.pulsanteRegistrazione != null)
            this.pulsanteRegistrazione.setOnClickListener(this);

        this.emailField = view.findViewById(R.id.inputEmail);
        this.usernameField = view.findViewById(R.id.inputUsername);
        this.passwordField = view.findViewById(R.id.inputPassword);
        this.rPasswordField = view.findViewById(R.id.inputRipetiPassword);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        System.out.println("Click");
    }

    @Override
    public void run() {
        while(this.running){
            if(this.emailField != null){
                if(this.controllaValiditaEmail())
                    this.emailField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontAviableColor));
                else
                    this.emailField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
            }
        }
    }
}