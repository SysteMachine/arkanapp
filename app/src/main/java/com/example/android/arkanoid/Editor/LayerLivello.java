package com.example.android.arkanoid.Editor;

import java.util.ArrayList;

public class LayerLivello {
    public static int MAX_RIGHE = 6, MIN_RIGHE = 1;
    public static int MIN_COLONNE = 5, MAX_COLONNE = 10;
    public static int MIN_ALTEZZA = 300, MAX_ALTEZZA = 500;
    public static int MIN_VITA_BLOCCO = 1, MAX_VITA_BLOCCO = 10;
    public static float MIN_PVPALLA = 0.5f, MAX_PVPALLA = 3;
    public static float MIN_PVPADDLE = 0.5f, MAX_PVPADDLE = 3;


    private int posizioneLivello;
    private String posizioneBlocchi;
    private int righe;
    private int colonne;
    private int altezza;
    private float percentualeIncrementoVelocitaPalla;
    private float percentualeIncrementoVelocitaPaddle;
    private int puntiPerColpo;
    private int puntiTerminazione;

    public LayerLivello(int posizioneLivello){
        this.posizioneLivello = posizioneLivello;
        this.posizioneBlocchi = "";
        this.righe = LayerLivello.MIN_RIGHE;
        this.colonne = LayerLivello.MIN_COLONNE;
        this.altezza = LayerLivello.MIN_ALTEZZA;
        this.percentualeIncrementoVelocitaPalla = 1;
        this.percentualeIncrementoVelocitaPaddle = 1;
        this.puntiPerColpo = 0;
        this.puntiTerminazione = 0;
    }

    public LayerLivello(String stringaSalvataggio){
        this.ripristinaSalvataggio(stringaSalvataggio);
    }

    /**
     * Controlla se un blocco esiste
     * @param posX Posizione X del blocco da controllare
     * @param posY Posizione Y del blocco da controllare
     * @return Restituisce true se il blocco esiste, altrimenti restituisce false
     */
    private boolean esisteBlocco(int posX, int posY){
        boolean esito = false;

        ArrayList<int[]> blocchi = this.parseListaBlocchi();
        for(int[] blocco : blocchi){
            if(blocco[0] == posX && blocco[1] == posY){
                esito = true;
                break;
            }
        }

        return esito;
    }

    /**
     * Aggiunge un blocco alla lista dei blocchi
     * @param posX PosizioneX del blocco
     * @param posY PosizioneY del blocco
     * @param vitaBlocco Vita del blocco
     * @return Restituisce true se l'inserimento riesce, altrimenti restituisce false;
     */
    public boolean addBlocco(int posX, int posY, int vitaBlocco){
        boolean esito = false;

        if(posX >= 0 && posX < this.colonne && posY >= 0 && posY < this.righe && vitaBlocco >= LayerLivello.MIN_VITA_BLOCCO && vitaBlocco <= LayerLivello.MAX_VITA_BLOCCO){
            StringBuilder sb = new StringBuilder();
            if(!this.esisteBlocco(posX, posY)){
                sb.append(this.posizioneBlocchi).append(",").append(posX).append(":").append(posY).append(":").append(vitaBlocco);
            }else{
                ArrayList<int[]> blocchi = this.parseListaBlocchi();
                for(int[] blocco : blocchi){
                    if(blocco[0] == posX && blocco[1] == posY)
                        sb.append(",").append(posX).append(":").append(posY).append(":").append(vitaBlocco);
                    else
                        sb.append(",").append(blocco[0]).append(":").append(blocco[1]).append(":").append(blocco[2]);
                }
            }
            this.posizioneBlocchi = sb.toString().startsWith(",") ? sb.substring(1) : sb.toString();
            esito = true;
        }

        return esito;
    }

    /**
     * Rimuove il blocco dall posizione indicata
     * @param posX PosizioneX del blocco
     * @param posY PosizioneY del blocco
     */
    public void rimuoviBlocco(int posX, int posY){
        ArrayList<int[]> blocchi = this.parseListaBlocchi();
        StringBuilder sb = new StringBuilder();
        for(int[] blocco : blocchi){
            if(blocco[0] != posX || blocco[1] != posY)
                sb.append(",").append(blocco[0]).append(":").append(blocco[1]).append(":").append(blocco[2]);
        }
        this.posizioneBlocchi = sb.toString().startsWith(",") ? sb.substring(1) : sb.toString();
    }

    /**
     * Esegue il parse della lista dei blocchi
     * @return Restituisce una matrice di blocchi rappresentata da elementi [0]posX, [1]posY, [2]vita
     */
    public ArrayList<int[]> parseListaBlocchi(){
        ArrayList<int[]> blocchi = new ArrayList<>();

        for(String blocco : this.posizioneBlocchi.split(",")){
            int[] elementiBlocco = new int[3];
            int indexElemento = 0;
            if(!blocco.equals("")){
                for(String elemento : blocco.split(":"))
                    elementiBlocco[indexElemento++] = Integer.parseInt(elemento);
                blocchi.add(elementiBlocco);
            }
        }

        return blocchi;
    }

