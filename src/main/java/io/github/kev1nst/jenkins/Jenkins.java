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

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static io.github.kev1nst.jenkins.common.Lang.toFullJobPath;

/**
 * Light-weight Jenkins client for Java, based on Jenkins REST API
 * @author kevinzhao
 * @since 20/07/2019
 */
public class Jenkins {
    static Log LOG = Logs.get();

    private static final String UTF_8 = "utf-8";
    private String url;
    private Header header;
    private int timeout = 10000;

    private Jenkins(String url, String account, String creds) {
        auth(url, account, creds);
    }

    /**
     * re-authenticate the jenkins instance if the crumb is expired
     * @param url the jenkins url
     * @param account the jenkins account username
     * @param creds the jenkins account password or api token
     * @return Jenkins obj
     */
    public Jenkins auth(String url, String account, String creds) {
        this.url = url;
        Header header = Header.create();

        String auth = null;
        try {
            auth = new String(Base64.getEncoder().encode(String.format("%s:%s", account, creds).getBytes(UTF_8)),UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new JenkinsException(e);
        }
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

    /**
     * static constructor of Jenkins class
     * @param url the jenkins url
     * @param account the jenkins account username
     * @param creds the jenkins account password or api token
     * @return the jenkins obj
     */
    public static Jenkins connect(String url, String account, String creds) {
        return new Jenkins(url, account, creds);
    }

    /**
     * build jenkins job with parameter
     * @param jobPath full job path of jenkins job
     * @param params  job parameter map, the key/value pair in this map should match the jenkins parameter key/value pair
     * @return the jenkins obj
     */
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

    /**
     * build jenkins job without parameter
     * @param jobPath full job path of jenkins job
     * @return the job builder obj, it allows synchronized job result waiting through await method
     */
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

