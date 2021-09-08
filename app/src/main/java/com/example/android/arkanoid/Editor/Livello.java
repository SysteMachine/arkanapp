package com.example.android.arkanoid.Editor;

import java.util.ArrayList;
import java.util.Iterator;

public class Livello {
    public static int MAX_LAYER = 10;
    public static int MIN_LAYER = 1;

    private int contatoreLivello;

    private String nomeLivello;
    private int indiceStile;

    private final ArrayList<LayerLivello> layer;

    public Livello(String parametro, boolean salvataggio){
        this.layer = new ArrayList<>();
        if(!salvataggio) {
            this.nomeLivello = parametro;
            this.contatoreLivello = 0;
            this.indiceStile = 0;
            this.creaLayerLivello();
        }else
            this.ripristinaSalvataggio(parametro);


    }

    /**
     * Crea un nuovo layerLivello
     * @return Restituisce il layer creato
     */
    public LayerLivello creaLayerLivello(){
        LayerLivello esito = null;

        if(this.contatoreLivello < Livello.MAX_LAYER) {
            LayerLivello ll = new LayerLivello(this.contatoreLivello++);
            this.layer.add(ll);
            esito = ll;
        }

        return esito;
    }

    /**
     * Elimina il layer lla posizione indicata
     * @param livelloLayer Livello del layer da eliminare
     */
    public void eliminaLayer(int livelloLayer){
        if(this.layer.size() -1 >= Livello.MIN_LAYER) {
            int contatoreLivello = 0;
            for (Iterator<LayerLivello> it = this.layer.iterator(); it.hasNext(); ) {
                LayerLivello ll = it.next();
                if (ll.getPosizioneLivello() == livelloLayer) {
                    it.remove();
                } else {
                    ll.setPosizioneLivello(contatoreLivello++);
                }
            }
            this.contatoreLivello = contatoreLivello;
        }
    }

    /**
     * Restituisce il livello nella posizione inserita
     * @param livelloLayer Livello del layer
     * @return restituisce un layerLivello o null in caso di valore mancante
     */
    public LayerLivello getLayerLivello(int livelloLayer){
        LayerLivello esito = null;

        for(LayerLivello ll : this.layer){
            if(ll.getPosizioneLivello() == livelloLayer){
                esito = ll;
                break;
            }
        }

        return esito;
    }

    /**
     * Restituisce la stringa di salvataggio del livello
     * @return Restituisce una Stringa di salvataggio
     */
    public String getSalvataggio(){
        StringBuilder sb = new StringBuilder();

        sb.append("]]").append("CONTATORE_LIVELLO==" + this.contatoreLivello);
        sb.append("]]").append("NOME_LIVELLO==" + this.nomeLivello);
        sb.append("]]").append("INDICE_STILE==" + this.indiceStile);

        for(LayerLivello ll : this.layer)
            sb.append("]]").append("LAYER==").append(ll.getSalvataggio());

        return sb.substring(2);
    }

    /**
     * Ripristina un salvataggio
     * @param stringaSalvataggio Stringa di salvataggio
     */
    public void ripristinaSalvataggio(String stringaSalvataggio){
        if(stringaSalvataggio != null && !stringaSalvataggio.equals("")){
            for(String elemento : stringaSalvataggio.split("]]")){
                String[] nomeValore = elemento.split("==");
                if(nomeValore.length == 2){
                    if(nomeValore[0].equals("CONTATORE_LIVELLO"))
                        this.contatoreLivello = Integer.parseInt(nomeValore[1]);
                    if(nomeValore[0].equals("NOME_LIVELLO"))
                        this.nomeLivello = nomeValore[1];
                    if(nomeValore[0].equals("INDICE_STILE"))
                        this.indiceStile = Integer.parseInt(nomeValore[1]);
                    if(nomeValore[0].equals("LAYER"))
                        this.layer.add(new LayerLivello(nomeValore[1]));
                }
            }
        }
    }

    //Getter
    public int getNLivelli(){
        return this.layer.size();
    }

    public int getContatoreLivello() {
        return contatoreLivello;
    }

    public String getNomeLivello() {
        return nomeLivello;
    }

    public int getIndiceStile() {
        return indiceStile;
    }

    //Setter

    public void setNomeLivello(String nomeLivello) {
        this.nomeLivello = nomeLivello;
    }

    public void setIndiceStile(int indiceStile) {
        this.indiceStile = indiceStile;
    }
}