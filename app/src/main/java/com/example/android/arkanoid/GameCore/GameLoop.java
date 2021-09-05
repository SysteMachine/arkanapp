package com.example.android.arkanoid.GameCore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.TextureView;

import com.example.android.arkanoid.R;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

//GameLoop
public class GameLoop extends TextureView implements Runnable {
    private boolean running;                                     //Flag di rendering del thread
    private boolean updateRunning;                               //Flag che indica se il gameLoop esegue l'update delle componenti
    private Thread gameThread;                                   //Thread di rendering

    private final Bitmap bitmapCanvas;                           //Bitmap di disegno
    private final int canvasWidht;                               //Larghezza della canvas
    private final int canvasHeight;                              //Altezza della canvas

    private final LinkedList<AbstractGameComponent> elementi;     //Elementi all'interno del gameLoop

    private int fpsTarget;                                        //Fps di riferimento del gameLoop
    private boolean showFPS;                                      //Flag di visualizzazione dell'fps su schermo
    private Typeface font;                                        //Font del gameloop

    public GameLoop(Context context, int fpsTarget, int canvasWidht, int canvasHeight) {
        super(context);

        this.fpsTarget = fpsTarget;
        this.running = false;
        this.updateRunning = true;
        this.showFPS = false;

        this.elementi = new LinkedList<>();

        this.canvasWidht = canvasWidht;
        this.canvasHeight = canvasHeight;

        bitmapCanvas = Bitmap.createBitmap(this.canvasWidht, this.canvasHeight, Bitmap.Config.ARGB_8888);
        this.font = ResourcesCompat.getFont(this.getContext(), R.font.font);
    }

    //Gestione della lista

    /**
     * Ordina le componenti all'interno del gameLoop
     * @param comparator Comparatore per l'ordinamento
     */
    public void sort(Comparator<AbstractGameComponent> comparator){
        this.elementi.sort(comparator);
    }

    /**
     * Effettua il sort sulla base dello zIndex sulle componenti
     */
    private void sort(){
        this.sort(new Comparator<AbstractGameComponent>() {
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
        boolean esito = false;

        if(this.addGameComponentNoSetup(component))
            component.setup(this.canvasWidht, this.canvasHeight);

        return esito;
    }

    /**
     * Aggiunge ul elemento al gameLoop senza eseguire la funzione di setup
     * @param component Componente da aggiungere all'interno del gameLoop
     * @return Restituisce l'esito dell'inserimento
     */
    public boolean addGameComponentNoSetup(AbstractGameComponent component){
        boolean esito = false;

        if(!this.elementi.contains(component)){
            esito = this.elementi.add(component);
            if(esito) {
                component.setGameLoop(this);
            }
            this.sort();
        }

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
            String messaggioFps = "" + Math.round(FPS);

            paint.setTextSize(16 * this.getResources().getDisplayMetrics().density);

            Rect bounds = new Rect();
            paint.getTextBounds(messaggioFps, 0, messaggioFps.length(), bounds);

            paint.setColor(Color.YELLOW);
            canvas.drawText(messaggioFps, 0, 40, paint);
        }
    }

    /**
     * Esegue l'operazione finale del game element
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    private void finalStep(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        for(AbstractGameComponent agc : this.elementi){
            agc.finalStep(dt, screenWidth, screenHeight, canvas, paint);
        }
    }

    //Override
    @Override
    public void run() {
        long time = 0;                                              //TimeStamp
        float dt = 1;                                               //DeltaTime
        float fps = this.fpsTarget;                                 //FpsAttuali
        int ns = (int)( Math.pow(10, 9) / this.fpsTarget );         //Nanosecondi ottimali d'attesa

        while(this.running){
            time = System.nanoTime();

            Canvas canvas = new Canvas(this.bitmapCanvas);
            Paint paint = new Paint();

            if(canvas != null){
                //Aggiornamento degli elementi su schermo
                try{
                    paint.setTypeface(this.font);
                    if(this.updateRunning)
                        this.update(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                    this.finalStep(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                    this.render(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                }catch(ConcurrentModificationException e){e.printStackTrace();}
                catch(Exception e){e.printStackTrace();}
            }

            //Disegno sullo schermo
            canvas = this.lockCanvas();
            if(canvas != null){
                int width = this.canvasWidht;
                int height = this.canvasHeight;
                int posY = 0;
                int posX = 0;

                if(this.getWidth() > this.getHeight()){
                    height = this.getHeight();
                    posY = 0;
                    width = (int)( this.canvasWidht * ( (float)height / this.canvasHeight ) );
                    posX = (this.getWidth() / 2) - (width / 2);
                }else {
                    width = this.getWidth();
                    posX = 0;
                    height = (int)( this.canvasHeight * ( (float)width / this.canvasWidht ) );
                    posY = (this.getHeight() / 2) - (height / 2);
                }

                canvas.drawBitmap(
                        Bitmap.createScaledBitmap(this.bitmapCanvas, width, height, false),
                        posX,
                        posY,
                        paint
                );
                this.unlockCanvasAndPost(canvas);

            }
            //---------------------

            //Attesa di ns
            long timeStamp = System.nanoTime();
            while(System.nanoTime() - timeStamp < ns){}

            //Calcolo del dt e degli fps
            long now = System.nanoTime() - time;
            dt = (float)( now / Math.pow(10, 9) );
            fps = (float)( Math.pow(10, 9) / now );

            //Incremento o decremento dei ns
            int nsIncremento = Math.abs(this.getFpsTarget() - fps) < 5 ? 10000 : 1000000;
            ns += fps > this.fpsTarget ? nsIncremento : 0;
            ns -= fps < this.fpsTarget && ns - nsIncremento > 0 ? nsIncremento : 0;
        }
    }

    //Beam


    public boolean isRunning() {
        return running;
    }

    public boolean isUpdateRunning() {
        return updateRunning;
    }

    public int getCanvasWidht() {
        return canvasWidht;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getFpsTarget() {
        return fpsTarget;
    }

    public Typeface getFont() {
        return font;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void setUpdateRunning(boolean updateRunning) {
        this.updateRunning = updateRunning;
    }

    public void setFpsTarget(int fpsTarget) {
        this.fpsTarget = fpsTarget;
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    public void setFont(Typeface font) {
        this.font = font;
    }
}