package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking;

import com.example.android.arkanoid.AgentSystem.Agente;

public interface MatchListener {
    public void match(Agente agente, String emailMatch);
}
