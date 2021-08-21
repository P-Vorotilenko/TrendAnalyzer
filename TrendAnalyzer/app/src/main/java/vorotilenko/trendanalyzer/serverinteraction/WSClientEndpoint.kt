package vorotilenko.trendanalyzer.serverinteraction

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.glassfish.tyrus.client.ClientManager
import org.glassfish.tyrus.client.ClientProperties
import vorotilenko.trendanalyzer.TradeInfo
import java.net.URI
import javax.websocket.*
import kotlin.concurrent.thread

@ClientEndpoint
class WSClientEndpoint(
    private val handler: Handler,
    private val symbolsMap: Map<String, List<String>>
) {
    /**
     * Called when a connection to the server is opened
     */
    @OnOpen
    fun onOpen(session: Session) {
        val message = ClientMessage(ClientMessageTypes.SUBSCRIBE_TO_UPD, gson.toJson(symbolsMap))
        session.asyncRemote.sendText(gson.toJson(message))
        Companion.session = session
    }

    /**
     * Called when a message from the server is received
     */
    @OnMessage
    fun onMessage(message: String, session: Session) {
        val serverMessage = gson.fromJson(message, ServerMessage::class.java)
        when (serverMessage.messageType) {
            ServerMessageTypes.INIT -> {
                val tradeInfoList: List<List<TradeInfo?>?> =
                    gson.fromJson(serverMessage.message, tradeInfoListType)
                tradeInfoList.filterNotNull()
                    .filterNot { it.isEmpty() }
                    .forEach { list ->
                        val filteredList = list.filterNotNull()
                        if (filteredList.isNotEmpty()) {
                            val handlerMessage =
                                handler.obtainMessage(ServerMessageTypes.INIT, filteredList)
                            handler.sendMessage(handlerMessage)
                        }
                        waitedSymbols--
                    }
            }
            ServerMessageTypes.NORMAL_MESSAGE -> {
                val tradeInfo = gson.fromJson(serverMessage.message, TradeInfo::class.java)
                val handlerMessage =
                    handler.obtainMessage(ServerMessageTypes.NORMAL_MESSAGE, tradeInfo)
                handler.sendMessage(handlerMessage)
            }
            ServerMessageTypes.INFO -> {
                Log.i("Trend Analyzer", "Message from server: ${serverMessage.message}")
            }
        }
    }

    companion object {
        /**
         * Object for parsing JSON
         */
        private val gson = Gson()

        /**
         * The type of list sent by the server. For parsing JSON
         */
        private val tradeInfoListType = object : TypeToken<List<List<TradeInfo?>?>>() {}.type

        /**
         * Connected session
         */
        @Volatile
        private var session: Session? = null

        /**
         * Flag which informs if the server is started
         */
        @Volatile
        var started = false
            private set

        /**
         * Flag which informs if there was a connection error
         */
        @Volatile
        var wasConnectionError = false
            private set

        /**
         * Count of symbols which wait for initial data
         */
        @Volatile
        var waitedSymbols = 0
            private set

        private fun startClient(handler: Handler, symbolsMap: Map<String, List<String>>) {
            symbolsMap.values.forEach { list -> waitedSymbols += list.size }
            //TODO: optimize
            thread {
                val client = ClientManager.createClient()
//                client.properties[ClientProperties.HANDSHAKE_TIMEOUT] = 10000
                try {
                    client.connectToServer(
                        WSClientEndpoint(handler, symbolsMap),
                        URI("ws://78.31.180.192:8025/taserver")
                    )
                    started = true
                    val handlerMessage = handler.obtainMessage(ServerMessageTypes.SERVER_STARTED)
                    handler.sendMessage(handlerMessage)
                } catch (e: DeploymentException) {
                    wasConnectionError = true
                    val handlerMessage = handler.obtainMessage(ServerMessageTypes.CONNECTION_ERROR)
                    handler.sendMessage(handlerMessage)
                }
            }
        }

        /**
         * Starts the client
         */
        fun start(handler: Handler, symbolsMap: Map<String, List<String>>) {
            if (!started) {
                synchronized(this) {
                    if (!started)
                        startClient(handler, symbolsMap)
                }
            }
        }

        /**
         * Stops the client
         */
        fun stop() {
            session?.close(
                CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "session ended.")
            )
            started = false
        }

        /**
         * Adds new updates
         */
        fun addUpdates(exchange: String, symbol: String) {
            val map = mapOf(exchange to setOf(symbol))
            val message = ClientMessage(ClientMessageTypes.ADD_UPD, gson.toJson(map))
            waitedSymbols++
            // TODO: optimize
            thread { session?.asyncRemote?.sendText(gson.toJson(message)) }
        }

        /**
         * Removes updates
         */
        fun removeUpdates(exchange: String, symbol: String) {
            val map = mapOf(exchange to setOf(symbol))
            val message = ClientMessage(ClientMessageTypes.REMOVE_UPD, gson.toJson(map))
            // TODO: optimize
            thread { session?.asyncRemote?.sendText(gson.toJson(message)) }
        }
    }
}