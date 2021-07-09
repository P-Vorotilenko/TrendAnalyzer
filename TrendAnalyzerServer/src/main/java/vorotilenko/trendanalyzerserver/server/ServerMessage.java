package vorotilenko.trendanalyzerserver.server;

/**
 * A message which {@link WSServerEndpoint} sends to the client (encoded by JSON)
 */
public class ServerMessage {
    /**
     * One of the {@link ServerMessageTypes}
     */
    public int messageType;
    /**
     * JSON-encoded message wrapped in the {@link ServerMessage}
     */
    public String message;

    public ServerMessage() {

    }

    public ServerMessage(int messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }
}
