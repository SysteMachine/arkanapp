package com.example.android.arkanoid.Util;

public class LoopTimer implements Runnable{
    private int ms;
    private TimerListener listener;

    private boolean running;
    private Thread threadRunning;

    private long timeStamp;
    public LoopTimer(TimerListener listener, int ms){
        this.ms = ms;
        this.listener = listener;

        this.running = true;
        this.threadRunning = new Thread(this);
        this.threadRunning.start();

        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * Ferma il timer
     */
    public void stop(){
        this.running = false;
    }

    @Override
    public void run() {
        while(running){
            if(System.currentTimeMillis() - this.timeStamp > this.ms){
                if(this.listener != null)
                    this.listener.timeIsZero();
                this.timeStamp = System.currentTimeMillis();
            }
        }
    }

    //Beam

    public int getMs() {
        return ms;
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }
}
