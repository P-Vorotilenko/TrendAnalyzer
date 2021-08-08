package vorotilenko.trendanalyzer.serverinteraction

import android.os.Handler
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
class WSClientEndpoint(private val handler: Handler) {

    /**
     * Called when a connection to the server is opened
     */
    @OnOpen
    fun onOpen(session: Session) {
        val symbols =
            arrayOf(Currencies.getSymbol(Currencies.BITCOIN, Currencies.TETHER) ?: "BTCUSDT")
        val exchangeMap = HashMap<String, Array<String>>()
        exchangeMap[ExchangeNames.BINANCE] = symbols
        exchangeMap[ExchangeNames.HUOBI] = symbols
        val message = ClientMessage(ClientMessageTypes.SUBSCRIBE_TO_UPD, gson.toJson(exchangeMap))
        message.messageType
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
                val tradeInfoList: List<List<TradeInfo?>?>? =
                    gson.fromJson(serverMessage.message, tradeInfoListType)
                tradeInfoList
                    ?.filterNotNull()
                    ?.filter { it.isNotEmpty() }
                    ?.forEach { list ->
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
        private val tradeInfoListType = object : TypeToken<List<List<TradeInfo?>?>?>(){}.type

        /**
         * Starts the client
         */
        fun start(handler: Handler) {
            thread {
                val client = ClientManager.createClient()
                client.connectToServer(
                    WSClientEndpoint(handler),
                    URI("ws://192.168.0.104:8025/taserver"))
            }
        }
    }
}