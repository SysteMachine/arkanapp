package com.example.android.arkanoid.Util;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioUtil {
    public static int MUSICA = 1;
    public static int EFFETTO = 2;

    private static int musicaAudio = 100;                                                   //Volume della musica
    private static int effettiAudio = 100;                                                  //Volume dell'audio
    private final static HashMap<String, MediaPlayer> audio = new HashMap<>();              //Contenitore degli audio
    private final static HashMap<String, Integer> associazioneTipo = new HashMap<>();       //Associazioni per il tipo di audio
    private final static ArrayList<String> stopRecord = new ArrayList<>();                  //Lista dei record stoppati per il ripristino

    /**
     * Carica un audio all'interno della scena
     * @param idAudio Id dell'audio da inserire
     * @param idRisorsa Id della risorsa da caricare
     * @param tipoRisorsa Il tipo di risorsa caricata
     * @param loop Flag di loop della risorsa
     * @param context Contesto per il caricamento
     * @return Restituisce true se il caricamento avviene con successo, altrimenti restituisce false
     */
    public static boolean loadAudio(String idAudio, int idRisorsa, int tipoRisorsa, boolean loop, Context context){
        boolean esito = false;

        if(!AudioUtil.audio.containsKey(idAudio)){
            MediaPlayer mediaPlayer = MediaPlayer.create(context, idRisorsa);

            mediaPlayer.setLooping(loop);
            AudioUtil.audio.put(idAudio, mediaPlayer);
            AudioUtil.associazioneTipo.put(idAudio, tipoRisorsa);

            esito = true;
        }

        return esito;
    }

    /**
     * Controlla l'esistenza di una chiave all'interno della lista
     * @param idAudio Id dell'audio da controllare
     * @return Restituisce l'esito del controllo
     */
    public static boolean exists(String idAudio){
        return AudioUtil.audio.containsKey(idAudio);
    }

    /**
     * Avvia l'esecuzione dell'audio
     * @param idAudio Audio da eseguire
     */
    public static void avviaAudio(String idAudio){
        MediaPlayer mediaPlayer = AudioUtil.audio.get(idAudio);
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        }
    }

    /**
     * Ferma l'esecuzione dell'audio
     * @param idAudio Id dell'audio da fermare
     */
    public static void fermaAudio(String idAudio){
        MediaPlayer mediaPlayer = AudioUtil.audio.get(idAudio);
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }
    }

    /**
     * Ferma tutti i mediaPlayer
     */
    public static void fermaIMediaPlayer(){
        for(String k : AudioUtil.audio.keySet()){
            AudioUtil.fermaAudio(k);
        }
    }

    /**
     * Elimina tutti i mediaplayer
     */
    public static void clear(){
        for(String k : AudioUtil.audio.keySet()){
            try{
                AudioUtil.audio.get(k).release();
            }catch (Exception e){e.printStackTrace();}
        }
        AudioUtil.audio.clear();
        AudioUtil.associazioneTipo.clear();
        AudioUtil.stopRecord.clear();
    }

    /**
     * Imposta il volume dell'audio della musica
     * @param volume Volume della musica
     */
    public static void setVolumeAudioMusica(int volume){
        AudioUtil.musicaAudio = volume;
        for(String c : AudioUtil.associazioneTipo.keySet()){
            if(AudioUtil.associazioneTipo.get(c) == AudioUtil.MUSICA){
                MediaPlayer mp = AudioUtil.audio.get(c);
                try{
                    mp.setVolume(volume / 100.0f, volume / 100.0f);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    /**
     * Imposta il volume dell'audio degli effetti
     * @param volume Volume delgli effetti
     */
    public static void setVolumeAudioEffetti(int volume){
        AudioUtil.effettiAudio = volume;
        for(String c : AudioUtil.associazioneTipo.keySet()){
            if(AudioUtil.associazioneTipo.get(c) == AudioUtil.EFFETTO){
                MediaPlayer mp = AudioUtil.audio.get(c);
                try{
                    mp.setVolume(volume / 100.0f, volume / 100.0f);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    /**
     * Ferma tutti i suoni in riproduzione
     */
    public static void pause(){
        for(String s : AudioUtil.audio.keySet()){
            MediaPlayer mp = AudioUtil.audio.get(s);
            if(mp != null && mp.isPlaying()){
                mp.pause();
                AudioUtil.stopRecord.add(s);
            }
        }
    }

    /**
     * Fa riprendere tutti i suoni in pausa
     */
    public static void resume(){
        for(String s : AudioUtil.stopRecord){
            MediaPlayer mp = AudioUtil.audio.get(s);
            if(mp != null)
                mp.start();
        }
        AudioUtil.stopRecord.clear();
    }

    //Beam
    public static int getVolumeMusicaAudio() {
        return musicaAudio;
    }

    public static int getVolumeEffettiAudio() {
        return effettiAudio;
    }
}