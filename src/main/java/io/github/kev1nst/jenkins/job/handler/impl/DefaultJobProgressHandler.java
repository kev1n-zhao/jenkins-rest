package io.github.kev1nst.jenkins.job.handler.impl;

import io.github.kev1nst.jenkins.job.handler.JobProgressHandler;
import io.github.kev1nst.jenkins.job.po.JobSubmission;
import io.github.kev1nst.jenkins.job.po.BuildStatus;
import io.github.kev1nst.jenkins.job.po.QueueItem;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * the default job progress adapter to extend with
 * @author kevinzhao
 * @since 20/07/2019
 */

public class DefaultJobProgressHandler implements JobProgressHandler {
    Log LOG= Logs.get();

    @Override
    public void onExecute(QueueItem item) {

    }

    public DefaultJobProgressHandler() {

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
