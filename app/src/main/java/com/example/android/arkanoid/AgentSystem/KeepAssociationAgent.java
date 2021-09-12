package com.example.android.arkanoid.AgentSystem;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;

public class KeepAssociationAgent extends Agente{
    public KeepAssociationAgent() {
        super("KeepAssociationAgent");
        this.addCompito(new MantieniConnessioneCompito());
        this.MS_DELAY = 5000;
    }

    private class MantieniConnessioneCompito extends Compito{
        public MantieniConnessioneCompito() {
            super("MantieniConnessioneCompito");
        }

        @Override
        public void action() {
            if(GA.salvataggio.isLogin()){
                MessageBox messageBox = new MessageBox(
                        GA.salvataggio.getEmail(),
                        this.myAgent.getNomeAgente(),
                        MessageBox.BROADCAST_MESSAGE,
                        this.myAgent.getNomeAgente(),
                        MessageBox.TYPE_IM_ALIVE,
                        "ALIVE");
                myAgent.inviaMessaggio(messageBox);
            }
        }
    }
}
