package org.k1.jenclight.job;

import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JobBuild {
    private static final Log LOG = Logs.get();

    public static final String SUCCESS = "SUCCESS";
    public static final String EXECUTABLE = "executable";
    public static final String RESULT = "result";
    private static final String API_JSON = "api/json";
    private static final int HTTP_TIMEOUT = 10000;
    private static final String TEXT_SIZE = "X-Text-Size";
    private static final String MORE_DATA = "X-More-Data";
    private static final int JOB_TIMEOUT = 1000 * 60 * 30;
    String queueUrl;
    Header header;
    int timeout = 10000;

    public JobBuild(String queueUrl, Header header) {
        this.queueUrl = queueUrl;
        this.header = header;
    }

    public JobResult await(int timeout, JobProgressHandler jobProgressHandler) {
        long startms=System.currentTimeMillis();
        if(jobProgressHandler!=null){
            jobProgressHandler.start(queueUrl,timeout);
        }
        String buildUrl = waitUntilExecuted(timeout,startms);

        Map jobstatus = waitUntilCompleted(buildUrl, jobProgressHandler,timeout,startms);
        boolean result = false;
        if (SUCCESS.equals(jobstatus.get(RESULT))) {
            LOG.info(String.format("build %s success !", buildUrl));
            result = true;
        } else {
            throw new RuntimeException(String.format("the build %s fail", buildUrl));
        }
        return new JobResult(result, "123");
    }

    public JobResult await(int timeout) {
        JobProgressHandler jobProgressHandler = new DefaultJobProgressHandler(LOG);
        return await(timeout, jobProgressHandler);
    }

    public JobResult await() {
        return await(JOB_TIMEOUT);
    }

    public JobResult await(JobProgressHandler jobProgressHandler) {
        return await(JOB_TIMEOUT, jobProgressHandler);
    }

    private Map waitUntilCompleted(String buildUrl, JobProgressHandler jobProgressHandler, int timeout, long startms) {
        Map jobstatus;
        int start = 0;
        do {
            jobstatus = getBuildStatus(buildUrl);
            start=getLog(buildUrl, start, jobProgressHandler);
            //LOG.info("waiting until job completed {}",buildUrl);
            sleep(2);
            timeoutValidation(buildUrl, timeout, startms);
        } while (jobstatus.get(RESULT) == null);
        return jobstatus;
    }

    private void timeoutValidation(String url, int timeout, long startms) {
        long du=System.currentTimeMillis()-startms;
        if(du>timeout){
            throw new RuntimeException(String.format("the jenkins object %s timeout| time passed - %s ms", url,du));
        }
    }

    private int getLog(String buildUrl, Integer start, JobProgressHandler jobProgressHandler) {
        boolean hasMoreLog = false;
        //do {
            String logUrl = String.format("%s/logText/progressiveText/", buildUrl) + API_JSON + "?start=" + start;
            Response response = Http.post3(logUrl, Lang.map("start", start), header, HTTP_TIMEOUT);
            if (!response.isOK()) {
                throw new RuntimeException(String.format("fail to get the jenkins log of build %s", buildUrl));
            }
            try (InputStreamReader isr = new InputStreamReader(response.getStream())) {
                Streams.eachLine(isr, new Each<String>() {
                    @Override
                    public void invoke(int index, String ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                        jobProgressHandler.log(ele);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            start = Integer.valueOf(response.getHeader().get(TEXT_SIZE));
            hasMoreLog = Boolean.valueOf(response.getHeader().get(MORE_DATA));
        //} while (hasMoreLog);
        return start;
    }

    private String waitUntilExecuted(int timeout,long startms) {
        Map<String, Object> status = null;

        do {
            status = getQueueStatus();
            LOG.info(String.format("waiting until job start executing %s",queueUrl));
            sleep(2);
            timeoutValidation(queueUrl,timeout,startms);
        }
        while (status.get(EXECUTABLE) == null);
        return (String) ((Map) status.get(EXECUTABLE)).get("url");
    }

    private void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            throw new RuntimeException(String.format("fail to get the jenkins job status %s", queueUrl), e);
        }
    }

    private Map getBuildStatus(String buildUrl) {
        Map jobstatus;
        Response response = Http.get(String.format("%s/" + API_JSON, buildUrl), header, timeout);
        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("fail to get jenkins job build from %s", queueUrl));
        }
        jobstatus = Json.fromJson(HashMap.class, response.getContent());
        return jobstatus;
    }

    private Map<String, Object> getQueueStatus() {
        Response response = Http.get(String.format("%s/" + API_JSON, queueUrl), header, timeout);
        if (response.getStatus() != 200) {
            throw new RuntimeException(String.format("fail to get jenkins queue item from %s", queueUrl));
        }
        return Json.fromJson(HashMap.class, response.getContent());
    }

}