    /**
     * Restituisce i dati di salvataggio
     * @return Dati di salvataggio
     */
    public String getSalvataggio(){
        ArrayList<String> datiSalvataggio = new ArrayList<>();
        datiSalvataggio.add("POSIZIONE_LIVELLO=" + this.posizioneLivello);
        datiSalvataggio.add("RIGHE=" + this.righe);
        datiSalvataggio.add("COLONNE=" + this.colonne);
        datiSalvataggio.add("ALTEZZA=" + this.altezza);
        datiSalvataggio.add("PV_PAL=" + this.percentualeIncrementoVelocitaPalla);
        datiSalvataggio.add("PV_PAD=" + this.percentualeIncrementoVelocitaPaddle);
        datiSalvataggio.add("PUNTI_COLPO=" + this.puntiPerColpo);
        datiSalvataggio.add("PUNTI_TERMINAZIONE=" + this.puntiTerminazione);
        datiSalvataggio.add("BLOCCHI=" + this.posizioneBlocchi);

        StringBuilder builder = new StringBuilder();
        for(String elemento : datiSalvataggio){
            builder.append("]").append(elemento);
        }

        return builder.substring(1);
    }

    /**
     * Ripristina lo stato del layer partendo da una stringa di salvataggio
     * @param salvataggio Stringa di salvataggio
     */
    private void ripristinaSalvataggio(String salvataggio){
        if(salvataggio != null && !salvataggio.equals("")){
            for(String parametro : salvataggio.split("]")){
                String[] nomeValore = parametro.split("=");
                String nomeParametro = nomeValore[0];
                String valoreParametro = nomeValore.length == 2 ? nomeValore[1] : "";

                if(nomeParametro.equals("POSIZIONE_LIVELLO"))
                    this.posizioneLivello = Integer.parseInt(valoreParametro);
                if(nomeParametro.equals("RIGHE"))
                    this.righe = Integer.parseInt(valoreParametro);
                if(nomeParametro.equals("COLONNE"))
                    this.colonne = Integer.parseInt(valoreParametro);
                if(nomeParametro.equals("ALTEZZA"))
                    this.altezza = Integer.parseInt(valoreParametro);
                if(nomeParametro.equals("PV_PAL"))
                    this.percentualeIncrementoVelocitaPalla = Float.parseFloat(valoreParametro);
                if(nomeParametro.equals("PV_PAD"))
                    this.percentualeIncrementoVelocitaPaddle = Float.parseFloat(valoreParametro);
                if(nomeParametro.equals("PUNTI_COLPO"))
                    this.puntiPerColpo = Integer.valueOf(valoreParametro);
                if(nomeParametro.equals("PUNTI_TERMINAZIONE"))
                    this.puntiTerminazione = Integer.valueOf(valoreParametro);
                if(nomeParametro.equals("BLOCCHI"))
                    this.posizioneBlocchi = valoreParametro;
            }
        }
    }

    //Beam

    //Getter
    public void setPosizioneLivello(int posizioneLivello) {
        this.posizioneLivello = posizioneLivello;
    }

    public void setPosizioneBlocchi(String posizioneBlocchi) {
        this.posizioneBlocchi = posizioneBlocchi;
    }

    public void setRighe(int righe) {
        if(righe >= LayerLivello.MIN_RIGHE && righe <= LayerLivello.MAX_RIGHE)
            this.righe = righe;
    }

    public void setColonne(int colonne) {
        if(colonne >= LayerLivello.MIN_COLONNE && colonne <= LayerLivello.MAX_COLONNE)
            this.colonne = colonne;
    }

    public void setAltezza(int altezza) {
        if(altezza >= LayerLivello.MIN_ALTEZZA && altezza <= LayerLivello.MAX_ALTEZZA)
            this.altezza = altezza;
    }

    public void setPercentualeIncrementoVelocitaPalla(float percentualeIncrementoVelocitaPalla) {
        if(percentualeIncrementoVelocitaPalla >= LayerLivello.MIN_PVPALLA && percentualeIncrementoVelocitaPalla <= LayerLivello.MAX_PVPALLA)
            this.percentualeIncrementoVelocitaPalla = percentualeIncrementoVelocitaPalla;
    }

    public void setPercentualeIncrementoVelocitaPaddle(float percentualeIncrementoVelocitaPaddle) {
        if(percentualeIncrementoVelocitaPaddle >= LayerLivello.MIN_PVPADDLE && percentualeIncrementoVelocitaPaddle <= LayerLivello.MAX_PVPADDLE)
            this.percentualeIncrementoVelocitaPaddle = percentualeIncrementoVelocitaPaddle;
    }

    public void setPuntiPerColpo(int puntiPerColpo) {
        if(puntiPerColpo >= 0)
            this.puntiPerColpo = puntiPerColpo;
    }

    public void setPuntiTerminazione(int puntiTerminazione) {
        if(puntiTerminazione >= 0)
            this.puntiTerminazione = puntiTerminazione;
    }

    //Setter
    public int getPosizioneLivello() {
        return posizioneLivello;
    }

    public String getPosizioneBlocchi() {
        return posizioneBlocchi;
    }

    public int getRighe() {
        return righe;
    }

    public int getColonne() {
        return colonne;
    }

    public int getAltezza() {
        return altezza;
    }

    public float getPercentualeIncrementoVelocitaPalla() {
        return percentualeIncrementoVelocitaPalla;
    }

    public float getPercentualeIncrementoVelocitaPaddle() {
        return percentualeIncrementoVelocitaPaddle;
    }

    public int getPuntiPerColpo() {
        return puntiPerColpo;
    }

    public int getPuntiTerminazione() {
        return puntiTerminazione;
    }
}