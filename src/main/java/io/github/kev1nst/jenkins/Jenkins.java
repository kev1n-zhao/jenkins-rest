package io.github.kev1nst.jenkins;

import io.github.kev1nst.jenkins.common.Constant;
import io.github.kev1nst.jenkins.common.JenkinsException;
import io.github.kev1nst.jenkins.job.JobBuilder;
import io.github.kev1nst.jenkins.job.po.JobSubmission;
import io.github.kev1nst.jenkins.common.Lang;
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

import static io.github.kev1nst.jenkins.common.Lang.toFullJobPath;

/**
 * Light-weight Jenkins client for Java, based on Jenkins REST API
 * @author kevinzhao
 * @date 20/07/2019
 */
public class Jenkins {

    private String url;
    private String account;
    private Header header;
    private int timeout = 10000;
    Log LOG= Logs.get();
    private Jenkins(String url, String account, String creds) {
        auth(url, account, creds);
    }

    public Jenkins auth(String url, String account, String creds) {
        this.url = url;
        this.account = account;
        Header header = Header.create();
        String auth = new BASE64Encoder().encode(String.format("%s:%s", account, creds).getBytes());
        header.addv(Constant.AUTHORIZATION, "Basic " + auth);
        Response res = Http.get(String.format(Constant.ISSUER_API_JSON, url), header, timeout);
        if (!res.isOK()) {
            throw new JenkinsException(String.format("fail the connect & authenticate to jenkins %s \n %s", url, descResponse(res)));
        }
        String crumb = (String) Json.fromJson(HashMap.class, res.getContent("utf-8")).get("crumb");
        header.addv(Constant.JENKINS_CRUMB, crumb);
        LOG.info("successfully retrieved the jenkins crumb");
        this.header = header;
        return this;
    }

    public static Jenkins connect(String url, String account, String creds) {
        return new Jenkins(url, account, creds);
    }

    public JobBuilder build(String jobPath, Map<String, Object> params) {
        String jobUrl;
        Object paramToSend = "\"";

        if (params != null) {
            Request request = Request.create(String.format(Constant.BUILD_WITH_PARAMETERS, url, toFullJobPath(jobPath)), Request.METHOD.GET, params);
            jobUrl = request.getUrl().toString();
        } else {
            jobUrl = String.format(Constant.BUILD, url, Lang.toFullJobPath(jobPath));
        }

        Response response = Http.post3(jobUrl, paramToSend, header, timeout);
        if (response.getStatus() != 201) {
            throw new JenkinsException(String.format("fail to build job %s on jenkins %s \n %s", jobPath, url, descResponse(response)));
        }
        JobSubmission submission=new JobSubmission(response);
        return new JobBuilder(submission, header);
    }

    public JobBuilder build(String jobPath) {
        return build(jobPath, null);
    }

    private String descResponse(Response response) {
        String format = "http status: %s \n response content: %s \n response detail: %s \n possible cause: %s";
        //TODO classify the cause of exception
        String cause = "unknown";
        return String.format(format, response.getStatus(), response.getContent(), response.getDetail(), cause);
    }

}

