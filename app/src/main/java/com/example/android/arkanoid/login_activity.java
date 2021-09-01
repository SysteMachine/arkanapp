package com.example.android.arkanoid;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;

public class login_activity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ConstraintLayout mainFrame;
    private FrameLayout fragmentFrame;
    private Button loginButton;
    private Button singinButton;
    private Button singinGuestButton;
    private Fragment fragmentRegistrazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        RecordSalvataggio rs = new RecordSalvataggio(this);
        System.out.println("Stampa1: " + rs.toString());
        if(rs.isLogin()){
            Intent newIntent = new Intent(this, main_menu_activity.class);
            newIntent.putExtra("LOGIN_TYPE", "ACCOUNT");
            newIntent.putExtra("LOGIN_EMAIL", rs.getEmail());
            newIntent.putExtra("LOGIN_USERNAME", rs.getNomeUtente());

            this.startActivity(newIntent);
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("FRAGMENT_VISIBLE", this.fragmentRegistrazione.isVisible());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentRegistrazione, this.fragmentRegistrazione)
                .addToBackStack(null)
                .commit();
        if(savedInstanceState.getBoolean("FRAGMENT_VISIBLE"))
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .show(this.fragmentRegistrazione)
                    .commit();
    }

    /**
     * Cambia la visibilità del fragment
     * @param visibilita 1 il fragment è visibile, altrimenti è nascosto
     */
    public void cambiaVisibilitaFragment(int visibilita){
        if(this.fragmentRegistrazione != null && this.fragmentFrame != null){
            if(visibilita == 0){
                class AL implements Animator.AnimatorListener{
                    login_activity la;
                    public AL(login_activity la){
                        this.la = la;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        this.la.getSupportFragmentManager()
                                .beginTransaction()
                                .hide(this.la.fragmentRegistrazione)
                                .commit();
                    }
                }

                AnimatorSet as = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.singin_close_animation);
                as.setTarget(this.fragmentFrame);
                as.addListener(new AL(this));
                as.start();


            }else{
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .show(this.fragmentRegistrazione)
                        .commit();

                AnimatorSet as = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.singin_open_animation);
                as.setTarget(this.fragmentFrame);
                as.start();
            }
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
        this.fragmentFrame = this.findViewById(R.id.fragmentRegistrazione);

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
                    intent.putExtra("LOGIN_TYPE", "ACCOUNT");
                    intent.putExtra("LOGIN_EMAIL", email);
                    intent.putExtra("LOGIN_PASSWORD", password);

                    this.startActivity(intent);
                }else{
                    this.scriviMessaggioErrore(this.getResources().getString(R.string.login_messaggio_errore_campi_vuoti));
                }
            }
        }

        if(v.equals(this.singinButton))
            this.cambiaVisibilitaFragment(1);

        if(v.equals(this.singinGuestButton)){
            Intent intent = new Intent(this, LoginController.class);
            intent.putExtra("LOGIN_TYPE", "GUEST");
            this.startActivity(intent);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_UP)
            this.cambiaVisibilitaFragment(0);
        return true;
    }
}