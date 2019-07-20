package org.k1.jenclight.job.handler.impl;

import org.k1.jenclight.job.handler.JobProgressHandler;
import org.k1.jenclight.job.po.BuildStatus;
import org.k1.jenclight.job.po.JobSubmission;
import org.k1.jenclight.job.po.QueueItem;
import org.nutz.log.Log;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class DefaultJobProgressHandler implements JobProgressHandler {
    Log LOG;

    @Override
    public void onExecute(QueueItem item) {
        System.out.println(item);
    }

    public DefaultJobProgressHandler(Log LOG) {
        this.LOG = LOG;
    }

    @Override
    public void log(String logEntry, int index) {
        LOG.info(logEntry);
    }

    @Override
    public void onSubmit(JobSubmission jobSubmission) {
        System.out.println(jobSubmission);
    }

    @Override
    public void onCompleted(BuildStatus buildStatus) {
        System.out.println(buildStatus);
    }

}
