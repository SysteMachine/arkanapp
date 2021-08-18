package com.example.android.arkanoid.Util;

import java.util.LinkedList;
import java.util.Objects;

public class ParamList {
    private LinkedList<ParamRecord> records;

    public ParamList(){
        this.records = new LinkedList<ParamRecord>();
    }

    /**
     * Controlla l'esistenza dell'elemento identificato dal nome all'interno della lista dei parametri
     * @param name Nome dell'elemento da identificare
     * @return Restituisce -1 nel caso in cui l'elemento non viene trovato, altrimenti restituisce l'indice in cui si trova
     */
    private int searchElement(String name){
        int esito = -1;

        for(int i = 0; i < this.records.size() && esito == -1; i++){
            if(this.records.get(i).getName().equals(name)){
                esito = i;
            }
        }

        return esito;
    }

    /**
     * Aggiunge un elemento all'interno della lista dei parametri
     * @param name Nome dell'elemento da inserire all'interno della lista
     * @param value Oggetto da associare al nome
     * @param <T> Tipo dell'oggetto inserito all'interno
     * @return Restituisce l'esito dell'inserimento
     */
    public <T> boolean add(String name, T value){
        boolean esito = false;
        if(this.records.contains(new ParamRecord(name, null, null))){
            ParamRecord record = new ParamRecord(
                    name,
                    value,
                    value.getClass()
            );

            esito = this.records.add(record);
        }
        return  esito;
    }

    /**
     * Restituisce l'elemento associato al nome se esistente
     * @param name Nome associato all'elemento della lista dei parametri
     * @param <T> Tipo di dato di ritorno
     * @return Restituisce l'elemento associato a name, con il cast al tipo T
     */
    public <T> T get(String name){
        T esito = null;
        int iElemento = this.searchElement(name);
        if(iElemento != -1){
            ParamRecord record = this.records.get(iElemento);

            Class c = record.getClasse();
            Object element = record.getValue();

            esito = (T)c.cast(element);
        }
        return esito;
    }

    /**
     * Classe che identifica l'informazione singola presente nella lista dei parametri
     */
    private class ParamRecord{
        private String name;
        private Object value;
        private Class classe;

        public ParamRecord(String name, Object value, Class classe){
            this.name = name;
            this.value = value;
            this.classe = classe;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Class getClasse() {
            return classe;
        }

        public void setClasse(Class classe) {
            this.classe = classe;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParamRecord that = (ParamRecord) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
