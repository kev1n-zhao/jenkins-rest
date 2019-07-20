package io.github.kev1nst.jenkins.job.po;

import org.nutz.http.Response;

/**
 * jenkins job queue item
 * @author kevinzhao
 * @since 20/07/2019
 */

public class JobSubmission extends Base{
    private static final String LOCATION = "Location";
    public JobSubmission(Response response) {
        super(response);
    }
    public String getUrl(){
        return (String)data.get(LOCATION);
    }
}
