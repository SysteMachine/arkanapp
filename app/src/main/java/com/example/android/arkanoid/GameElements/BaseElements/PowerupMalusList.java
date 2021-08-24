package com.example.android.arkanoid.GameElements.BaseElements;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.Util.Util;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class PowerupMalusList {
    private final ArrayList<PowerupMalusRecord> powerupList;

    public PowerupMalusList(){
        this.powerupList = new ArrayList<PowerupMalusRecord>();
    }


    public void addPowerupMalus(Class<? extends AbstractPowerUpMalus> classe, int probabilita){
        this.powerupList.add(
                new PowerupMalusRecord(classe, probabilita)
        );
    }

    /**
     * Restituisce un powerup
     * @param position Posizione di spawn del powerup
     * @param size Dimensione del powerup
     * @param gameLoop Gameloop
     * @param <T> Tipo di restituzione del metodo
     * @return Restituisce un powerup
     */
    public <T extends AbstractPowerUpMalus> T getPowerup(Vector2D position, Vector2D size, GameLoop gameLoop){
        T esito = null;

        for(int i = 0; i < this.powerupList.size() && esito == null; i++){
            PowerupMalusRecord record = this.powerupList.get(i);
            if(Util.probabilita(record.getProbabilitaSpawn())){
                try{
                    Constructor constructor = record.getClasse().getConstructor(Vector2D.class, Vector2D.class, GameLoop.class);
                    esito = (T)constructor.newInstance(position, size, gameLoop);
                }catch (Exception e){e.printStackTrace();}
            }
        }

        return esito;
    }

    public class PowerupMalusRecord{
        private final Class<? extends AbstractPowerUpMalus> classe;
        private final int probabilitaSpawn;

        public PowerupMalusRecord(Class<? extends AbstractPowerUpMalus> classe, int percentualeSpawn) {
            this.classe = classe;
            this.probabilitaSpawn = percentualeSpawn;
        }

        //Beam
        public Class<? extends AbstractPowerUpMalus> getClasse() {
            return classe;
        }

        public int getProbabilitaSpawn() {
            return probabilitaSpawn;
        }
    }
}
