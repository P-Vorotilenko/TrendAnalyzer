package vorotilenko.trendanalyzer.serverinteraction

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.glassfish.tyrus.client.ClientManager
import vorotilenko.trendanalyzer.TradeInfo
import java.net.URI
import javax.websocket.ClientEndpoint
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
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
                val messageStr = gson.fromJson(serverMessage.message, String::class.java)
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
        private val tradeInfoListType = object : TypeToken<List<List<TradeInfo?>?>?>() {}.type

        /**
         * Starts the client
         */
        fun start(handler: Handler, symbolsMap: Map<String, List<String>>) {
            thread {
                val client = ClientManager.createClient()
                client.connectToServer(
                    WSClientEndpoint(handler, symbolsMap),
                    URI("ws://192.168.0.104:8025/taserver")
                )
            }
        }
    }
}