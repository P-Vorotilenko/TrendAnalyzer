package vorotilenko.trendanalyzer.serverinteraction

/**
 * Class for parsing JSON messages which are sent from [WSClientEndpoint] to server.
 */
data class ClientMessage(
    /**
     * One of the [ClientMessageTypes]
     */
    val messageType: Int,
    /**
     * JSON-encoded message wrapped in the [ClientMessage]
     */
    val message: String
)