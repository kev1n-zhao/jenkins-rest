package org.k1.jenclight.job;

import org.k1.jenclight.job.po.QueueItem;

public interface JobProgressHandler {
    void log(String logEntry);

    /**
     *  @param queueItem the job queue url in jenkins
     * @param timeout the timeout config passed from the await method
     */
    void start(QueueItem queueItem, int timeout);
    void success(String buildUrl);
    void fail(String buildUrl);
}
