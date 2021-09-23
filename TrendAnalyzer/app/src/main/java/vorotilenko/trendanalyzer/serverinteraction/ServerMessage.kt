package vorotilenko.trendanalyzer.serverinteraction

/**
 * A message which the server sends to the [WSClientEndpoint] (encoded by JSON).
 */
data class ServerMessage(
    /**
     * One of the [ServerMessageTypes].
     */
    val messageType: Int,
    /**
     * JSON-encoded message wrapped in the [ServerMessage].
     */
    val message: String
)
