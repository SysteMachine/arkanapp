package com.example.android.arkanoid.Util;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioUtil {
    private static int globalAudio = 100;
    private static HashMap<String, MediaPlayer> audio = new HashMap<>();
    private static ArrayList<String> chiavi = new ArrayList<>();

    /**
     * Carica un audio
     * @param idAudio Identificativo dell'audio
     * @param idRisorsa Id della risorsa
     * @param context Contesto
     * @return Restituisce l'esito del caricamento della musica
     */
    public static boolean loadAudio(String idAudio, int idRisorsa, Context context){
        boolean esito = false;

        if(!AudioUtil.audio.containsKey(idAudio)){
            AudioUtil.chiavi.add(idAudio);
            MediaPlayer mediaPlayer = MediaPlayer.create(context, idRisorsa);
            AudioUtil.audio.put(idAudio, mediaPlayer);
            esito = true;
        }

        return esito;
    }

    /**
     * Restituisce il madiaplayer associato all'id del mediaplayer
     * @param idMediaPlayer Id del mediaplayer
     * @return Restituisce il mediaplayer associato all'id o null in caso di mancato valore
     */
    public static MediaPlayer getMediaPlayer(String idMediaPlayer){
        MediaPlayer esito = null;

        if(AudioUtil.audio.containsKey(idMediaPlayer)){
            esito = AudioUtil.audio.get(idMediaPlayer);
        }

        return esito;
    }

    /**
     * Ferma tutti i mediaPlayer
     */
    public static void fermaIMediaPlayer(){
        for(String k : AudioUtil.chiavi){
            AudioUtil.audio.get(k).stop();
        }
    }

    /**
     * Elimina tutti i mediaplayer
     */
    public static void clear(){
        AudioUtil.fermaIMediaPlayer();
        AudioUtil.audio.clear();
        AudioUtil.chiavi.clear();
    }

    /**
     * Imposta il valore dell'audio globale
     * @param globalAudio Valore dell'audio globale
     */
    public static void setGlobalAudio(int globalAudio){
        AudioUtil.globalAudio = globalAudio;
        for(String c : AudioUtil.chiavi){
            MediaPlayer mp = AudioUtil.audio.get(c);
            mp.setVolume(AudioUtil.getGlobalAudio() / 100.0f, AudioUtil.getGlobalAudio() / 100.0f);
        }
    }

    /**
     * Restituisce il valore dell'audio globale
     * @return
     */
    public static int getGlobalAudio(){
        return AudioUtil.globalAudio;
    }
}
