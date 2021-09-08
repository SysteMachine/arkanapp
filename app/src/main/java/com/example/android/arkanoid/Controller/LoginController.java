package com.example.android.arkanoid.Controller;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.QueryExecutor;
import com.example.android.arkanoid.login_activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.example.android.arkanoid.main_menu_activity;
public class LoginController extends AppCompatActivity {
    public static final String ACCOUNT_LOGIN_TYPE = "LOGIN_TYPE";
    public static final String ACCOUNT_LOGIN_EMAIL = "LOGIN_EMAIL";
    public static final String ACCOUNT_LOGIN_PASSWORD = "LOGIN_PASSWORD";
    public static final String TYPE_ACCOUNT = "ACCOUNT";
    public static final String TYPE_GUEST = "GUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String username = null;  //Username del giocatore
        Intent intent = this.getIntent();
        Intent nuovaIntent = new Intent();

        if(intent != null){
            String loginType = intent.getStringExtra(LoginController.ACCOUNT_LOGIN_TYPE);
            if(loginType.equals( LoginController.TYPE_ACCOUNT )){
                //Login account
                String email = intent.getStringExtra(LoginController.ACCOUNT_LOGIN_EMAIL);
                String password = intent.getStringExtra(LoginController.ACCOUNT_LOGIN_PASSWORD);

                if(email != null && password != null)
                    try{
                        username = QueryExecutor.loginUtente(email, password);
                    }catch (Exception e){e.printStackTrace();}

                if(username != null){
                    nuovaIntent.setClass(this, main_menu_activity.class);

                    RecordSalvataggio salvataggio = new RecordSalvataggio(this);
                    salvataggio.setLogin(true);
                    salvataggio.setEmail(email);
                    salvataggio.setPassword(password);
                    salvataggio.setNomeUtente(username);
                }else{
                    nuovaIntent.putExtra(login_activity.ERROR_MESSAGE, this.getResources().getString(R.string.login_messaggio_errore_account_non_trovato));
                    nuovaIntent.setClass(this, login_activity.class);
                    RecordSalvataggio salvataggio = new RecordSalvataggio(this);
                    salvataggio.setLogin(false);
                    salvataggio.setEmail("");
                    salvataggio.setNomeUtente("");
                    salvataggio.setPassword("");
                }
            }else{
                //Login guest
                nuovaIntent.setClass(this, main_menu_activity.class);

                RecordSalvataggio salvataggio = new RecordSalvataggio(this);
                salvataggio.setLogin(false);
                salvataggio.setEmail("");
                salvataggio.setNomeUtente("");
                salvataggio.setPassword("");
            }

            this.startActivity(nuovaIntent);

        }
    }
}