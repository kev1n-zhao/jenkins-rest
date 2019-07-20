package org.k1.jenclight.job;

import org.k1.jenclight.common.Constant;
import org.k1.jenclight.common.JenkinsException;
import org.k1.jenclight.job.handler.impl.DefaultJobProgressHandler;
import org.k1.jenclight.job.handler.JobProgressHandler;
import org.k1.jenclight.job.po.BuildStatus;
import org.k1.jenclight.job.po.JobSubmission;
import org.k1.jenclight.job.po.QueueItem;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JobBuilder {
    private static final Log LOG = Logs.get();


    private static final int HTTP_TIMEOUT = 10000;
    private static final String TEXT_SIZE = "X-Text-Size";
    private static final String MORE_DATA = "X-More-Data";
    private static final int JOB_TIMEOUT = 1000 * 60 * 30;
    JobSubmission jobSubmission;
    Header header;
    int timeout = 10000;

    public JobBuilder(JobSubmission item, Header header) {
        this.jobSubmission = item;
        this.header = header;
    }

    public BuildStatus await(int timeout, JobProgressHandler jobProgressHandler) {
        long startms = System.currentTimeMillis();
        if (jobProgressHandler != null) {
            jobProgressHandler.onSubmit(jobSubmission);
        }
        QueueItem item = waitUntilExecuted(timeout, startms, jobProgressHandler);

        BuildStatus buildStatus = waitUntilCompleted(item, timeout, startms, jobProgressHandler);
        return buildStatus;
    }

    /**
     * @param timeout in ms
     * @return
     */
    public BuildStatus await(int timeout) {
        JobProgressHandler jobProgressHandler = new DefaultJobProgressHandler(LOG);
        return await(timeout, jobProgressHandler);
    }

    public BuildStatus await() {
        return await(JOB_TIMEOUT);
    }

    public BuildStatus await(JobProgressHandler jobProgressHandler) {
        return await(JOB_TIMEOUT, jobProgressHandler);
    }

    private BuildStatus waitUntilCompleted(QueueItem item, int timeout, long startms, JobProgressHandler jobProgressHandler) {
        BuildStatus buildStatus;
        int start = 0;
        do {
            buildStatus = getBuildStatus(item);
            start = getLog(buildStatus.getUrl(), start, jobProgressHandler);
            //LOG.info("waiting until job completed {}",buildUrl);
            timeoutValidation(item.getExecutableUrl(), timeout, startms);
            sleep();
        } while (!buildStatus.isCompleted());
        jobProgressHandler.onCompleted(buildStatus);
        if (buildStatus.isSuccess()) {
            LOG.info(String.format("build %s success !", buildStatus.getUrl()));
        } else {
            LOG.error(String.format("the build %s fail", buildStatus.getUrl()));
        }
        return buildStatus;
    }

    private void timeoutValidation(String url, int timeout, long startms) {
        long du = System.currentTimeMillis() - startms;
        if (du > timeout) {
            throw new JenkinsException(String.format("the jenkins object %s timeout| time passed - %s ms", url, du));
        }
    }

    private int getLog(String buildUrl, Integer start, JobProgressHandler jobProgressHandler) {
        boolean hasMoreLog = false;
        //do {
        String logUrl = String.format("%s/logText/progressiveText/", buildUrl) + Constant.API_JSON + "?start=" + start;
        Response response = Http.post3(logUrl, Lang.map("start", start), header, HTTP_TIMEOUT);
        if (!response.isOK()) {
            throw new JenkinsException(String.format("fail to get the jenkins log of build %s", buildUrl));
        }
        try (InputStreamReader isr = new InputStreamReader(response.getStream())) {
            Streams.eachLine(isr, new Each<String>() {
                @Override
                public void invoke(int index, String ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                    jobProgressHandler.log(ele, index);
                }
            });
        } catch (Exception e) {
            throw new JenkinsException(e);
        }
        start = Integer.valueOf(response.getHeader().get(TEXT_SIZE));
        hasMoreLog = Boolean.valueOf(response.getHeader().get(MORE_DATA));
        //} while (hasMoreLog);
        return start;
    }

    private QueueItem waitUntilExecuted(int timeout, long startms, JobProgressHandler jobProgressHandler) {
        QueueItem item = null;
        LOG.info(String.format("waiting until job start executing %s ....", jobSubmission.getUrl()));
        do {
            item = getQueueStatus(jobSubmission);
            sleep();
            timeoutValidation(jobSubmission.getUrl(), timeout, startms);
        }
        while (item.getExecutable() == null);
        jobProgressHandler.onExecute(item);
        return item;
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new JenkinsException(String.format("fail to get the jenkins job status %s", jobSubmission.getUrl()), e);
        }
    }

    private BuildStatus getBuildStatus(QueueItem item) {
        Map jobstatus;
        Response response = Http.get(String.format("%s/" + Constant.API_JSON, item.getExecutableUrl()), header, timeout);
        if (response.getStatus() != 200) {
            throw new JenkinsException(String.format("fail to get jenkins job build from %s", jobSubmission.getUrl()));
        }
        return new BuildStatus(response);
    }

    private QueueItem getQueueStatus(JobSubmission jobSubmission) {
        Response response = Http.get(String.format("%s/" + Constant.API_JSON, jobSubmission.getUrl()), header, timeout);
        if (response.getStatus() != 200) {
            throw new JenkinsException(String.format("fail to get jenkins queue item from %s", jobSubmission.getUrl()));
        }
        return new QueueItem(response);
    }

}
