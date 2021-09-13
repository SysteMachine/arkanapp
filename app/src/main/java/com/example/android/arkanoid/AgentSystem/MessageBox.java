package com.example.android.arkanoid.AgentSystem;

public class MessageBox {
    public static String BROADCAST_MESSAGE = "MESSAGE_BOX_BROADCAST";
    public static String TYPE_CALL_FOR_PROPOSAL = "CALL_FOR_PROPOSAL";
    public static String TYPE_PROPOSE = "PROPROSE";
    public static String TYPE_REJECT = "REJECT";
    public static String TYPE_ACCEPT = "ACCEPT";
    public static String TYPE_IM_ALIVE = "IM_ALIVE";
    public static String TYPE_TEXT_MESSAGE = "TEXT_MESSAGE";

    private String from;                    //Da chi arriva il messaggio
    private String fromAgentName;           //NOme dell'agente che invia il messaggio
    private String to;                      //A chi è destinato
    private String toAgentName;             //NOme dell'agente destinatario del messaggio
    private String messageType;             //Tipologia di messaggio
    private String content;                 //Contenuto del messaggio

    public MessageBox(String from, String fromAgentName, String to, String toAgentName, String messageType, String content){
        this.from = from;
        this.fromAgentName = fromAgentName;
        this.to = to;
        this.toAgentName = toAgentName;
        this.messageType = messageType;
        this.content = content;
    }

    /**
     * Crea il messaggio partendo da una stringa di salvataggio
     * @param inMessage Messaggio in ingresso
     */
    public MessageBox(String inMessage){
        String[] partiMessaggio = inMessage.split(":");
        this.from = partiMessaggio[0];
        this.fromAgentName = partiMessaggio[1];
        this.to = partiMessaggio[2];
        this.toAgentName = partiMessaggio[3];
        this.messageType = partiMessaggio[4];
        this.content = partiMessaggio[5];
    }

    /**
     * Restituisce la stringa del messaggio
     * @return Stringa con le parti che compongono il messaggio in csv : come separatore
     */
    public String getStringaMessaggio(){
        return new StringBuilder()
                .append(this.from).append(":")
                .append(this.fromAgentName).append(":")
                .append(this.to).append(":")
                .append(this.toAgentName).append(":")
                .append(this.messageType).append(":")
                .append(content).toString();
    }

    //BEam

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public String getFromAgentName() {
        return fromAgentName;
    }

    public String getToAgentName() {
        return toAgentName;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFromAgentName(String fromAgentName) {
        this.fromAgentName = fromAgentName;
    }

    public void setToAgentName(String toAgentName) {
        this.toAgentName = toAgentName;
    }
}