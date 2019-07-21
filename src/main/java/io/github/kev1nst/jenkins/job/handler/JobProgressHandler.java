package io.github.kev1nst.jenkins.job.handler;

import io.github.kev1nst.jenkins.job.po.BuildStatus;
import io.github.kev1nst.jenkins.job.po.JobSubmission;
import io.github.kev1nst.jenkins.job.po.QueueItem;

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
     * @param item queueItem of jenkins job, from which you can get the executable detail
     */
    void onExecute(QueueItem item);

    /**
     * trigger after job complete
     * @param status the build status of jenkins job
     *              from which you can get the complete status and other attrs
     */
    void onCompleted(BuildStatus status);
}
