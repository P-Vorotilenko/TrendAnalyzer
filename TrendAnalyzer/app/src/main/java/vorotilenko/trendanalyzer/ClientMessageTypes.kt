package vorotilenko.trendanalyzer

/**
 * Types of JSON messages which are sent from [WSClientEndpoint] to the server.
 */
object ClientMessageTypes {
    /**
     * Subscribe to updates
     */
    const val SUBSCRIBE_TO_UPD = 1
    /**
     * Unsubscribe from all updates
     */
    const val UNSUB_FROM_ALL_UPD = 2
    /**
     * Add updates
     */
    const val ADD_UPD = 3
    /**
     * Remove updates
     */
    const val REMOVE_UPD = 4
}