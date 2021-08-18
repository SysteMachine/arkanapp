package com.example.android.arkanoid.GameCore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.TextureView;

import java.util.Comparator;
import java.util.LinkedList;

//GameLoop
public class GameLoop extends TextureView implements Runnable {
    private boolean running;                                //Flag di rendering del thread
    private Thread gameThread;                              //Thread di rendering

    private final LinkedList<AbstractGameComponent> elementi;     //Elementi all'interno del gameLoop
    private int fpsTarget;                                        //Fps di riferimento del gameLoop

    private boolean showFPS;                                 //Flag di visualizzazione dell'fps su schermo

    public GameLoop(Context context, int fpsTarget) {
        super(context);

        this.fpsTarget = fpsTarget;
        this.running = false;
        this.showFPS = false;

        this.elementi = new LinkedList<>();
    }

    //Gestione della lista

    /**
     * Ordina le componenti all'interno del gameLoop
     * @param comparator Comparatore per l'ordinamento
     */
    public void sort(Comparator<? extends AbstractGameComponent> comparator){
        this.elementi.sort((Comparator<? super AbstractGameComponent>) comparator);
    }

    /**
     * Effettua il sort sulla base dello zIndex sulle componenti
     */
    private void sort(){
        this.elementi.sort(new Comparator<AbstractGameComponent>() {
            @Override
            public int compare(AbstractGameComponent o1, AbstractGameComponent o2) {
                int esito = 0;
                if(o1.getzIndex() > o2.getzIndex())
                    esito = 1;
                else if(o1.getzIndex() < o2.getzIndex())
                    esito = -1;
                return esito;
            }
        });
    }

    /**
     * Aggiunge ul elemento al gameLoop
     * @param component Componente da aggiungere all'interno del gameLoop
     * @return Restituisce l'esito dell'inserimento
     */
    public boolean addGameComponent(AbstractGameComponent component){
        boolean esito = this.elementi.add(component);
        if(esito)
            component.setGameLoop(this);
        this.sort();

        return esito;
    }

    /**
     * Aggiunge ul elemento al gameLoop e lo inizializza con il metodo setup
     * @param component Componente da aggiungere all'interno del gameLoop
     * @return Restituisce l'esito dell'inserimento
     */
    public boolean addGameComponentWithSetup(AbstractGameComponent component){
        boolean esito = this.addGameComponent(component);
        if(esito)
            component.setup(this.getWidth(), this.getHeight());

        return esito;
    }

    /**
     * Cancella l'elemento all'interno del gameLoop
     * @param component Elemento da rimuovere dal gameLoop
     * @return Restituisce l'esito della rimozione
     */
    public boolean removeGameComponent(AbstractGameComponent component){
        boolean esito = false;
        if(this.elementi.contains(component)){
            esito = this.elementi.remove(component);
            component.removeGameLoop();
        }
        return esito;
    }

    /**
     * Elimina tutti gli elementi del gameLoop
     */
    public void removeAll(){
        this.elementi.clear();
    }

    //--------------------

    /**
     * Avvia il rendering degli elementi all'interno del gameLoop
     */
    public void start(){
        if(!this.running && this.gameThread == null){
            this.gameThread = new Thread(this);
            this.running = true;
            this.gameThread.start();
        }
    }

    /**
     * Ferma il rendering degli elementi all'interno del gameLoop
     */
    public void stop(){
        if(this.running && this.gameThread != null){
            this.running = false;
            try{
                this.gameThread.join();
            }catch (InterruptedException e){e.printStackTrace();}
            this.gameThread = null;
        }

    }

    /**
     * Effettua il setup degli elementi all'interno del gameLoop
     */
    public void setupElements(){
        this.setup(this.getWidth(), this.getHeight());
    }

    /**
     * Effettua il setup degli elementi presenti all'interno del gameLoop
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     */
    private void setup(int screenWidth, int screenHeight){
        for(AbstractGameComponent agc : this.elementi){
            agc.setup(screenWidth, screenHeight);
        }
    }

    /**
     * Aggiorna tutti gli elementi presenti all'interno del gameLoop
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    private void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        for(AbstractGameComponent agc : this.elementi){
            agc.update(dt, screenWidth, screenHeight, canvas, paint);
        }
    }

    /**
     * Disegna tutti gli elementi presenti all'interno del gameLoop
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    private void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        //Pulizia dello schermo
        paint.setColor(Color.rgb(50, 50, 50));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(0, 0, this.getWidth(), this.getHeight()), paint);

        for(AbstractGameComponent agc : this.elementi){
            agc.render(dt, screenWidth, screenHeight, canvas, paint);
        }

        if(this.showFPS){
            float FPS = 1000 / (dt * 1000);
            String messaggioFps = "FPS: " + Math.round(FPS);

            paint.setTextSize(16 * this.getResources().getDisplayMetrics().density);

            Rect bounds = new Rect();
            paint.getTextBounds(messaggioFps, 0, messaggioFps.length(), bounds);

            paint.setColor(Color.YELLOW);
            canvas.drawText(messaggioFps, 0, 30, paint);
        }
    }

    //Beam

    public boolean isRunning() {
        return running;
    }

    public LinkedList<AbstractGameComponent> getElementi() {
        return elementi;
    }

    public int getFpsTarget() {
        return fpsTarget;
    }

    public void setFpsTarget(int fpsTarget){
        this.fpsTarget = fpsTarget;
    }

    public boolean isShowFPS() { return showFPS; }

    public void setShowFPS(boolean showFPS) { this.showFPS = showFPS; }

    @Override
    public void run() {
        int fps = this.fpsTarget;
        long time = 0;
        int ms = 1000 / fps;
        float dt = 1;
        while(this.running){
            time = System.currentTimeMillis();

            Canvas canvas = this.lockCanvas();
            Paint paint = new Paint();

            if(canvas != null){
                //Aggiornamento degli elementi su schermo
                try{
                    this.update(dt, this.getWidth(), this.getHeight(), canvas, paint);
                    this.render(dt, this.getWidth(), this.getHeight(), canvas, paint);
                }catch(Exception e){}
                this.unlockCanvasAndPost(canvas);
            }

            try{
                Thread.sleep(ms);
            }catch (InterruptedException e){e.printStackTrace();}
            long now = System.currentTimeMillis() - time;

            dt = ((float)now) / 1000;

            fps = (int)(1000 / now);
            ms += fps > this.fpsTarget ? 1 : 0;
            ms -= fps < this.fpsTarget && ms - 1 > 0 ? 1 : 0;
        }
    }
}