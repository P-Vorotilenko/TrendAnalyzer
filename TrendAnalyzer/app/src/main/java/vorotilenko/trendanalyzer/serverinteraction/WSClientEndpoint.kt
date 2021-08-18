package vorotilenko.trendanalyzer.serverinteraction

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.glassfish.tyrus.client.ClientManager
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
                tradeInfoList
                    .filterNotNull()
                    .filterNot { it.isEmpty() }
                    .forEach { list ->
                        val filteredList = list.filterNotNull()
                        if (filteredList.isNotEmpty()) {
                            val handlerMessage =
                                handler.obtainMessage(ServerMessageTypes.INIT, filteredList)
                            handler.sendMessage(handlerMessage)
                        }
                    }
            }
            ServerMessageTypes.NORMAL_MESSAGE -> {
                val tradeInfo = gson.fromJson(serverMessage.message, TradeInfo::class.java)
                val handlerMessage =
                    handler.obtainMessage(ServerMessageTypes.NORMAL_MESSAGE, tradeInfo)
                handler.sendMessage(handlerMessage)
            }
            ServerMessageTypes.INFO -> {
                val messageStr = serverMessage.message
                Log.i("Trend Analyzer", "Message from server: $messageStr")
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
        private var started = false

        /**
         * Starts the client
         */
        fun start(handler: Handler, symbolsMap: Map<String, List<String>>) {
            if (!started) {
                synchronized(this) {
                    if (!started) {
                        thread {
                            val client = ClientManager.createClient()
                            client.connectToServer(
                                WSClientEndpoint(handler, symbolsMap),
                                URI("ws://192.168.0.104:8025/taserver")
                            )
                        }
                        started = true
                    }
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
            thread { session?.asyncRemote?.sendText(gson.toJson(message)) }
        }

        /**
         * Removes updates
         */
        fun removeUpdates(exchange: String, symbol: String) {
            val map = mapOf(exchange to setOf(symbol))
            val message = ClientMessage(ClientMessageTypes.REMOVE_UPD, gson.toJson(map))
            thread { session?.asyncRemote?.sendText(gson.toJson(message)) }
        }
    }
}