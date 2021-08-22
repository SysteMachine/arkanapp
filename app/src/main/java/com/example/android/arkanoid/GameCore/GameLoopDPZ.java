package com.example.android.arkanoid.GameCore;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.android.arkanoid.OpenGLUtility.ShaderLoader;
import com.example.android.arkanoid.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameLoopDPZ extends GLSurfaceView implements GLSurfaceView.Renderer {
    private long lastTime = 0;

    private static int PUNTI_PER_VERTICE = 2;

    private final String A_COLOR = "a_Color";
    private int aColorLocation;
    private final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    private float[] matriceProiezione = new float[16];  //16 è il massimo numero di elementi che la matrice di proiezione può avere matrice 4x4

    private boolean isGl2Supported;
    private boolean renderedSetted;

    private float[] verticiTavolo = {
            0f, 0f,
            0f, 14f,
            9f, 14f,
            9f, 0f
    };//Possiamo solo usare triangoli in opengl quindi dobbiamo ricreare i vertici per avere due triangoli che formano un rettangolo

    private float[] verticiTriangolariTavolo = {
            // Order of coordinates: X, Y, R, G, B
            // Triangle Fan
            0f, 0f, 1f, 1f, 1f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,
            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    private FloatBuffer verticiMemoriaOpengl;

    public GameLoopDPZ(Context context) {
        super(context);
        int glVersion = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion;
        this.isGl2Supported = glVersion > 0x20000;

        if(this.isGl2Supported){
            this.setEGLContextClientVersion(2);
            this.setRenderer(this);
            this.renderedSetted = true;

            //Creaiamo il buffer per trasportare i dati dalla vm alla memoria nativa di opengl ordinando i byte secondo l'ordine della piattaforma
            //Questo perchè il codice lavorando su vm e utilizzando tecniche di gestione della memoria non consente a opengl che lavora direttamente
            //Sull'hardware grafico di accedere alla memoria se non per effetto delle jni
            this.verticiMemoriaOpengl = ByteBuffer.allocateDirect(this.verticiTriangolariTavolo.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.verticiMemoriaOpengl.put(this.verticiTriangolariTavolo);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.20f, 0.20f, 0.20f, 0);

        String vertex_shader_source = ShaderLoader.getSource(this.getContext(), R.raw.vertex_shader);
        String fragment_shader_source = ShaderLoader.getSource(this.getContext(), R.raw.fragment_shader);

        int vertex_shader = ShaderLoader.comileVertexShader(vertex_shader_source);
        int fragment_shader = ShaderLoader.comileFragmentShader(fragment_shader_source);

        if(vertex_shader != -1 && fragment_shader != -1){
            int glProgram = ShaderLoader.linkProgram(vertex_shader, fragment_shader);

            if(glProgram != -1){
                GLES20.glValidateProgram(glProgram);
                int[] esitoValidazione = new int[1];
                GLES20.glGetProgramiv(glProgram, GLES20.GL_VALIDATE_STATUS, esitoValidazione, 0);
                if(esitoValidazione[0] != 0){
                    GLES20.glUseProgram(glProgram);

                    this.aPositionLocation = GLES20.glGetAttribLocation(glProgram, this.A_POSITION);  //Con bind possiamo assegnare un id prima del link
                    this.aColorLocation = GLES20.glGetAttribLocation(glProgram, this.A_COLOR);
                    this.uMatrixLocation = GLES20.glGetUniformLocation(glProgram, this.U_MATRIX);

                    int stride = (2 + 3) * 4;   //due elementi per i vertici, tre elementi per il colore moltiplicato per i 4 byte dei float

                    this.verticiMemoriaOpengl.position(0);
                    GLES20.glVertexAttribPointer(this.aPositionLocation, 2, GLES20.GL_FLOAT, false, stride, this.verticiMemoriaOpengl);
                    //il secondo parametro indica il numero di elementi per vertice
                    //il 4 solo con gli interi
                    //il 5 solo se passiamo più valori, tipo indica quanti byte saltare per raccogliere il prossimo valore

                    this.verticiMemoriaOpengl.position(2);
                    GLES20.glVertexAttribPointer(this.aColorLocation, 3, GLES20.GL_FLOAT, false, stride, this.verticiMemoriaOpengl);

                    GLES20.glEnableVertexAttribArray(this.aPositionLocation);   //Abilitiamo l'uso dell'array
                    GLES20.glEnableVertexAttribArray(this.aColorLocation);   //Abilitiamo l'uso dell'array
                }
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = width > height ? (float)width / (float)height : (float)height / (float)width;
        if(width > height){
            Matrix.orthoM(this.matriceProiezione, 0, -ratio, ratio, -1, 1, -1, 1);
        }else{
            Matrix.orthoM(this.matriceProiezione, 0, -1, 1, -ratio, ratio, -1, 1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//TODO pag108
        GLES20.glUniformMatrix4fv(this.uMatrixLocation, 1, false, this.matriceProiezione, 0);

        //GLES20.glUniform4f(this.uColorLotation, 1f, 1f, 1f, 0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        //GLES20.glUniform4f(this.uColorLotation, 0, 0, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        //GLES20.glUniform4f(this.uColorLotation, 1, 0, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        //GLES20.glUniform4f(this.uColorLotation, 0, 1, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }

    //Beam

    public boolean isRenderedSetted() {
        return renderedSetted;
    }
}
