package vorotilenko.trendanalyzer.serverinteraction

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.glassfish.tyrus.client.ClientManager
import vorotilenko.trendanalyzer.MainActivity
import vorotilenko.trendanalyzer.TradeInfo
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.CONNECTION_ERROR
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.LOADING
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.NORMAL
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import java.net.URI
import java.text.Normalizer
import javax.websocket.*

@ClientEndpoint
object WSClientEndpoint {
    /**
     * Object for parsing JSON.
     */
    private val gson = Gson()

    /**
     * The type of list sent by the server. For parsing JSON.
     */
    private val tradeInfoListType = object : TypeToken<List<List<TradeInfo?>?>>() {}.type

    /**
     * Connected session.
     */
    @Volatile
    private var session: Session? = null

    /**
     * View model of the application.
     */
    @Volatile
    private lateinit var appViewModel: AppViewModel

    /**
     * Flag which informs if the server is started.
     */
    @Volatile
    var started = false
        private set

    /**
     * Flag which informs if there was a connection error.
     */
    @Volatile
    var wasConnectionError = false
        private set

    /**
     * Count of symbols which wait for initial data.
     */
    @Volatile
    private var waitedSymbols = 0
        @Synchronized
        private set(value) {
            field = value
            if (field == 0)
                launchOnMainThread { appViewModel.serverEndpointStateData.value = NORMAL }
            else
                launchOnMainThread { appViewModel.serverEndpointStateData.value = LOADING }
        }

    /**
     * Same as [addUpdates], but without incrementing [waitedSymbols].
     */
    private fun addUpdatesNotIncrementing(exchange: String, symbol: String) = launchOnIOThread {
        val map = mapOf(exchange to setOf(symbol))
        val message = ClientMessage(ClientMessageTypes.ADD_UPD, gson.toJson(map))
        session?.asyncRemote?.sendText(gson.toJson(message))
    }

    /**
     * Called when a connection to the server is opened.
     */
    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        val observedSymbols = appViewModel.observedSymbols
        waitedSymbols = observedSymbols.size
        observedSymbols.forEach {
            val exchangeName = it.exchangeName
            val symbol = it.symbolTicker
            if (exchangeName != null && symbol != null)
                addUpdatesNotIncrementing(exchangeName, symbol)
        }
    }

    /**
     * Displays initial data on [MainActivity].
     */
    private fun postInitialData(tradeInfo: List<TradeInfo>) {
        val data = appViewModel.tradeInfoListsQueueData
        launchOnMainThread {
            val queue = data.value?.also { it.add(tradeInfo) }
            data.value = queue
        }
    }

    /**
     * Handles message with initial data.
     */
    private fun handleInitMessage(serverMessage: ServerMessage) {
        gson.fromJson<List<List<TradeInfo?>?>>(serverMessage.message, tradeInfoListType)
            .filterNotNull()
            .filterNot { it.isEmpty() }
            .forEach { list ->
                val filteredList = list.filterNotNull()
                if (filteredList.isNotEmpty()) {
                    postInitialData(filteredList)
                }
                waitedSymbols--
            }
    }

    /**
     * Displays [TradeInfo] data on [MainActivity].
     */
    private fun postNormalData(tradeInfo: TradeInfo) {
        val data = appViewModel.tradeInfoQueueData
        launchOnMainThread {
            val queue = data.value?.also { it.add(tradeInfo) }
            data.postValue(queue)
        }
    }

    /**
     * Handles message with [TradeInfo].
     */
    private fun handleNormalMessage(serverMessage: ServerMessage) {
        val tradeInfo = gson.fromJson(serverMessage.message, TradeInfo::class.java)
        postNormalData(tradeInfo)
    }

    /**
     * Handles info message
     */
    private fun handleInfoMessage(serverMessage: ServerMessage) =
        Log.i("Trend Analyzer", "Message from server: ${serverMessage.message}")

    /**
     * Called when a message from the server is received.
     */
    @OnMessage
    fun onMessage(message: String, session: Session) {
        val serverMessage = gson.fromJson(message, ServerMessage::class.java)
        when (serverMessage.messageType) {
            ServerMessageTypes.INIT -> handleInitMessage(serverMessage)
            ServerMessageTypes.NORMAL_MESSAGE -> handleNormalMessage(serverMessage)
            ServerMessageTypes.INFO -> handleInfoMessage(serverMessage)
        }
    }

    /**
     * Launches the block of code in coroutine on the IO thread.
     */
    private fun launchOnIOThread(block: () -> Unit) =
        appViewModel.viewModelScope.launch(Dispatchers.IO) { block() }

    /**
     * Launches the block of codi in coroutine on the Main thread.
     */
    private fun launchOnMainThread(block: () -> Unit) =
        appViewModel.viewModelScope.launch(Dispatchers.Main) { block() }

    private fun startClient(appViewModel: AppViewModel) {
        this.appViewModel = appViewModel
        launchOnIOThread {
            this.appViewModel = appViewModel
            val client = ClientManager.createClient()
            //client.properties[ClientProperties.HANDSHAKE_TIMEOUT] = 10000
            try {
                client.connectToServer(this, URI("ws://78.31.180.192:8025/taserver"))
                started = true
            } catch (e: DeploymentException) {
                wasConnectionError = true
                appViewModel.serverEndpointStateData.postValue(CONNECTION_ERROR)
            }
        }
    }

    /**
     * Starts the client.
     */
    fun start(appViewModel: AppViewModel) {
        if (!started) {
            synchronized(this) {
                if (!started)
                    startClient(appViewModel)
            }
        }
    }

    /**
     * Stops the client.
     */
    fun stop() = launchOnIOThread {
        if (started) {
            synchronized(this) {
                if (started) {
                    val closeReasonCode = CloseReason.CloseCodes.NORMAL_CLOSURE
                    val closeReasonPhrase = "session ended."
                    session?.close(CloseReason(closeReasonCode, closeReasonPhrase))
                    session = null
                    started = false
                }
            }
        }
    }

    /**
     * Adds new updates.
     */
    fun addUpdates(exchange: String, symbol: String) = launchOnIOThread {
        val map = mapOf(exchange to setOf(symbol))
        val message = ClientMessage(ClientMessageTypes.ADD_UPD, gson.toJson(map))
        waitedSymbols++
        session?.asyncRemote?.sendText(gson.toJson(message))
    }

    /**
     * Removes updates.
     */
    fun removeUpdates(exchange: String, symbol: String) = launchOnIOThread {
        val map = mapOf(exchange to setOf(symbol))
        val message = ClientMessage(ClientMessageTypes.REMOVE_UPD, gson.toJson(map))
        session?.asyncRemote?.sendText(gson.toJson(message))
    }
}