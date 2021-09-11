package com.example.android.arkanoid.Util;

public class Timer implements Runnable{
    private int intervallo;
    private long timeStamp;
    private int timerAttuale;

    private Thread thread;
    private boolean lockTime;

    private TimerListener listener;

    public Timer(int intervallo){
        this.intervallo = intervallo;
        this.lockTime = false;
    }

    /**
     * Avvia il timer
     */
    public void avviaTimer(){
        this.timeStamp = System.currentTimeMillis();
        this.thread = new Thread(this);
        this.thread.start();
    }

    /**
     * Aggiunge secondi al timer
     * @param secondi Secondi da aggiungere
     */
    public void aggiungiSecondi(int secondi){
        if(this.thread != null) {
            this.intervallo += secondi;
        }
    }

    @Override
    public void run() {
        long lockTimeStamp = System.currentTimeMillis();
        while (System.currentTimeMillis() - this.timeStamp < this.intervallo * 1000){
            this.timerAttuale = (int)Math.round((System.currentTimeMillis() - this.timeStamp) / 1000.0f);
            try{
                Thread.sleep(100);
            }catch (Exception e){e.printStackTrace();}
            if(this.lockTime)//Se il tempo è bloccato, aggiunge il tempo al time stamp
                this.timeStamp += System.currentTimeMillis() - lockTimeStamp;
            lockTimeStamp = System.currentTimeMillis();
        }
        if(this.listener != null)
            this.listener.timeIsZero();
    }

    //Beam

    public int getTimerAttuale() {
        return this.intervallo - timerAttuale;
    }

    public boolean isLockTime() {
        return lockTime;
    }

    public TimerListener getListener() {
        return listener;
    }

    public void setLockTime(boolean lockTime) {
        this.lockTime = lockTime;
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }
}