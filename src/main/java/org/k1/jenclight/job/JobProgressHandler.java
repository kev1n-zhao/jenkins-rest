package org.k1.jenclight.job;

public interface JobProgressHandler {
    void log(String logEntry);

    /**
     *
     * @param queueUrl the job queue url in jenkins
     * @param timeout the timeout config passed from the await method
     */
    void start(String queueUrl,int timeout);
    void success(String buildUrl);
    void fail(String buildUrl);
}
