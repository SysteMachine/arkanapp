package com.example.android.arkanoid.DataStructure;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RecordSalvataggio implements Serializable{
    private final String FILE_NAME = "save.ark";

    private boolean login;
    private String email;
    private String nomeUtente;
    private String password;

    private transient Context context;

    public RecordSalvataggio(Context context){
        this();
        this.context = context;

        this.loadFile();
    }

    private RecordSalvataggio(){
        this.login = false;
        this.email = "";
        this.nomeUtente = "";
        this.password = "";
    }

    /**
     * Salva l'istanza
     */
    private void saveFile(){
        try{
            ObjectOutputStream os = new ObjectOutputStream(context.openFileOutput(this.FILE_NAME, Context.MODE_PRIVATE));
            os.writeObject(this);
            os.flush();
            os.close();
        }catch (Exception e ){e.printStackTrace();}
    }

    /**
     * Carica l'istanza
     */
    private void loadFile(){
        try{
            ObjectInputStream is = new ObjectInputStream(context.openFileInput(this.FILE_NAME));
            RecordSalvataggio record = (RecordSalvataggio) is.readObject();
            is.close();

            //Recupero dello stato
            this.nomeUtente = record.nomeUtente;
            this.login = record.login;
            this.email = record.email;
            this.password = record.password;
        }catch (FileNotFoundException e){}
        catch (InvalidClassException e){this.saveFile(); e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
        this.saveFile();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.saveFile();
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
        this.saveFile();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.saveFile();
    }

    @Override
    public String toString() {
        return "RecordSalvataggio{" +
                "FILE_NAME='" + FILE_NAME + '\'' +
                ", login=" + login +
                ", email='" + email + '\'' +
                ", nomeUtente='" + nomeUtente + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
