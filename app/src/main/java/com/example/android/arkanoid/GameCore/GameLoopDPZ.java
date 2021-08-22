package com.example.android.arkanoid.GameCore;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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

    private final String U_COLOR = "u_Color";
    private int uColorLotation;
    private final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private boolean isGl2Supported;
    private boolean renderedSetted;

    private float[] verticiTavolo = {
            0f, 0f,
            0f, 14f,
            9f, 14f,
            9f, 0f
    };//Possiamo solo usare triangoli in opengl quindi dobbiamo ricreare i vertici per avere due triangoli che formano un rettangolo

    private float[] verticiTriangolariTavolo = {
            // Triangolo 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            // Triangolo 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            // Linea
            -0.5f, 0f,
            0.5f, 0f,
            // Giocatori
            0f, -0.25f,
            0f, 0.25f
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

                    this.uColorLotation = GLES20.glGetUniformLocation(glProgram, this.U_COLOR);
                    this.aPositionLocation = GLES20.glGetAttribLocation(glProgram, this.A_POSITION);  //Con bind possiamo assegnare un id prima del link

                    this.verticiMemoriaOpengl.position(0);
                    GLES20.glVertexAttribPointer(this.aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, this.verticiMemoriaOpengl);
                    //il secondo parametro indica il numero di elementi per vertice
                    //il 4 solo con gli interi
                    //il 5 solo se passiamo più valori

                    GLES20.glEnableVertexAttribArray(this.aPositionLocation);   //Abilitiamo l'uso dell'array
                }
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniform4f(this.uColorLotation, 1f, 1f, 1f, 0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glUniform4f(this.uColorLotation, 0, 0, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        GLES20.glUniform4f(this.uColorLotation, 1, 0, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

        gl.glPointSize(10);
        GLES20.glUniform4f(this.uColorLotation, 0, 1, 0, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }

    //Beam

    public boolean isRenderedSetted() {
        return renderedSetted;
    }
}
