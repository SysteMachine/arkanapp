package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;

import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.Util.ParamList;

public class ModalitaChaos extends ModalitaClassica{

    protected int DIMENSIONE_ZONA_TIMER = 40;

    private CountDownTimer timer;
    private Boolean timerRunning;
    String tempoRimanente = "";
    private long msTime = 30000; //30s in millisecondi

    public ModalitaChaos(Stile stile, GameStatus status) {
        super(stile, status);
        this.inizializzaPowerUP();
        this.registraEventi();
        this.codiceModalita = 2;
        startTimer();
        timerRunning = true;
    }

    protected void InizializzaChaos(){
        startTimer();
        timerRunning = true;

    }


    protected void startStop(){
        if(timerRunning)
            stopTimer();
        else
            startTimer();
    }


    protected void startTimer(){
        timer = new CountDownTimer(msTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                msTime = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        timerRunning = true;
    }

    protected void stopTimer(){
        timer.cancel();
        timerRunning = false;
    }

    protected void updateTimer(){
        int secondi = (int) msTime % 30000 / 1000; //conversione da millisecondi a secondi

        if (secondi < 10)
            tempoRimanente = "0" + secondi;
        else
            tempoRimanente = "" + secondi;

    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.render(dt, screenWidth, screenHeight, canvas, paint);
        this.disegnaTimer(dt, screenWidth, screenHeight, canvas, paint);
    }

    protected void disegnaTimer(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        float lastSize = paint.getTextSize();
        paint.setTextSize(screenHeight * this.PERCENTUALE_DIMENSIONE_FONT);

        String messaggio = this.tempoRimanente; ;
        Rect bound = new Rect();
        paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

        float startX = (screenWidth * 0.5f) - (bound.right * 0.5f);
        float startY = (this.DIMENSIONE_ZONA_TIMER * 0.5f) - (bound.bottom * 0.5f);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(messaggio, startX, startY, paint);
        paint.setTextSize(lastSize);

    }

    @Override
    protected void eventoColpoBlocco(ParamList paramList) {
        super.eventoColpoBlocco(paramList);
    }

    @Override
    protected void eventoColpoPaddle(ParamList paramList) {
        super.eventoColpoPaddle(paramList);
    }

    @Override
    protected void eventoRotturaBlocco(ParamList parametri) {
        super.eventoRotturaBlocco(parametri);
    }

    @Override
    protected void eventoRimozioneParticella(ParamList parametri) {
        super.eventoRimozioneParticella(parametri);
    }

    @Override
    protected void eventoRimozionePowerup(ParamList parametri) {
        super.eventoRimozionePowerup(parametri);
    }

    @Override
    protected void eventoRaccoltaPowerup(ParamList parametri) {
        super.eventoRaccoltaPowerup(parametri);
    }
}

