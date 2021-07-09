package vorotilenko.trendanalyzerserver;

import vorotilenko.trendanalyzerserver.clients.binance.BinanceClient;
import vorotilenko.trendanalyzerserver.clients.huobi.HuobiClient;
import vorotilenko.trendanalyzerserver.dbinteraction.DBInteraction;
import vorotilenko.trendanalyzerserver.server.WSServerEndpoint;

public class App
{

    public static void main(String[] args) {
        // For the start acceleration, the exchange clients are built before
        // other modules of program start to refer to them
        BinanceClient.getInstance();
        HuobiClient.getInstance();
        // Starts the DB scheduled cleanup
        DBInteraction.startDBCleaner();
        // Starts the server
        WSServerEndpoint.start();
    }
}
