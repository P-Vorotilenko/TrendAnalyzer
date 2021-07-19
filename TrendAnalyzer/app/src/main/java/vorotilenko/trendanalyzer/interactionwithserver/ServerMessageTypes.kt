package vorotilenko.trendanalyzer.interactionwithserver

/**
 * Types of JSON messages which are sent from the server to the [WSClientEndpoint]
 */
object ServerMessageTypes {
    /**
     * Message with initial data
     */
    const val INIT = 0
    /**
     * Normal message
     */
    const val NORMAL_MESSAGE = 1
}