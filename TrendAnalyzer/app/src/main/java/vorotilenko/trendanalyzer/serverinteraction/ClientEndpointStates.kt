package vorotilenko.trendanalyzer.serverinteraction

/**
 * Contains the states of [WSClientEndpoint].
 */
object ClientEndpointStates {
    /**
     * All is working. Data is been received from server.
     */
    const val NORMAL = 0

    /**
     * Was connection error.
     */
    const val CONNECTION_ERROR = 1

    /**
     * Not all of initial data was received.
     */
    const val LOADING = 2
}