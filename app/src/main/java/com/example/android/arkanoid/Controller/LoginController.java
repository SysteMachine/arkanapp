package com.example.android.arkanoid.Controller;

import com.example.android.arkanoid.MainActivity;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.login_activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class LoginController extends AppCompatActivity {
    private final String QUERY_LOGIN = "SELECT user_username FROM user WHERE user_email LIKE EMAIL AND user_password LIKE PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean error = false;      //Flag per il controllo di errori
        String user_username = "";  //Username del giocatore

        Intent intent = this.getIntent();
        if(intent != null){
            String email = intent.getStringExtra("LOGIN_EMAIL");
            String password = intent.getStringExtra("LOGIN_PASSWORD");

            if(email != null && password != null){
                String query = DBUtil.repalceJolly(this.QUERY_LOGIN, "EMAIL", email);
                query = DBUtil.repalceJolly(query, "PASSWORD", password);

                try{
                    String esitoQuery = DBUtil.executeQuery(query);
                    System.out.println(query);
                    if(!esitoQuery.equals("ERROR")){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(esitoQuery.getBytes())));
                        int entryCount = 0;
                        String targetRow = "";
                        String row = "";
                        while((row = reader.readLine()) != null){
                            if(entryCount++ == 0)
                                targetRow = row;
                        }

                        if(entryCount == 1){
                            JSONObject jObject = new JSONObject(targetRow);
                            user_username = jObject.getString("user_username");
                        }else
                            error = true;
                    }else
                        error = true;
                }catch (Exception e){
                    error = true;
                }
            }
        }

        Intent newIntent = null;
        if(error){
            newIntent = new Intent(this, login_activity.class);
            newIntent.putExtra("LOGIN_ERRORE", this.getResources().getString(R.string.login_messaggio_errore_account_non_trovato));
        }else{
            newIntent = new Intent(this, MainActivity.class);
            newIntent.putExtra("LOGIN_EMAIL", intent.getStringExtra("LOGIN_EMAIL"));
            newIntent.putExtra("LOGIN_USERNAME", user_username);
        }
        this.startActivity(newIntent);

    }
}
