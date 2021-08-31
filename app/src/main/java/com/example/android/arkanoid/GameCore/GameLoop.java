package com.example.android.arkanoid.GameCore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import com.example.android.arkanoid.R;

import java.io.File;
import java.net.URI;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

//GameLoop
public class GameLoop extends CustomTextureView implements Runnable {
    private boolean running;                                     //Flag di rendering del thread
    private Thread gameThread;                                   //Thread di rendering

    private final LinkedList<AbstractGameComponent> elementi;     //Elementi all'interno del gameLoop
    private int fpsTarget;                                        //Fps di riferimento del gameLoop

    private boolean showFPS;                                      //Flag di visualizzazione dell'fps su schermo

    private Bitmap bitmapCanvas;                                  //Bitmap di disegno
    private int canvasWidht;                                      //Larghezza della canvas
    private int canvasHeight;                                     //Altezza della canvas

    private Typeface font;                                        //Font del gameloop

    public GameLoop(Context context, int fpsTarget, int canvasWidht, int canvasHeight) {
        super(context);

        this.setSurfaceTextureListener(this);

        this.fpsTarget = fpsTarget;
        this.running = false;
        this.showFPS = false;

        this.elementi = new LinkedList<>();

        this.canvasWidht = canvasWidht;
        this.canvasHeight = canvasHeight;

        bitmapCanvas = Bitmap.createBitmap(this.canvasWidht, this.canvasHeight, Bitmap.Config.ARGB_8888);

        this.font = ResourcesCompat.getFont(this.getContext(), R.font.font);
    }

    /**
     * Calcola la nuova dimensione della canvas di disegno
     * @param width Larghezza attuale dello schermo
     * @param height Altezza attuale dello schermo
     */
    private void calcolaNuovaDimensioneCanvas(int width, int height){
        if(width <= height){
            this.canvasWidht = width;
            this.canvasHeight = (int)((float)this.canvasWidht * (16.0f / 9.0f));
        }else{
            this.canvasHeight = height;
            this.canvasWidht = (int)((float)this.canvasHeight * (9.0f / 16.0f));
        }

        this.bitmapCanvas = Bitmap.createScaledBitmap(this.bitmapCanvas, this.canvasWidht, this.canvasHeight, true);
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
        boolean esito = false;

        if(!this.elementi.contains(component)){
            esito = this.elementi.add(component);
            if(esito)
                component.setGameLoop(this);
            this.sort();
        }

        return esito;
    }

    /**
     * Aggiunge ul elemento al gameLoop e lo inizializza con il metodo setup
     * @param component Componente da aggiungere all'interno del gameLoop
     * @return Restituisce l'esito dell'inserimento
     */
    public boolean addGameComponentWithSetup(AbstractGameComponent component){
        boolean esito = this.addGameComponent(component);
        if(esito){
            component.setup(this.canvasWidht, this.canvasHeight);
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
     * Effettua il setup degli elementi all'interno del gameLoop
     */
    public void setupElements(){
        this.setup(this.canvasWidht, this.canvasHeight);
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

    public int getCanvasWidht() {
        return canvasWidht;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    //Override
    @Override
    public void run() {
        long time = 0;
        float dt = 1;
        float fps = this.fpsTarget;
        int ns = (int)( Math.pow(10, 9) / this.fpsTarget );

        while(this.running){
            time = System.nanoTime();

            Canvas canvas = new Canvas(this.bitmapCanvas);
            Paint paint = new Paint();

            if(canvas != null){
                //Aggiornamento degli elementi su schermo
                try{
                    paint.setTypeface(this.font);
                    this.update(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                    this.finalStep(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                    this.render(dt, this.canvasWidht, this.canvasHeight, canvas, paint);
                }catch(ConcurrentModificationException e){e.printStackTrace();}
                catch(Exception e){e.printStackTrace();}
            }

            //Disegno sullo schermo
            canvas = this.lockCanvas();
            if(canvas != null){
                int posX = (int)((this.getWidth() * 0.5f) - (this.canvasWidht * 0.5f));
                int posY = (int)((this.getHeight() * 0.5f) - (this.canvasHeight * 0.5f));
                canvas.drawBitmap(
                        this.bitmapCanvas,
                        posX,
                        posY,
                        paint
                );
                this.unlockCanvasAndPost(canvas);
            }
            //---------------------

            long timeStamp = System.nanoTime();
            while(System.nanoTime() - timeStamp < ns){}

            long now = System.nanoTime() - time;
            dt = (float)( now / Math.pow(10, 9) );
            fps = (float)( Math.pow(10, 9) / now );


            ns += fps > this.fpsTarget ? 50000 : 0;
            ns -= fps < this.fpsTarget && ns - 50000 > 0 ? 50000 : 0;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.calcolaNuovaDimensioneCanvas(width, height);
        for(AbstractGameComponent agc : this.elementi)
            agc.ownerSizeChange(this.canvasWidht, this.canvasHeight);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        this.calcolaNuovaDimensioneCanvas(width, height);
        for(AbstractGameComponent agc : this.elementi)
            agc.ownerSizeChange(this.canvasWidht, this.canvasHeight);
    }
}