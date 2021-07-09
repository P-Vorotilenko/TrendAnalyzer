package vorotilenko.trendanalyzerserver.server;

/**
 * Class for parsing JSON messages which are sent from client to {@link WSServerEndpoint}.
 */
public class ClientMessage {
    /**
     * One of the {@link ClientMessageTypes}
     */
    public int messageType;
    /**
     * JSON-encoded message wrapped in the {@link ClientMessage}
     */
    public String message;
}
