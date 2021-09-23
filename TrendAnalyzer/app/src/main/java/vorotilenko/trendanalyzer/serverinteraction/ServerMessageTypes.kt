package vorotilenko.trendanalyzer.serverinteraction

/**
 * Types of JSON messages which are sent from the server to the [WSClientEndpoint].
 */
object ServerMessageTypes {
    /**
     * Message with initial data.
     */
    const val INIT = 0
    /**
     * Normal message.
     */
    const val NORMAL_MESSAGE = 1
    /**
     * String with information about client-server interaction.
     */
    const val INFO = 2
}