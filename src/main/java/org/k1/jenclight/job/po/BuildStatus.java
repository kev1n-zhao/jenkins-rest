package org.k1.jenclight.job.po;

import org.nutz.http.Response;

/**
 * @author kevinzhao
 * @since 20/07/2019
 */

public class BuildStatus extends Base {

    public static final String SUCCESS = "SUCCESS";

    public static final String RESULT = "result";
    private static final String URL = "url";

    public BuildStatus(Response response) {
        super(response);
    }
    public boolean isCompleted(){
        return data.get(RESULT)!=null;
    }
    public boolean isSuccess(){
        return data.get(RESULT)!=null&&SUCCESS.equals(data.get(RESULT));
    }

    public String getUrl(){
        return (String) data.get(URL);
    }

}
