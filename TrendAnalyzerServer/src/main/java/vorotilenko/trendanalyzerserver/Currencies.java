package vorotilenko.trendanalyzerserver;

public class Currencies {
    public static final String BTC = "BTC";
    public static final String ETH = "ETH";
    public static final String ADA = "ADA";
    public static final String XRP = "XRP";
    public static final String DOT = "DOT";
    public static final String UNI = "UNI";
    public static final String BCH = "BCH";
    public static final String LTC = "LTC";
    public static final String SOL = "SOL";
    public static final String LINK = "LINK";
    public static final String USDT = "USDT";

    private static final String[] array = {
            BTC, ETH, ADA, XRP, DOT, UNI, BCH, LTC, SOL, LINK, USDT
    };

    /**
     * @return Array which contains all currencies
     */
    public static String[] getArray() {
        return array.clone();
    }
}
