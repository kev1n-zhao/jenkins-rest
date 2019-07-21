package io.github.kev1nst.jenkins.job.handler.impl;

import io.github.kev1nst.jenkins.job.handler.JobProgressHandler;
import io.github.kev1nst.jenkins.job.po.JobSubmission;
import io.github.kev1nst.jenkins.job.po.BuildStatus;
import io.github.kev1nst.jenkins.job.po.QueueItem;
import org.nutz.log.Log;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class DefaultJobProgressHandler implements JobProgressHandler {
    Log LOG;

    @Override
    public void onExecute(QueueItem item) {

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

    }

    @Override
    public void onCompleted(BuildStatus buildStatus) {

    }

}
