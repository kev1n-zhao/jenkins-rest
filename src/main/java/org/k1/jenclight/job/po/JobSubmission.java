package org.k1.jenclight.job.po;

import org.nutz.http.Response;

import java.util.Map;

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
