package org.k1.jenclight.job.handler;

import org.k1.jenclight.job.po.BuildStatus;
import org.k1.jenclight.job.po.JobSubmission;
import org.k1.jenclight.job.po.QueueItem;

public interface JobProgressHandler {
    /**
     *
     * @param logEntry one line of job log
     * @param index log index
     */
    void log(String logEntry, int index);

    /**
     * trigger after job submitted to jenkins
     *  @param jobSubmission the job queue url in jenkins
     * */
    void onSubmit(JobSubmission jobSubmission);

    /**
     * trigger after job start to execute
     * @param item
     */
    void onExecute(QueueItem item);

    /**
     * trigger after job complete
     * @param status
     */
    void onCompleted(BuildStatus status);
}
