package vorotilenko.trendanalyzerserver.dbinteraction;

import java.io.Closeable;
import java.util.logging.Logger;

/**
 * Accumulates data for sending to the DB and sends it by sets
 * using BATCH queries when a given number is reached
 */
public class TradesAccumulator implements Closeable {
    /**
     * The default number of datasets after which the submission occurs
     */
    private static final int DEFAULT_MAX_DATASETS_NUM = 10;

    /**
     * The sending data to the DB thread
     */
    private AsyncDataSender sender;

    /**
     * An array in which datasets are accumulated to be sent to the DB
     */
    private final Object[] dataSets;
    /**
     * The number of accumulated data sets, after reaching which
     * the sending to the database follows
     */
    private final int maxDatasetsNum;
    /**
     * The number of accumulated datasets at the current time
     */
    private int currDatasetsNum = 0;

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * @param batchSender BatchSender for sending data to the DB as BATCH requests
     */
    public TradesAccumulator(BatchSender batchSender) {
        maxDatasetsNum = DEFAULT_MAX_DATASETS_NUM;
        dataSets = new Object[maxDatasetsNum];

        sender = new AsyncDataSender(batchSender);
        sender.start();
    }

    /**
     * @param batchSender    BatchSender for sending data to the DB as BATCH requests
     * @param maxDatasetsNum The number of accumulated data sets, after reaching which
     *                       the sending to the DB will take place
     */
    public TradesAccumulator(BatchSender batchSender, int maxDatasetsNum) {
        this.maxDatasetsNum = maxDatasetsNum;
        dataSets = new Object[maxDatasetsNum];

        sender = new AsyncDataSender(batchSender);
        sender.start();
    }

    /**
     * Adds a request to the current requests list.
     * Writes a record to the DB when the number of requests is equal to maxDatasetsNum.
     *
     * @param data An object containing data to be sent to the DB
     */
    public void add(Object data) {
        dataSets[currDatasetsNum++] = data;
        if (currDatasetsNum == maxDatasetsNum) {
            try {
                sender.sendData(dataSets);
            } catch (NullPointerException e) {
                logger.info("An error occurred while sending data " +
                        "to the TrendAnalyzer DB.");
                e.printStackTrace();
            }
            currDatasetsNum = 0;
        }
    }

    @Override
    public void close() {
        if (sender != null) {
            sender.interrupt();
            sender = null;
        }
    }
}
