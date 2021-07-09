package vorotilenko.trendanalyzerserver.server;

/**
 * Types of JSON messages which are sent from the client to the {@link WSServerEndpoint}
 */
public class ClientMessageTypes {
    /**
     * Subscribe to updates
     */
    public static final int SUBSCRIBE_TO_UPD = 1;
    /**
     * Unsubscribe from all updates
     */
    public static final int UNSUB_FROM_ALL_UPD = 2;
    /**
     * Add updates
     */
    public static final int ADD_UPD = 3;
    /**
     * Remove updates
     */
    public static final int REMOVE_UPD = 4;
}
