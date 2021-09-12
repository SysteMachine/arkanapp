package com.example.android.arkanoid.AgentSystem;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;

public class KeepAssociationAgent extends Agente{
    public KeepAssociationAgent() {
        super("KeepAssociationAgent");
        this.addCompito(new MantieniConnessioneCompito());
    }

    private class MantieniConnessioneCompito extends Compito{
        public MantieniConnessioneCompito() {
            super("MantieniConnessioneCompito");
        }

        @Override
        public void action() {
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(GA.contesto);
            if(recordSalvataggio.isLogin()){
                MessageBox messageBox = new MessageBox(
                        recordSalvataggio.getEmail(),
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
