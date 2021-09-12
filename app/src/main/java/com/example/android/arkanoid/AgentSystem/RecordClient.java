package com.example.android.arkanoid.AgentSystem;

public class RecordClient {
    private String email;
    private String ip;
    private int porta;

    public RecordClient(String email, String ip, int porta) {
        this.email = email;
        this.ip = ip;
        this.porta = porta;
    }

    public String getEmail() {
        return email;
    }

    public String getIp() {
        return ip;
    }

    public int getPorta() {
        return porta;
    }
}