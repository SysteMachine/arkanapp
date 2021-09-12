package com.example.android.arkanoid.AgentSystem;

import android.content.Context;

public class GA {
    public static Context contesto = null;
    private static boolean setup = false;
    public static Container container = new Container();
    public static Channel channel = new Channel();


    /**
     * Setup del GA
     */
    public static void setup(Context context){
        if(!GA.setup){
            GA.contesto = context;
            GA.setup = true;
            GA.container.addAgente(new DF());
            GA.container.addAgente(new KeepAssociationAgent());
            GA.channel = new Channel();
        }
    }
}
