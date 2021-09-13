package com.example.android.arkanoid.AgentSystem;

import android.content.Context;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;

public class GA {
    private static boolean setup = false;
    public static Container container = new Container();
    public static Channel channel = new Channel();
    public static RecordSalvataggio salvataggio;


    /**
     * Setup del GA
     */
    public static void setup(Context context){
        if(!GA.setup){
            GA.salvataggio = new RecordSalvataggio(context);
            GA.setup = true;
            GA.container.addAgente(new DF());
            GA.container.addAgente(new KeepAssociationAgent());
            if(GA.channel == null)
                GA.channel = new Channel();
        }
    }

    /**
     * Reset del GA
     */
    public static void cancel(){
        if(GA.setup){
            for(Agente a : GA.container.getListaAgenti())
                a.doDelete();
            GA.setup = false;
        }
    }
}
