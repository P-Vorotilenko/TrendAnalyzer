package vorotilenko.trendanalyzerserver.dbinteraction;

import com.sun.istack.internal.Nullable;

import java.sql.Connection;

/**
 * Forms a BATCH query from the passed data array and sends this query to the DB
 */
public interface BatchSender {
    /**
     * Forms a BATCH query from the passed data array and sends this query to the DB
     */
    void sendBatch(@Nullable Object[] data, Connection dbConnection);
}
