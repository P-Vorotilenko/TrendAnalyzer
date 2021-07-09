package vorotilenko.trendanalyzerserver.dbinteraction;

import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Works in separate thread, waiting for getting a dataset for sending it to the DB
 */
public class AsyncDataSender extends Thread {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * A queue of datasets to send
     */
    private final Queue<Object[]> dataSetsQueue = new LinkedList<>();

    /**
     * An object that generates and sends BATCH queries to the DB
     */
    private final BatchSender batchSender;

    /**
     * @param batchSender An object that generates and sends BATCH queries to the DB
     */
    public AsyncDataSender(BatchSender batchSender) {
        super();
        this.batchSender = batchSender;
    }

    /**
     * Sends data to the DB
     *
     * @param data Array of data to send
     */
    public void sendData(@NotNull Object[] data)
            throws NullPointerException {

        // The data is put into the variable this.dataSetsQueue.
        // Notify method is called for the datasetsQueue.
        // The notify method informs the sending data to DB thread that
        // the data needs to be sent
        if (data == null)
            throw new NullPointerException("data == null");
        synchronized (dataSetsQueue) {
            dataSetsQueue.offer(data);
            dataSetsQueue.notify();
        }
    }

    @Override
    public void run() {
        try (Connection dbConnection = DBConnection.getNewConnection()) {
            while (!isInterrupted()) {
                Object[] data;
                // Only the work with queue is synchronized. The data is been sending
                // asynchronously. The main thread can put the new data to the queue
                // while this dataset is been sending
                synchronized (dataSetsQueue) {
                    while ((data = dataSetsQueue.poll()) == null)
                        dataSetsQueue.wait();
                }
                batchSender.sendBatch(data, dbConnection);
            }
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }
}
