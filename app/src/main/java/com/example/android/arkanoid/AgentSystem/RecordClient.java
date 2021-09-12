package com.example.android.arkanoid.AgentSystem;

public class RecordClient {
    private String email;
    private String clientip;
    private String ip;
    private String localIp;
    private int porta;

    public RecordClient(String email, String clientip, String ip, String localip, int porta) {
        this.email = email;
        this.clientip = clientip;
        this.ip = ip;
        this.localIp = localip;
        this.porta = porta;
    }

    public String getEmail() {
        return email;
    }

    public String getClientip() {
        return clientip;
    }

    public String getIp() {
        return ip;
    }

    public String getLocalIp() {
        return localIp;
    }

    public int getPorta() {
        return porta;
    }
}