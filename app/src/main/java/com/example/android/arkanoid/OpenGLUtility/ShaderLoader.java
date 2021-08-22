package com.example.android.arkanoid.OpenGLUtility;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL;

public class ShaderLoader {
    /**
     * Legge il sorgente di una shader dalla cartella raw
     * @param context Contesto
     * @param resourceId Id della risorsa da caricare
     * @return Restituisce il contenuto dello shader
     */
    public static String getSource(Context context, int resourceId){
        StringBuilder sb = new StringBuilder();

        InputStream is = context.getResources().openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try{
            String riga = reader.readLine();
            while(riga != null){
                sb.append(riga + "\n");
                riga = reader.readLine();
            }
        }catch(Exception e){e.printStackTrace();}

        return sb.toString();
    }

    /**
     * Compila il vertexShader
     * @param shader Shader da compilare
     * @return Restituisce l'id associato alla shader compilata, altrimenti restituisce -1
     */
    public static int comileVertexShader(String shader){
        int esito = -1;

        int  shaderObjectId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if(shaderObjectId != 0){
            GLES20.glShaderSource(shaderObjectId, shader);
            GLES20.glCompileShader(shaderObjectId);

            int[] statoCompilazione = new int[1];
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, statoCompilazione, 0);
            System.out.println(GLES20.glGetShaderInfoLog(shaderObjectId));
            if(statoCompilazione[0] == 0){
                GLES20.glDeleteShader(shaderObjectId);
            }
            else
                esito = shaderObjectId;
        }

        return esito;
    }

    /**
     * Compila il fragmentShader
     * @param shader Shader da compilare
     * @return Restituisce l'id associato alla shader compilata, altrimenti restituisce -1
     */
    public static int comileFragmentShader(String shader){
        int esito = -1;

        int  shaderObjectId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if(shaderObjectId != 0){
            GLES20.glShaderSource(shaderObjectId, shader);
            GLES20.glCompileShader(shaderObjectId);

            int[] statoCompilazione = new int[1];
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, statoCompilazione, 0);
            System.out.println(GLES20.glGetShaderInfoLog(shaderObjectId));
            if(statoCompilazione[0] == 0)
                GLES20.glDeleteShader(shaderObjectId);
            else
                esito = shaderObjectId;
        }

        return esito;
    }

    /**
     * Collega al programma opengl gli shader
     * @param vertex_shader Vertex shader
     * @param fragment_shader Fragment shader
     * @return Restituisce l'id del programma opengl
     */
    public static int linkProgram(int vertex_shader, int fragment_shader){
        int esito = -1;

        int programObjectId = GLES20.glCreateProgram();
        if(programObjectId != 0){
            GLES20.glAttachShader(programObjectId, vertex_shader);
            GLES20.glAttachShader(programObjectId, fragment_shader);
            GLES20.glLinkProgram(programObjectId);

            int[] esitoCollegamento = new int[1];
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, esitoCollegamento, 0);
            System.out.println(GLES20.glGetProgramInfoLog(programObjectId));

            if(esitoCollegamento[0] != 0)
                esito = programObjectId;
            else
                GLES20.glDeleteProgram(programObjectId);
        }

        return esito;
    }
}
