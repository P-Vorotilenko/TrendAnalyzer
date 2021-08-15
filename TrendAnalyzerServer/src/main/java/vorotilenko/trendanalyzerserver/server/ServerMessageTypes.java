package vorotilenko.trendanalyzerserver.server;

/**
 * Types of JSON messages which are sent from the {@link WSServerEndpoint} to the client
 */
public class ServerMessageTypes {
    /**
     * Message with initial data
     */
    public static final int INIT = 0;
    /**
     * Normal message
     */
    public static final int NORMAL_MESSAGE = 1;
    /**
     * String with information about client-server interaction
     */
    public static final int INFO = 2;
}
