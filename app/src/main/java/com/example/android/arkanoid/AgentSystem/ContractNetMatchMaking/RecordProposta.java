package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking;

public class RecordProposta{
    private String email;
    private float punteggioMassimo;
    private float punteggioMedio;

    public RecordProposta(String email, float punteggioMassimo, float punteggioMedio) {
        this.email = email;
        this.punteggioMassimo = punteggioMassimo;
        this.punteggioMedio = punteggioMedio;
    }

    public String getEmail() {
        return email;
    }

    public float getPunteggioMassimo() {
        return punteggioMassimo;
    }

    public float getPunteggioMedio() {
        return punteggioMedio;
    }
}