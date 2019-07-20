package org.k1.jenclight.job;

import org.k1.jenclight.job.po.QueueItem;
import org.nutz.log.Log;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class DefaultJobProgressHandler implements JobProgressHandler {
    Log LOG;

    public DefaultJobProgressHandler(Log LOG) {
        this.LOG = LOG;
    }

    @Override
    public void log(String logEntry) {
        LOG.info(logEntry);
    }

    @Override
    public void start(QueueItem queueItem, int timeout) {

    }

    @Override
    public void success(String buildUrl) {

    }

    @Override
    public void fail(String buildUrl) {

    }

}
