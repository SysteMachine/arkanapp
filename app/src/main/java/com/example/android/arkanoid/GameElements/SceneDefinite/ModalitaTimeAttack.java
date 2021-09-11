package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;

import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.Util.ParamList;

public class ModalitaTimeAttack extends ModalitaClassica{

    protected int DIMENSIONE_ZONA_TIMER = 60;

    private CountDownTimer timer;
    private Boolean timerRunning;
    String tempoRimanente = "";
    private long msTime = 180000; //3 minuti in millisecondi
    int secondi;
    long secondiRimanenti = 180000;

    public ModalitaTimeAttack(Stile stile, GameStatus status) {
        super(stile, status);
        this.inizializzaPowerUP();
        this.registraEventi();
        this.codiceModalita = 2;
        startTimer();
    }

    @Override
    protected void inizializzaPowerUP() {
        super.inizializzaPowerUP();
    }

    protected void startStop(){
        if(timerRunning)
            stopTimer();
        else
            startTimer();
    }


    protected void startTimer(){
        timerRunning = true;
        timer = new CountDownTimer(msTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                msTime = millisUntilFinished;
                secondiRimanenti = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    protected void stopTimer(){
        timer.cancel();
        timerRunning = false;
    }

    protected void updateTimer(){
            secondi = (int) msTime / 1000; //conversione da millisecondi a secondi

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

        msTime = secondiRimanenti;
        msTime += 5000;

    }

    public int getSecondi() {
        return secondi;
    }

    public void setSecondi(int secondi) {
        this.secondi = secondi;
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

    @Override
    protected void registraEventi() {
        super.registraEventi();
    }

    @Override
    public boolean metodoGenerazioneMappa(int i, int j) {
        return super.metodoGenerazioneMappa(i, j);
    }



}

