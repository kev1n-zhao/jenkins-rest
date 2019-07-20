package org.k1.jenclight;

import org.k1.jenclight.job.JobBuild;
import org.k1.jenclight.job.po.QueueItem;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Light-weight Jenkins client for Java, based on Jenkins REST API
 * @author kevinzhao
 * @date 20/07/2019
 */
public class Jenkins {
    private static final String AUTHORIZATION = "Authorization";
    private static final String JENKINS_CRUMB = "Jenkins-Crumb";
    private String url;
    private String account;
    private Header header;
    private int timeout = 10000;
    Log LOG= Logs.get();
    private Jenkins(String url, String account, String creds) {
        this.url = url;
        this.account = account;
        Header header = Header.create();
        String auth = new BASE64Encoder().encode(String.format("%s:%s", account, creds).getBytes());
        header.addv(AUTHORIZATION, "Basic " + auth);
        Response res = Http.get(String.format("%s/crumbIssuer/api/json", url), header, timeout);
        if (!res.isOK()) {
            throw new RuntimeException(String.format("fail the connect & authenticate to jenkins %s \n %s", url, descResponse(res)));
        }
        String crumb = (String) Json.fromJson(HashMap.class, res.getContent("utf-8")).get("crumb");
        header.addv(JENKINS_CRUMB, crumb);
        LOG.info("successfully retrieved the jenkins crumb");
        this.header = header;
    }

    public static Jenkins connect(String url, String account, String creds) {
        return new Jenkins(url, account, creds);
    }

    public JobBuild build(String jobPath, Map<String, Object> params) {
        String jobUrl;
        Object paramToSend = "\"";

        if (params != null) {
            Request request = Request.create(String.format("%s/%s/buildWithParameters", url, toFullJobPath(jobPath)), Request.METHOD.GET, params);
            jobUrl = request.getUrl().toString();
        } else {
            jobUrl = String.format("%s/%s/build", url, toFullJobPath(jobPath));
        }

        Response response = Http.post3(jobUrl, paramToSend, header, timeout);
        if (response.getStatus() != 201) {
            throw new RuntimeException(String.format("fail to build job %s on jenkins %s \n %s", jobPath, url, descResponse(response)));
        }
        QueueItem qi=new QueueItem(response);
        return new JobBuild(qi, header);
    }

    public JobBuild build(String jobPath) {
        return build(jobPath, null);
    }

    private String descResponse(Response response) {
        String format = "http status: %s \n response content: %s \n response detail: %s \n possible cause: %s";
        //TODO classify the cause of exception
        String cause = "unknown";
        return String.format(format, response.getStatus(), response.getContent(), response.getDetail(), cause);
    }

    /**
     * Parses the provided job name for folders to get the full path for the job.
     *
     * @param jobName the fullName of the job.
     * @return the path of the job including folders if present.
     */
    public static String toFullJobPath(final String jobName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("job/");
        final String[] parts = jobName.split("/");
        if (parts.length == 1) {
            sb.append(parts[0]);
            return sb.toString();
        }
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i != parts.length - 1){
                sb.append("/job/");
            }
        }
        return sb.toString();
    }
}

